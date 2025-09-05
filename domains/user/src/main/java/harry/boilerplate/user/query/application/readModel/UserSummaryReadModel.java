package harry.boilerplate.user.query.application.readmodel;

/**
 * 사용자 요약 정보 Read Model
 * 사용자 목록 조회 시 사용되는 불변 데이터 객체
 * Requirements 7.4: UserQueryDao 구현
 */
public class UserSummaryReadModel {
    
    private final String userId;
    private final String name;
    private final String email;
    private final String createdAt;
    
    public UserSummaryReadModel(String userId, String name, String email, String createdAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
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
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    @Override
    public String toString() {
        return "UserSummaryReadModel{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}