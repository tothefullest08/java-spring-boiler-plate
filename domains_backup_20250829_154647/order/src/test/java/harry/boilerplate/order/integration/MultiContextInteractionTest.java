package harry.boilerplate.order.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import harry.boilerplate.order.application.command.dto.AddCartItemCommand;
import harry.boilerplate.order.application.command.dto.PlaceOrderCommand;
import harry.boilerplate.order.application.command.handler.AddCartItemCommandHandler;
import harry.boilerplate.order.application.command.handler.PlaceOrderCommandHandler;
import harry.boilerplate.order.application.query.dto.CartSummaryQuery;
import harry.boilerplate.order.application.query.dto.CartSummaryResult;
import harry.boilerplate.order.application.query.dto.OrderHistoryQuery;
import harry.boilerplate.order.application.query.dto.OrderHistoryResult;
import harry.boilerplate.order.application.query.handler.CartSummaryQueryHandler;
import harry.boilerplate.order.application.query.handler.OrderHistoryQueryHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

/**
 * 멀티 컨텍스트 상호작용 시나리오 테스트
 * Requirements: 8.5, 8.6
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "logging.level.org.springframework.web.client=DEBUG"
})
@DisplayName("멀티 컨텍스트 상호작용 테스트")
class MultiContextInteractionTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private AddCartItemCommandHandler addCartItemCommandHandler;

    @Autowired
    private PlaceOrderCommandHandler placeOrderCommandHandler;

    @Autowired
    private CartSummaryQueryHandler cartSummaryQueryHandler;

    @Autowired
    private OrderHistoryQueryHandler orderHistoryQueryHandler;

    private WireMockServer shopApiMockServer;
    private WireMockServer userApiMockServer;

    // 테스트 데이터
    private final String user1Id = "user-001";
    private final String user2Id = "user-002";
    private final String shop1Id = "shop-001";
    private final String shop2Id = "shop-002";
    private final String menu1Id = "menu-001";
    private final String menu2Id = "menu-002";

    @BeforeEach
    void setUp() {
        // Shop API Mock 서버 시작 (Shop Context 포트 시뮬레이션)
        shopApiMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8081));
        shopApiMockServer.start();

        // User API Mock 서버 시작 (User Context 포트 시뮬레이션)
        userApiMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8083));
        userApiMockServer.start();

        setupMultiShopApiMocks();
        setupMultiUserApiMocks();
    }

    @AfterEach
    void tearDown() {
        if (shopApiMockServer != null) {
            shopApiMockServer.stop();
        }
        if (userApiMockServer != null) {
            userApiMockServer.stop();
        }
    }

    @Test
    @DisplayName("여러 사용자가 동시에 다른 가게에서 주문하는 시나리오")
    @Transactional
    void 여러_사용자_동시_다른가게_주문() throws InterruptedException {
        // Given: 동시성 테스트를 위한 ExecutorService
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        // When: 두 사용자가 동시에 다른 가게에서 주문
        // 사용자 1 - 가게 1에서 주문
        executorService.execute(() -> {
            try {
                AddCartItemCommand command1 = new AddCartItemCommand(
                        user1Id, shop1Id, menu1Id, Arrays.asList("매운맛"), 2);
                addCartItemCommandHandler.handle(command1);

                PlaceOrderCommand placeOrderCommand1 = new PlaceOrderCommand(user1Id);
                placeOrderCommandHandler.handle(placeOrderCommand1);
            } finally {
                latch.countDown();
            }
        });

        // 사용자 2 - 가게 2에서 주문
        executorService.execute(() -> {
            try {
                AddCartItemCommand command2 = new AddCartItemCommand(
                        user2Id, shop2Id, menu2Id, Arrays.asList("보통맛"), 1);
                addCartItemCommandHandler.handle(command2);

                PlaceOrderCommand placeOrderCommand2 = new PlaceOrderCommand(user2Id);
                placeOrderCommandHandler.handle(placeOrderCommand2);
            } finally {
                latch.countDown();
            }
        });

        // Then: 모든 작업이 완료될 때까지 대기
        boolean completed = latch.await(30, TimeUnit.SECONDS);
        assertThat(completed).isTrue();

        // 각 사용자의 주문 이력 확인
        OrderHistoryResult user1Orders = orderHistoryQueryHandler.handle(new OrderHistoryQuery(user1Id));
        OrderHistoryResult user2Orders = orderHistoryQueryHandler.handle(new OrderHistoryQuery(user2Id));

        assertThat(user1Orders.getOrders()).hasSize(1);
        assertThat(user2Orders.getOrders()).hasSize(1);

        // 서로 다른 가게에서 주문했는지 확인
        assertThat(user1Orders.getOrders().get(0).getShopId()).isEqualTo(shop1Id);
        assertThat(user2Orders.getOrders().get(0).getShopId()).isEqualTo(shop2Id);

        executorService.shutdown();
    }

    @Test
    @DisplayName("동일 사용자가 여러 가게를 순차적으로 주문하는 시나리오")
    @Transactional
    void 동일_사용자_여러가게_순차_주문() {
        // Given: 사용자 1이 가게 1에서 첫 번째 주문
        AddCartItemCommand firstOrderCommand = new AddCartItemCommand(
                user1Id, shop1Id, menu1Id, Arrays.asList("매운맛", "치즈 추가"), 1);
        addCartItemCommandHandler.handle(firstOrderCommand);

        PlaceOrderCommand firstPlaceOrder = new PlaceOrderCommand(user1Id);
        String firstOrderId = placeOrderCommandHandler.handle(firstPlaceOrder);

        // When: 동일 사용자가 다른 가게에서 두 번째 주문
        AddCartItemCommand secondOrderCommand = new AddCartItemCommand(
                user1Id, shop2Id, menu2Id, Arrays.asList("보통맛"), 2);
        addCartItemCommandHandler.handle(secondOrderCommand);

        PlaceOrderCommand secondPlaceOrder = new PlaceOrderCommand(user1Id);
        String secondOrderId = placeOrderCommandHandler.handle(secondPlaceOrder);

        // Then: 두 주문이 모두 성공적으로 생성되었는지 확인
        assertThat(firstOrderId).isNotNull();
        assertThat(secondOrderId).isNotNull();
        assertThat(firstOrderId).isNotEqualTo(secondOrderId);

        // 주문 이력 확인
        OrderHistoryResult orderHistory = orderHistoryQueryHandler.handle(new OrderHistoryQuery(user1Id));
        assertThat(orderHistory.getOrders()).hasSize(2);

        // 각각 다른 가게에서 주문했는지 확인
        boolean hasShop1Order = orderHistory.getOrders().stream()
                .anyMatch(order -> order.getShopId().equals(shop1Id));
        boolean hasShop2Order = orderHistory.getOrders().stream()
                .anyMatch(order -> order.getShopId().equals(shop2Id));

        assertThat(hasShop1Order).isTrue();
        assertThat(hasShop2Order).isTrue();
    }

    @Test
    @DisplayName("가게 영업시간 변경에 따른 주문 처리 시나리오")
    @Transactional
    void 가게_영업시간_변경에_따른_주문_처리() {
        // Given: 처음에는 영업 중인 가게
        String dynamicShopId = "dynamic-shop-123";
        setupDynamicShopStatus(dynamicShopId, true);

        // 첫 번째 주문은 성공
        AddCartItemCommand firstCommand = new AddCartItemCommand(
                user1Id, dynamicShopId, menu1Id, Arrays.asList("매운맛"), 1);
        addCartItemCommandHandler.handle(firstCommand);

        CartSummaryResult cartAfterFirstAdd = cartSummaryQueryHandler.handle(new CartSummaryQuery(user1Id));
        assertThat(cartAfterFirstAdd.getCartSummary().getItems()).hasSize(1);

        // When: 가게가 영업 중단으로 변경
        setupDynamicShopStatus(dynamicShopId, false);

        // Then: 추가 주문 시도 시 실패해야 함
        AddCartItemCommand secondCommand = new AddCartItemCommand(
                user1Id, dynamicShopId, menu1Id, Arrays.asList("보통맛"), 1);

        assertThatThrownBy(() -> addCartItemCommandHandler.handle(secondCommand))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("가게가 영업 중이 아닙니다");
    }

    @Test
    @DisplayName("Shop Context와 User Context 동시 장애 시 복구 시나리오")
    @Transactional
    void Shop_User_Context_동시_장애_복구() {
        // Given: Shop Context와 User Context 모두 첫 번째 호출에서 실패
        setupFailureAndRecoveryScenario();

        // When: 주문 시도 (재시도 로직이 동작해야 함)
        AddCartItemCommand command = new AddCartItemCommand(
                user1Id, shop1Id, menu1Id, Arrays.asList("매운맛"), 1);

        // Then: 재시도 후 성공해야 함
        assertThatCode(() -> addCartItemCommandHandler.handle(command))
                .doesNotThrowAnyException();

        // 재시도 호출 검증
        shopApiMockServer.verify(2, getRequestedFor(urlEqualTo("/api/shops/" + shop1Id)));
        userApiMockServer.verify(2, getRequestedFor(urlEqualTo("/api/users/" + user1Id)));
    }

    @Test
    @DisplayName("메뉴 정보 변경 중 주문 일관성 테스트")
    @Transactional
    void 메뉴_정보_변경중_주문_일관성() {
        // Given: 장바구니에 메뉴 추가
        AddCartItemCommand addCommand = new AddCartItemCommand(
                user1Id, shop1Id, menu1Id, Arrays.asList("매운맛"), 1);
        addCartItemCommandHandler.handle(addCommand);

        // 장바구니에 아이템이 추가되었는지 확인
        CartSummaryResult cartBeforeChange = cartSummaryQueryHandler.handle(new CartSummaryQuery(user1Id));
        assertThat(cartBeforeChange.getCartSummary().getItems()).hasSize(1);

        // When: 메뉴 정보가 변경되어도 (가격 변경 등)
        setupChangedMenuInfo();

        // Then: 이미 담긴 장바구니의 아이템은 주문 시점의 정보로 처리되어야 함
        PlaceOrderCommand placeOrderCommand = new PlaceOrderCommand(user1Id);
        String orderId = placeOrderCommandHandler.handle(placeOrderCommand);

        assertThat(orderId).isNotNull();

        // 주문이 성공적으로 생성되었는지 확인
        OrderHistoryResult orderHistory = orderHistoryQueryHandler.handle(new OrderHistoryQuery(user1Id));
        assertThat(orderHistory.getOrders()).hasSize(1);
    }

    @Test
    @DisplayName("컨텍스트 간 통신 타임아웃 처리 테스트")
    @Transactional
    void 컨텍스트간_통신_타임아웃_처리() {
        // Given: Shop API가 응답 지연
        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + shop1Id))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(10000) // 10초 지연
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "shop": {
                                        "id": "%s",
                                        "name": "지연 테스트 가게",
                                        "open": true,
                                        "minOrderAmount": 10000
                                    }
                                }
                                """.formatted(shop1Id))));

        // When & Then: 타임아웃으로 인한 예외 발생
        AddCartItemCommand command = new AddCartItemCommand(
                user1Id, shop1Id, menu1Id, Arrays.asList("매운맛"), 1);

        assertThatThrownBy(() -> addCartItemCommandHandler.handle(command))
                .isInstanceOf(RuntimeException.class);
    }

    private void setupMultiShopApiMocks() {
        // 가게 1 API Mocks
        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + shop1Id))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "shop": {
                                        "id": "%s",
                                        "name": "테스트 가게 1",
                                        "open": true,
                                        "minOrderAmount": 10000
                                    }
                                }
                                """.formatted(shop1Id))));

        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + shop1Id + "/menus/" + menu1Id))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "menu": {
                                        "id": "%s",
                                        "name": "메뉴 1",
                                        "description": "테스트 메뉴 1",
                                        "basePrice": 15000,
                                        "open": true
                                    }
                                }
                                """.formatted(menu1Id))));

        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + shop1Id + "/menus/" + menu1Id + "/options"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "options": [
                                        { "name": "매운맛", "price": 0 },
                                        { "name": "치즈 추가", "price": 2000 },
                                        { "name": "보통맛", "price": 0 }
                                    ]
                                }
                                """)));

        // 가게 2 API Mocks
        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + shop2Id))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "shop": {
                                        "id": "%s",
                                        "name": "테스트 가게 2",
                                        "open": true,
                                        "minOrderAmount": 12000
                                    }
                                }
                                """.formatted(shop2Id))));

        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + shop2Id + "/menus/" + menu2Id))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "menu": {
                                        "id": "%s",
                                        "name": "메뉴 2",
                                        "description": "테스트 메뉴 2",
                                        "basePrice": 18000,
                                        "open": true
                                    }
                                }
                                """.formatted(menu2Id))));

        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + shop2Id + "/menus/" + menu2Id + "/options"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "options": [
                                        { "name": "보통맛", "price": 0 },
                                        { "name": "추가 토핑", "price": 3000 }
                                    ]
                                }
                                """)));
    }

    private void setupMultiUserApiMocks() {
        // 사용자 1 Mock
        userApiMockServer.stubFor(get(urlEqualTo("/api/users/" + user1Id))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "id": "%s",
                                    "name": "테스트 사용자 1",
                                    "email": "user1@example.com"
                                }
                                """.formatted(user1Id))));

        // 사용자 2 Mock
        userApiMockServer.stubFor(get(urlEqualTo("/api/users/" + user2Id))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "id": "%s",
                                    "name": "테스트 사용자 2",
                                    "email": "user2@example.com"
                                }
                                """.formatted(user2Id))));
    }

    private void setupDynamicShopStatus(String shopId, boolean isOpen) {
        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + shopId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "shop": {
                                        "id": "%s",
                                        "name": "동적 상태 가게",
                                        "open": %s,
                                        "minOrderAmount": 10000
                                    }
                                }
                                """.formatted(shopId, isOpen))));

        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + shopId + "/menus/" + menu1Id))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "menu": {
                                        "id": "%s",
                                        "name": "동적 메뉴",
                                        "description": "동적 테스트 메뉴",
                                        "basePrice": 15000,
                                        "open": true
                                    }
                                }
                                """.formatted(menu1Id))));

        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + shopId + "/menus/" + menu1Id + "/options"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "options": [
                                        { "name": "매운맛", "price": 0 },
                                        { "name": "보통맛", "price": 0 }
                                    ]
                                }
                                """)));
    }

    private void setupFailureAndRecoveryScenario() {
        // Shop API 실패 후 복구 시나리오
        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + shop1Id))
                .inScenario("shop-failure-recovery")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Shop Service Unavailable"))
                .willSetStateTo("Failed"));

        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + shop1Id))
                .inScenario("shop-failure-recovery")
                .whenScenarioStateIs("Failed")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "shop": {
                                        "id": "%s",
                                        "name": "복구된 가게 1",
                                        "open": true,
                                        "minOrderAmount": 10000
                                    }
                                }
                                """.formatted(shop1Id))));

        // User API 실패 후 복구 시나리오
        userApiMockServer.stubFor(get(urlEqualTo("/api/users/" + user1Id))
                .inScenario("user-failure-recovery")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("User Service Unavailable"))
                .willSetStateTo("Failed"));

        userApiMockServer.stubFor(get(urlEqualTo("/api/users/" + user1Id))
                .inScenario("user-failure-recovery")
                .whenScenarioStateIs("Failed")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "id": "%s",
                                    "name": "복구된 사용자 1",
                                    "email": "recovered-user1@example.com"
                                }
                                """.formatted(user1Id))));
    }

    private void setupChangedMenuInfo() {
        // 메뉴 정보가 변경된 상황 (가격 인상)
        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + shop1Id + "/menus/" + menu1Id))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "menu": {
                                        "id": "%s",
                                        "name": "메뉴 1 (가격 인상)",
                                        "description": "가격이 인상된 테스트 메뉴 1",
                                        "basePrice": 18000,
                                        "open": true
                                    }
                                }
                                """.formatted(menu1Id))));
    }
}
