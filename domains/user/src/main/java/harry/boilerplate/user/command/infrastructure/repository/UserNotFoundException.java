package harry.boilerplate.user.command.infrastructure.repository;

import harry.boilerplate.common.exception.ApplicationException;
import harry.boilerplate.common.exception.CommonSystemErrorCode;
import harry.boilerplate.common.exception.ErrorCode;

/**
 * 사용자를 찾을 수 없을 때 발생하는 예외
 * Infrastructure 레이어에서 발생하는 애플리케이션 예외
 */
public class UserNotFoundException extends ApplicationException {
    
    private final String userId;
    
    public UserNotFoundException(String userId) {
        super("사용자를 찾을 수 없습니다: " + userId, null);
        this.userId = userId;
    }
    
    public UserNotFoundException(String userId, Throwable cause) {
        super("사용자를 찾을 수 없습니다: " + userId, cause);
        this.userId = userId;
    }
    
    @Override
    public ErrorCode getErrorCode() {
        return CommonSystemErrorCode.RESOURCE_NOT_FOUND;
    }
    
    public String getUserId() {
        return userId;
    }
}