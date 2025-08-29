package harry.boilerplate.order.presentation.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import harry.boilerplate.common.response.CommandResultResponse;
import harry.boilerplate.order.presentation.command.dto.AddCartItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import harry.boilerplate.order.infrastructure.external.shop.ShopApiClient;
import harry.boilerplate.order.infrastructure.external.user.UserApiClient;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CartCommandController API 통합 테스트
 * Requirements: 10.5 - REST API 통합 테스트
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@DisplayName("Cart Command Controller 통합 테스트")
class CartCommandControllerIntegrationTest {

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

    @MockBean
    private ShopApiClient shopApiClient;

    @MockBean
    private UserApiClient userApiClient;

    private AddCartItemRequest addCartItemRequest;
    private String testUserId;

    @BeforeEach
    void setUp() {
        testUserId = "test-user-123";
        
        List<String> selectedOptionIds = Arrays.asList("option-1", "option-2");
        addCartItemRequest = new AddCartItemRequest(
            "test-shop-456",
            "test-menu-789",
            selectedOptionIds,
            2
        );

        // 외부 API 호출 Mock 설정
        when(userApiClient.isValidUser(anyString())).thenReturn(true);
        when(shopApiClient.isShopOpen(anyString())).thenReturn(true);
    }

    @Test
    @DisplayName("장바구니 아이템 추가 API 성공")
    void 장바구니_아이템_추가_API_성공() throws Exception {
        // When & Then
        MvcResult result = mockMvc.perform(post("/api/v1/carts/items")
                .header("X-User-Id", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCartItemRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("장바구니에 아이템이 추가되었습니다"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andReturn();

        // 응답 검증
        String responseBody = result.getResponse().getContentAsString();
        CommandResultResponse response = objectMapper.readValue(responseBody, CommandResultResponse.class);
        
        assertThat(response.getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getMessage()).isEqualTo("장바구니에 아이템이 추가되었습니다");
    }

    @Test
    @DisplayName("장바구니 아이템 추가 API 실패 - 사용자 ID 헤더 누락")
    void 장바구니_아이템_추가_API_실패_사용자ID_헤더_누락() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/carts/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCartItemRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("장바구니 아이템 추가 API 실패 - 유효하지 않은 사용자")
    void 장바구니_아이템_추가_API_실패_유효하지_않은_사용자() throws Exception {
        // Given - 유효하지 않은 사용자
        when(userApiClient.isValidUser(anyString())).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/v1/carts/items")
                .header("X-User-Id", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCartItemRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("장바구니 아이템 추가 API 실패 - 가게 영업 중단")
    void 장바구니_아이템_추가_API_실패_가게_영업_중단() throws Exception {
        // Given - 가게가 영업 중이 아님
        when(shopApiClient.isShopOpen(anyString())).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/v1/carts/items")
                .header("X-User-Id", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCartItemRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("장바구니 아이템 추가 API 실패 - 유효하지 않은 요청 데이터")
    void 장바구니_아이템_추가_API_실패_유효하지_않은_요청_데이터() throws Exception {
        // Given - 잘못된 요청 데이터 (수량이 0)
        AddCartItemRequest invalidRequest = new AddCartItemRequest(
            "test-shop-456",
            "test-menu-789",
            Arrays.asList("option-1"),
            0 // 유효하지 않은 수량
        );

        // When & Then
        mockMvc.perform(post("/api/v1/carts/items")
                .header("X-User-Id", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("장바구니 아이템 추가 API 실패 - 빈 가게 ID")
    void 장바구니_아이템_추가_API_실패_빈_가게_ID() throws Exception {
        // Given - 빈 가게 ID
        AddCartItemRequest invalidRequest = new AddCartItemRequest(
            "", // 빈 가게 ID
            "test-menu-789",
            Arrays.asList("option-1"),
            1
        );

        // When & Then
        mockMvc.perform(post("/api/v1/carts/items")
                .header("X-User-Id", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("장바구니 아이템 추가 API 실패 - 빈 메뉴 ID")
    void 장바구니_아이템_추가_API_실패_빈_메뉴_ID() throws Exception {
        // Given - 빈 메뉴 ID
        AddCartItemRequest invalidRequest = new AddCartItemRequest(
            "test-shop-456",
            "", // 빈 메뉴 ID
            Arrays.asList("option-1"),
            1
        );

        // When & Then
        mockMvc.perform(post("/api/v1/carts/items")
                .header("X-User-Id", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("장바구니 아이템 수량 변경 API 성공")
    void 장바구니_아이템_수량_변경_API_성공() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/carts/items/{menuId}", "test-menu-789")
                .header("X-User-Id", testUserId)
                .param("quantity", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("장바구니 아이템 수량이 변경되었습니다"));
    }

    @Test
    @DisplayName("장바구니 아이템 삭제 API 성공")
    void 장바구니_아이템_삭제_API_성공() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/carts/items/{menuId}", "test-menu-789")
                .header("X-User-Id", testUserId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("장바구니에서 아이템이 삭제되었습니다"));
    }

    @Test
    @DisplayName("장바구니 전체 비우기 API 성공")
    void 장바구니_전체_비우기_API_성공() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/carts")
                .header("X-User-Id", testUserId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("장바구니가 비워졌습니다"));
    }

    @Test
    @DisplayName("잘못된 Content-Type으로 요청 시 실패")
    void 잘못된_ContentType으로_요청_시_실패() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/carts/items")
                .header("X-User-Id", testUserId)
                .contentType(MediaType.TEXT_PLAIN)
                .content("invalid content"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("빈 요청 본문으로 요청 시 실패")
    void 빈_요청_본문으로_요청_시_실패() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/carts/items")
                .header("X-User-Id", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 JSON 형식으로 요청 시 실패")
    void 잘못된_JSON_형식으로_요청_시_실패() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/carts/items")
                .header("X-User-Id", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }
}
