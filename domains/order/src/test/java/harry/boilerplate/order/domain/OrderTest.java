package harry.boilerplate.order.domain;

import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.order.command.domain.aggregate.Order;
import harry.boilerplate.order.command.domain.entity.OrderLineItem;
import harry.boilerplate.order.command.domain.exception.OrderDomainException;
import harry.boilerplate.order.command.domain.exception.OrderErrorCode;
import harry.boilerplate.order.command.domain.valueObject.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Order 도메인 애그리게이트 테스트
 */
@DisplayName("Order 애그리게이트 테스트")
class OrderTest {

    @Nested
    @DisplayName("Order 생성")
    class CreateOrder {

        @Test
        @DisplayName("정상적인 Order 생성")
        void 정상적인_Order_생성() {
            // Given
            UserId userId = UserId.generate();
            ShopId shopId = ShopId.generate();
            List<OrderLineItem> orderItems = Arrays.asList(
                new OrderLineItem(
                    MenuId.of("menu-1"), "삼겹살", 
                    Arrays.asList(), 2, Money.of(new BigDecimal("30000"))
                ),
                new OrderLineItem(
                    MenuId.of("menu-2"), "냉면", 
                    Arrays.asList(), 1, Money.of(new BigDecimal("8000"))
                )
            );

            // When
            Order order = new Order(userId, shopId, orderItems);

            // Then
            assertThat(order.getId()).isNotNull();
            assertThat(order.getUserId()).isEqualTo(userId);
            assertThat(order.getShopId()).isEqualTo(shopId);
            assertThat(order.getOrderItems()).hasSize(2);
            assertThat(order.getTotalPrice()).isEqualTo(Money.of(new BigDecimal("38000")));
            assertThat(order.getOrderTime()).isNotNull();
        }

        @Test
        @DisplayName("UserId가 null인 경우 예외 발생")
        void UserId가_null인_경우_예외_발생() {
            // Given
            ShopId shopId = ShopId.generate();
            List<OrderLineItem> orderItems = Arrays.asList(
                new OrderLineItem(
                    MenuId.of("menu-1"), "삼겹살", 
                    Arrays.asList(), 2, Money.of(new BigDecimal("30000"))
                )
            );

            // When & Then
            assertThatThrownBy(() -> new Order(null, shopId, orderItems))
                    .isInstanceOf(OrderDomainException.class)
                    .extracting(e -> ((OrderDomainException) e).getErrorCode())
                    .isEqualTo(OrderErrorCode.INVALID_USER_ID);
        }

        @Test
        @DisplayName("ShopId가 null인 경우 예외 발생")
        void ShopId가_null인_경우_예외_발생() {
            // Given
            UserId userId = UserId.generate();
            List<OrderLineItem> orderItems = Arrays.asList(
                new OrderLineItem(
                    MenuId.of("menu-1"), "삼겹살", 
                    Arrays.asList(), 2, Money.of(new BigDecimal("30000"))
                )
            );

            // When & Then
            assertThatThrownBy(() -> new Order(userId, null, orderItems))
                    .isInstanceOf(OrderDomainException.class)
                    .extracting(e -> ((OrderDomainException) e).getErrorCode())
                    .isEqualTo(OrderErrorCode.INVALID_SHOP_ID);
        }

        @Test
        @DisplayName("OrderItems가 null인 경우 예외 발생")
        void OrderItems가_null인_경우_예외_발생() {
            // Given
            UserId userId = UserId.generate();
            ShopId shopId = ShopId.generate();

            // When & Then
            assertThatThrownBy(() -> new Order(userId, shopId, null))
                    .isInstanceOf(OrderDomainException.class)
                    .extracting(e -> ((OrderDomainException) e).getErrorCode())
                    .isEqualTo(OrderErrorCode.EMPTY_ORDER_ITEMS);
        }

        @Test
        @DisplayName("OrderItems가 빈 리스트인 경우 예외 발생")
        void OrderItems가_빈_리스트인_경우_예외_발생() {
            // Given
            UserId userId = UserId.generate();
            ShopId shopId = ShopId.generate();
            List<OrderLineItem> emptyItems = Arrays.asList();

            // When & Then
            assertThatThrownBy(() -> new Order(userId, shopId, emptyItems))
                    .isInstanceOf(OrderDomainException.class)
                    .extracting(e -> ((OrderDomainException) e).getErrorCode())
                    .isEqualTo(OrderErrorCode.EMPTY_ORDER_ITEMS);
        }
    }

    @Nested
    @DisplayName("주문 가격 계산")
    class OrderPriceCalculation {

