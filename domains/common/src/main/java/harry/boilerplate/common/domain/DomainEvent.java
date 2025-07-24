package harry.boilerplate.common.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * 모든 도메인 이벤트가 구현해야 하는 기본 인터페이스
 */
public interface DomainEvent {
    /**
     * 이벤트 고유 식별자
     */
    UUID getEventId();
    
    /**
     * 이벤트 발생 시점
     */
    Instant getOccurredAt();
    
    /**
     * 이벤트를 발생시킨 애그리게이트의 ID
     */
    String getAggregateId();
    
    /**
     * 이벤트를 발생시킨 애그리게이트의 타입
     */
    String getAggregateType();
    
    /**
     * 이벤트 버전
     */
    int getVersion();
}