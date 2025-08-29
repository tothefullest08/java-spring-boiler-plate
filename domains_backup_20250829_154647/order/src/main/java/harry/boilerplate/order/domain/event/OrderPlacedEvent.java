package harry.boilerplate.order.domain.event;

import harry.boilerplate.common.domain.event.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * 주문 완료 도메인 이벤트
 * Order 애그리게이트에서 주문이 완료되었을 때 발행되는 이벤트
 */
public class OrderPlacedEvent implements DomainEvent {
    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final String aggregateId;
    private final String aggregateType = "Order";
    private final int version = 1;

    // 비즈니스 데이터
    private final String userId;
    private final String shopId;
    private final BigDecimal totalAmount;

    public OrderPlacedEvent(String orderId, String userId, String shopId, BigDecimal totalAmount) {
        this.aggregateId = orderId;
        this.userId = userId;
        this.shopId = shopId;
        this.totalAmount = totalAmount;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}