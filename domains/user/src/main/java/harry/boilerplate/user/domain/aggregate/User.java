package harry.boilerplate.user.domain.aggregate;

import harry.boilerplate.common.domain.entity.AggregateRoot;
import harry.boilerplate.user.domain.event.UserRegisteredEvent;
import harry.boilerplate.user.domain.exception.UserDomainException;
import harry.boilerplate.user.domain.exception.UserErrorCode;
import harry.boilerplate.user.domain.valueObject.UserId;
import jakarta.persistence.*;
import java.util.regex.Pattern;

/**
 * User 애그리게이트 루트
 * 사용자 정보와 유효성 검증을 담당하는 도메인 모델
 */
@Entity
@Table(name = "user")
public class User extends AggregateRoot<User, UserId> {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    
    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "email", unique = true, length = 255)
    private String email;
    
    // JPA 기본 생성자
    protected User() {
        this.id = UserId.generate().getValue();
    }
    
    // 도메인 생성자
    public User(String name, String email) {
        this();
        validateName(name);
        validateEmail(email);
        
        this.name = name;
        this.email = email;
    }
    
    // 팩토리 메서드
    public static User create(String name, String email) {
        User user = new User(name, email);
        
        // 사용자 등록 도메인 이벤트 발행
        user.addDomainEvent(new UserRegisteredEvent(
            user.getId().getValue(),
            user.getName(),
            user.getEmail()
        ));
        
        return user;
    }
    
    @Override
    public UserId getId() {
        return UserId.of(this.id);
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    /**
     * 사용자 유효성 검증
     * Requirements 7.2: 사용자 유효성 검증 로직 구현
     * 
     * @return 유효한 사용자인지 여부
     */
    public boolean isValid() {
        return this.name != null && !this.name.trim().isEmpty() 
            && this.email != null && isValidEmail(this.email);
    }
    
    /**
     * 사용자 이름 변경
     * 
     * @param newName 새로운 사용자 이름
     */
    public void changeName(String newName) {
        validateName(newName);
        this.name = newName;
    }
    
    /**
     * 이메일 변경
     * 
     * @param newEmail 새로운 이메일
     */
    public void changeEmail(String newEmail) {
        validateEmail(newEmail);
        this.email = newEmail;
    }
    
    // 비즈니스 규칙 검증 메서드들
    
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new UserDomainException(UserErrorCode.INVALID_USER_NAME, "사용자 이름은 필수입니다");
        }
        
        if (name.length() > 100) {
            throw new UserDomainException(UserErrorCode.INVALID_USER_NAME, "사용자 이름은 100자 이하여야 합니다");
        }
    }
    
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new UserDomainException(UserErrorCode.INVALID_EMAIL_FORMAT, "이메일은 필수입니다");
        }
        
        if (!isValidEmail(email)) {
            throw new UserDomainException(UserErrorCode.INVALID_EMAIL_FORMAT, "올바른 이메일 형식이 아닙니다");
        }
    }
    
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        User user = (User) obj;
        return getId() != null && getId().equals(user.getId());
    }
    
    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}