package harry.boilerplate.user.command.domain.event;

import harry.boilerplate.common.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * 사용자 등록 도메인 이벤트
 * 새로운 사용자가 등록되었을 때 발행되는 이벤트
 */
public class UserRegisteredEvent implements DomainEvent {
    
    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final String aggregateId;
    private final String aggregateType = "User";
    private final int version = 1;
    
    // 비즈니스 데이터
    private final String userName;
    private final String email;
    
    public UserRegisteredEvent(String userId, String userName, String email) {
        this.aggregateId = userId;
        this.userName = userName;
        this.email = email;
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
    
    public String getUserName() {
        return userName;
    }
    
    public String getEmail() {
        return email;
    }
    
    @Override
    public String toString() {
        return "UserRegisteredEvent{" +
                "eventId=" + eventId +
                ", occurredAt=" + occurredAt +
                ", aggregateId='" + aggregateId + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}