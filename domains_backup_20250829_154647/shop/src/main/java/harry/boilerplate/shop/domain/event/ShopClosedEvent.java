package harry.boilerplate.shop.domain.event;

import harry.boilerplate.common.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * 가게가 영업을 종료했을 때 발행되는 도메인 이벤트
 * Requirements: 1.5 - 가게 영업 상태 변경 시 이벤트 발행
 */
public class ShopClosedEvent implements DomainEvent {
    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final String aggregateId; // shopId
    private final String aggregateType = "Shop";
    private final int version = 1;
    
    // 비즈니스 데이터
    private final String shopName;
    private final String reason;
    
    public ShopClosedEvent(String shopId, String shopName, String reason) {
        this.aggregateId = shopId;
        this.shopName = shopName;
        this.reason = reason;
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
    
    // 비즈니스 데이터 접근자
    public String getShopName() {
        return shopName;
    }
    
    public String getReason() {
        return reason;
    }
    
    @Override
    public String toString() {
        return "ShopClosedEvent{" +
                "eventId=" + eventId +
                ", occurredAt=" + occurredAt +
                ", aggregateId='" + aggregateId + '\'' +
                ", aggregateType='" + aggregateType + '\'' +
                ", version=" + version +
                ", shopName='" + shopName + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}