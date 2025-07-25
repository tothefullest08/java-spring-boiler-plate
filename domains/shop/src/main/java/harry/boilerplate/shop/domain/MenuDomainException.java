package harry.boilerplate.shop.domain;

import harry.boilerplate.common.exception.DomainException;
import harry.boilerplate.common.exception.ErrorCode;

/**
 * Menu 도메인 예외
 * Menu 애그리게이트의 비즈니스 규칙 위반 시 발생하는 예외
 */
public class MenuDomainException extends DomainException {
    private final MenuErrorCode errorCode;

    public MenuDomainException(MenuErrorCode errorCode) {
        super(errorCode.getMessage(), null);
        this.errorCode = errorCode;
    }

    public MenuDomainException(MenuErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public MenuErrorCode getMenuErrorCode() {
        return errorCode;
    }
}