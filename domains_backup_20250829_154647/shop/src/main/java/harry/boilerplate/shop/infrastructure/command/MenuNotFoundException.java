package harry.boilerplate.shop.infrastructure.command;

import harry.boilerplate.shop.domain.aggregate.MenuDomainException;
import harry.boilerplate.shop.domain.aggregate.MenuErrorCode;
import harry.boilerplate.shop.domain.valueObject.MenuId;

/**
 * Menu를 찾을 수 없을 때 발생하는 예외
 */
public class MenuNotFoundException extends MenuDomainException {

    public MenuNotFoundException(MenuId menuId) {
        super(MenuErrorCode.MENU_NOT_FOUND);
    }
}