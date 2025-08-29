package harry.boilerplate.shop.domain.aggregate;

import harry.boilerplate.common.exception.ErrorCode;

/**
 * Menu 도메인 에러 코드
 * 형식: MENU-DOMAIN-{CODE}
 */
public enum MenuErrorCode implements ErrorCode {
    MENU_NOT_FOUND("MENU-DOMAIN-001", "메뉴를 찾을 수 없습니다"),
    MENU_ALREADY_OPEN("MENU-DOMAIN-002", "이미 공개된 메뉴입니다"),
    INSUFFICIENT_OPTION_GROUPS("MENU-DOMAIN-003", "옵션그룹이 부족합니다. 최소 1개 이상 필요합니다"),
    INVALID_REQUIRED_OPTION_GROUP_COUNT("MENU-DOMAIN-004", "필수 옵션그룹은 1~3개 범위여야 합니다"),
    NO_PAID_OPTION_GROUP("MENU-DOMAIN-005", "유료 옵션그룹이 최소 1개 필요합니다"),
    DUPLICATE_OPTION_GROUP_NAME("MENU-DOMAIN-006", "중복된 옵션그룹 이름입니다"),
    MAX_REQUIRED_OPTION_GROUPS_EXCEEDED("MENU-DOMAIN-007", "필수 옵션그룹 최대 개수(3개)를 초과했습니다"),
    OPTION_GROUP_NOT_FOUND("MENU-DOMAIN-008", "옵션그룹을 찾을 수 없습니다"),
    OPTION_NOT_FOUND("MENU-DOMAIN-009", "옵션을 찾을 수 없습니다"),
    CANNOT_DELETE_REQUIRED_OPTION_GROUP("MENU-DOMAIN-010", "필수 조건을 위반하여 옵션그룹을 삭제할 수 없습니다"),
    MENU_NAME_REQUIRED("MENU-DOMAIN-011", "메뉴 이름은 필수입니다"),
    SHOP_ID_REQUIRED("MENU-DOMAIN-012", "가게 ID는 필수입니다"),
    BASE_PRICE_REQUIRED("MENU-DOMAIN-013", "기본 가격은 필수입니다"),
    INVALID_BASE_PRICE("MENU-DOMAIN-014", "기본 가격은 0 이상이어야 합니다"),
    OPTION_GROUP_REQUIRED("MENU-DOMAIN-015", "옵션그룹은 필수입니다"),
    OPTION_GROUP_ID_REQUIRED("MENU-DOMAIN-016", "옵션그룹 ID는 필수입니다"),
    NEW_OPTION_GROUP_NAME_REQUIRED("MENU-DOMAIN-017", "새로운 옵션그룹 이름은 필수입니다"),
    CURRENT_OPTION_NAME_REQUIRED("MENU-DOMAIN-018", "현재 옵션 이름은 필수입니다"),
    CURRENT_OPTION_PRICE_REQUIRED("MENU-DOMAIN-019", "현재 옵션 가격은 필수입니다"),
    NEW_OPTION_NAME_REQUIRED("MENU-DOMAIN-020", "새로운 옵션 이름은 필수입니다"),
    MENU_REQUIRED("MENU-DOMAIN-021", "메뉴는 필수입니다");

    private final String code;
    private final String message;

    MenuErrorCode(String code, String message) {
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