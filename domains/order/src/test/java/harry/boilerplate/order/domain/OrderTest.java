package harry.boilerplate.order.domain;

import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.order.domain.aggregate.Cart;
import harry.boilerplate.order.domain.aggregate.Order;
import harry.boilerplate.order.domain.exception.OrderDomainException;
import harry.boilerplate.order.domain.exception.OrderErrorCode;
import harry.boilerplate.order.domain.entity.OrderLineItem;
import harry.boilerplate.order.domain.valueObject.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Order 애그리게이트 테스트
 */
class OrderTest {
    
    @Test
    void 주문_생성_성공() {
        // Given
        UserId userId = UserId.of("user-1");
        ShopId shopId = ShopId.of("shop-1");
        
        OrderLineItem item1 = new OrderLineItem(
            MenuId.of("menu-1"), "삼겹살", 
            Arrays.asList(OptionId.of("option-1")), Arrays.asList("매운맛"), 
            2, Money.of(20000)
        );
        OrderLineItem item2 = new OrderLineItem(
            MenuId.of("menu-2"), "냉면", 
            Arrays.asList(OptionId.of("option-2")), Arrays.asList("곱빼기"), 
            1, Money.of(8000)
        );
        List<OrderLineItem> orderItems = Arrays.asList(item1, item2);
        
        // When
        Order order = new Order(userId, shopId, orderItems);
        
        // Then
        assertThat(order.getId()).isNotNull();
        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getShopId()).isEqualTo(shopId);
        assertThat(order.getOrderItems()).hasSize(2);
        assertThat(order.getItemCount()).isEqualTo(2);
        assertThat(order.getTotalQuantity()).isEqualTo(3);
        assertThat(order.getTotalPrice()).isEqualTo(Money.of(28000));
        assertThat(order.getPrice()).isEqualTo(Money.of(28000));
        assertThat(order.getOrderTime()).isNotNull();
        assertThat(order.getOrderTime()).isBefore(LocalDateTime.now().plusSeconds(1));
    }
    
    @Test
    void 주문_생성_실패_사용자ID_null() {
        // Given
        ShopId shopId = ShopId.of("shop-1");
        OrderLineItem item = new OrderLineItem(
            MenuId.of("menu-1"), "삼겹살", 
            Arrays.asList(OptionId.of("option-1")), Arrays.asList("매운맛"), 
            1, Money.of(10000)
        );
        List<OrderLineItem> orderItems = Arrays.asList(item);
        
        // When & Then
        assertThatThrownBy(() -> new Order(null, shopId, orderItems))
            .isInstanceOf(OrderDomainException.class)
            .extracting(e -> ((OrderDomainException) e).getErrorCode())
            .isEqualTo(OrderErrorCode.INVALID_USER_ID);
    }
    
    @Test
    void 주문_생성_실패_가게ID_null() {
        // Given
        UserId userId = UserId.of("user-1");
        OrderLineItem item = new OrderLineItem(
            MenuId.of("menu-1"), "삼겹살", 
            Arrays.asList(OptionId.of("option-1")), Arrays.asList("매운맛"), 
            1, Money.of(10000)
        );
        List<OrderLineItem> orderItems = Arrays.asList(item);
        
        // When & Then
        assertThatThrownBy(() -> new Order(userId, null, orderItems))
            .isInstanceOf(OrderDomainException.class)
            .extracting(e -> ((OrderDomainException) e).getErrorCode())
            .isEqualTo(OrderErrorCode.INVALID_SHOP_ID);
    }
    
    @Test
    void 주문_생성_실패_주문_아이템_null() {
        // Given
        UserId userId = UserId.of("user-1");
        ShopId shopId = ShopId.of("shop-1");
        
        // When & Then
        assertThatThrownBy(() -> new Order(userId, shopId, null))
            .isInstanceOf(OrderDomainException.class)
            .extracting(e -> ((OrderDomainException) e).getErrorCode())
            .isEqualTo(OrderErrorCode.EMPTY_ORDER_ITEMS);
    }
    
    @Test
    void 주문_생성_실패_주문_아이템_빈_리스트() {
        // Given
        UserId userId = UserId.of("user-1");
        ShopId shopId = ShopId.of("shop-1");
        List<OrderLineItem> emptyItems = Arrays.asList();
        
        // When & Then
        assertThatThrownBy(() -> new Order(userId, shopId, emptyItems))
            .isInstanceOf(OrderDomainException.class)
            .extracting(e -> ((OrderDomainException) e).getErrorCode())
            .isEqualTo(OrderErrorCode.EMPTY_ORDER_ITEMS);
    }
    
    @Test
    void 장바구니로부터_주문_생성_성공() {
        // Given
        UserId userId = UserId.of("user-1");
        ShopId shopId = ShopId.of("shop-1");
        Cart cart = new Cart(userId);
        
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));
        cart.addItem(shopId, menuId, options, 2);
        
        // When
        Order order = Order.fromCart(cart);
        
        // Then
        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getShopId()).isEqualTo(shopId);
        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(order.getTotalQuantity()).isEqualTo(2);
    }
    
    @Test
    void 장바구니로부터_주문_생성_실패_장바구니_null() {
        // When & Then
        assertThatThrownBy(() -> Order.fromCart(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("장바구니는 필수입니다");
    }
    
    @Test
    void 장바구니로부터_주문_생성_실패_빈_장바구니() {
        // Given
        UserId userId = UserId.of("user-1");
        Cart emptyCart = new Cart(userId);
        
        // When & Then
        assertThatThrownBy(() -> Order.fromCart(emptyCart))
            .isInstanceOf(OrderDomainException.class)
            .extracting(e -> ((OrderDomainException) e).getErrorCode())
            .isEqualTo(OrderErrorCode.EMPTY_ORDER_ITEMS);
    }
    
    @Test
    void 특정_사용자의_주문인지_확인_성공() {
        // Given
        UserId userId = UserId.of("user-1");
        ShopId shopId = ShopId.of("shop-1");
        OrderLineItem item = new OrderLineItem(
            MenuId.of("menu-1"), "삼겹살", 
            Arrays.asList(OptionId.of("option-1")), Arrays.asList("매운맛"), 
            1, Money.of(10000)
        );
        Order order = new Order(userId, shopId, Arrays.asList(item));
        
        // When & Then
        assertThat(order.belongsToUser(userId)).isTrue();
        assertThat(order.belongsToUser(UserId.of("other-user"))).isFalse();
    }
    
    @Test
    void 특정_가게의_주문인지_확인_성공() {
        // Given
        UserId userId = UserId.of("user-1");
        ShopId shopId = ShopId.of("shop-1");
        OrderLineItem item = new OrderLineItem(
            MenuId.of("menu-1"), "삼겹살", 
            Arrays.asList(OptionId.of("option-1")), Arrays.asList("매운맛"), 
            1, Money.of(10000)
        );
        Order order = new Order(userId, shopId, Arrays.asList(item));
        
        // When & Then
        assertThat(order.isFromShop(shopId)).isTrue();
        assertThat(order.isFromShop(ShopId.of("other-shop"))).isFalse();
    }
}