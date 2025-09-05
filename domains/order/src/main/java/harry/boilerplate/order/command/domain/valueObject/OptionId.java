package harry.boilerplate.order.command.domain.valueObject;

import harry.boilerplate.common.domain.entity.EntityId;

import java.util.UUID;

/**
 * Option의 식별자 (Order Context에서 사용)
 */
public class OptionId extends EntityId {
    
    public OptionId(String value) {
        super(value);
    }
    
    public static OptionId generate() {
        return new OptionId(UUID.randomUUID().toString());
    }
    
    public static OptionId of(String value) {
        return new OptionId(value);
    }
}