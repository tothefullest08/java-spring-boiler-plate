package harry.boilerplate.shop.infrastructure.command;

import harry.boilerplate.shop.command.domain.aggregate.Menu;
import harry.boilerplate.shop.command.domain.aggregate.MenuRepository;
import harry.boilerplate.shop.command.domain.aggregate.Shop;
import harry.boilerplate.shop.command.domain.valueObject.*;
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
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import harry.boilerplate.shop.command.domain.entity.OptionGroup;

import static org.assertj.core.api.Assertions.*;

/**
 * MenuRepositoryImpl 통합 테스트
 * Testcontainers를 사용한 MySQL 통합 테스트
 * Requirements: 9.1 - Repository 패턴을 사용하여 쓰기 최적화를 수행한다
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(harry.boilerplate.shop.command.infrastructure.repository.MenuRepositoryImpl.class)
class MenuRepositoryImplIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password")
            .withReuse(true);

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
    private MenuRepository menuRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private Shop testShop;
    private ShopId testShopId;
    private Menu testMenu;
    private MenuId testMenuId;

    @BeforeEach
    void setUp() {
        // Shop 생성
        testShopId = ShopId.generate();
        Map<DayOfWeek, LocalTime[]> weeklyHours = new EnumMap<>(DayOfWeek.class);
        weeklyHours.put(DayOfWeek.MONDAY, new LocalTime[] { LocalTime.of(9, 0), LocalTime.of(22, 0) });
        BusinessHours businessHours = new BusinessHours(weeklyHours);
        testShop = new Shop("테스트 가게", Money.of(new BigDecimal("10000")), businessHours);
        entityManager.persistAndFlush(testShop);

        // Menu 생성
        testMenuId = MenuId.generate();
        testMenu = new Menu(testShopId, "삼겹살", "맛있는 삼겹살", Money.of(new BigDecimal("15000")));

        // 옵션그룹 추가
        testMenu.addOptionGroup("고기 굽기", true);
    }

    @Test
    void save_새로운_메뉴_저장_성공() {
        // When
        menuRepository.save(testMenu);
        entityManager.flush();
        entityManager.clear();

        // Then
        Menu savedMenu = entityManager.find(Menu.class, testMenu.getId().getValue());
        assertThat(savedMenu).isNotNull();
        assertThat(savedMenu.getName()).isEqualTo("삼겹살");
        assertThat(savedMenu.getDescription()).isEqualTo("맛있는 삼겹살");
        assertThat(savedMenu.getBasePrice().getAmount()).isEqualByComparingTo(new BigDecimal("15000"));
        assertThat(savedMenu.getShopId()).isEqualTo(testShopId);
        assertThat(savedMenu.isOpen()).isFalse();
    }

    @Test
    void save_기존_메뉴_수정_성공() {
        // Given
        entityManager.persistAndFlush(testMenu);
        MenuId savedMenuId = testMenu.getId();
        entityManager.clear();

        // When
        Menu newMenu = new Menu(testShopId, "수정된 삼겹살", "더 맛있는 삼겹살", Money.of(new BigDecimal("15000")));
        // ID를 기존 것으로 설정하기 위해 리플렉션 사용
        try {
            java.lang.reflect.Field idField = Menu.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(newMenu, savedMenuId.getValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        menuRepository.save(newMenu);
        entityManager.flush();
        entityManager.clear();

        // Then
        Menu savedMenu = entityManager.find(Menu.class, savedMenuId.getValue());
        assertThat(savedMenu.getName()).isEqualTo("수정된 삼겹살");
        assertThat(savedMenu.getDescription()).isEqualTo("더 맛있는 삼겹살");
    }

    @Test
    void save_null_메뉴_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> menuRepository.save(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Menu cannot be null");
    }

    @Test
    void findById_존재하는_메뉴_조회_성공() {
        // Given
        entityManager.persistAndFlush(testMenu);
        MenuId savedMenuId = testMenu.getId();
        entityManager.clear();

        // When
        Menu foundMenu = menuRepository.findById(savedMenuId);

        // Then
        assertThat(foundMenu).isNotNull();
        assertThat(foundMenu.getId()).isEqualTo(savedMenuId);
        assertThat(foundMenu.getName()).isEqualTo("삼겹살");
        assertThat(foundMenu.getShopId()).isEqualTo(testShopId);
    }

    @Test
    void findById_존재하지_않는_메뉴_예외_발생() {
        // Given
        MenuId nonExistentId = MenuId.generate();

        // When & Then
        assertThatThrownBy(() -> menuRepository.findById(nonExistentId))
                .isInstanceOf(harry.boilerplate.shop.command.infrastructure.repository.MenuNotFoundException.class);
    }

    @Test
    void findById_null_ID_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> menuRepository.findById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("MenuId cannot be null");
    }

    @Test
    void findByShopId_가게별_메뉴_조회_성공() {
        // Given
        Menu menu1 = new Menu(testShopId, "메뉴1", "설명1", Money.of(new BigDecimal("10000")));
        Menu menu2 = new Menu(testShopId, "메뉴2", "설명2", Money.of(new BigDecimal("12000")));
        Menu menu3 = new Menu(ShopId.generate(), "다른가게메뉴", "설명3", Money.of(new BigDecimal("8000")));

        entityManager.persistAndFlush(menu1);
        entityManager.persistAndFlush(menu2);
        entityManager.persistAndFlush(menu3);
        entityManager.clear();

        // When
        List<Menu> menus = menuRepository.findByShopId(testShopId);

        // Then
        assertThat(menus).hasSize(2);
        assertThat(menus).extracting(Menu::getName).containsExactly("메뉴1", "메뉴2");
        assertThat(menus).allMatch(menu -> menu.getShopId().equals(testShopId));
    }

    @Test
    void findByShopId_null_ShopId_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> menuRepository.findByShopId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ShopId cannot be null");
    }

    @Test
    void existsById_존재하는_메뉴_true_반환() {
        // Given
        entityManager.persistAndFlush(testMenu);
        MenuId savedMenuId = testMenu.getId();

        // When
        boolean exists = menuRepository.existsById(savedMenuId);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_존재하지_않는_메뉴_false_반환() {
        // Given
        MenuId nonExistentId = MenuId.generate();

        // When
        boolean exists = menuRepository.existsById(nonExistentId);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void existsById_null_ID_false_반환() {
        // When
        boolean exists = menuRepository.existsById(null);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void delete_존재하는_메뉴_삭제_성공() {
        // Given
        entityManager.persistAndFlush(testMenu);
        MenuId savedMenuId = testMenu.getId();
        entityManager.clear();

        // When
        Menu menuToDelete = menuRepository.findById(savedMenuId);
        menuRepository.delete(menuToDelete);
        entityManager.flush();

        // Then
        Menu deletedMenu = entityManager.find(Menu.class, savedMenuId.getValue());
        assertThat(deletedMenu).isNull();
    }

    @Test
    void delete_null_메뉴_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> menuRepository.delete(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Menu cannot be null");
    }

    @Test
    void deleteById_존재하는_메뉴_삭제_성공() {
        // Given
        entityManager.persistAndFlush(testMenu);
        MenuId savedMenuId = testMenu.getId();

        // When
        menuRepository.deleteById(savedMenuId);
        entityManager.flush();

        // Then
        Menu deletedMenu = entityManager.find(Menu.class, savedMenuId.getValue());
        assertThat(deletedMenu).isNull();
    }

    @Test
    void deleteById_존재하지_않는_메뉴_예외_없음() {
        // Given
        MenuId nonExistentId = MenuId.generate();

        // When & Then - 예외가 발생하지 않아야 함
        assertThatCode(() -> menuRepository.deleteById(nonExistentId))
                .doesNotThrowAnyException();
    }

    @Test
    void deleteById_null_ID_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> menuRepository.deleteById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("MenuId cannot be null");
    }

    @Test
    void deleteByShopId_가게별_메뉴_일괄_삭제_성공() {
        // Given
        Menu menu1 = new Menu(testShopId, "메뉴1", "설명1", Money.of(new BigDecimal("10000")));
        Menu menu2 = new Menu(testShopId, "메뉴2", "설명2", Money.of(new BigDecimal("12000")));
        Menu menu3 = new Menu(ShopId.generate(), "다른가게메뉴", "설명3", Money.of(new BigDecimal("8000")));

        entityManager.persistAndFlush(menu1);
        entityManager.persistAndFlush(menu2);
        entityManager.persistAndFlush(menu3);

        // When
        menuRepository.deleteByShopId(testShopId);
        entityManager.flush();
        entityManager.clear();

        // Then
        @SuppressWarnings("unchecked")
        List<Menu> remainingMenus = entityManager.getEntityManager()
                .createQuery("SELECT m FROM Menu m")
                .getResultList();
        assertThat(remainingMenus).hasSize(1);
        assertThat(remainingMenus.get(0).getName()).isEqualTo("다른가게메뉴");
    }

    @Test
    void deleteByShopId_null_ShopId_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> menuRepository.deleteByShopId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ShopId cannot be null");
    }

    @Test
    void 옵션그룹_포함_메뉴_저장_및_조회_성공() {
        // Given
        testMenu.addOptionGroup("사이드", false);

        // When
        menuRepository.save(testMenu);
        entityManager.flush();
        entityManager.clear();

        // Then
        Menu savedMenu = menuRepository.findById(testMenu.getId());
        assertThat(savedMenu.getOptionGroups()).hasSize(2);
        java.util.List<String> names = savedMenu.getOptionGroups().stream()
                .map(OptionGroup::getName)
                .toList();
        assertThat(names).containsExactlyInAnyOrder("고기 굽기", "사이드");
        assertThat(savedMenu.getOptionGroups().stream()
                .anyMatch(og -> og.getName().equals("고기 굽기") && og.isRequired())).isTrue();
        assertThat(savedMenu.getOptionGroups().stream()
                .anyMatch(og -> og.getName().equals("사이드") && !og.isRequired())).isTrue();
    }

    @Test
    void 트랜잭션_롤백_테스트() {
        // Given
        entityManager.persistAndFlush(testMenu);
        entityManager.clear();

        Long initialCount = (Long) entityManager.getEntityManager()
                .createQuery("SELECT COUNT(m) FROM Menu m")
                .getSingleResult();

        // When - 별도 트랜잭션(REQUIRES_NEW)에서 저장 후 롤백
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        try {
            template.executeWithoutResult(status -> {
                Menu newMenu = new Menu(testShopId, "새 메뉴", "설명", Money.of(new BigDecimal("20000")));
                entityManager.persist(newMenu);
                entityManager.flush();
                status.setRollbackOnly();
            });
        } catch (Exception ignored) {
        }

        entityManager.clear();

        // Then - 롤백으로 인해 개수가 변경되지 않아야 함
        Long finalCount = (Long) entityManager.getEntityManager()
                .createQuery("SELECT COUNT(m) FROM Menu m")
                .getSingleResult();
        assertThat(finalCount).isEqualTo(initialCount);
    }
}