package harry.boilerplate.order.domain;

import harry.boilerplate.common.domain.entity.EntityId;

import java.util.UUID;

/**
 * Option 식별자 ValueObject (Order Context에서 사용)
 */
public class OptionId extends EntityId {
    
    public OptionId() {
        super(UUID.randomUUID().toString());
    }
    
    public OptionId(String value) {
        super(value);
    }
    
    public static OptionId of(String value) {
        return new OptionId(value);
    }
    
    public static OptionId generate() {
        return new OptionId();
    }
}