package harry.boilerplate.common.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 애그리게이트 루트 기본 클래스
 */
public abstract class AggregateRoot<T extends AggregateRoot<T, ID>, ID> {
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    /**
     * 애그리게이트의 고유 식별자 반환
     */
    public abstract ID getId();
    
    /**
     * 도메인 이벤트 추가
     */
    protected void addDomainEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }
    
    /**
     * 발생한 도메인 이벤트 목록 반환 (읽기 전용)
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
    
    /**
     * 도메인 이벤트 목록 초기화
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}