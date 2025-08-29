package harry.boilerplate.shop.command.infrastructure.repository;

import harry.boilerplate.shop.command.domain.exception.ShopDomainException;
import harry.boilerplate.shop.command.domain.exception.ShopErrorCode;
import harry.boilerplate.shop.command.domain.valueObject.ShopId;

/**
 * Shop을 찾을 수 없을 때 발생하는 예외
 */
public class ShopNotFoundException extends ShopDomainException {
    
    public ShopNotFoundException(ShopId shopId) {
        super(ShopErrorCode.SHOP_NOT_FOUND);
    }
}