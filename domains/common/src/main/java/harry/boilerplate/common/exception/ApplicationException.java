package harry.boilerplate.common.exception;

/**
 * 애플리케이션 예외 기본 클래스
 */
public abstract class ApplicationException extends RuntimeException {
    
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ApplicationException(String message) {
        super(message);
    }
    
    /**
     * 에러 코드 반환
     */
    public abstract ErrorCode getErrorCode();
}