package harry.boilerplate.common.domain.entity;

import java.util.Objects;

/**
 * 값 객체(Value Object) 기본 클래스
 * 불변성과 동등성 비교를 제공
 */
public abstract class ValueObject {
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return equalsByValue(obj);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getEqualityComponents());
    }
    
    /**
     * 값 기반 동등성 비교
     * 하위 클래스에서 구현해야 함
     */
    protected abstract boolean equalsByValue(Object other);
    
    /**
     * 동등성 비교에 사용할 컴포넌트들 반환
     * 하위 클래스에서 구현해야 함
     */
    protected abstract Object[] getEqualityComponents();
}