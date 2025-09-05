package harry.boilerplate.order.domain.event;

import harry.boilerplate.common.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * 장바구니 아이템 추가 도메인 이벤트
 * Cart 애그리게이트에서 아이템이 추가되었을 때 발행되는 이벤트
 */
public class CartItemAddedEvent implements DomainEvent {
    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final String aggregateId;
    private final String aggregateType = "Cart";
    private final int version = 1;
    
    // 비즈니스 데이터
    private final String userId;
    private final String shopId;
    private final String menuId;
    private final int quantity;
    
    public CartItemAddedEvent(String cartId, String userId, String shopId, String menuId, int quantity) {
        this.aggregateId = cartId;
        this.userId = userId;
        this.shopId = shopId;
        this.menuId = menuId;
        this.quantity = quantity;
    }
    
    @Override
    public UUID getEventId() {
        return eventId;
    }
    
    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }
    
    @Override
    public String getAggregateId() {
        return aggregateId;
    }
    
    @Override
    public String getAggregateType() {
        return aggregateType;
    }
    
    @Override
    public int getVersion() {
        return version;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getShopId() {
        return shopId;
    }
    
    public String getMenuId() {
        return menuId;
    }
    
    public int getQuantity() {
        return quantity;
    }
}