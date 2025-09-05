package harry.boilerplate.order.command.infrastructure.repository;

import harry.boilerplate.order.command.domain.aggregate.Cart;
import harry.boilerplate.order.command.domain.aggregate.CartRepository;
import harry.boilerplate.order.command.domain.entity.CartLineItem;
import harry.boilerplate.order.command.domain.valueObject.*;
import harry.boilerplate.common.domain.entity.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * CartRepositoryImpl 통합 테스트
 * Testcontainers를 사용한 MySQL 통합 테스트
 * Requirements: 9.1 - Repository 패턴을 사용하여 쓰기 최적화를 수행한다
 */
@Disabled("Requires Docker/Testcontainers environment")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(harry.boilerplate.order.command.infrastructure.repository.CartRepositoryImpl.class)
class CartRepositoryImplIntegrationTest {

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
    private CartRepository cartRepository;

    private Cart testCart;
    private CartId testCartId;
    private UserId testUserId;
    private ShopId testShopId;

    @BeforeEach
    void setUp() {
        testCartId = CartId.generate();
        testUserId = UserId.generate();
        testShopId = ShopId.generate();
        testCart = new Cart(testUserId);
        
        // 장바구니에 아이템 추가
        MenuId menuId = MenuId.generate();
        List<OptionId> options = List.of(OptionId.generate());
        testCart.start(testShopId);
        testCart.addItem(menuId, options, 2);
    }

    @Test
    void save_새로운_장바구니_저장_성공() {
        // When
        cartRepository.save(testCart);
        entityManager.flush();
        entityManager.clear();

        // Then
        Cart savedCart = entityManager.find(Cart.class, testCartId.getValue());
        assertThat(savedCart).isNotNull();
        assertThat(savedCart.getUserId()).isEqualTo(testUserId);
        assertThat(savedCart.getShopId()).isEqualTo(testShopId);
        assertThat(savedCart.getItems()).hasSize(1);
    }

    @Test
    void save_기존_장바구니_수정_성공() {
        // Given
        entityManager.persistAndFlush(testCart);
        entityManager.clear();

        // When
        Cart existingCart = cartRepository.findById(testCartId);
        MenuId newMenuId = MenuId.generate();
        existingCart.addItem(newMenuId, List.of(), 1);
        cartRepository.save(existingCart);
        entityManager.flush();
        entityManager.clear();

        // Then
        Cart updatedCart = entityManager.find(Cart.class, testCartId.getValue());
        assertThat(updatedCart.getItems()).hasSize(2);
    }

    @Test
    void save_null_장바구니_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> cartRepository.save(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cart cannot be null");
    }

    @Test
    void findById_존재하는_장바구니_조회_성공() {
        // Given
        entityManager.persistAndFlush(testCart);
        entityManager.clear();

        // When
        Cart foundCart = cartRepository.findById(testCartId);

        // Then
        assertThat(foundCart).isNotNull();
        assertThat(foundCart.getId()).isEqualTo(testCartId);
        assertThat(foundCart.getUserId()).isEqualTo(testUserId);
        assertThat(foundCart.getShopId()).isEqualTo(testShopId);
    }

    @Test
    void findById_존재하지_않는_장바구니_null_반환() {
        // Given
        CartId nonExistentId = CartId.generate();

        // When
        Cart foundCart = cartRepository.findById(nonExistentId);

        // Then
        assertThat(foundCart).isNull();
    }

