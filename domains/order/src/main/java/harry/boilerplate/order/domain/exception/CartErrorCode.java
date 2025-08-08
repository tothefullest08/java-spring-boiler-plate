package harry.boilerplate.order.domain.exception;

import harry.boilerplate.common.exception.ErrorCode;

/**
 * Cart 도메인 에러 코드
 * 형식: CART-DOMAIN-XXX
 */
public enum CartErrorCode implements ErrorCode {
    
    // 장바구니 생성 관련 에러
    USER_ID_REQUIRED("CART-DOMAIN-001", "사용자 ID는 필수입니다"),
    
    // 장바구니 조회 관련 에러
    CART_NOT_FOUND("CART-DOMAIN-002", "장바구니를 찾을 수 없습니다"),
    
    // 아이템 추가 관련 에러
    MENU_ID_REQUIRED("CART-DOMAIN-003", "메뉴 ID는 필수입니다"),
    SHOP_ID_REQUIRED("CART-DOMAIN-004", "가게 ID는 필수입니다"),
    INVALID_QUANTITY("CART-DOMAIN-005", "수량은 1개 이상이어야 합니다"),
    DIFFERENT_SHOP_MENU("CART-DOMAIN-006", "다른 가게의 메뉴는 추가할 수 없습니다"),
    INVALID_MENU_ID("CART-DOMAIN-009", "올바르지 않은 메뉴 ID입니다"),
    
    // 장바구니 상태 관련 에러
    EMPTY_CART("CART-DOMAIN-007", "장바구니가 비어있습니다"),
    CART_ALREADY_ORDERED("CART-DOMAIN-008", "이미 주문 완료된 장바구니입니다");
    
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