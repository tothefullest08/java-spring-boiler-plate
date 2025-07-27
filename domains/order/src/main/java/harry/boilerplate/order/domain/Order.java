package harry.boilerplate.order.domain;

import harry.boilerplate.common.domain.entity.AggregateRoot;
import harry.boilerplate.common.domain.entity.Money;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order 애그리게이트 루트
 * 주문 정보와 주문 라인 아이템들을 관리
 */
public class Order extends AggregateRoot<Order, OrderId> {
    
    private OrderId id;
    private UserId userId;
    private ShopId shopId;
    private List<OrderLineItem> orderItems;
    private Money totalPrice;
    private LocalDateTime orderTime;
    
    // 기본 생성자 (JPA용)
    protected Order() {
        this.orderItems = new ArrayList<>();
    }
    
    // 새 주문 생성
    public Order(UserId userId, ShopId shopId, List<OrderLineItem> orderItems) {
        if (userId == null) {
            throw new OrderDomainException(OrderErrorCode.INVALID_USER_ID);
        }
        if (shopId == null) {
            throw new OrderDomainException(OrderErrorCode.INVALID_SHOP_ID);
        }
        if (orderItems == null || orderItems.isEmpty()) {
            throw new OrderDomainException(OrderErrorCode.EMPTY_ORDER_ITEMS);
        }
        
        this.id = OrderId.generate();
        this.userId = userId;
        this.shopId = shopId;
        this.orderItems = new ArrayList<>(orderItems);
        this.totalPrice = calculateTotalPrice();
        this.orderTime = LocalDateTime.now();
        
        // 도메인 이벤트 발행 (향후 구현)
        // addDomainEvent(new OrderPlacedEvent(this.id.getValue(), this.userId.getValue(), this.shopId.getValue(), this.totalPrice));
    }
    
    // 기존 주문 복원 (Repository용)
    public Order(OrderId id, UserId userId, ShopId shopId, List<OrderLineItem> orderItems, 
                Money totalPrice, LocalDateTime orderTime) {
        this.id = id;
        this.userId = userId;
        this.shopId = shopId;
        this.orderItems = new ArrayList<>(orderItems != null ? orderItems : new ArrayList<>());
        this.totalPrice = totalPrice;
        this.orderTime = orderTime;
    }
    
    /**
     * Cart로부터 Order 생성
     */
    public static Order fromCart(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("장바구니는 필수입니다");
        }
        if (cart.isEmpty()) {
            throw new OrderDomainException(OrderErrorCode.EMPTY_ORDER_ITEMS);
        }
        if (cart.getShopId() == null) {
            throw new OrderDomainException(OrderErrorCode.INVALID_SHOP_ID);
        }
        
        // TODO: Shop Context API를 통해 실제 메뉴 정보와 가격을 조회하여 OrderLineItem 생성
        // 현재는 기본 구현만 제공
        List<OrderLineItem> orderItems = new ArrayList<>();
        for (CartLineItem cartItem : cart.getItems()) {
            // 실제 구현에서는 Shop Context API를 통해 메뉴 이름과 옵션 이름, 가격을 조회해야 함
            String menuName = "메뉴명"; // TODO: Shop Context API 호출
            List<String> optionNames = List.of(); // TODO: Shop Context API 호출
            Money unitPrice = Money.of(10000); // TODO: Shop Context API 호출
            
            OrderLineItem orderItem = OrderLineItem.fromCartLineItem(cartItem, menuName, optionNames, unitPrice);
            orderItems.add(orderItem);
        }
        
        return new Order(cart.getUserId(), cart.getShopId(), orderItems);
    }
    
    /**
     * 주문 총 가격 계산
     */
    public Money getPrice() {
        return totalPrice;
    }
    
    /**
     * 주문 총 가격 재계산
     */
    private Money calculateTotalPrice() {
        return orderItems.stream()
            .map(OrderLineItem::getTotalPrice)
            .reduce(Money.zero(), Money::add);
    }
    
    /**
     * 주문 아이템 개수
     */
    public int getItemCount() {
        return orderItems.size();
    }
    
    /**
     * 주문 총 수량
     */
    public int getTotalQuantity() {
        return orderItems.stream()
            .mapToInt(OrderLineItem::getQuantity)
            .sum();
    }
    
    /**
     * 특정 사용자의 주문인지 확인
     */
    public boolean belongsToUser(UserId userId) {
        return this.userId.equals(userId);
    }
    
    /**
     * 특정 가게의 주문인지 확인
     */
    public boolean isFromShop(ShopId shopId) {
        return this.shopId.equals(shopId);
    }
    
    @Override
    public OrderId getId() {
        return id;
    }
    
    public UserId getUserId() {
        return userId;
    }
    
    public ShopId getShopId() {
        return shopId;
    }
    
    public List<OrderLineItem> getOrderItems() {
        return List.copyOf(orderItems); // 불변 리스트 반환
    }
    
    public Money getTotalPrice() {
        return totalPrice;
    }
    
    public LocalDateTime getOrderTime() {
        return orderTime;
    }
}