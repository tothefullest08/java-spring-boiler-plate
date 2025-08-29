package harry.boilerplate.common.domain.entity;

import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

import harry.boilerplate.common.domain.event.DomainEvent;

/**
 * DDD 애그리게이트 루트 기본 클래스
 * 모든 애그리게이트 루트는 이 클래스를 상속해야 함
 * 
 * @param <T> 애그리게이트 루트 타입
 * @param <ID> 애그리게이트 루트 ID 타입
 */
public abstract class AggregateRoot<T extends AggregateRoot<T, ID>, ID> extends BaseEntity {
    
    @Transient
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    /**
     * 애그리게이트 루트의 고유 식별자 반환
     */
    public abstract ID getId();
    
    /**
     * 도메인 이벤트 추가
     */
    protected void addDomainEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }
    
    /**
     * 모든 도메인 이벤트 조회
     */
    public List<DomainEvent> getDomainEvents() {
        return new ArrayList<>(domainEvents);
    }
    
    /**
     * 도메인 이벤트 목록 초기화
     * 이벤트 발행 후 호출되어야 함
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
    
    /**
     * 도메인 이벤트 발생 여부 확인
     */
    public boolean hasDomainEvents() {
        return !domainEvents.isEmpty();
    }
}