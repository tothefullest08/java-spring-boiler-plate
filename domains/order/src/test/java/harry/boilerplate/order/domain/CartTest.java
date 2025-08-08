package harry.boilerplate.order.domain;

import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.common.domain.event.DomainEvent;
import harry.boilerplate.order.domain.aggregate.Cart;
import harry.boilerplate.order.domain.exception.CartDomainException;
import harry.boilerplate.order.domain.exception.CartErrorCode;
import harry.boilerplate.order.domain.aggregate.Order;
import harry.boilerplate.order.domain.entity.CartLineItem;
import harry.boilerplate.order.domain.event.CartItemAddedEvent;
import harry.boilerplate.order.domain.valueObject.*;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Cart 애그리게이트 테스트
 */
class CartTest {

    @Test
    void 장바구니_생성_성공() {
        // Given
        UserId userId = UserId.of("user-1");

        // When
        Cart cart = new Cart(userId);

        // Then
        assertThat(cart.getId()).isNotNull();
        assertThat(cart.getUserId()).isEqualTo(userId);
        assertThat(cart.getShopId()).isNull();
        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.isEmpty()).isTrue();
    }

    @Test
    void 장바구니_생성_실패_사용자ID_null() {
        // When & Then
        assertThatThrownBy(() -> new Cart(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자 ID는 필수입니다");
    }

    @Test
    void 장바구니_시작_성공() {
        // Given
        UserId userId = UserId.of("user-1");
        Cart cart = new Cart(userId);
        ShopId shopId = ShopId.of("shop-1");

        // When
        cart.start(shopId);

        // Then
        assertThat(cart.getShopId()).isEqualTo(shopId);
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void 장바구니_시작_실패_가게ID_null() {
        // Given
        UserId userId = UserId.of("user-1");
        Cart cart = new Cart(userId);

        // When & Then
        assertThatThrownBy(() -> cart.start(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가게 ID는 필수입니다");
    }

    @Test
    void 장바구니에_아이템_추가_성공() {
        // Given
        UserId userId = UserId.of("user-1");
        Cart cart = new Cart(userId);
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));

        // When
        cart.addItem(menuId, options, 2);

        // Then
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItemCount()).isEqualTo(1);
        assertThat(cart.getTotalQuantity()).isEqualTo(2);
        assertThat(cart.isEmpty()).isFalse();

        CartLineItem item = cart.getItems().get(0);
        assertThat(item.getMenuId()).isEqualTo(menuId);
        assertThat(item.getSelectedOptions()).containsExactlyElementsOf(options);
        assertThat(item.getQuantity()).isEqualTo(2);
    }

    @Test
    void 장바구니에_아이템_추가_실패_메뉴ID_null() {
        // Given
        UserId userId = UserId.of("user-1");
        Cart cart = new Cart(userId);
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));

        // When & Then
        assertThatThrownBy(() -> cart.addItem(null, options, 1))
                .isInstanceOf(CartDomainException.class)
                .extracting(e -> ((CartDomainException) e).getErrorCode())
                .isEqualTo(CartErrorCode.INVALID_MENU_ID);
    }

    @Test
    void 장바구니에_아이템_추가_실패_수량_0이하() {
        // Given
        UserId userId = UserId.of("user-1");
        Cart cart = new Cart(userId);
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));

        // When & Then
        assertThatThrownBy(() -> cart.addItem(menuId, options, 0))
                .isInstanceOf(CartDomainException.class)
                .extracting(e -> ((CartDomainException) e).getErrorCode())
                .isEqualTo(CartErrorCode.INVALID_QUANTITY);
    }

    @Test
    void 동일한_메뉴와_옵션_조합_병합_성공() {
        // Given
        UserId userId = UserId.of("user-1");
        Cart cart = new Cart(userId);
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));

        // When
        cart.addItem(menuId, options, 2);
        cart.addItem(menuId, options, 3);

        // Then
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getTotalQuantity()).isEqualTo(5);

        CartLineItem item = cart.getItems().get(0);
        assertThat(item.getQuantity()).isEqualTo(5);
    }

    @Test
    void 다른_메뉴_아이템_별도_추가_성공() {
        // Given
        UserId userId = UserId.of("user-1");
        Cart cart = new Cart(userId);
        MenuId menuId1 = MenuId.of("menu-1");
        MenuId menuId2 = MenuId.of("menu-2");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));

        // When
        cart.addItem(menuId1, options, 1);
        cart.addItem(menuId2, options, 2);

        // Then
        assertThat(cart.getItems()).hasSize(2);
        assertThat(cart.getTotalQuantity()).isEqualTo(3);
    }

    @Test
    void 가게_검증_포함_아이템_추가_같은_가게_성공() {
        // Given
        UserId userId = UserId.of("user-1");
        Cart cart = new Cart(userId);
        ShopId shopId = ShopId.of("shop-1");
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));

        // When
        cart.addItem(shopId, menuId, options, 1);

        // Then
        assertThat(cart.getShopId()).isEqualTo(shopId);
        assertThat(cart.getItems()).hasSize(1);
    }

    @Test
    void 가게_검증_포함_아이템_추가_다른_가게_장바구니_초기화() {
        // Given
        UserId userId = UserId.of("user-1");
        Cart cart = new Cart(userId);
        ShopId shopId1 = ShopId.of("shop-1");
        ShopId shopId2 = ShopId.of("shop-2");
        MenuId menuId1 = MenuId.of("menu-1");
        MenuId menuId2 = MenuId.of("menu-2");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));

        // When
        cart.addItem(shopId1, menuId1, options, 1);
        cart.addItem(shopId2, menuId2, options, 2);

        // Then
        assertThat(cart.getShopId()).isEqualTo(shopId2);
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getMenuId()).isEqualTo(menuId2);
        assertThat(cart.getTotalQuantity()).isEqualTo(2);
    }

    @Test
    void 장바구니_아이템_제거_성공() {
        // Given
        UserId userId = UserId.of("user-1");
        Cart cart = new Cart(userId);
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));

        cart.addItem(menuId, options, 1);

        // When
        cart.removeItem(menuId, options);

        // Then
        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.isEmpty()).isTrue();
    }

    @Test
    void 장바구니_비우기_성공() {
        // Given
        UserId userId = UserId.of("user-1");
        Cart cart = new Cart(userId);
        ShopId shopId = ShopId.of("shop-1");
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));

        cart.addItem(shopId, menuId, options, 1);

        // When
        cart.clear();

        // Then
        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.getShopId()).isNull();
        assertThat(cart.isEmpty()).isTrue();
    }

    @Test
    void 장바구니로부터_주문_생성_성공() {
        // Given
        UserId userId = UserId.of("user-1");
        Cart cart = new Cart(userId);
        ShopId shopId = ShopId.of("shop-1");
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));

        cart.addItem(shopId, menuId, options, 2);

        // When
        Order order = cart.placeOrder();

        // Then
        assertThat(order).isNotNull();
        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getShopId()).isEqualTo(shopId);
        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(order.getTotalQuantity()).isEqualTo(2);

        // 주문 후 장바구니가 비워졌는지 확인
        assertThat(cart.isEmpty()).isTrue();
        assertThat(cart.getShopId()).isNull();
    }

    @Test
    void 빈_장바구니로부터_주문_생성_실패() {
        // Given
        UserId userId = UserId.of("user-1");
        Cart emptyCart = new Cart(userId);

        // When & Then
        assertThatThrownBy(() -> emptyCart.placeOrder())
                .isInstanceOf(CartDomainException.class)
                .extracting(e -> ((CartDomainException) e).getErrorCode())
                .isEqualTo(CartErrorCode.EMPTY_CART);
    }

    @Test
    void 장바구니_총_금액_계산_성공() {
        // Given
        UserId userId = UserId.of("user-1");
        Cart cart = new Cart(userId);
        MenuId menuId1 = MenuId.of("menu-1");
        MenuId menuId2 = MenuId.of("menu-2");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));

        // When
        cart.addItem(menuId1, options, 2); // 2개 * 10,000원 = 20,000원
        cart.addItem(menuId2, options, 1); // 1개 * 10,000원 = 10,000원

        // Then
        assertThat(cart.getTotalPrice()).isEqualTo(Money.of(30000));
    }

    @Test
    void 빈_장바구니_총_금액_0원() {
        // Given
        UserId userId = UserId.of("user-1");
        Cart emptyCart = new Cart(userId);

        // When & Then
        assertThat(emptyCart.getTotalPrice()).isEqualTo(Money.zero());
        assertThat(emptyCart.getTotalPrice().isZero()).isTrue();
    }
    
    @Test
    void 장바구니에_아이템_추가_시_CartItemAddedEvent_발행() {
        // Given
        UserId userId = UserId.of("user-1");
        Cart cart = new Cart(userId);
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));

        // When
        cart.addItem(menuId, options, 2);

        // Then
        assertThat(cart.hasDomainEvents()).isTrue();
        List<DomainEvent> events = cart.getDomainEvents();
        assertThat(events).hasSize(1);
        
        DomainEvent event = events.get(0);
        assertThat(event).isInstanceOf(CartItemAddedEvent.class);
        
        CartItemAddedEvent cartItemAddedEvent = (CartItemAddedEvent) event;
        assertThat(cartItemAddedEvent.getAggregateId()).isEqualTo(cart.getId().getValue());
        assertThat(cartItemAddedEvent.getAggregateType()).isEqualTo("Cart");
        assertThat(cartItemAddedEvent.getUserId()).isEqualTo(userId.getValue());
        assertThat(cartItemAddedEvent.getShopId()).isNull(); // shopId가 설정되지 않은 상태
        assertThat(cartItemAddedEvent.getMenuId()).isEqualTo(menuId.getValue());
        assertThat(cartItemAddedEvent.getQuantity()).isEqualTo(2);
        assertThat(cartItemAddedEvent.getEventId()).isNotNull();
        assertThat(cartItemAddedEvent.getOccurredAt()).isNotNull();
        assertThat(cartItemAddedEvent.getVersion()).isEqualTo(1);
    }
    
    @Test
    void 가게_검증_포함_아이템_추가_시_CartItemAddedEvent_발행() {
        // Given
        UserId userId = UserId.of("user-1");
        Cart cart = new Cart(userId);
        ShopId shopId = ShopId.of("shop-1");
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));

        // When
        cart.addItem(shopId, menuId, options, 1);

        // Then
        assertThat(cart.hasDomainEvents()).isTrue();
        List<DomainEvent> events = cart.getDomainEvents();
        assertThat(events).hasSize(1);
        
        DomainEvent event = events.get(0);
        assertThat(event).isInstanceOf(CartItemAddedEvent.class);
        
        CartItemAddedEvent cartItemAddedEvent = (CartItemAddedEvent) event;
        assertThat(cartItemAddedEvent.getAggregateId()).isEqualTo(cart.getId().getValue());
        assertThat(cartItemAddedEvent.getUserId()).isEqualTo(userId.getValue());
        assertThat(cartItemAddedEvent.getShopId()).isEqualTo(shopId.getValue());
        assertThat(cartItemAddedEvent.getMenuId()).isEqualTo(menuId.getValue());
        assertThat(cartItemAddedEvent.getQuantity()).isEqualTo(1);
    }
    
    @Test
    void 동일한_메뉴와_옵션_조합_병합_시_CartItemAddedEvent_발행() {
        // Given
        UserId userId = UserId.of("user-1");
        Cart cart = new Cart(userId);
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));

        // When
        cart.addItem(menuId, options, 2);
        cart.addItem(menuId, options, 3);

        // Then
        assertThat(cart.hasDomainEvents()).isTrue();
        List<DomainEvent> events = cart.getDomainEvents();
        assertThat(events).hasSize(2); // 두 번의 addItem 호출로 2개의 이벤트 발행
        
        // 첫 번째 이벤트
        CartItemAddedEvent firstEvent = (CartItemAddedEvent) events.get(0);
        assertThat(firstEvent.getQuantity()).isEqualTo(2);
        
        // 두 번째 이벤트
        CartItemAddedEvent secondEvent = (CartItemAddedEvent) events.get(1);
        assertThat(secondEvent.getQuantity()).isEqualTo(3);
    }
}