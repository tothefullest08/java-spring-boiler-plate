package harry.boilerplate.shop.presentation.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import harry.boilerplate.common.response.CommandResultResponse;
import harry.boilerplate.shop.command.presentation.dto.CreateShopRequest;
import harry.boilerplate.shop.command.presentation.dto.UpdateShopRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ShopCommandController API 통합 테스트
 * Requirements: 10.5 - REST API 통합 테스트
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@DisplayName("Shop Command Controller 통합 테스트")
class ShopCommandControllerIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateShopRequest createShopRequest;
    private UpdateShopRequest updateShopRequest;

    @BeforeEach
    void setUp() {
        // 가게 생성 요청 데이터
        createShopRequest = new CreateShopRequest(
            "맛있는 가게",
            new BigDecimal("15000")
        );

        // 가게 수정 요청 데이터
        updateShopRequest = new UpdateShopRequest(
            "수정된 가게명",
            new BigDecimal("20000")
        );
    }

    @Test
    @DisplayName("가게 생성 API 성공")
    void 가게_생성_API_성공() throws Exception {
        // When & Then
        MvcResult result = mockMvc.perform(post("/api/shops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createShopRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("가게가 성공적으로 생성되었습니다"))
                .andExpect(jsonPath("$.resourceId").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andReturn();

        // 응답 검증
        String responseBody = result.getResponse().getContentAsString();
        CommandResultResponse response = objectMapper.readValue(responseBody, CommandResultResponse.class);
        
        assertThat(response.getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getResourceId()).isNotNull();
        assertThat(response.getResourceId()).isNotEmpty();
    }

    @Test
    @DisplayName("가게 생성 API 실패 - 유효하지 않은 데이터")
    void 가게_생성_API_실패_유효하지_않은_데이터() throws Exception {
        // Given - 잘못된 요청 데이터 (이름이 빈 문자열)
        CreateShopRequest invalidRequest = new CreateShopRequest(
            "", // 빈 가게 이름
            new BigDecimal("15000")
        );

        // When & Then
        mockMvc.perform(post("/api/shops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("가게 생성 API 실패 - 최소 주문 금액이 음수")
    void 가게_생성_API_실패_최소_주문_금액이_음수() throws Exception {
        // Given - 음수 최소 주문 금액
        CreateShopRequest invalidRequest = new CreateShopRequest(
            "테스트 가게",
            new BigDecimal("-1000") // 음수 금액
        );

        // When & Then
        mockMvc.perform(post("/api/shops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("가게 수정 API 성공")
    void 가게_수정_API_성공() throws Exception {
        // Given - 먼저 가게를 생성
        MvcResult createResult = mockMvc.perform(post("/api/shops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createShopRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponseBody = createResult.getResponse().getContentAsString();
        CommandResultResponse createResponse = objectMapper.readValue(createResponseBody, CommandResultResponse.class);
        String shopId = createResponse.getResourceId();

        // When & Then - 가게 정보 수정
        mockMvc.perform(put("/api/shops/{shopId}", shopId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateShopRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("가게 정보가 성공적으로 수정되었습니다"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @DisplayName("가게 수정 API 실패 - 존재하지 않는 가게")
    void 가게_수정_API_실패_존재하지_않는_가게() throws Exception {
        // Given - 존재하지 않는 가게 ID
        String nonExistentShopId = "non-existent-shop-id";

        // When & Then
        mockMvc.perform(put("/api/shops/{shopId}", nonExistentShopId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateShopRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("가게 수정 API 실패 - 유효하지 않은 데이터")
    void 가게_수정_API_실패_유효하지_않은_데이터() throws Exception {
        // Given - 먼저 가게를 생성
        MvcResult createResult = mockMvc.perform(post("/api/shops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createShopRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponseBody = createResult.getResponse().getContentAsString();
        CommandResultResponse createResponse = objectMapper.readValue(createResponseBody, CommandResultResponse.class);
        String shopId = createResponse.getResourceId();

        // Given - 잘못된 수정 요청 데이터
        UpdateShopRequest invalidRequest = new UpdateShopRequest(
            "", // 빈 가게 이름
            new BigDecimal("20000")
        );

        // When & Then
        mockMvc.perform(put("/api/shops/{shopId}", shopId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 Content-Type으로 요청 시 실패")
    void 잘못된_ContentType으로_요청_시_실패() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/shops")
                .contentType(MediaType.TEXT_PLAIN)
                .content("invalid content"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("빈 요청 본문으로 요청 시 실패")
    void 빈_요청_본문으로_요청_시_실패() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/shops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 JSON 형식으로 요청 시 실패")
    void 잘못된_JSON_형식으로_요청_시_실패() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/shops")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }
}
