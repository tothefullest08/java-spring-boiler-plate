package harry.boilerplate.user.query.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 사용자 유효성 검증 Query DTO
 * Requirements 7.2: 사용자 유효성 검증 기능
 */
public class UserValidationQuery {
    
    @NotBlank(message = "사용자 ID는 필수입니다")
    private final String userId;
    
    public UserValidationQuery(String userId) {
        this.userId = userId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    @Override
    public String toString() {
        return "UserValidationQuery{" +
                "userId='" + userId + '\'' +
                '}';
    }
}