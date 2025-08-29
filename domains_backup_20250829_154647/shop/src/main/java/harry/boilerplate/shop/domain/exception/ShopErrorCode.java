package harry.boilerplate.shop.domain.exception;

import harry.boilerplate.common.exception.ErrorCode;

/**
 * Shop 도메인 에러 코드
 * 형식: SHOP-DOMAIN-XXX
 */
public enum ShopErrorCode implements ErrorCode {
    
    // 가게 생성 관련 에러
    SHOP_NAME_REQUIRED("SHOP-DOMAIN-001", "가게 이름은 필수입니다"),
    MIN_ORDER_AMOUNT_REQUIRED("SHOP-DOMAIN-002", "최소 주문 금액은 필수입니다"),
    INVALID_MIN_ORDER_AMOUNT("SHOP-DOMAIN-003", "최소 주문 금액은 0원 이상이어야 합니다"),
    BUSINESS_HOURS_REQUIRED("SHOP-DOMAIN-004", "영업시간 정보는 필수입니다"),
    
    // 가게 조회 관련 에러
    SHOP_NOT_FOUND("SHOP-DOMAIN-005", "가게를 찾을 수 없습니다"),
    
    // 가게 운영 관련 에러
    SHOP_CLOSED("SHOP-DOMAIN-006", "현재 영업시간이 아닙니다"),
    INVALID_OPERATING_HOURS("SHOP-DOMAIN-007", "올바르지 않은 영업시간입니다"),
    CLOSE_REASON_REQUIRED("SHOP-DOMAIN-008", "가게 폐점 사유는 필수입니다");
    
    private final String code;
    private final String message;
    
    ShopErrorCode(String code, String message) {
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