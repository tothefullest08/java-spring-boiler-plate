package harry.boilerplate.user.query.application.readModel;

/**
 * 사용자 상세 정보 Read Model
 * 사용자 상세 조회 시 사용되는 불변 데이터 객체
 * Requirements 7.4: UserQueryDao 구현
 */
public class UserDetailReadModel {
    
    private final String userId;
    private final String name;
    private final String email;
    private final boolean isValid;
    private final String createdAt;
    private final String updatedAt;
    
    public UserDetailReadModel(String userId, String name, String email, boolean isValid, 
                              String createdAt, String updatedAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.isValid = isValid;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
        return "UserDetailReadModel{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", isValid=" + isValid +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}