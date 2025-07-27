package harry.boilerplate.order.domain;

import harry.boilerplate.common.exception.ErrorCode;

/**
 * Order 도메인 에러 코드
 */
public enum OrderErrorCode implements ErrorCode {
    
    ORDER_NOT_FOUND("ORDER-DOMAIN-001", "주문을 찾을 수 없습니다"),
    EMPTY_ORDER_ITEMS("ORDER-DOMAIN-002", "주문 아이템이 비어있습니다"),
    INVALID_ORDER_AMOUNT("ORDER-DOMAIN-003", "유효하지 않은 주문 금액입니다"),
    ORDER_ALREADY_PLACED("ORDER-DOMAIN-004", "이미 주문이 완료되었습니다"),
    INVALID_USER_ID("ORDER-DOMAIN-005", "유효하지 않은 사용자 ID입니다"),
    INVALID_SHOP_ID("ORDER-DOMAIN-006", "유효하지 않은 가게 ID입니다"),
    ORDER_CANNOT_BE_MODIFIED("ORDER-DOMAIN-007", "주문을 수정할 수 없습니다");
    
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