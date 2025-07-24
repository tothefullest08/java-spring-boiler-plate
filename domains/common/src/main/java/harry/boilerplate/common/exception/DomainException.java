package harry.boilerplate.common.exception;

/**
 * 도메인 예외 기본 클래스
 */
public abstract class DomainException extends RuntimeException {
    
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 에러 코드 반환
     */
    public abstract ErrorCode getErrorCode();
}