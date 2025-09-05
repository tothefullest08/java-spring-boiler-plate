package harry.boilerplate.order.infrastructure.command;

import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.order.domain.aggregate.Order;
import harry.boilerplate.order.domain.entity.OrderLineItem;
import harry.boilerplate.order.domain.valueObject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(OrderRepositoryImpl.class)
@DisplayName("OrderRepositoryImpl 통합 테스트")
class OrderRepositoryImplIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withReuse(true);

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepositoryImpl orderRepository;

    private OrderId testOrderId;
    private UserId testUserId;
    private ShopId testShopId;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrderId = OrderId.generate();
        testUserId = UserId.generate();
        testShopId = ShopId.generate();
        
        // OrderLineItem 생성
        List<SelectedOption> selectedOptions = List.of(
            new SelectedOption(OptionId.generate(), "덜 익힘", BigDecimal.ZERO),
            new SelectedOption(OptionId.generate(), "김치", new BigDecimal("2000"))
        );
        OrderLineItem item = new OrderLineItem(
            MenuId.generate(), "삼겹살", selectedOptions, 2, Money.of(new BigDecimal("20000"))
        );
        
        // Order 생성
        testOrder = new Order(testUserId, testShopId, List.of(item));
    }

    @Test
    @DisplayName("새로운 주문 저장 성공")
    void save_새로운_주문_저장_성공() {
        // When
        orderRepository.save(testOrder);
        entityManager.flush();
        entityManager.clear();
        
        // Then
        Order savedOrder = entityManager.find(Order.class, testOrder.getId().getValue());
        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getId()).isEqualTo(testOrder.getId());
        assertThat(savedOrder.getUserId()).isEqualTo(testUserId);
        assertThat(savedOrder.getShopId()).isEqualTo(testShopId);
        assertThat(savedOrder.getOrderItems()).hasSize(1);
        
        OrderLineItem savedItem = savedOrder.getOrderItems().get(0);
        assertThat(savedItem.getQuantity()).isEqualTo(2);
        assertThat(savedItem.getSelectedOptions()).hasSize(2);
    }

    @Test
    @DisplayName("기존 주문 수정 성공")
    void save_기존_주문_수정_성공() {
        // Given
        entityManager.persistAndFlush(testOrder);
        entityManager.clear();
        
        // When - Order는 불변 객체이므로 새로운 OrderLineItem을 추가한 새 Order를 생성
        OrderLineItem newItem = new OrderLineItem(
            MenuId.generate(), "냉면", List.of(), 1, Money.of(new BigDecimal("8000"))
        );
        
        List<OrderLineItem> updatedItems = List.of(
            testOrder.getOrderItems().get(0), // 기존 아이템
            newItem // 새 아이템
        );
        Order updatedOrder = new Order(
            testOrder.getId(),
            testOrder.getUserId(),
            testOrder.getShopId(),
            updatedItems,
            Money.of(new BigDecimal("28000")),
            testOrder.getOrderTime()
        );
        
        orderRepository.save(updatedOrder);
        entityManager.flush();
        entityManager.clear();
        
        // Then
        Order savedOrder = entityManager.find(Order.class, testOrder.getId().getValue());
        assertThat(savedOrder.getOrderItems()).hasSize(2);
    }

    @Test
    @DisplayName("주문 ID로 조회 성공")
    void findById_주문_ID로_조회_성공() {
        // Given
        entityManager.persistAndFlush(testOrder);
        entityManager.clear();

        // When
        Order foundOrder = orderRepository.findById(testOrder.getId());

        // Then
        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getId()).isEqualTo(testOrder.getId());
        assertThat(foundOrder.getUserId()).isEqualTo(testUserId);
        assertThat(foundOrder.getShopId()).isEqualTo(testShopId);
    }

    @Test
    @DisplayName("존재하지 않는 주문 ID로 조회 시 null 반환")
    void findById_존재하지_않는_주문_ID로_조회_시_null_반환() {
        // When
        Order foundOrder = orderRepository.findById(OrderId.generate());

        // Then
        assertThat(foundOrder).isNull();
    }

    @Test
    @DisplayName("null OrderId로 조회 시 예외 발생")
    void findById_null_OrderId로_조회_시_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> orderRepository.findById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("OrderId cannot be null");
    }

    @Test
    @DisplayName("사용자별 주문 목록 조회 성공")
    void findByUserId_사용자별_주문_목록_조회_성공() {
        // Given
        entityManager.persistAndFlush(testOrder);
        
        // 다른 사용자의 주문 생성
        Order anotherUserOrder = new Order(
            UserId.generate(), 
            testShopId, 
            List.of(new OrderLineItem(
                MenuId.generate(), "냉면", List.of(), 1, Money.of(new BigDecimal("8000"))
            ))
        );
        entityManager.persistAndFlush(anotherUserOrder);
        entityManager.clear();

        // When
        List<Order> userOrders = orderRepository.findByUserId(testUserId);

        // Then
        assertThat(userOrders).hasSize(1);
        assertThat(userOrders.get(0).getUserId()).isEqualTo(testUserId);
    }

    @Test
    @DisplayName("주문이 없는 사용자 조회 시 빈 목록 반환")
    void findByUserId_주문이_없는_사용자_조회_시_빈_목록_반환() {
        // When
        List<Order> userOrders = orderRepository.findByUserId(UserId.generate());

        // Then
        assertThat(userOrders).isEmpty();
    }

    @Test
    @DisplayName("null UserId로 조회 시 예외 발생")
    void findByUserId_null_UserId로_조회_시_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> orderRepository.findByUserId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("UserId cannot be null");
    }

    @Test
    @DisplayName("주문 삭제 성공")
    void delete_주문_삭제_성공() {
        // Given
        entityManager.persistAndFlush(testOrder);
        OrderId orderId = testOrder.getId();

        // When
        orderRepository.deleteById(orderId);
        entityManager.flush();

        // Then
        Order deletedOrder = entityManager.find(Order.class, orderId.getValue());
        assertThat(deletedOrder).isNull();
    }

    @Test
    @DisplayName("null OrderId로 삭제 시 예외 발생")
    void deleteById_null_OrderId로_삭제_시_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> orderRepository.deleteById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("OrderId cannot be null");
    }

    @Test
    @DisplayName("주문 존재 여부 확인")
    void existsById_주문_존재_여부_확인() {
        // Given
        entityManager.persistAndFlush(testOrder);
        OrderId orderId = testOrder.getId();
        OrderId nonExistentOrderId = OrderId.generate();

        // When & Then
        assertThat(orderRepository.existsById(orderId)).isTrue();
        assertThat(orderRepository.existsById(nonExistentOrderId)).isFalse();
    }

    @Test
    @DisplayName("주문 아이템 포함 저장 및 조회 성공")
    void 주문_아이템_포함_저장_및_조회_성공() {
        // Given
        List<SelectedOption> options2 = List.of(
            new SelectedOption(OptionId.generate(), "적당히", BigDecimal.ZERO),
            new SelectedOption(OptionId.generate(), "콩나물", new BigDecimal("1500"))
        );
        OrderLineItem item2 = new OrderLineItem(
            MenuId.generate(), "냉면", options2, 3, Money.of(new BigDecimal("25500"))
        );
        
        Order orderWithMultipleItems = new Order(
            testUserId, testShopId, List.of(testOrder.getOrderItems().get(0), item2)
        );

        // When
        orderRepository.save(orderWithMultipleItems);
        entityManager.flush();
        entityManager.clear();

        // Then
        Order savedOrder = entityManager.find(Order.class, orderWithMultipleItems.getId().getValue());
        assertThat(savedOrder.getOrderItems()).hasSize(2);

        OrderLineItem savedItem1 = savedOrder.getOrderItems().get(0);
        assertThat(savedItem1.getQuantity()).isEqualTo(2);
        assertThat(savedItem1.getSelectedOptions()).hasSize(2);

        OrderLineItem savedItem2 = savedOrder.getOrderItems().get(1);
        assertThat(savedItem2.getQuantity()).isEqualTo(3);
        assertThat(savedItem2.getSelectedOptions()).hasSize(2);
    }

    @Test
    @DisplayName("트랜잭션 롤백 테스트")
    void 트랜잭션_롤백_테스트() {
        // Given
        entityManager.persistAndFlush(testOrder);
        Long initialCount = entityManager.getEntityManager()
                .createQuery("SELECT COUNT(o) FROM Order o", Long.class)
                .getSingleResult();

        // When - 트랜잭션이 롤백되어야 함
        try {
            Order newOrder = new Order(UserId.generate(), ShopId.generate(), List.of(
                new OrderLineItem(MenuId.generate(), "피자", List.of(), 1, Money.of(new BigDecimal("25000")))
            ));
            orderRepository.save(newOrder);
            throw new RuntimeException("강제 예외 발생");
        } catch (RuntimeException e) {
            // 예외 발생으로 트랜잭션 롤백
        }

        entityManager.clear();

        // Then - 롤백으로 인해 개수가 변경되지 않아야 함
        Long finalCount = entityManager.getEntityManager()
                .createQuery("SELECT COUNT(o) FROM Order o", Long.class)
                .getSingleResult();
        assertThat(finalCount).isEqualTo(initialCount);
    }
}