        @Test
        @DisplayName("단일 아이템 주문 가격 계산")
        void 단일_아이템_주문_가격_계산() {
            // Given
            UserId userId = UserId.generate();
            ShopId shopId = ShopId.generate();
            List<OrderLineItem> orderItems = Arrays.asList(
                new OrderLineItem(
                    MenuId.of("menu-1"), "삼겹살", 
                    Arrays.asList(), 3, Money.of(new BigDecimal("45000"))
                )
            );

            // When
            Order order = new Order(userId, shopId, orderItems);

            // Then
            assertThat(order.getPrice()).isEqualTo(Money.of(new BigDecimal("45000")));
            assertThat(order.getTotalPrice()).isEqualTo(Money.of(new BigDecimal("45000")));
        }

        @Test
        @DisplayName("다중 아이템 주문 가격 계산")
        void 다중_아이템_주문_가격_계산() {
            // Given
            UserId userId = UserId.generate();
            ShopId shopId = ShopId.generate();
            List<OrderLineItem> orderItems = Arrays.asList(
                new OrderLineItem(
                    MenuId.of("menu-1"), "삼겹살", 
                    Arrays.asList(), 2, Money.of(new BigDecimal("30000"))
                ),
                new OrderLineItem(
                    MenuId.of("menu-2"), "냉면", 
                    Arrays.asList(), 1, Money.of(new BigDecimal("8000"))
                ),
                new OrderLineItem(
                    MenuId.of("menu-3"), "김치찌개", 
                    Arrays.asList(), 1, Money.of(new BigDecimal("12000"))
                )
            );

            // When
            Order order = new Order(userId, shopId, orderItems);

            // Then
            assertThat(order.getPrice()).isEqualTo(Money.of(new BigDecimal("50000")));
        }
    }

    @Nested
    @DisplayName("주문 정보 조회")
    class OrderInformation {

        @Test
        @DisplayName("주문 아이템 개수 조회")
        void 주문_아이템_개수_조회() {
            // Given
            UserId userId = UserId.generate();
            ShopId shopId = ShopId.generate();
            List<OrderLineItem> orderItems = Arrays.asList(
                new OrderLineItem(
                    MenuId.of("menu-1"), "삼겹살", 
                    Arrays.asList(), 2, Money.of(new BigDecimal("30000"))
                ),
                new OrderLineItem(
                    MenuId.of("menu-2"), "냉면", 
                    Arrays.asList(), 1, Money.of(new BigDecimal("8000"))
                )
            );

            // When
            Order order = new Order(userId, shopId, orderItems);

            // Then
            assertThat(order.getItemCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("주문 총 수량 조회")
        void 주문_총_수량_조회() {
            // Given
            UserId userId = UserId.generate();
            ShopId shopId = ShopId.generate();
            List<OrderLineItem> orderItems = Arrays.asList(
                new OrderLineItem(
                    MenuId.of("menu-1"), "삼겹살", 
                    Arrays.asList(), 3, Money.of(new BigDecimal("45000"))
                ),
                new OrderLineItem(
                    MenuId.of("menu-2"), "냉면", 
                    Arrays.asList(), 2, Money.of(new BigDecimal("16000"))
                )
            );

            // When
            Order order = new Order(userId, shopId, orderItems);

            // Then
            assertThat(order.getTotalQuantity()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("주문 소유권 검증")
    class OrderOwnership {

        @Test
        @DisplayName("특정 사용자의 주문인지 확인")
        void 특정_사용자의_주문인지_확인() {
            // Given
            UserId userId = UserId.generate();
            ShopId shopId = ShopId.generate();
            List<OrderLineItem> orderItems = Arrays.asList(
                new OrderLineItem(
                    MenuId.of("menu-1"), "삼겹살", 
                    Arrays.asList(), 2, Money.of(new BigDecimal("30000"))
                )
            );
            Order order = new Order(userId, shopId, orderItems);

            // When & Then
            assertThat(order.belongsToUser(userId)).isTrue();
            assertThat(order.belongsToUser(UserId.generate())).isFalse();
        }

        @Test
        @DisplayName("특정 가게의 주문인지 확인")
        void 특정_가게의_주문인지_확인() {
            // Given
            UserId userId = UserId.generate();
            ShopId shopId = ShopId.generate();
            List<OrderLineItem> orderItems = Arrays.asList(
                new OrderLineItem(
                    MenuId.of("menu-1"), "삼겹살", 
                    Arrays.asList(), 2, Money.of(new BigDecimal("30000"))
                )
            );
            Order order = new Order(userId, shopId, orderItems);

            // When & Then
            assertThat(order.isFromShop(shopId)).isTrue();
            assertThat(order.isFromShop(ShopId.generate())).isFalse();
        }
    }
}