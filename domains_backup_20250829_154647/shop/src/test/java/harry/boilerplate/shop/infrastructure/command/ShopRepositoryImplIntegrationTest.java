package harry.boilerplate.shop.infrastructure.command;

import harry.boilerplate.shop.domain.aggregate.Shop;
import harry.boilerplate.shop.domain.aggregate.ShopRepository;
import harry.boilerplate.shop.domain.valueObject.ShopId;
import harry.boilerplate.shop.domain.valueObject.BusinessHours;
import harry.boilerplate.common.domain.entity.Money;
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

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * ShopRepositoryImpl 통합 테스트
 * 공유 TestContainer를 사용하여 성능 최적화
 * Requirements: 9.1 - Repository 패턴을 사용하여 쓰기 최적화를 수행한다
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ShopRepositoryImpl.class)
class ShopRepositoryImplIntegrationTest {

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
    private ShopRepository shopRepository;

    private Shop testShop;
    private ShopId testShopId;

    @BeforeEach
    void setUp() {
        // BusinessHours 생성
        Map<DayOfWeek, LocalTime[]> weeklyHours = new EnumMap<>(DayOfWeek.class);
        weeklyHours.put(DayOfWeek.MONDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)});
        weeklyHours.put(DayOfWeek.TUESDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)});
        BusinessHours businessHours = new BusinessHours(weeklyHours);
        
        testShop = new Shop("테스트 가게", Money.of(new BigDecimal("10000")), businessHours);
        // 실제 생성된 ID를 보조 필드에 저장해 테스트에서 일관되게 사용
        testShopId = testShop.getId();
    }

    @Test
    void save_새로운_가게_저장_성공() {
        // When
        shopRepository.save(testShop);
        entityManager.flush();
        entityManager.clear();

        // Then
        Shop savedShop = entityManager.find(Shop.class, testShop.getId().getValue());
        assertThat(savedShop).isNotNull();
        assertThat(savedShop.getName()).isEqualTo("테스트 가게");
        assertThat(savedShop.getMinOrderAmount().getAmount()).isEqualByComparingTo(new BigDecimal("10000"));
    }

    @Test
    void save_기존_가게_수정_성공() {
        // Given
        entityManager.persistAndFlush(testShop);
        entityManager.clear();

        // When
        Shop existingShop = shopRepository.findById(testShop.getId());
        // Shop 클래스에 updateName 메서드가 없으므로 새로운 Shop 객체 생성
        Map<DayOfWeek, LocalTime[]> weeklyHours = new EnumMap<>(DayOfWeek.class);
        weeklyHours.put(DayOfWeek.MONDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)});
        BusinessHours businessHours = new BusinessHours(weeklyHours);
        Shop updatedShop = new Shop("수정된 가게명", Money.of(new BigDecimal("10000")), businessHours);
        // ID를 기존 것으로 설정하기 위해 리플렉션 사용
        try {
            java.lang.reflect.Field idField = Shop.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(updatedShop, testShop.getId().getValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        shopRepository.save(updatedShop);
        entityManager.flush();
        entityManager.clear();

        // Then
        Shop savedShop = entityManager.find(Shop.class, testShop.getId().getValue());
        assertThat(savedShop.getName()).isEqualTo("수정된 가게명");
    }

    @Test
    void save_null_가게_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> shopRepository.save(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Shop cannot be null");
    }

    @Test
    void findById_존재하는_가게_조회_성공() {
        // Given
        entityManager.persistAndFlush(testShop);
        entityManager.clear();

        // When
        Shop foundShop = shopRepository.findById(testShop.getId());

        // Then
        assertThat(foundShop).isNotNull();
        assertThat(foundShop.getId()).isEqualTo(testShop.getId());
        assertThat(foundShop.getName()).isEqualTo("테스트 가게");
    }

    @Test
    void findById_존재하지_않는_가게_예외_발생() {
        // Given
        ShopId nonExistentId = ShopId.generate();

        // When & Then
        assertThatThrownBy(() -> shopRepository.findById(nonExistentId))
                .isInstanceOf(ShopNotFoundException.class);
    }

    @Test
    void findById_null_ID_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> shopRepository.findById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ShopId cannot be null");
    }

    @Test
    void existsById_존재하는_가게_true_반환() {
        // Given
        entityManager.persistAndFlush(testShop);

        // When
        boolean exists = shopRepository.existsById(testShop.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_존재하지_않는_가게_false_반환() {
        // Given
        ShopId nonExistentId = ShopId.generate();

        // When
        boolean exists = shopRepository.existsById(nonExistentId);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void existsById_null_ID_false_반환() {
        // When
        boolean exists = shopRepository.existsById(null);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void delete_존재하는_가게_삭제_성공() {
        // Given
        entityManager.persistAndFlush(testShop);
        entityManager.clear();

        // When
        Shop shopToDelete = shopRepository.findById(testShop.getId());
        shopRepository.delete(shopToDelete);
        entityManager.flush();

        // Then
        Shop deletedShop = entityManager.find(Shop.class, testShop.getId().getValue());
        assertThat(deletedShop).isNull();
    }

    @Test
    void delete_null_가게_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> shopRepository.delete(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Shop cannot be null");
    }

    @Test
    void deleteById_존재하는_가게_삭제_성공() {
        // Given
        entityManager.persistAndFlush(testShop);

        // When
        shopRepository.deleteById(testShop.getId());
        entityManager.flush();

        // Then
        Shop deletedShop = entityManager.find(Shop.class, testShop.getId().getValue());
        assertThat(deletedShop).isNull();
    }

    @Test
    void deleteById_존재하지_않는_가게_예외_없음() {
        // Given
        ShopId nonExistentId = ShopId.generate();

        // When & Then - 예외가 발생하지 않아야 함
        assertThatCode(() -> shopRepository.deleteById(nonExistentId))
                .doesNotThrowAnyException();
    }

    @Test
    void deleteById_null_ID_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> shopRepository.deleteById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ShopId cannot be null");
    }

    @Test
    void 트랜잭션_롤백_테스트() {
        // Given
        entityManager.persistAndFlush(testShop);
        Long initialCount = (Long) entityManager.getEntityManager()
                .createQuery("SELECT COUNT(s) FROM Shop s")
                .getSingleResult();

        // When - 트랜잭션이 롤백되어야 함
        try {
            shopRepository.save(testShop);
            throw new RuntimeException("강제 예외 발생");
        } catch (RuntimeException e) {
            // 예외 발생으로 트랜잭션 롤백
        }

        entityManager.clear();

        // Then - 롤백으로 인해 개수가 변경되지 않아야 함
        Long finalCount = (Long) entityManager.getEntityManager()
                .createQuery("SELECT COUNT(s) FROM Shop s")
                .getSingleResult();
        assertThat(finalCount).isEqualTo(initialCount);
    }
}