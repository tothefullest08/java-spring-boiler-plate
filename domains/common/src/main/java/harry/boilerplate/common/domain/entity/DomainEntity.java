package harry.boilerplate.common.domain.entity;

/**
 * 도메인 엔티티 기본 클래스
 * 애그리게이트 내부에서 사용되는 엔티티들의 기본 클래스
 * BaseEntity를 상속하여 공통 필드(created_at, updated_at)를 포함
 */
public abstract class DomainEntity<T extends DomainEntity<T, TID>, TID> extends BaseEntity {
    
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        
        if (!(other instanceof DomainEntity)) {
            return false;
        }
        
        @SuppressWarnings("unchecked")
        T typedOther = (T) other;
        return equals(typedOther);
    }
    
    public boolean equals(T other) {
        if (other == null) {
            return false;
        }
        
        if (getId() == null) {
            return false;
        }
        
        if (other.getClass().equals(getClass())) {
            return getId().equals(other.getId());
        }
        
        return super.equals(other);
    }
    
    @Override
    public int hashCode() {
        return getId() == null ? 0 : getId().hashCode();
    }
    
    /**
     * 엔티티의 고유 식별자 반환
     */
    public abstract TID getId();
}