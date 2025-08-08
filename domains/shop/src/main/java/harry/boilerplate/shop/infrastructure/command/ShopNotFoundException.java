package harry.boilerplate.shop.infrastructure.command;

import harry.boilerplate.shop.domain.aggregate.ShopDomainException;
import harry.boilerplate.shop.domain.aggregate.ShopErrorCode;
import harry.boilerplate.shop.domain.valueObject.ShopId;

/**
 * Shop을 찾을 수 없을 때 발생하는 예외
 */
public class ShopNotFoundException extends ShopDomainException {
    
    public ShopNotFoundException(ShopId shopId) {
        super(ShopErrorCode.SHOP_NOT_FOUND);
    }
}