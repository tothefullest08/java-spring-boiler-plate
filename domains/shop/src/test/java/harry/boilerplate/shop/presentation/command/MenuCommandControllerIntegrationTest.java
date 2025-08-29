package harry.boilerplate.shop.presentation.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import harry.boilerplate.common.response.CommandResultResponse;
import harry.boilerplate.shop.command.presentation.dto.CreateMenuRequest;
import harry.boilerplate.shop.command.presentation.dto.CreateShopRequest;

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
 * MenuCommandController API 통합 테스트
 * Requirements: 10.5 - REST API 통합 테스트
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@DisplayName("Menu Command Controller 통합 테스트")
class MenuCommandControllerIntegrationTest {

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

    private CreateMenuRequest createMenuRequest;
    private String testShopId;

    @BeforeEach
    void setUp() throws Exception {
        // 테스트를 위한 가게 먼저 생성
        CreateShopRequest createShopRequest = new CreateShopRequest(
            "테스트 가게",
            new BigDecimal("15000")
        );

        MvcResult createShopResult = mockMvc.perform(post("/api/shops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createShopRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String createShopResponseBody = createShopResult.getResponse().getContentAsString();
        CommandResultResponse createShopResponse = objectMapper.readValue(createShopResponseBody, CommandResultResponse.class);
        testShopId = createShopResponse.getResourceId();

        // 메뉴 생성 요청 데이터
        createMenuRequest = new CreateMenuRequest(
            "삼겹살",
            "맛있는 삼겹살",
            new BigDecimal("15000")
        );


    }

    @Test
    @DisplayName("메뉴 생성 API 성공")
    void 메뉴_생성_API_성공() throws Exception {
        // When & Then
        MvcResult result = mockMvc.perform(post("/api/shops/{shopId}/menus", testShopId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMenuRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("메뉴가 성공적으로 생성되었습니다"))
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
    @DisplayName("메뉴 생성 API 실패 - 존재하지 않는 가게")
    void 메뉴_생성_API_실패_존재하지_않는_가게() throws Exception {
        // Given - 존재하지 않는 가게 ID
        String nonExistentShopId = "non-existent-shop-id";

        // When & Then
        mockMvc.perform(post("/api/shops/{shopId}/menus", nonExistentShopId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMenuRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("메뉴 생성 API 실패 - 유효하지 않은 데이터")
    void 메뉴_생성_API_실패_유효하지_않은_데이터() throws Exception {
        // Given - 잘못된 요청 데이터 (이름이 빈 문자열)
        CreateMenuRequest invalidRequest = new CreateMenuRequest(
            "", // 빈 메뉴 이름
            "맛있는 삼겹살",
            new BigDecimal("15000")
        );

        // When & Then
        mockMvc.perform(post("/api/shops/{shopId}/menus", testShopId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메뉴 생성 API 실패 - 음수 가격")
    void 메뉴_생성_API_실패_음수_가격() throws Exception {
        // Given - 음수 가격
        CreateMenuRequest invalidRequest = new CreateMenuRequest(
            "삼겹살",
            "맛있는 삼겹살",
            new BigDecimal("-1000") // 음수 가격
        );

        // When & Then
        mockMvc.perform(post("/api/shops/{shopId}/menus", testShopId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메뉴 공개 API 성공")
    void 메뉴_공개_API_성공() throws Exception {
        // Given - 먼저 메뉴를 생성
        MvcResult createResult = mockMvc.perform(post("/api/shops/{shopId}/menus", testShopId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMenuRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponseBody = createResult.getResponse().getContentAsString();
        CommandResultResponse createResponse = objectMapper.readValue(createResponseBody, CommandResultResponse.class);
        String menuId = createResponse.getResourceId();

        // When & Then - 메뉴 공개
        mockMvc.perform(put("/api/shops/{shopId}/menus/{menuId}/open", testShopId, menuId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("메뉴가 성공적으로 공개되었습니다"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @DisplayName("메뉴 공개 API 실패 - 존재하지 않는 메뉴")
    void 메뉴_공개_API_실패_존재하지_않는_메뉴() throws Exception {
        // Given - 존재하지 않는 메뉴 ID
        String nonExistentMenuId = "non-existent-menu-id";

        // When & Then
        mockMvc.perform(put("/api/shops/{shopId}/menus/{menuId}/open", testShopId, nonExistentMenuId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("메뉴 공개 API 실패 - 존재하지 않는 가게")
    void 메뉴_공개_API_실패_존재하지_않는_가게() throws Exception {
        // Given - 존재하지 않는 가게 ID
        String nonExistentShopId = "non-existent-shop-id";
        String menuId = "any-menu-id";

        // When & Then
        mockMvc.perform(put("/api/shops/{shopId}/menus/{menuId}/open", nonExistentShopId, menuId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("잘못된 Content-Type으로 요청 시 실패")
    void 잘못된_ContentType으로_요청_시_실패() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/shops/{shopId}/menus", testShopId)
                .contentType(MediaType.TEXT_PLAIN)
                .content("invalid content"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("빈 요청 본문으로 요청 시 실패")
    void 빈_요청_본문으로_요청_시_실패() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/shops/{shopId}/menus", testShopId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 JSON 형식으로 요청 시 실패")
    void 잘못된_JSON_형식으로_요청_시_실패() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/shops/{shopId}/menus", testShopId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }
}
