package harry.boilerplate.shop.domain.event;

import harry.boilerplate.common.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * 메뉴가 공개되었을 때 발행되는 도메인 이벤트
 * Requirements: 2.3 - 메뉴 공개 시 이벤트 발행
 */
public class MenuOpenedEvent implements DomainEvent {
    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final String aggregateId; // menuId
    private final String aggregateType = "Menu";
    private final int version = 1;
    
    // 비즈니스 데이터
    private final String shopId;
    private final String menuName;
    private final String description;
    
    public MenuOpenedEvent(String menuId, String shopId, String menuName, String description) {
        this.aggregateId = menuId;
        this.shopId = shopId;
        this.menuName = menuName;
        this.description = description;
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
    public String getShopId() {
        return shopId;
    }
    
    public String getMenuName() {
        return menuName;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return "MenuOpenedEvent{" +
                "eventId=" + eventId +
                ", occurredAt=" + occurredAt +
                ", aggregateId='" + aggregateId + '\'' +
                ", aggregateType='" + aggregateType + '\'' +
                ", version=" + version +
                ", shopId='" + shopId + '\'' +
                ", menuName='" + menuName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}