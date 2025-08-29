package harry.boilerplate.order.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import harry.boilerplate.order.application.command.dto.AddCartItemCommand;
import harry.boilerplate.order.application.command.dto.PlaceOrderCommand;
import harry.boilerplate.order.application.command.handler.AddCartItemCommandHandler;
import harry.boilerplate.order.application.command.handler.PlaceOrderCommandHandler;
import harry.boilerplate.order.application.query.dto.CartSummaryQuery;
import harry.boilerplate.order.application.query.dto.CartSummaryResult;
import harry.boilerplate.order.application.query.handler.CartSummaryQueryHandler;
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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

/**
 * 전체 주문 플로우 End-to-End 테스트
 * Requirements: 8.5, 8.6
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "logging.level.org.springframework.web.client=DEBUG"
})
@DisplayName("주문 플로우 E2E 테스트")
class OrderPlacementE2ETest {

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

    private WireMockServer shopApiMockServer;
    private WireMockServer userApiMockServer;

    // 테스트 데이터
    private final String userId = "test-user-123";
    private final String shopId = "test-shop-456";
    private final String menuId = "test-menu-789";

    @BeforeEach
    void setUp() {
        // Shop API Mock 서버 시작
        shopApiMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8081));
        shopApiMockServer.start();

        // User API Mock 서버 시작
        userApiMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8083));
        userApiMockServer.start();

        setupShopApiMocks();
        setupUserApiMocks();
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
    @DisplayName("전체 주문 플로우 E2E 테스트 - 성공 시나리오")
    @Transactional
    void 전체_주문_플로우_성공() {
        // Given: 빈 장바구니에서 시작

        // When 1: 첫 번째 메뉴 아이템을 장바구니에 추가
        AddCartItemCommand addFirstItemCommand = new AddCartItemCommand(
                userId, shopId, menuId, Arrays.asList("매운맛", "치즈 추가"), 2);
        
        addCartItemCommandHandler.handle(addFirstItemCommand);

        // Then 1: 장바구니에 아이템이 추가되었는지 확인
        CartSummaryResult cartAfterFirstItem = cartSummaryQueryHandler.handle(new CartSummaryQuery(userId));
        assertThat(cartAfterFirstItem).isNotNull();
        assertThat(cartAfterFirstItem.getCartSummary().getShopId()).isEqualTo(shopId);
        assertThat(cartAfterFirstItem.getCartSummary().getItems()).hasSize(1);
        assertThat(cartAfterFirstItem.getCartSummary().getItems().get(0).getQuantity()).isEqualTo(2);

        // When 2: 동일한 메뉴+옵션 조합을 추가 (병합되어야 함)
        AddCartItemCommand addSameItemCommand = new AddCartItemCommand(
                userId, shopId, menuId, Arrays.asList("매운맛", "치즈 추가"), 1);
        
        addCartItemCommandHandler.handle(addSameItemCommand);

        // Then 2: 동일한 메뉴+옵션이 병합되었는지 확인
        CartSummaryResult cartAfterMerge = cartSummaryQueryHandler.handle(new CartSummaryQuery(userId));
        assertThat(cartAfterMerge.getCartSummary().getItems()).hasSize(1);
        assertThat(cartAfterMerge.getCartSummary().getItems().get(0).getQuantity()).isEqualTo(3); // 2 + 1 = 3

        // When 3: 다른 옵션 조합으로 동일 메뉴 추가 (별도 라인 아이템)
        AddCartItemCommand addDifferentOptionCommand = new AddCartItemCommand(
                userId, shopId, menuId, Arrays.asList("매운맛", "곱빼기"), 1);
        
        addCartItemCommandHandler.handle(addDifferentOptionCommand);

        // Then 3: 별도 라인 아이템으로 추가되었는지 확인
        CartSummaryResult cartAfterDifferentOption = cartSummaryQueryHandler.handle(new CartSummaryQuery(userId));
        assertThat(cartAfterDifferentOption.getCartSummary().getItems()).hasSize(2);

        // When 4: 주문 생성
        PlaceOrderCommand placeOrderCommand = new PlaceOrderCommand(userId);
        String orderId = placeOrderCommandHandler.handle(placeOrderCommand);

        // Then 4: 주문이 성공적으로 생성되었는지 확인
        assertThat(orderId).isNotNull();
        assertThat(orderId).isNotBlank();

        // 주문 생성 후 장바구니가 초기화되는지 확인
        CartSummaryResult cartAfterOrder = cartSummaryQueryHandler.handle(new CartSummaryQuery(userId));
        assertThat(cartAfterOrder.isEmpty() || cartAfterOrder.getCartSummary().getItems().isEmpty()).isTrue();

        // API 호출 검증
        verifyShopApiCalls();
        verifyUserApiCalls();
    }

    @Test
    @DisplayName("다른 가게 메뉴 추가 시 장바구니 초기화 플로우")
    @Transactional
    void 다른_가게_메뉴_추가시_장바구니_초기화() {
        String anotherShopId = "another-shop-999";
        String anotherMenuId = "another-menu-888";

        // 다른 가게 API Mock 설정
        setupAnotherShopApiMocks(anotherShopId, anotherMenuId);

        // Given: 첫 번째 가게의 메뉴를 장바구니에 추가
        AddCartItemCommand firstShopCommand = new AddCartItemCommand(
                userId, shopId, menuId, Arrays.asList("매운맛"), 1);
        addCartItemCommandHandler.handle(firstShopCommand);

        // 첫 번째 가게의 아이템이 장바구니에 있는지 확인
        CartSummaryResult cartWithFirstShop = cartSummaryQueryHandler.handle(new CartSummaryQuery(userId));
        assertThat(cartWithFirstShop.getCartSummary().getShopId()).isEqualTo(shopId);
        assertThat(cartWithFirstShop.getCartSummary().getItems()).hasSize(1);

        // When: 다른 가게의 메뉴를 장바구니에 추가
        AddCartItemCommand secondShopCommand = new AddCartItemCommand(
                userId, anotherShopId, anotherMenuId, Arrays.asList("보통맛"), 2);
        addCartItemCommandHandler.handle(secondShopCommand);

        // Then: 장바구니가 새로운 가게로 초기화되었는지 확인
        CartSummaryResult cartWithSecondShop = cartSummaryQueryHandler.handle(new CartSummaryQuery(userId));
        assertThat(cartWithSecondShop.getCartSummary().getShopId()).isEqualTo(anotherShopId);
        assertThat(cartWithSecondShop.getCartSummary().getItems()).hasSize(1);
        assertThat(cartWithSecondShop.getCartSummary().getItems().get(0).getMenuId()).isEqualTo(anotherMenuId);
        assertThat(cartWithSecondShop.getCartSummary().getItems().get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("가게가 영업 중단 상태일 때 주문 실패")
    @Transactional
    void 가게_영업중단시_주문_실패() {
        // Given: 가게가 영업 중단 상태로 Mock 설정
        String closedShopId = "closed-shop-123";
        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + closedShopId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "shop": {
                                        "id": "%s",
                                        "name": "영업 중단 가게",
                                        "open": false,
                                        "minOrderAmount": 10000
                                    }
                                }
                                """.formatted(closedShopId))));

        // When & Then: 영업 중단인 가게의 메뉴 추가 시 예외 발생
        AddCartItemCommand command = new AddCartItemCommand(
                userId, closedShopId, menuId, Arrays.asList("매운맛"), 1);

        assertThatThrownBy(() -> addCartItemCommandHandler.handle(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("가게가 영업 중이 아닙니다");
    }

    @Test
    @DisplayName("유효하지 않은 사용자의 주문 시도")
    @Transactional
    void 유효하지않은_사용자_주문_시도() {
        String invalidUserId = "invalid-user-999";

        // Given: 유효하지 않은 사용자 Mock 설정
        userApiMockServer.stubFor(get(urlEqualTo("/api/users/" + invalidUserId))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("User not found")));

        // When & Then: 유효하지 않은 사용자의 주문 시도 시 예외 발생
        AddCartItemCommand command = new AddCartItemCommand(
                invalidUserId, shopId, menuId, Arrays.asList("매운맛"), 1);

        assertThatThrownBy(() -> addCartItemCommandHandler.handle(command))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("API 서버 장애 시 재시도 및 복구 테스트")
    @Transactional
    void API서버_장애시_재시도_복구() {
        // Given: 첫 번째 호출은 실패, 두 번째 호출은 성공하도록 설정
        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + shopId))
                .inScenario("server-failure")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error"))
                .willSetStateTo("Failed"));

        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + shopId))
                .inScenario("server-failure")
                .whenScenarioStateIs("Failed")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "shop": {
                                        "id": "%s",
                                        "name": "테스트 가게",
                                        "open": true,
                                        "minOrderAmount": 10000
                                    }
                                }
                                """.formatted(shopId))));

        // When: 장바구니에 아이템 추가 (재시도 로직이 동작해야 함)
        AddCartItemCommand command = new AddCartItemCommand(
                userId, shopId, menuId, Arrays.asList("매운맛"), 1);

        // Then: 재시도 후 성공적으로 추가되어야 함
        assertThatCode(() -> addCartItemCommandHandler.handle(command))
                .doesNotThrowAnyException();

        // 재시도 확인
        shopApiMockServer.verify(2, getRequestedFor(urlEqualTo("/api/shops/" + shopId)));
    }

    private void setupShopApiMocks() {
        // 가게 영업 상태 확인 Mock
        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + shopId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "shop": {
                                        "id": "%s",
                                        "name": "테스트 가게",
                                        "open": true,
                                        "minOrderAmount": 10000
                                    }
                                }
                                """.formatted(shopId))));

        // 메뉴 정보 조회 Mock
        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + shopId + "/menus/" + menuId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "menu": {
                                        "id": "%s",
                                        "name": "테스트 메뉴",
                                        "description": "맛있는 테스트 메뉴",
                                        "basePrice": 15000,
                                        "open": true
                                    }
                                }
                                """.formatted(menuId))));

        // 메뉴 옵션 조회 Mock
        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + shopId + "/menus/" + menuId + "/options"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "options": [
                                        {
                                            "name": "매운맛",
                                            "price": 0
                                        },
                                        {
                                            "name": "치즈 추가",
                                            "price": 2000
                                        },
                                        {
                                            "name": "곱빼기",
                                            "price": 1000
                                        }
                                    ]
                                }
                                """)));
    }

    private void setupUserApiMocks() {
        // 사용자 유효성 검증 Mock
        userApiMockServer.stubFor(get(urlEqualTo("/api/users/" + userId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "id": "%s",
                                    "name": "테스트 사용자",
                                    "email": "test@example.com"
                                }
                                """.formatted(userId))));
    }

    private void setupAnotherShopApiMocks(String anotherShopId, String anotherMenuId) {
        // 다른 가게 영업 상태 확인 Mock
        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + anotherShopId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "shop": {
                                        "id": "%s",
                                        "name": "다른 테스트 가게",
                                        "open": true,
                                        "minOrderAmount": 12000
                                    }
                                }
                                """.formatted(anotherShopId))));

        // 다른 가게 메뉴 정보 조회 Mock
        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + anotherShopId + "/menus/" + anotherMenuId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "menu": {
                                        "id": "%s",
                                        "name": "다른 가게 메뉴",
                                        "description": "다른 가게의 맛있는 메뉴",
                                        "basePrice": 18000,
                                        "open": true
                                    }
                                }
                                """.formatted(anotherMenuId))));

        // 다른 가게 메뉴 옵션 조회 Mock
        shopApiMockServer.stubFor(get(urlEqualTo("/api/shops/" + anotherShopId + "/menus/" + anotherMenuId + "/options"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "options": [
                                        {
                                            "name": "보통맛",
                                            "price": 0
                                        },
                                        {
                                            "name": "추가 토핑",
                                            "price": 3000
                                        }
                                    ]
                                }
                                """)));
    }

    private void verifyShopApiCalls() {
        // 가게 영업 상태 확인 호출 검증
        shopApiMockServer.verify(getRequestedFor(urlEqualTo("/api/shops/" + shopId)));
        
        // 메뉴 정보 조회 호출 검증
        shopApiMockServer.verify(getRequestedFor(urlEqualTo("/api/shops/" + shopId + "/menus/" + menuId)));
        
        // 메뉴 옵션 조회 호출 검증
        shopApiMockServer.verify(getRequestedFor(urlEqualTo("/api/shops/" + shopId + "/menus/" + menuId + "/options")));
    }

    private void verifyUserApiCalls() {
        // 사용자 유효성 검증 호출 검증
        userApiMockServer.verify(getRequestedFor(urlEqualTo("/api/users/" + userId)));
    }
}
