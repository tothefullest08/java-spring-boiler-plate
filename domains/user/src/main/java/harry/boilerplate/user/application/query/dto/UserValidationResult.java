package harry.boilerplate.user.application.query.dto;

/**
 * 사용자 유효성 검증 결과 DTO
 * Requirements 7.2: 사용자 유효성 검증 결과
 */
public class UserValidationResult {
    
    private final String userId;
    private final boolean isValid;
    private final String reason;
    
    public UserValidationResult(String userId, boolean isValid, String reason) {
        this.userId = userId;
        this.isValid = isValid;
        this.reason = reason;
    }
    
    public static UserValidationResult valid(String userId) {
        return new UserValidationResult(userId, true, "유효한 사용자입니다");
    }
    
    public static UserValidationResult invalid(String userId, String reason) {
        return new UserValidationResult(userId, false, reason);
    }
    
    public String getUserId() {
        return userId;
    }
    
    public boolean isValid() {
        return isValid;
    }
    
    public String getReason() {
        return reason;
    }
    
    @Override
    public String toString() {
        return "UserValidationResult{" +
                "userId='" + userId + '\'' +
                ", isValid=" + isValid +
                ", reason='" + reason + '\'' +
                '}';
    }
}