    @Test
    void findById_null_ID_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> cartRepository.findById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CartId cannot be null");
    }

    @Test
    void findByUserId_사용자별_장바구니_조회_성공() {
        // Given
        entityManager.persistAndFlush(testCart);
        entityManager.clear();

        // When
        Cart foundCart = cartRepository.findByUserId(testUserId);

        // Then
        assertThat(foundCart).isNotNull();
        assertThat(foundCart.getUserId()).isEqualTo(testUserId);
    }

    @Test
    void findByUserId_존재하지_않는_사용자_null_반환() {
        // Given
        UserId nonExistentUserId = UserId.generate();

        // When
        Cart foundCart = cartRepository.findByUserId(nonExistentUserId);

        // Then
        assertThat(foundCart).isNull();
    }

    @Test
    void findByUserId_null_UserId_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> cartRepository.findByUserId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("UserId cannot be null");
    }

    @Test
    void existsById_존재하는_장바구니_true_반환() {
        // Given
        entityManager.persistAndFlush(testCart);

        // When
        boolean exists = cartRepository.existsById(testCartId);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_존재하지_않는_장바구니_false_반환() {
        // Given
        CartId nonExistentId = CartId.generate();

        // When
        boolean exists = cartRepository.existsById(nonExistentId);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void existsById_null_ID_false_반환() {
        // When
        boolean exists = cartRepository.existsById(null);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void delete_존재하는_장바구니_삭제_성공() {
        // Given
        entityManager.persistAndFlush(testCart);
        entityManager.clear();

        // When
        Cart cartToDelete = cartRepository.findById(testCartId);
        cartRepository.delete(cartToDelete);
        entityManager.flush();

        // Then
        Cart deletedCart = entityManager.find(Cart.class, testCartId.getValue());
        assertThat(deletedCart).isNull();
    }

    @Test
    void delete_null_장바구니_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> cartRepository.delete(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cart cannot be null");
    }

    @Test
    void deleteById_존재하는_장바구니_삭제_성공() {
        // Given
        entityManager.persistAndFlush(testCart);

        // When
        cartRepository.deleteById(testCartId);
        entityManager.flush();

        // Then
        Cart deletedCart = entityManager.find(Cart.class, testCartId.getValue());
        assertThat(deletedCart).isNull();
    }

    @Test
    void deleteById_존재하지_않는_장바구니_예외_없음() {
        // Given
        CartId nonExistentId = CartId.generate();

        // When & Then - 예외가 발생하지 않아야 함
        assertThatCode(() -> cartRepository.deleteById(nonExistentId))
                .doesNotThrowAnyException();
    }

    @Test
    void deleteById_null_ID_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> cartRepository.deleteById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CartId cannot be null");
    }

    @Test
    void findByIdOptional_존재하는_장바구니_Optional_반환() {
        // Given
        entityManager.persistAndFlush(testCart);

        // When
        Optional<Cart> foundCart = cartRepository.findByIdOptional(testCartId);

        // Then
        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getId()).isEqualTo(testCartId);
    }

    @Test
    void findByIdOptional_존재하지_않는_장바구니_빈_Optional_반환() {
        // Given
        CartId nonExistentId = CartId.generate();

        // When
        Optional<Cart> foundCart = cartRepository.findByIdOptional(nonExistentId);

        // Then
        assertThat(foundCart).isEmpty();
    }

    @Test
    void findByUserIdOptional_존재하는_사용자_Optional_반환() {
        // Given
        entityManager.persistAndFlush(testCart);

        // When
        Optional<Cart> foundCart = cartRepository.findByUserIdOptional(testUserId);

        // Then
        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getUserId()).isEqualTo(testUserId);
    }

    @Test
    void findByUserIdOptional_존재하지_않는_사용자_빈_Optional_반환() {
        // Given
        UserId nonExistentUserId = UserId.generate();

        // When
        Optional<Cart> foundCart = cartRepository.findByUserIdOptional(nonExistentUserId);

        // Then
        assertThat(foundCart).isEmpty();
    }

    @Test
    void existsByUserId_존재하는_사용자_true_반환() {
        // Given
        entityManager.persistAndFlush(testCart);

        // When
        boolean exists = cartRepository.existsByUserId(testUserId);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByUserId_존재하지_않는_사용자_false_반환() {
        // Given
        UserId nonExistentUserId = UserId.generate();

        // When
        boolean exists = cartRepository.existsByUserId(nonExistentUserId);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void existsByUserId_null_UserId_false_반환() {
        // When
        boolean exists = cartRepository.existsByUserId(null);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void deleteByUserId_사용자별_장바구니_삭제_성공() {
        // Given
        Cart anotherCart = new Cart(testUserId);
        entityManager.persistAndFlush(testCart);
        entityManager.persistAndFlush(anotherCart);

        // When
        cartRepository.deleteByUserId(testUserId);
        entityManager.flush();

        // Then
        Long remainingCount = entityManager.getEntityManager()
                .createQuery("SELECT COUNT(c) FROM Cart c WHERE c.userId = :userId", Long.class)
                .setParameter("userId", testUserId.getValue())
                .getSingleResult();
        assertThat(remainingCount).isZero();
    }

    @Test
    void deleteByUserId_null_UserId_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> cartRepository.deleteByUserId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("UserId cannot be null");
    }

    @Test
    void 장바구니_아이템_포함_저장_및_조회_성공() {
        // Given
        MenuId menuId2 = MenuId.generate();
        List<OptionId> options2 = List.of(
            OptionId.generate(),
            OptionId.generate()
        );
        testCart.addItem(menuId2, options2, 3);

        // When
        cartRepository.save(testCart);
        entityManager.flush();
        entityManager.clear();

        // Then
        Cart savedCart = cartRepository.findById(testCartId);
        assertThat(savedCart.getItems()).hasSize(2);
        
        CartLineItem savedItem1 = savedCart.getItems().get(0);
        assertThat(savedItem1.getQuantity()).isEqualTo(2);
        assertThat(savedItem1.getSelectedOptions()).hasSize(1);
        
        CartLineItem savedItem2 = savedCart.getItems().get(1);
        assertThat(savedItem2.getQuantity()).isEqualTo(3);
        assertThat(savedItem2.getSelectedOptions()).hasSize(2);
    }

    @Test
    void 트랜잭션_롤백_테스트() {
        // Given
        entityManager.persistAndFlush(testCart);
        Long initialCount = entityManager.getEntityManager()
                .createQuery("SELECT COUNT(c) FROM Cart c", Long.class)
                .getSingleResult();

        // When - 트랜잭션이 롤백되어야 함
        try {
            Cart newCart = new Cart(UserId.generate());
            cartRepository.save(newCart);
            throw new RuntimeException("강제 예외 발생");
        } catch (RuntimeException e) {
            // 예외 발생으로 트랜잭션 롤백
        }

        entityManager.clear();

        // Then - 롤백으로 인해 개수가 변경되지 않아야 함
        Long finalCount = entityManager.getEntityManager()
                .createQuery("SELECT COUNT(c) FROM Cart c", Long.class)
                .getSingleResult();
        assertThat(finalCount).isEqualTo(initialCount);
    }
}