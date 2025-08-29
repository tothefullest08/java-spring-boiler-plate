package harry.boilerplate.shop.domain.valueObject;

import harry.boilerplate.common.domain.entity.EntityId;

import java.util.UUID;

/**
 * Shop 애그리게이트의 식별자
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