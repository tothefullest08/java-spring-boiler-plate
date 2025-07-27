package harry.boilerplate.order.domain;

import harry.boilerplate.common.domain.entity.EntityId;

import java.util.UUID;

/**
 * Shop 식별자 ValueObject (Order Context에서 사용)
 */
public class ShopId extends EntityId {
    
    public ShopId() {
        super(UUID.randomUUID().toString());
    }
    
    public ShopId(String value) {
        super(value);
    }
    
    public static ShopId of(String value) {
        return new ShopId(value);
    }
    
    public static ShopId generate() {
        return new ShopId();
    }
}