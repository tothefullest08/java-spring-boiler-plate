package harry.boilerplate.shop.domain.exception;

import harry.boilerplate.common.exception.DomainException;
import harry.boilerplate.common.exception.ErrorCode;

/**
 * Shop 도메인 예외
 */
public class ShopDomainException extends DomainException {
    
    private final ShopErrorCode errorCode;
    
    public ShopDomainException(ShopErrorCode errorCode) {
        super(errorCode.getMessage(), null);
        this.errorCode = errorCode;
    }
    
    public ShopDomainException(ShopErrorCode errorCode, String additionalMessage) {
        super(errorCode.getMessage() + ": " + additionalMessage, null);
        this.errorCode = errorCode;
    }
    
    public ShopDomainException(ShopErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
    
    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}