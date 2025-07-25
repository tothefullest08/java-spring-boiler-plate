package harry.boilerplate.shop.domain;

import harry.boilerplate.common.exception.ErrorCode;

/**
 * Shop 도메인 에러 코드
 * 형식: SHOP-DOMAIN-{CODE}
 */
public enum ShopErrorCode implements ErrorCode {
    SHOP_NOT_FOUND("SHOP-DOMAIN-001", "가게를 찾을 수 없습니다"),
    SHOP_NOT_OPEN("SHOP-DOMAIN-002", "가게가 영업 중이 아닙니다"),
    INVALID_OPERATING_HOURS("SHOP-DOMAIN-003", "잘못된 영업시간입니다"),
    INVALID_MIN_ORDER_AMOUNT("SHOP-DOMAIN-004", "잘못된 최소 주문금액입니다"),
    SHOP_NAME_REQUIRED("SHOP-DOMAIN-005", "가게 이름은 필수입니다"),
    CLOSE_REASON_REQUIRED("SHOP-DOMAIN-006", "영업 종료 사유는 필수입니다");

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