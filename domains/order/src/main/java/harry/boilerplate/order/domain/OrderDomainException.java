package harry.boilerplate.order.domain;

import harry.boilerplate.common.exception.DomainException;
import harry.boilerplate.common.exception.ErrorCode;

/**
 * Order 도메인 예외
 */
public class OrderDomainException extends DomainException {
    
    private final OrderErrorCode errorCode;
    
    public OrderDomainException(OrderErrorCode errorCode) {
        super(errorCode.getMessage(), null);
        this.errorCode = errorCode;
    }
    
    public OrderDomainException(OrderErrorCode errorCode, String additionalMessage) {
        super(errorCode.getMessage() + ": " + additionalMessage, null);
        this.errorCode = errorCode;
    }
    
    public OrderDomainException(OrderErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
    
    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}