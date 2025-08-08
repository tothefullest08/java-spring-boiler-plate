package harry.boilerplate.order.domain.valueObject;

import harry.boilerplate.common.domain.entity.EntityId;

import java.util.UUID;

/**
 * User의 식별자 (Order Context에서 사용)
 */
public class UserId extends EntityId {
    
    public UserId(String value) {
        super(value);
    }
    
    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }
    
    public static UserId of(String value) {
        return new UserId(value);
    }
}