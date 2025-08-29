package harry.boilerplate.shop.command.domain.exception;

import harry.boilerplate.common.exception.DomainException;
import harry.boilerplate.common.exception.ErrorCode;

/**
 * Menu 도메인 예외
 */
public class MenuDomainException extends DomainException {
    
    private final MenuErrorCode errorCode;
    
    public MenuDomainException(MenuErrorCode errorCode) {
        super(errorCode.getMessage(), null);
        this.errorCode = errorCode;
    }
    
    public MenuDomainException(MenuErrorCode errorCode, String additionalMessage) {
        super(errorCode.getMessage() + ": " + additionalMessage, null);
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
}