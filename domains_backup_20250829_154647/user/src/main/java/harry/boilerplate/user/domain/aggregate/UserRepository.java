package harry.boilerplate.user.domain.aggregate;

import java.util.Optional;

import harry.boilerplate.user.domain.valueObject.UserId;

/**
 * User Repository 인터페이스
 * User 애그리게이트의 영속성 계층 추상화
 */
public interface UserRepository {
    
    /**
     * 사용자 저장
     * 
     * @param user 저장할 사용자
     */
    void save(User user);
    
    /**
     * 사용자 ID로 조회
     * 
     * @param userId 사용자 ID
     * @return 사용자 (존재하지 않으면 Optional.empty())
     */
    Optional<User> findById(UserId userId);
    
    /**
     * 사용자 ID로 조회 (Command 용)
     * 
     * @param userId 사용자 ID
     * @return 사용자 (존재하지 않으면 null)
     */
    User find(UserId userId);
    
    /**
     * 이메일로 사용자 조회
     * 
     * @param email 이메일
     * @return 사용자 (존재하지 않으면 Optional.empty())
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 사용자 존재 여부 확인
     * 
     * @param userId 사용자 ID
     * @return 존재 여부
     */
    boolean existsById(UserId userId);
    
    /**
     * 이메일 중복 확인
     * 
     * @param email 이메일
     * @return 중복 여부
     */
    boolean existsByEmail(String email);
    
    /**
     * 사용자 삭제
     * 
     * @param userId 사용자 ID
     */
    void deleteById(UserId userId);
}