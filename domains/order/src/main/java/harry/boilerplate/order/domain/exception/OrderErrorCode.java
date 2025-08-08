package harry.boilerplate.order.domain.exception;

import harry.boilerplate.common.exception.ErrorCode;

/**
 * Order 도메인 에러 코드
 * 형식: ORDER-DOMAIN-XXX
 */
public enum OrderErrorCode implements ErrorCode {
    
    // 주문 생성 관련 에러
    USER_ID_REQUIRED("ORDER-DOMAIN-001", "사용자 ID는 필수입니다"),
    SHOP_ID_REQUIRED("ORDER-DOMAIN-002", "가게 ID는 필수입니다"),
    EMPTY_ORDER_ITEMS("ORDER-DOMAIN-003", "주문 항목이 비어있습니다"),
    INVALID_TOTAL_PRICE("ORDER-DOMAIN-004", "총 주문 금액이 올바르지 않습니다"),
    INVALID_USER_ID("ORDER-DOMAIN-008", "올바르지 않은 사용자 ID입니다"),
    INVALID_SHOP_ID("ORDER-DOMAIN-009", "올바르지 않은 가게 ID입니다"),
    
    // 주문 조회 관련 에러
    ORDER_NOT_FOUND("ORDER-DOMAIN-005", "주문을 찾을 수 없습니다"),
    
    // 주문 상태 관련 에러
    ORDER_ALREADY_PLACED("ORDER-DOMAIN-006", "이미 주문이 완료되었습니다"),
    INVALID_ORDER_STATUS("ORDER-DOMAIN-007", "올바르지 않은 주문 상태입니다");
    
    private final String code;
    private final String message;
    
    OrderErrorCode(String code, String message) {
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