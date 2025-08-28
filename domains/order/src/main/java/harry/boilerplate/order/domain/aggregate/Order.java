package harry.boilerplate.order.domain.aggregate;

import harry.boilerplate.common.domain.entity.AggregateRoot;
import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.order.domain.event.OrderPlacedEvent;
import harry.boilerplate.order.domain.entity.CartLineItem;
import harry.boilerplate.order.domain.entity.OrderLineItem;
import harry.boilerplate.order.domain.valueObject.*;
import harry.boilerplate.order.domain.exception.OrderDomainException;
import harry.boilerplate.order.domain.exception.OrderErrorCode;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order 애그리게이트 루트
 * 주문 정보와 주문 라인 아이템들을 관리
 */
@Entity
@Table(name = "order_table")
public class Order extends AggregateRoot<Order, OrderId> {

    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "user_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private String userId;

    @Column(name = "shop_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private String shopId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineItem> orderItems;

    @Column(name = "total_price", precision = 10, scale = 2, nullable = false)
    private java.math.BigDecimal totalPrice;

    @Column(name = "order_time", nullable = false)
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
        
        this.id = OrderId.generate().getValue();
        this.userId = userId.getValue();
        this.shopId = shopId.getValue();
        // 자식과의 양방향 연관관계를 생성자에서 확정 (setter 사용하지 않음)
        this.orderItems = new ArrayList<>();
        for (OrderLineItem item : orderItems) {
            OrderLineItem attached = new OrderLineItem(
                this,
                item.getMenuId(),
                item.getMenuName(),
                item.getSelectedOptions(),
                item.getQuantity(),
                item.getTotalPrice()
            );
            this.orderItems.add(attached);
        }
        this.totalPrice = calculateTotalPrice().getAmount();
        this.orderTime = LocalDateTime.now();
        
        // 도메인 이벤트 발행
        addDomainEvent(new OrderPlacedEvent(
            this.id,
            this.userId,
            this.shopId,
            this.totalPrice
        ));
    }
    
    // 기존 주문 복원 (Repository용)
    public Order(OrderId id, UserId userId, ShopId shopId, List<OrderLineItem> orderItems, 
                Money totalPrice, LocalDateTime orderTime) {
        this.id = id != null ? id.getValue() : null;
        this.userId = userId != null ? userId.getValue() : null;
        this.shopId = shopId != null ? shopId.getValue() : null;
        this.orderItems = new ArrayList<>(orderItems != null ? orderItems : new ArrayList<>());
        this.totalPrice = totalPrice != null ? totalPrice.getAmount() : java.math.BigDecimal.ZERO;
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
            // 실제 구현에서는 Shop Context API를 통해 메뉴 이름과 옵션 정보, 가격을 조회해야 함
            String menuName = "메뉴명"; // TODO: Shop Context API 호출
            List<SelectedOption> selectedOptions = List.of(); // TODO: Shop Context API 호출하여 SelectedOption 생성
            Money unitPrice = Money.of(10000); // TODO: Shop Context API 호출
            
            OrderLineItem orderItem = OrderLineItem.fromCartLineItem(cartItem, menuName, selectedOptions, unitPrice);
            orderItems.add(orderItem);
        }
        
        return new Order(cart.getUserId(), cart.getShopId(), orderItems);
    }
    
    /**
     * 주문 총 가격 계산
     */
    public Money getPrice() {
        return Money.of(totalPrice);
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
        if (userId == null) {
            return false;
        }
        return this.userId.equals(userId.getValue());
    }
    
    /**
     * 특정 가게의 주문인지 확인
     */
    public boolean isFromShop(ShopId shopId) {
        if (shopId == null) {
            return false;
        }
        return this.shopId.equals(shopId.getValue());
    }
    
    @Override
    public OrderId getId() {
        return OrderId.of(id);
    }
    
    public UserId getUserId() {
        return UserId.of(userId);
    }
    
    public ShopId getShopId() {
        return ShopId.of(shopId);
    }
    
    public List<OrderLineItem> getOrderItems() {
        return List.copyOf(orderItems); // 불변 리스트 반환
    }
    
    public Money getTotalPrice() {
        return Money.of(totalPrice);
    }
    
    public LocalDateTime getOrderTime() {
        return orderTime;
    }
}