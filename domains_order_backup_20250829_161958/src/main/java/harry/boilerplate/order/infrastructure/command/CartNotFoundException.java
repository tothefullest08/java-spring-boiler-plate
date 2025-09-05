package harry.boilerplate.order.infrastructure.command;

import harry.boilerplate.common.exception.ApplicationException;
import harry.boilerplate.common.exception.CommonSystemErrorCode;
import harry.boilerplate.common.exception.ErrorCode;

/**
 * 장바구니를 찾을 수 없을 때 발생하는 예외
 */
public class CartNotFoundException extends ApplicationException {
    
    public CartNotFoundException(String cartId) {
        super("장바구니를 찾을 수 없습니다: " + cartId, null);
    }
    
    public CartNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public ErrorCode getErrorCode() {
        return CommonSystemErrorCode.RESOURCE_NOT_FOUND;
    }
}