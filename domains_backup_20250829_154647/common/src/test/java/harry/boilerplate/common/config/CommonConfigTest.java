package harry.boilerplate.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CommonConfig 테스트
 */
class CommonConfigTest {
    
    private final CommonConfig commonConfig = new CommonConfig();
    
    @Test
    void PasswordEncoder_빈_생성_테스트() {
        // When
        PasswordEncoder passwordEncoder = commonConfig.passwordEncoder();
        
        // Then
        assertThat(passwordEncoder).isNotNull();
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
    }
    
    @Test
    void PasswordEncoder_암호화_테스트() {
        // Given
        PasswordEncoder passwordEncoder = commonConfig.passwordEncoder();
        String rawPassword = "testPassword123";
        
        // When
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        // Then
        assertThat(encodedPassword).isNotNull();
        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
        assertThat(passwordEncoder.matches("wrongPassword", encodedPassword)).isFalse();
    }
    
    @Test
    void ObjectMapper_빈_생성_테스트() {
        // When
        ObjectMapper objectMapper = commonConfig.objectMapper();
        
        // Then
        assertThat(objectMapper).isNotNull();
        assertThat(objectMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)).isFalse();
    }
    
    @Test
    void ObjectMapper_JavaTime_모듈_등록_테스트() throws Exception {
        // Given
        ObjectMapper objectMapper = commonConfig.objectMapper();
        LocalDateTime now = LocalDateTime.now();
        
        // When
        String json = objectMapper.writeValueAsString(now);
        LocalDateTime deserializedDateTime = objectMapper.readValue(json, LocalDateTime.class);
        
        // Then
        assertThat(json).contains("T"); // ISO 8601 형식 확인
        assertThat(deserializedDateTime).isEqualTo(now);
    }
    
    @Test
    void ObjectMapper_날짜_형식_테스트() throws Exception {
        // Given
        ObjectMapper objectMapper = commonConfig.objectMapper();
        TestDateObject testObject = new TestDateObject(LocalDateTime.of(2024, 1, 15, 10, 30, 45));
        
        // When
        String json = objectMapper.writeValueAsString(testObject);
        TestDateObject deserializedObject = objectMapper.readValue(json, TestDateObject.class);
        
        // Then
        assertThat(json).contains("2024-01-15T10:30:45");
        assertThat(deserializedObject.getDateTime()).isEqualTo(testObject.getDateTime());
    }
    
    /**
     * 테스트용 날짜 객체
     */
    private static class TestDateObject {
        private LocalDateTime dateTime;
        
        public TestDateObject() {}
        
        public TestDateObject(LocalDateTime dateTime) {
            this.dateTime = dateTime;
        }
        
        public LocalDateTime getDateTime() {
            return dateTime;
        }
        
        public void setDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
        }
    }
}