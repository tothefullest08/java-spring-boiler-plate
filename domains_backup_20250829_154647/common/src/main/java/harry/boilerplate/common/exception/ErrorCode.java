package harry.boilerplate.common.exception;

/**
 * 에러 코드 인터페이스
 * 형식: {DOMAIN}-{LAYER}-{CODE}
 * 예시: SHOP-DOMAIN-001, ORDER-APP-002
 */
public interface ErrorCode {
    /**
     * 에러 코드 반환
     */
    String getCode();
    
    /**
     * 에러 메시지 반환
     */
    String getMessage();
}