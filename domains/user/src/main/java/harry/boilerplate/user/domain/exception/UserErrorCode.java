package harry.boilerplate.user.domain.exception;

import harry.boilerplate.common.exception.ErrorCode;

/**
 * User Context 도메인 에러 코드
 * 형식: USER-DOMAIN-XXX
 */
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND("USER-DOMAIN-001", "사용자를 찾을 수 없습니다"),
    INVALID_USER_ID("USER-DOMAIN-002", "잘못된 사용자 ID입니다"),
    INVALID_EMAIL_FORMAT("USER-DOMAIN-003", "잘못된 이메일 형식입니다"),
    DUPLICATE_EMAIL("USER-DOMAIN-004", "이미 존재하는 이메일입니다"),
    INVALID_USER_NAME("USER-DOMAIN-005", "잘못된 사용자 이름입니다");
    
    private final String code;
    private final String message;
    
    UserErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    @Override
    public String getCode() {
        return code;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
}