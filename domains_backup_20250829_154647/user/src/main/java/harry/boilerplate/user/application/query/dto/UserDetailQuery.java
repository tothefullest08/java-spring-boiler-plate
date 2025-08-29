package harry.boilerplate.user.application.query.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 사용자 상세 정보 조회 Query DTO
 * Requirements 7.3: 사용자 상세 정보 조회 기능
 */
public class UserDetailQuery {
    
    @NotBlank(message = "사용자 ID는 필수입니다")
    private final String userId;
    
    public UserDetailQuery(String userId) {
        this.userId = userId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    @Override
    public String toString() {
        return "UserDetailQuery{" +
                "userId='" + userId + '\'' +
                '}';
    }
}