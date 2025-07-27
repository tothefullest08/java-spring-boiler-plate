package harry.boilerplate.order.domain;

import harry.boilerplate.common.domain.entity.EntityId;

import java.util.UUID;

/**
 * User 식별자 ValueObject (Order Context에서 사용)
 */
public class UserId extends EntityId {
    
    public UserId() {
        super(UUID.randomUUID().toString());
    }
    
    public UserId(String value) {
        super(value);
    }
    
    public static UserId of(String value) {
        return new UserId(value);
    }
    
    public static UserId generate() {
        return new UserId();
    }
}