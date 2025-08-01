package harry.boilerplate.shop.domain.aggregate;

import harry.boilerplate.common.exception.DomainException;
import harry.boilerplate.common.exception.ErrorCode;

/**
 * Shop 도메인 예외
 * Shop 애그리게이트의 비즈니스 규칙 위반 시 발생하는 예외
 */
public class ShopDomainException extends DomainException {
    private final ShopErrorCode errorCode;

    public ShopDomainException(ShopErrorCode errorCode) {
        super(errorCode.getMessage(), null);
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

    public ShopErrorCode getShopErrorCode() {
        return errorCode;
    }
}