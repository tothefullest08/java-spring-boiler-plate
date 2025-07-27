package harry.boilerplate.order.domain;

import harry.boilerplate.common.domain.entity.EntityId;

import java.util.UUID;

/**
 * Cart 애그리게이트의 식별자 ValueObject
 */
public class CartId extends EntityId {
    
    public CartId() {
        super(UUID.randomUUID().toString());
    }
    
    public CartId(String value) {
        super(value);
    }
    
    public static CartId of(String value) {
        return new CartId(value);
    }
    
    public static CartId generate() {
        return new CartId();
    }
}