package harry.boilerplate.order.domain.valueObject;

import harry.boilerplate.common.domain.entity.EntityId;

import java.util.UUID;

/**
 * Cart 애그리게이트의 식별자
 */
public class CartId extends EntityId {
    
    public CartId(String value) {
        super(value);
    }
    
    public static CartId generate() {
        return new CartId(UUID.randomUUID().toString());
    }
    
    public static CartId of(String value) {
        return new CartId(value);
    }
}