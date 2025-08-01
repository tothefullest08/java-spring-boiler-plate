package harry.boilerplate.shop.domain.valueobject;

import harry.boilerplate.common.domain.entity.EntityId;

/**
 * Shop 애그리게이트의 고유 식별자
 */
public class ShopId extends EntityId {
    
    public ShopId() {
        super();
    }
    
    public ShopId(String value) {
        super(value);
    }
}