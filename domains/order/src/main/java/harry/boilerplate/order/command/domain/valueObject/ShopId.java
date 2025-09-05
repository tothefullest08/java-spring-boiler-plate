package harry.boilerplate.order.command.domain.valueObject;

import harry.boilerplate.common.domain.entity.EntityId;

import java.util.UUID;

/**
 * Shop의 식별자 (Order Context에서 사용)
 */
public class ShopId extends EntityId {
    
    public ShopId(String value) {
        super(value);
    }
    
    public static ShopId generate() {
        return new ShopId(UUID.randomUUID().toString());
    }
    
    public static ShopId of(String value) {
        return new ShopId(value);
    }
}