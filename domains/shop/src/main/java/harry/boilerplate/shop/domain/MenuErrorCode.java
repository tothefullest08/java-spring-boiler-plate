package harry.boilerplate.shop.domain;

import harry.boilerplate.common.exception.ErrorCode;

/**
 * Menu 도메인 에러 코드
 */
public enum MenuErrorCode implements ErrorCode {
    MENU_NOT_FOUND("MENU-DOMAIN-001", "메뉴를 찾을 수 없습니다"),
    MENU_ALREADY_OPEN("MENU-DOMAIN-002", "이미 공개된 메뉴입니다"),
    INSUFFICIENT_OPTION_GROUPS("MENU-DOMAIN-003", "메뉴 공개를 위해서는 최소 1개의 옵션그룹이 필요합니다"),
    INVALID_REQUIRED_OPTION_GROUP_COUNT("MENU-DOMAIN-004", "필수 옵션그룹은 1~3개 범위에 있어야 합니다"),
    NO_PAID_OPTION_GROUP("MENU-DOMAIN-005", "메뉴 공개를 위해서는 최소 1개의 유료 옵션그룹이 필요합니다"),
    DUPLICATE_OPTION_GROUP_NAME("MENU-DOMAIN-006", "중복된 옵션그룹 이름입니다"),
    MAX_REQUIRED_OPTION_GROUPS_EXCEEDED("MENU-DOMAIN-007", "필수 옵션그룹은 최대 3개까지만 허용됩니다"),
    OPTION_GROUP_NOT_FOUND("MENU-DOMAIN-008", "옵션그룹을 찾을 수 없습니다"),
    CANNOT_DELETE_REQUIRED_OPTION_GROUP("MENU-DOMAIN-009", "메뉴가 공개된 상태에서는 최소 조건을 만족해야 합니다");
    
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