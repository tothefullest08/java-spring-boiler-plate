package harry.boilerplate.user.infrastructure.command;

import harry.boilerplate.user.domain.aggregate.User;
import harry.boilerplate.user.domain.aggregate.UserRepository;
import harry.boilerplate.user.domain.valueObject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * UserRepositoryImpl 통합 테스트
 * Testcontainers를 사용한 MySQL 통합 테스트
 * Requirements: 9.1 - Repository 패턴을 사용하여 쓰기 최적화를 수행한다
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserRepositoryImpl.class)
class UserRepositoryImplIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password")
            .withReuse(true); // 컨테이너 재사용으로 성능 최적화

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private UserId testUserId;

    @BeforeEach
    void setUp() {
        testUser = new User("홍길동", "hong@example.com");
        testUserId = testUser.getId();
    }

    @Test
    void save_새로운_사용자_저장_성공() {
        // When
        userRepository.save(testUser);
        entityManager.flush();
        entityManager.clear();

        // Then
        User savedUser = entityManager.find(User.class, testUserId.getValue());
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("홍길동");
        assertThat(savedUser.getEmail()).isEqualTo("hong@example.com");
    }

    @Test
    void save_기존_사용자_수정_성공() {
        // Given
        entityManager.persistAndFlush(testUser);
        entityManager.clear();

        // When
        User existingUser = userRepository.find(testUserId);
        existingUser.changeName("김철수");
        existingUser.changeEmail("kim@example.com");
        userRepository.save(existingUser);
        entityManager.flush();
        entityManager.clear();

        // Then
        User updatedUser = entityManager.find(User.class, testUserId.getValue());
        assertThat(updatedUser.getName()).isEqualTo("김철수");
        assertThat(updatedUser.getEmail()).isEqualTo("kim@example.com");
    }

    @Test
    void save_ID가_null인_새_사용자_저장_성공() {
        // Given
        User newUser = new User("이영희", "lee@example.com");

        // When
        userRepository.save(newUser);
        entityManager.flush();

        // Then
        assertThat(newUser.getId()).isNotNull();
        User savedUser = entityManager.find(User.class, newUser.getId().getValue());
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("이영희");
    }

    @Test
    void findById_존재하는_사용자_조회_성공() {
        // Given
        entityManager.persistAndFlush(testUser);
        entityManager.clear();

        // When
        Optional<User> foundUser = userRepository.findById(testUserId);

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(testUserId);
        assertThat(foundUser.get().getName()).isEqualTo("홍길동");
        assertThat(foundUser.get().getEmail()).isEqualTo("hong@example.com");
    }

    @Test
    void findById_존재하지_않는_사용자_빈_Optional_반환() {
        // Given
        UserId nonExistentId = UserId.generate();

        // When
        Optional<User> foundUser = userRepository.findById(nonExistentId);

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void findById_null_ID_빈_Optional_반환() {
        // When
        Optional<User> foundUser = userRepository.findById(null);

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void findByEmail_존재하는_이메일_조회_성공() {
        // Given
        entityManager.persistAndFlush(testUser);
        entityManager.clear();

        // When
        Optional<User> foundUser = userRepository.findByEmail("hong@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("hong@example.com");
        assertThat(foundUser.get().getName()).isEqualTo("홍길동");
    }

    @Test
    void findByEmail_존재하지_않는_이메일_빈_Optional_반환() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void findByEmail_null_이메일_빈_Optional_반환() {
        // When
        Optional<User> foundUser = userRepository.findByEmail(null);

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void findByEmail_빈_이메일_빈_Optional_반환() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void existsById_존재하는_사용자_true_반환() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        boolean exists = userRepository.existsById(testUserId);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_존재하지_않는_사용자_false_반환() {
        // Given
        UserId nonExistentId = UserId.generate();

        // When
        boolean exists = userRepository.existsById(nonExistentId);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void existsById_null_ID_false_반환() {
        // When
        boolean exists = userRepository.existsById(null);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmail_존재하는_이메일_true_반환() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        boolean exists = userRepository.existsByEmail("hong@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_존재하지_않는_이메일_false_반환() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmail_null_이메일_false_반환() {
        // When
        boolean exists = userRepository.existsByEmail(null);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmail_빈_이메일_false_반환() {
        // When
        boolean exists = userRepository.existsByEmail("");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void find_존재하는_사용자_조회_성공() {
        // Given
        entityManager.persistAndFlush(testUser);
        entityManager.clear();

        // When
        User foundUser = userRepository.find(testUserId);

        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(testUserId);
        assertThat(foundUser.getName()).isEqualTo("홍길동");
    }

    @Test
    void find_존재하지_않는_사용자_null_반환() {
        // Given
        UserId nonExistentId = UserId.generate();

        // When
        User foundUser = userRepository.find(nonExistentId);

        // Then
        assertThat(foundUser).isNull();
    }

    @Test
    void find_null_ID_null_반환() {
        // When
        User foundUser = userRepository.find(null);

        // Then
        assertThat(foundUser).isNull();
    }

    @Test
    void deleteById_존재하는_사용자_삭제_성공() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        userRepository.deleteById(testUserId);
        entityManager.flush();

        // Then
        User deletedUser = entityManager.find(User.class, testUserId.getValue());
        assertThat(deletedUser).isNull();
    }

    @Test
    void deleteById_존재하지_않는_사용자_예외_없음() {
        // Given
        UserId nonExistentId = UserId.generate();

        // When & Then - 예외가 발생하지 않아야 함
        assertThatCode(() -> userRepository.deleteById(nonExistentId))
                .doesNotThrowAnyException();
    }

    @Test
    void deleteById_null_ID_예외_없음() {
        // When & Then - 예외가 발생하지 않아야 함
        assertThatCode(() -> userRepository.deleteById(null))
                .doesNotThrowAnyException();
    }

    @Test
    void 이메일_중복_검증_테스트() {
        // Given
        User user1 = new User("사용자1", "duplicate@example.com");
        User user2 = new User("사용자2", "duplicate@example.com");
        
        entityManager.persistAndFlush(user1);

        // When & Then - 중복 이메일로 저장 시 예외 발생해야 함
        assertThatThrownBy(() -> {
            entityManager.persistAndFlush(user2);
            entityManager.flush();
        }).isInstanceOf(Exception.class); // 데이터베이스 제약 조건 위반
    }

    @Test
    void 여러_사용자_저장_및_조회_테스트() {
        // Given
        User user1 = new User("사용자1", "user1@example.com");
        User user2 = new User("사용자2", "user2@example.com");
        User user3 = new User("사용자3", "user3@example.com");

        // When
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        entityManager.flush();

        // Then
        assertThat(userRepository.existsByEmail("user1@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("user2@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("user3@example.com")).isTrue();
        
        Optional<User> foundUser1 = userRepository.findByEmail("user1@example.com");
        Optional<User> foundUser2 = userRepository.findByEmail("user2@example.com");
        Optional<User> foundUser3 = userRepository.findByEmail("user3@example.com");
        
        assertThat(foundUser1).isPresent();
        assertThat(foundUser2).isPresent();
        assertThat(foundUser3).isPresent();
        
        assertThat(foundUser1.get().getName()).isEqualTo("사용자1");
        assertThat(foundUser2.get().getName()).isEqualTo("사용자2");
        assertThat(foundUser3.get().getName()).isEqualTo("사용자3");
    }

    @Test
    void 트랜잭션_롤백_테스트() {
        // Given
        entityManager.persistAndFlush(testUser);
        Long initialCount = entityManager.getEntityManager()
                .createQuery("SELECT COUNT(u) FROM User u", Long.class)
                .getSingleResult();

        // When - 트랜잭션이 롤백되어야 함
        try {
            User newUser = new User("새 사용자", "new@example.com");
            userRepository.save(newUser);
            throw new RuntimeException("강제 예외 발생");
        } catch (RuntimeException e) {
            // 예외 발생으로 트랜잭션 롤백
        }

        entityManager.clear();

        // Then - 롤백으로 인해 개수가 변경되지 않아야 함
        Long finalCount = entityManager.getEntityManager()
                .createQuery("SELECT COUNT(u) FROM User u", Long.class)
                .getSingleResult();
        assertThat(finalCount).isEqualTo(initialCount);
    }
}