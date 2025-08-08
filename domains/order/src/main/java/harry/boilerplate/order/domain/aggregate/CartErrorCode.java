package harry.boilerplate.order.domain.aggregate;

import harry.boilerplate.common.exception.ErrorCode;

/**
 * Cart 도메인 에러 코드
 */
public enum CartErrorCode implements ErrorCode {
    
    CART_NOT_FOUND("CART-DOMAIN-001", "장바구니를 찾을 수 없습니다"),
    DIFFERENT_SHOP_MENU("CART-DOMAIN-002", "다른 가게의 메뉴는 추가할 수 없습니다"),
    EMPTY_CART("CART-DOMAIN-003", "장바구니가 비어있습니다"),
    MINIMUM_ORDER_AMOUNT_NOT_MET("CART-DOMAIN-004", "최소 주문금액을 충족하지 않습니다"),
    INVALID_MENU_ID("CART-DOMAIN-005", "유효하지 않은 메뉴 ID입니다"),
    INVALID_QUANTITY("CART-DOMAIN-006", "수량은 1개 이상이어야 합니다"),
    SHOP_NOT_OPEN("CART-DOMAIN-007", "가게가 영업 중이 아닙니다");
    
    private final String code;
    private final String message;
    
    CartErrorCode(String code, String message) {
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