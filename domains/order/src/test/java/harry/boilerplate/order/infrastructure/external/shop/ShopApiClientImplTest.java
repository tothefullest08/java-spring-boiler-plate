package harry.boilerplate.order.command.infrastructure.external.shop;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

/**
 * ShopApiClient WireMock 단위 테스트
 * Requirements: 8.1, 8.2, 8.3
 */
@DisplayName("ShopApiClient 단위 테스트")
class ShopApiClientImplTest {

    private WireMockServer wireMockServer;
    private ShopApiClientImpl shopApiClient;
    private final String shopId = "test-shop-123";
    private final String menuId = "test-menu-456";

    @BeforeEach
    void setUp() {
        // WireMock 서버 시작 (랜덤 포트)
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        
        // API Client 초기화
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + wireMockServer.port();
        shopApiClient = new ShopApiClientImpl(restTemplate, baseUrl);
    }

    @AfterEach
    void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    @DisplayName("가게 영업 상태 확인 - 영업 중")
    void isShopOpen_영업중인_가게_true_반환() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/api/shops/" + shopId))
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

        // When
        boolean result = shopApiClient.isShopOpen(shopId);

        // Then
        assertThat(result).isTrue();
        
        // WireMock 호출 검증
        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/shops/" + shopId)));
    }

    @Test
    @DisplayName("가게 영업 상태 확인 - 영업 중단")
    void isShopOpen_영업중단인_가게_false_반환() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/api/shops/" + shopId))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                        "shop": {
                            "id": "%s",
                            "name": "테스트 가게",
                            "open": false,
                            "minOrderAmount": 10000
                        }
                    }
                    """.formatted(shopId))));

        // When
        boolean result = shopApiClient.isShopOpen(shopId);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("가게 영업 상태 확인 - 응답 데이터 없음")
    void isShopOpen_응답데이터없음_false_반환() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/api/shops/" + shopId))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("{}")));

        // When
        boolean result = shopApiClient.isShopOpen(shopId);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("가게 영업 상태 확인 - API 호출 실패 재시도 후 예외")
    void isShopOpen_API호출실패_재시도후_예외발생() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/api/shops/" + shopId))
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("Internal Server Error")));

        // When & Then
        assertThatThrownBy(() -> shopApiClient.isShopOpen(shopId))
            .isInstanceOf(RestClientException.class);
        
        // 2회 재시도 확인
        wireMockServer.verify(2, getRequestedFor(urlEqualTo("/api/shops/" + shopId)));
    }

    @Test
    @DisplayName("메뉴 정보 조회 - 성공")
    void getMenu_정상조회_메뉴정보_반환() {
        // Given
        String expectedName = "테스트 메뉴";
        String expectedDescription = "맛있는 테스트 메뉴입니다";
        BigDecimal expectedPrice = new BigDecimal("15000.00");
        
        wireMockServer.stubFor(get(urlEqualTo("/api/shops/" + shopId + "/menus/" + menuId))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                        "menu": {
                            "id": "%s",
                            "name": "%s",
                            "description": "%s",
                            "basePrice": %s,
                            "open": true
                        }
                    }
                    """.formatted(menuId, expectedName, expectedDescription, expectedPrice))));

        // When
        ShopApiClient.MenuInfoResponse result = shopApiClient.getMenu(shopId, menuId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(menuId);
        assertThat(result.getName()).isEqualTo(expectedName);
        assertThat(result.getDescription()).isEqualTo(expectedDescription);
        assertThat(result.getBasePrice()).isEqualByComparingTo(expectedPrice);
        assertThat(result.isOpen()).isTrue();
    }

    @Test
    @DisplayName("메뉴 정보 조회 - 메뉴를 찾을 수 없음")
    void getMenu_메뉴없음_예외발생() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/api/shops/" + shopId + "/menus/" + menuId))
            .willReturn(aResponse()
                .withStatus(404)
                .withBody("Menu not found")));

        // When & Then
        assertThatThrownBy(() -> shopApiClient.getMenu(shopId, menuId))
            .isInstanceOf(RestClientException.class);
    }

    @Test
    @DisplayName("메뉴 옵션 조회 - 성공")
    void getMenuOptions_정상조회_옵션목록_반환() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/api/shops/" + shopId + "/menus/" + menuId + "/options"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                        "menu": {
                            "id": "%s",
                            "name": "테스트 메뉴",
                            "optionGroups": [
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
                            ]
                        }
                    }
                    """.formatted(menuId))));

        // When
        List<ShopApiClient.OptionInfoResponse> result = shopApiClient.getMenuOptions(shopId, menuId);

        // Then
        assertThat(result).hasSize(3);
        
        ShopApiClient.OptionInfoResponse option1 = result.get(0);
        assertThat(option1.getName()).isEqualTo("매운맛");
        assertThat(option1.getPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        
        ShopApiClient.OptionInfoResponse option2 = result.get(1);
        assertThat(option2.getName()).isEqualTo("치즈 추가");
        assertThat(option2.getPrice()).isEqualByComparingTo(new BigDecimal("2000"));
        
        ShopApiClient.OptionInfoResponse option3 = result.get(2);
        assertThat(option3.getName()).isEqualTo("곱빼기");
        assertThat(option3.getPrice()).isEqualByComparingTo(new BigDecimal("1000"));
    }

    @Test
    @DisplayName("메뉴 옵션 조회 - 옵션 없음")
    void getMenuOptions_옵션없음_빈목록_반환() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/api/shops/" + shopId + "/menus/" + menuId + "/options"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                        "menu": {
                            "id": "%s",
                            "name": "테스트 메뉴",
                            "optionGroups": []
                        }
                    }
                    """.formatted(menuId))));

        // When
        List<ShopApiClient.OptionInfoResponse> result = shopApiClient.getMenuOptions(shopId, menuId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("메뉴 옵션 조회 - API 호출 실패")
    void getMenuOptions_API호출실패_예외발생() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/api/shops/" + shopId + "/menus/" + menuId + "/options"))
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("Internal Server Error")));

        // When & Then
        assertThatThrownBy(() -> shopApiClient.getMenuOptions(shopId, menuId))
            .isInstanceOf(RestClientException.class);
    }

    @Test
    @DisplayName("가게 영업 상태 확인 - 첫 번째 호출 실패, 두 번째 호출 성공")
    void isShopOpen_첫번째실패_두번째성공_재시도로직확인() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/api/shops/" + shopId))
            .inScenario("retry-scenario")
            .whenScenarioStateIs("Started")
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("Internal Server Error"))
            .willSetStateTo("Failed"));

        wireMockServer.stubFor(get(urlEqualTo("/api/shops/" + shopId))
            .inScenario("retry-scenario")
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

        // When
        boolean result = shopApiClient.isShopOpen(shopId);

        // Then
        assertThat(result).isTrue();
        
        // 2회 호출 확인 (첫 번째 실패, 두 번째 성공)
        wireMockServer.verify(2, getRequestedFor(urlEqualTo("/api/shops/" + shopId)));
    }
}
