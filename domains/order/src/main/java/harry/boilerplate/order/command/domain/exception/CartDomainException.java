package harry.boilerplate.order.command.domain.exception;

import harry.boilerplate.common.exception.DomainException;
import harry.boilerplate.common.exception.ErrorCode;

/**
 * Cart 도메인 예외
 */
public class CartDomainException extends DomainException {
    
    private final CartErrorCode errorCode;
    
    public CartDomainException(CartErrorCode errorCode) {
        super(errorCode.getMessage(), null);
        this.errorCode = errorCode;
    }
    
    public CartDomainException(CartErrorCode errorCode, String additionalMessage) {
        super(errorCode.getMessage() + ": " + additionalMessage, null);
        this.errorCode = errorCode;
    }
    
    public CartDomainException(CartErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
    
    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}