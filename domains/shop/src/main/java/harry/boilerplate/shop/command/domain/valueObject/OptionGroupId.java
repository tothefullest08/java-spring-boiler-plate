package harry.boilerplate.shop.command.domain.valueObject;

import harry.boilerplate.common.domain.entity.EntityId;

import java.util.UUID;

/**
 * OptionGroup의 식별자
 */
public class OptionGroupId extends EntityId {
    
    public OptionGroupId(String value) {
        super(value);
    }
    
    public static OptionGroupId generate() {
        return new OptionGroupId(UUID.randomUUID().toString());
    }
    
    public static OptionGroupId of(String value) {
        return new OptionGroupId(value);
    }
}