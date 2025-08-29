package harry.boilerplate.common.domain.entity;

import java.util.Objects;
import java.util.UUID;

/**
 * 엔티티 ID를 위한 기본 값 객체
 * UUID 기반 ID 생성을 지원
 */
public abstract class EntityId extends ValueObject {
    
    private final String value;
    
    protected EntityId() {
        this.value = UUID.randomUUID().toString();
    }
    
    protected EntityId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("ID value cannot be null or empty");
        }
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    protected boolean equalsByValue(Object other) {
        if (this.getClass() != other.getClass()) return false;
        EntityId that = (EntityId) other;
        return Objects.equals(this.value, that.value);
    }
    
    @Override
    protected Object[] getEqualityComponents() {
        return new Object[]{value};
    }
    
    @Override
    public String toString() {
        return value;
    }
}