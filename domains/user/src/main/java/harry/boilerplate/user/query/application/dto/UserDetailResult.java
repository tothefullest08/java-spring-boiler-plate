package harry.boilerplate.user.query.application.dto;

import harry.boilerplate.user.query.application.readmodel.UserDetailReadModel;

/**
 * 사용자 상세 정보 조회 결과 DTO
 * Requirements 7.3: 사용자 상세 정보 조회 결과
 */
public class UserDetailResult {
    
    private final String userId;
    private final String name;
    private final String email;
    private final boolean isValid;
    private final String createdAt;
    private final String updatedAt;
    
    public UserDetailResult(String userId, String name, String email, boolean isValid, 
                           String createdAt, String updatedAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.isValid = isValid;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public static UserDetailResult from(UserDetailReadModel readModel) {
        return new UserDetailResult(
            readModel.getUserId(),
            readModel.getName(),
            readModel.getEmail(),
            readModel.isValid(),
            readModel.getCreatedAt(),
            readModel.getUpdatedAt()
        );
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public boolean isValid() {
        return isValid;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    @Override
    public String toString() {
        return "UserDetailResult{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", isValid=" + isValid +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}