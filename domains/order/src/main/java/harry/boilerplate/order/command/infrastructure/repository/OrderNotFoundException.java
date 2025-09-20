package harry.boilerplate.order.command.infrastructure.repository;

import harry.boilerplate.common.exception.ApplicationException;
import harry.boilerplate.common.exception.CommonSystemErrorCode;
import harry.boilerplate.common.exception.ErrorCode;

/**
 * 주문을 찾을 수 없을 때 발생하는 예외
 */
public class OrderNotFoundException extends ApplicationException {
    
    public OrderNotFoundException(String orderId) {
        super("주문을 찾을 수 없습니다: " + orderId, null);
    }
    
    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public ErrorCode getErrorCode() {
        return CommonSystemErrorCode.RESOURCE_NOT_FOUND;
    }
}