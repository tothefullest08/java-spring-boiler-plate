package harry.boilerplate.shop.command.domain.exception;

import harry.boilerplate.common.exception.ErrorCode;

/**
 * Menu 도메인 에러 코드
 * 형식: MENU-DOMAIN-XXX
 */
public enum MenuErrorCode implements ErrorCode {

    // 메뉴 생성 관련 에러
    SHOP_ID_REQUIRED("MENU-DOMAIN-001", "가게 ID는 필수입니다"),
    MENU_NAME_REQUIRED("MENU-DOMAIN-002", "메뉴 이름은 필수입니다"),
    BASE_PRICE_REQUIRED("MENU-DOMAIN-003", "기본 가격은 필수입니다"),
    INVALID_BASE_PRICE("MENU-DOMAIN-004", "기본 가격은 0원 이상이어야 합니다"),

    // 메뉴 조회 관련 에러
    MENU_NOT_FOUND("MENU-DOMAIN-005", "메뉴를 찾을 수 없습니다"),

    // 메뉴 공개 관련 에러
    MENU_ALREADY_OPEN("MENU-DOMAIN-006", "이미 공개된 메뉴입니다"),
    INSUFFICIENT_OPTION_GROUPS("MENU-DOMAIN-007", "메뉴 공개를 위해서는 최소 1개의 옵션그룹이 필요합니다"),
    INVALID_REQUIRED_OPTION_GROUP_COUNT("MENU-DOMAIN-008", "필수 옵션그룹은 최대 3개까지만 허용됩니다"),
    NO_PAID_OPTION_GROUP("MENU-DOMAIN-009", "최소 1개의 유료 옵션그룹이 필요합니다"),

    // 옵션그룹 관리 관련 에러
    NEW_OPTION_GROUP_NAME_REQUIRED("MENU-DOMAIN-010", "새로운 옵션그룹 이름은 필수입니다"),
    OPTION_GROUP_NOT_FOUND("MENU-DOMAIN-011", "옵션그룹을 찾을 수 없습니다"),
    DUPLICATE_OPTION_GROUP_NAME("MENU-DOMAIN-012", "이미 존재하는 옵션그룹 이름입니다"),
    MAX_REQUIRED_OPTION_GROUPS_EXCEEDED("MENU-DOMAIN-013", "필수 옵션그룹은 최대 3개까지만 허용됩니다"),
    OPTION_GROUP_ID_REQUIRED("MENU-DOMAIN-014", "옵션그룹 ID는 필수입니다"),
    CURRENT_OPTION_NAME_REQUIRED("MENU-DOMAIN-015", "현재 옵션 이름은 필수입니다"),
    CURRENT_OPTION_PRICE_REQUIRED("MENU-DOMAIN-016", "현재 옵션 가격은 필수입니다"),
    NEW_OPTION_NAME_REQUIRED("MENU-DOMAIN-017", "새로운 옵션 이름은 필수입니다"),
    NEW_OPTION_PRICE_REQUIRED("MENU-DOMAIN-018", "새로운 옵션 가격은 필수입니다"),
    CANNOT_DELETE_REQUIRED_OPTION_GROUP("MENU-DOMAIN-019", "필수 옵션그룹은 삭제할 수 없습니다"),
    MENU_REQUIRED("MENU-DOMAIN-20", "메뉴는 필수입니다"),
    OPTION_GROUP_REQUIRED("MENU-DOMAIN-021", "옵션그룹은 필수입니다"),
    OPTION_NOT_FOUND("MENU-DOMAIN-022", "옵션을 찾을 수 없습니다");
 
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