package harry.boilerplate.user.domain.exception;

import harry.boilerplate.common.exception.DomainException;
import harry.boilerplate.common.exception.ErrorCode;

/**
 * User Context 도메인 예외
 * 사용자 도메인 비즈니스 규칙 위반 시 발생하는 예외
 */
public class UserDomainException extends DomainException {
    
    private final UserErrorCode errorCode;
    
    public UserDomainException(UserErrorCode errorCode) {
        super(errorCode.getMessage(), null);
        this.errorCode = errorCode;
    }
    
    public UserDomainException(UserErrorCode errorCode, String additionalMessage) {
        super(errorCode.getMessage() + ": " + additionalMessage, null);
        this.errorCode = errorCode;
    }
    
    public UserDomainException(UserErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
    
    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}