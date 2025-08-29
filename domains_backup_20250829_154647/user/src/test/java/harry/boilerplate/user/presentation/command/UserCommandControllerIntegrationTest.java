package harry.boilerplate.user.presentation.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import harry.boilerplate.common.response.CommandResultResponse;
import harry.boilerplate.user.presentation.command.dto.CreateUserRequest;
import harry.boilerplate.user.presentation.command.dto.UpdateUserRequest;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserCommandController API 통합 테스트
 * Requirements: 10.5 - REST API 통합 테스트
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@DisplayName("User Command Controller 통합 테스트")
class UserCommandControllerIntegrationTest {

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

    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach
    void setUp() {
        createUserRequest = new CreateUserRequest(
            "홍길동",
            "hong@example.com"
        );

        updateUserRequest = new UpdateUserRequest(
            "김길동",
            "kim@example.com"
        );
    }

    @Test
    @DisplayName("사용자 생성 API 성공")
    void 사용자_생성_API_성공() throws Exception {
        // When & Then
        MvcResult result = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("사용자가 성공적으로 생성되었습니다"))
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
    @DisplayName("사용자 생성 API 실패 - 유효하지 않은 데이터")
    void 사용자_생성_API_실패_유효하지_않은_데이터() throws Exception {
        // Given - 잘못된 요청 데이터 (이름이 빈 문자열)
        CreateUserRequest invalidRequest = new CreateUserRequest(
            "", // 빈 이름
            "hong@example.com"
        );

        // When & Then
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("사용자 생성 API 실패 - 유효하지 않은 이메일")
    void 사용자_생성_API_실패_유효하지_않은_이메일() throws Exception {
        // Given - 잘못된 이메일 형식
        CreateUserRequest invalidRequest = new CreateUserRequest(
            "홍길동",
            "invalid-email" // 유효하지 않은 이메일 형식
        );

        // When & Then
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("사용자 생성 API 실패 - 중복 이메일")
    void 사용자_생성_API_실패_중복_이메일() throws Exception {
        // Given - 먼저 사용자 생성
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated());

        // When & Then - 같은 이메일로 다시 생성 시도
        CreateUserRequest duplicateRequest = new CreateUserRequest(
            "다른이름",
            "hong@example.com" // 중복 이메일
        );

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("사용자 정보 수정 API 성공")
    void 사용자_정보_수정_API_성공() throws Exception {
        // Given - 먼저 사용자를 생성
        MvcResult createResult = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponseBody = createResult.getResponse().getContentAsString();
        CommandResultResponse createResponse = objectMapper.readValue(createResponseBody, CommandResultResponse.class);
        String userId = createResponse.getResourceId();

        // When & Then - 사용자 정보 수정
        mockMvc.perform(put("/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("사용자 정보가 성공적으로 수정되었습니다"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @DisplayName("사용자 정보 수정 API 실패 - 존재하지 않는 사용자")
    void 사용자_정보_수정_API_실패_존재하지_않는_사용자() throws Exception {
        // Given - 존재하지 않는 사용자 ID
        String nonExistentUserId = "non-existent-user-id";

        // When & Then
        mockMvc.perform(put("/users/{userId}", nonExistentUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자 정보 수정 API 실패 - 유효하지 않은 데이터")
    void 사용자_정보_수정_API_실패_유효하지_않은_데이터() throws Exception {
        // Given - 먼저 사용자를 생성
        MvcResult createResult = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponseBody = createResult.getResponse().getContentAsString();
        CommandResultResponse createResponse = objectMapper.readValue(createResponseBody, CommandResultResponse.class);
        String userId = createResponse.getResourceId();

        // Given - 잘못된 수정 요청 데이터
        UpdateUserRequest invalidRequest = new UpdateUserRequest(
            "", // 빈 이름
            "kim@example.com"
        );

        // When & Then
        mockMvc.perform(put("/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 Content-Type으로 요청 시 실패")
    void 잘못된_ContentType으로_요청_시_실패() throws Exception {
        // When & Then
        mockMvc.perform(post("/users")
                .contentType(MediaType.TEXT_PLAIN)
                .content("invalid content"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("빈 요청 본문으로 요청 시 실패")
    void 빈_요청_본문으로_요청_시_실패() throws Exception {
        // When & Then
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 JSON 형식으로 요청 시 실패")
    void 잘못된_JSON_형식으로_요청_시_실패() throws Exception {
        // When & Then
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }
}
