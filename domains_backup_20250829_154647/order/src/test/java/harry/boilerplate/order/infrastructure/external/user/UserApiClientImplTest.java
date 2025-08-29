package harry.boilerplate.order.infrastructure.external.user;

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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

/**
 * UserApiClient WireMock 단위 테스트
 * Requirements: 8.4
 */
@DisplayName("UserApiClient 단위 테스트")
class UserApiClientImplTest {

    private WireMockServer wireMockServer;
    private UserApiClientImpl userApiClient;
    private final String userId = "test-user-123";

    @BeforeEach
    void setUp() {
        // WireMock 서버 시작 (랜덤 포트)
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        
        // API Client 초기화
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + wireMockServer.port();
        userApiClient = new UserApiClientImpl(restTemplate, baseUrl);
    }

    @AfterEach
    void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    @DisplayName("사용자 유효성 검증 - 유효한 사용자")
    void isValidUser_유효한사용자_true_반환() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/api/users/" + userId))
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

        // When
        boolean result = userApiClient.isValidUser(userId);

        // Then
        assertThat(result).isTrue();
        
        // WireMock 호출 검증
        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/users/" + userId)));
    }

    @Test
    @DisplayName("사용자 유효성 검증 - 사용자 없음 (404)")
    void isValidUser_사용자없음_false_반환() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/api/users/" + userId))
            .willReturn(aResponse()
                .withStatus(404)
                .withBody("User not found")));

        // When & Then
        assertThatThrownBy(() -> userApiClient.isValidUser(userId))
            .isInstanceOf(RestClientException.class);
    }

    @Test
    @DisplayName("사용자 유효성 검증 - 응답 데이터 없음")
    void isValidUser_응답데이터없음_false_반환() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/api/users/" + userId))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("{}")));

        // When
        boolean result = userApiClient.isValidUser(userId);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("사용자 유효성 검증 - ID 없는 응답")
    void isValidUser_ID없는응답_false_반환() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/api/users/" + userId))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                        "name": "테스트 사용자",
                        "email": "test@example.com"
                    }
                    """)));

        // When
        boolean result = userApiClient.isValidUser(userId);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("사용자 유효성 검증 - null ID 응답")
    void isValidUser_null_ID응답_false_반환() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/api/users/" + userId))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                        "id": null,
                        "name": "테스트 사용자",
                        "email": "test@example.com"
                    }
                    """)));

        // When
        boolean result = userApiClient.isValidUser(userId);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("사용자 유효성 검증 - API 호출 실패 재시도 후 예외")
    void isValidUser_API호출실패_재시도후_예외발생() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/api/users/" + userId))
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("Internal Server Error")));

        // When & Then
        assertThatThrownBy(() -> userApiClient.isValidUser(userId))
            .isInstanceOf(RestClientException.class);
        
        // 2회 재시도 확인
        wireMockServer.verify(2, getRequestedFor(urlEqualTo("/api/users/" + userId)));
    }

    @Test
    @DisplayName("사용자 유효성 검증 - 첫 번째 호출 실패, 두 번째 호출 성공")
    void isValidUser_첫번째실패_두번째성공_재시도로직확인() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/api/users/" + userId))
            .inScenario("retry-scenario")
            .whenScenarioStateIs("Started")
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("Internal Server Error"))
            .willSetStateTo("Failed"));

        wireMockServer.stubFor(get(urlEqualTo("/api/users/" + userId))
            .inScenario("retry-scenario")
            .whenScenarioStateIs("Failed")
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

        // When
        boolean result = userApiClient.isValidUser(userId);

        // Then
        assertThat(result).isTrue();
        
        // 2회 호출 확인 (첫 번째 실패, 두 번째 성공)
        wireMockServer.verify(2, getRequestedFor(urlEqualTo("/api/users/" + userId)));
    }

    @Test
    @DisplayName("사용자 유효성 검증 - 잘못된 JSON 응답")
    void isValidUser_잘못된JSON응답_예외발생() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/api/users/" + userId))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("invalid json {")));

        // When & Then
        assertThatThrownBy(() -> userApiClient.isValidUser(userId))
            .isInstanceOf(RestClientException.class);
    }

    @Test
    @DisplayName("사용자 유효성 검증 - 타임아웃 시뮬레이션")
    void isValidUser_타임아웃_예외발생() {
        // Given
        wireMockServer.stubFor(get(urlEqualTo("/api/users/" + userId))
            .willReturn(aResponse()
                .withStatus(200)
                .withFixedDelay(10000) // 10초 지연
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                        "id": "%s",
                        "name": "테스트 사용자",
                        "email": "test@example.com"
                    }
                    """.formatted(userId))));

        // When & Then
        // RestTemplate의 기본 타임아웃으로 인해 예외 발생
        assertThatThrownBy(() -> userApiClient.isValidUser(userId))
            .isInstanceOf(RestClientException.class);
    }
}



