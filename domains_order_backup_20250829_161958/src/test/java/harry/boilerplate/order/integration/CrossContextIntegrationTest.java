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
import harry.boilerplate.order.domain.exception.CartDomainException;
import harry.boilerplate.order.domain.exception.CartErrorCode;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

/**
 * 크로스 컨텍스트 통합 테스트
 * - 실제 비즈니스 시나리오를 통한 컨텍스트 간 상호작용 검증
 * Requirements: 8.5, 8.6
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.hikari.maximum-pool-size=10",
    "logging.level.org.springframework.web.client=DEBUG"
})
@DisplayName("크로스 컨텍스트 통합 테스트")
class CrossContextIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("integration_test_db")
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

    // 실제 비즈니스 시나리오 테스트 데이터
    private final String customerId = "customer-kim-12345";
    private final String pizzaShopId = "pizza-shop-001";
    private final String chickenShopId = "chicken-shop-002";
    private final String pizzaMenuId = "margherita-pizza";
    private final String chickenMenuId = "fried-chicken";

    @BeforeEach
    void setUp() {
        // Shop Context 시뮬레이션 (포트 8081)
        shopApiMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8081));
        shopApiMockServer.start();

        // User Context 시뮬레이션 (포트 8083)
        userApiMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8083));
        userApiMockServer.start();

        setupRealBusinessScenarioMocks();
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
    @DisplayName("실제 음식 주문 시나리오 - 피자 주문 후 치킨으로 변경")
    @Transactional
    void 실제_음식_주문_시나리오_피자에서_치킨으로_변경() {
        // Given: 고객이 피자집에서 마르게리타 피자를 장바구니에 담음
        AddCartItemCommand pizzaOrderCommand = new AddCartItemCommand(
                customerId, 
                pizzaShopId, 
                pizzaMenuId, 
                Arrays.asList("올리브 추가", "치즈 추가"), 
                2
        );

        // When: 피자를 장바구니에 추가
        addCartItemCommandHandler.handle(pizzaOrderCommand);

        // Then: 장바구니에 피자가 담겼는지 확인
        CartSummaryResult cartWithPizza = cartSummaryQueryHandler.handle(new CartSummaryQuery(customerId));
        assertThat(cartWithPizza.getCartSummary()).isNotNull();
        assertThat(cartWithPizza.getCartSummary().getShopId()).isEqualTo(pizzaShopId);
        assertThat(cartWithPizza.getCartSummary().getShopName()).isEqualTo("맛있는 피자집");
        assertThat(cartWithPizza.getCartSummary().getItems()).hasSize(1);
        assertThat(cartWithPizza.getCartSummary().getItems().get(0).getMenuName()).isEqualTo("마르게리타 피자");
        assertThat(cartWithPizza.getCartSummary().getItems().get(0).getQuantity()).isEqualTo(2);

        // 예상 가격 계산: (기본가 18000 + 올리브 추가 1000 + 치즈 추가 2000) * 2개 = 42000원
        BigDecimal expectedPizzaPrice = new BigDecimal("42000.00");
        assertThat(cartWithPizza.getCartSummary().getTotalPrice()).isEqualByComparingTo(expectedPizzaPrice);

        // When: 고객이 마음을 바꿔서 치킨집으로 변경 (장바구니 초기화됨)
        AddCartItemCommand chickenOrderCommand = new AddCartItemCommand(
                customerId, 
                chickenShopId, 
                chickenMenuId, 
                Arrays.asList("양념", "순살"), 
                1
        );
        addCartItemCommandHandler.handle(chickenOrderCommand);

        // Then: 장바구니가 치킨집으로 변경되고 피자는 사라짐
        CartSummaryResult cartWithChicken = cartSummaryQueryHandler.handle(new CartSummaryQuery(customerId));
        assertThat(cartWithChicken.getCartSummary().getShopId()).isEqualTo(chickenShopId);
        assertThat(cartWithChicken.getCartSummary().getShopName()).isEqualTo("바삭바삭 치킨집");
        assertThat(cartWithChicken.getCartSummary().getItems()).hasSize(1);
        assertThat(cartWithChicken.getCartSummary().getItems().get(0).getMenuName()).isEqualTo("후라이드 치킨");

        // When: 최종 주문
        PlaceOrderCommand placeOrderCommand = new PlaceOrderCommand(customerId);
        String orderId = placeOrderCommandHandler.handle(placeOrderCommand);

        // Then: 치킨 주문이 성공적으로 생성됨
        assertThat(orderId).isNotNull();

        OrderHistoryResult orderHistory = orderHistoryQueryHandler.handle(new OrderHistoryQuery(customerId));
        assertThat(orderHistory.getOrders()).hasSize(1);
        assertThat(orderHistory.getOrders().get(0).getShopId()).isEqualTo(chickenShopId);
        assertThat(orderHistory.getOrders().get(0).getOrderItems()).hasSize(1);

        // 최종 주문 후 장바구니는 비어있어야 함
        CartSummaryResult emptyCart = cartSummaryQueryHandler.handle(new CartSummaryQuery(customerId));
        assertThat(emptyCart.isEmpty() || (emptyCart.getCartSummary() != null && emptyCart.getCartSummary().getItems().isEmpty())).isTrue();
    }

    @Test
    @DisplayName("동일 가게에서 여러 메뉴 주문 및 옵션 조합 테스트")
    @Transactional
    void 동일_가게_여러메뉴_옵션조합_테스트() {
        // Given: 피자집에서 여러 종류 주문
        
        // 마르게리타 피자 2개 (올리브 추가)
        AddCartItemCommand margheritaCommand = new AddCartItemCommand(
                customerId, pizzaShopId, pizzaMenuId, Arrays.asList("올리브 추가"), 2);
        addCartItemCommandHandler.handle(margheritaCommand);

        // 마르게리타 피자 1개 (치즈 추가 + 올리브 추가) - 다른 옵션 조합
        AddCartItemCommand margheritaWithCheeseCommand = new AddCartItemCommand(
                customerId, pizzaShopId, pizzaMenuId, Arrays.asList("올리브 추가", "치즈 추가"), 1);
        addCartItemCommandHandler.handle(margheritaWithCheeseCommand);

        // 마르게리타 피자 1개 (올리브 추가만) - 첫 번째와 동일한 조합이므로 병합되어야 함
        AddCartItemCommand margheritaSameOptionCommand = new AddCartItemCommand(
                customerId, pizzaShopId, pizzaMenuId, Arrays.asList("올리브 추가"), 1);
        addCartItemCommandHandler.handle(margheritaSameOptionCommand);

        // When: 장바구니 확인
        CartSummaryResult cart = cartSummaryQueryHandler.handle(new CartSummaryQuery(customerId));

        // Then: 두 개의 라인 아이템이 있어야 함 (옵션 조합이 다르므로)
        assertThat(cart.getCartSummary().getItems()).hasSize(2);

        // 첫 번째 라인: 올리브 추가만 - 3개 (2 + 1 병합)
        boolean hasOliveOnlyLine = cart.getCartSummary().getItems().stream()
                .anyMatch(item -> item.getQuantity() == 3 && 
                         item.getSelectedOptions().size() == 1 &&
                         item.getSelectedOptions().contains("올리브 추가"));
        assertThat(hasOliveOnlyLine).isTrue();

        // 두 번째 라인: 올리브 + 치즈 추가 - 1개
        boolean hasOliveAndCheeseLine = cart.getCartSummary().getItems().stream()
                .anyMatch(item -> item.getQuantity() == 1 && 
                         item.getSelectedOptions().size() == 2 &&
                         item.getSelectedOptions().contains("올리브 추가") &&
                         item.getSelectedOptions().contains("치즈 추가"));
        assertThat(hasOliveAndCheeseLine).isTrue();

        // 총 금액 확인
        // 올리브만 3개: (18000 + 1000) * 3 = 57000
        // 올리브+치즈 1개: (18000 + 1000 + 2000) * 1 = 21000
        // 총합: 78000원
        BigDecimal expectedTotal = new BigDecimal("78000.00");
        assertThat(cart.getCartSummary().getTotalPrice()).isEqualByComparingTo(expectedTotal);
    }

    @Test
    @DisplayName("최소 주문금액 미달로 인한 주문 실패 시나리오")
    @Transactional
    void 최소_주문금액_미달_주문_실패() {
        // Given: 피자집 최소 주문금액은 30000원인데, 작은 메뉴만 주문
        setupSmallOrderScenario();

        String smallMenuId = "small-pizza";
        AddCartItemCommand smallOrderCommand = new AddCartItemCommand(
                customerId, pizzaShopId, smallMenuId, Collections.emptyList(), 1);

        addCartItemCommandHandler.handle(smallOrderCommand);

        // 장바구니에는 담기지만
        CartSummaryResult cart = cartSummaryQueryHandler.handle(new CartSummaryQuery(customerId));
        assertThat(cart.getCartSummary().getItems()).hasSize(1);
        assertThat(cart.getCartSummary().getTotalPrice()).isEqualByComparingTo(new BigDecimal("8000.00")); // 최소 주문금액 미달

        // When & Then: 주문 시도 시 최소 주문금액 미달로 실패
        PlaceOrderCommand placeOrderCommand = new PlaceOrderCommand(customerId);
        
        assertThatThrownBy(() -> placeOrderCommandHandler.handle(placeOrderCommand))
                .isInstanceOf(CartDomainException.class)
                .satisfies(e -> {
                    CartDomainException cartException = (CartDomainException) e;
                    assertThat(cartException.getErrorCode()).isEqualTo(CartErrorCode.MINIMUM_ORDER_AMOUNT_NOT_MET);
                });
    }

    @Test
    @DisplayName("가게 영업 중단 중 주문 시도 실패 시나리오")
    @Transactional
    void 가게_영업중단중_주문_시도_실패() {
        // Given: 피자집이 일시적으로 영업 중단
        setupClosedShopScenario();

        // When & Then: 영업 중단인 가게에 주문 시도 시 실패
        AddCartItemCommand command = new AddCartItemCommand(
                customerId, pizzaShopId, pizzaMenuId, Arrays.asList("올리브 추가"), 1);

        assertThatThrownBy(() -> addCartItemCommandHandler.handle(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("가게가 영업 중이 아닙니다");

        // 장바구니는 비어있어야 함
        CartSummaryResult emptyCart = cartSummaryQueryHandler.handle(new CartSummaryQuery(customerId));
        assertThat(emptyCart.isEmpty() || (emptyCart.getCartSummary() != null && emptyCart.getCartSummary().getItems().isEmpty())).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 주문 시도")
    @Transactional
    void 존재하지않는_사용자_주문_시도() {
        String nonExistentCustomerId = "non-existent-customer";

        // Given: 존재하지 않는 사용자 ID
        userApiMockServer.stubFor(get(urlEqualTo("/api/users/" + nonExistentCustomerId))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("User not found")));

        // When & Then: 존재하지 않는 사용자의 주문 시도 시 실패
        AddCartItemCommand command = new AddCartItemCommand(
                nonExistentCustomerId, pizzaShopId, pizzaMenuId, Arrays.asList("올리브 추가"), 1);

        assertThatThrownBy(() -> addCartItemCommandHandler.handle(command))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("컨텍스트 간 API 호출 순서 및 데이터 일관성 검증")
    @Transactional
    void 컨텍스트간_API_호출순서_데이터일관성_검증() {
        // Given: 정상적인 주문 플로우
        AddCartItemCommand command = new AddCartItemCommand(
                customerId, pizzaShopId, pizzaMenuId, Arrays.asList("올리브 추가"), 1);

        // When: 주문 처리
        addCartItemCommandHandler.handle(command);
        PlaceOrderCommand placeOrder = new PlaceOrderCommand(customerId);
        String orderId = placeOrderCommandHandler.handle(placeOrder);

        // Then: API 호출 순서 검증
        // 1. 사용자 유효성 검증 (User Context)
        userApiMockServer.verify(getRequestedFor(urlEqualTo("/api/users/" + customerId)));
        
        // 2. 가게 영업 상태 확인 (Shop Context)
        shopApiMockServer.verify(getRequestedFor(urlEqualTo("/api/shops/" + pizzaShopId)));
        
        // 3. 메뉴 정보 조회 (Shop Context)
        shopApiMockServer.verify(getRequestedFor(urlEqualTo("/api/shops/" + pizzaShopId + "/menus/" + pizzaMenuId)));
        
        // 4. 메뉴 옵션 정보 조회 (Shop Context)
        shopApiMockServer.verify(getRequestedFor(urlEqualTo("/api/shops/" + pizzaShopId + "/menus/" + pizzaMenuId + "/options")));

        // 데이터 일관성 검증
        OrderHistoryResult orderHistory = orderHistoryQueryHandler.handle(new OrderHistoryQuery(customerId));
        assertThat(orderHistory.getOrders()).hasSize(1);
        assertThat(orderHistory.getOrders().get(0).getOrderId()).isEqualTo(orderId);
        assertThat(orderHistory.getOrders().get(0).getShopId()).isEqualTo(pizzaShopId);
    }

    private void setupRealBusinessScenarioMocks() {
        // 고객 정보 Mock
        userApiMockServer.stubFor(get(urlEqualTo("/api/users/" + customerId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "id": "%s",
                                    "name": "김고객",
                                    "email": "kim.customer@example.com"
                                }
                                """.formatted(customerId))));

        // 피자집 정보 Mock
        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + pizzaShopId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "shop": {
                                        "id": "%s",
                                        "name": "맛있는 피자집",
                                        "open": true,
                                        "minOrderAmount": 30000
                                    }
                                }
                                """.formatted(pizzaShopId))));

        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + pizzaShopId + "/menus/" + pizzaMenuId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "menu": {
                                        "id": "%s",
                                        "name": "마르게리타 피자",
                                        "description": "신선한 토마토와 모짜렐라 치즈가 들어간 클래식 피자",
                                        "basePrice": 18000,
                                        "open": true
                                    }
                                }
                                """.formatted(pizzaMenuId))));

        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + pizzaShopId + "/menus/" + pizzaMenuId + "/options"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "options": [
                                        { "name": "올리브 추가", "price": 1000 },
                                        { "name": "치즈 추가", "price": 2000 },
                                        { "name": "페퍼로니 추가", "price": 3000 }
                                    ]
                                }
                                """)));

        // 치킨집 정보 Mock
        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + chickenShopId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "shop": {
                                        "id": "%s",
                                        "name": "바삭바삭 치킨집",
                                        "open": true,
                                        "minOrderAmount": 15000
                                    }
                                }
                                """.formatted(chickenShopId))));

        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + chickenShopId + "/menus/" + chickenMenuId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "menu": {
                                        "id": "%s",
                                        "name": "후라이드 치킨",
                                        "description": "바삭하고 맛있는 후라이드 치킨",
                                        "basePrice": 16000,
                                        "open": true
                                    }
                                }
                                """.formatted(chickenMenuId))));

        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + chickenShopId + "/menus/" + chickenMenuId + "/options"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "options": [
                                        { "name": "양념", "price": 0 },
                                        { "name": "순살", "price": 2000 },
                                        { "name": "뼈있는", "price": 0 }
                                    ]
                                }
                                """)));
    }

    private void setupSmallOrderScenario() {
        String smallMenuId = "small-pizza";
        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + pizzaShopId + "/menus/" + smallMenuId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "menu": {
                                        "id": "%s",
                                        "name": "미니 피자",
                                        "description": "작은 사이즈 피자",
                                        "basePrice": 8000,
                                        "open": true
                                    }
                                }
                                """.formatted(smallMenuId))));

        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + pizzaShopId + "/menus/" + smallMenuId + "/options"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "options": []
                                }
                                """)));
    }

    private void setupClosedShopScenario() {
        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + pizzaShopId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "shop": {
                                        "id": "%s",
                                        "name": "맛있는 피자집 (임시 휴업)",
                                        "open": false,
                                        "minOrderAmount": 30000
                                    }
                                }
                                """.formatted(pizzaShopId))));
    }
}