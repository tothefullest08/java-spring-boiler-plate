package harry.boilerplate.shop.command.infrastructure.repository;

import harry.boilerplate.shop.command.domain.exception.MenuDomainException;
import harry.boilerplate.shop.command.domain.exception.MenuErrorCode;
import harry.boilerplate.shop.command.domain.valueObject.MenuId;

/**
 * Menu를 찾을 수 없을 때 발생하는 예외
 */
public class MenuNotFoundException extends MenuDomainException {

    public MenuNotFoundException(MenuId menuId) {
        super(MenuErrorCode.MENU_NOT_FOUND);
    }
}