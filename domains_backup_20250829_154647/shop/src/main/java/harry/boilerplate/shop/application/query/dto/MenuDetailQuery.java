package harry.boilerplate.shop.application.query.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 메뉴 상세 조회 쿼리 DTO
 */
public class MenuDetailQuery {
    
    @NotBlank(message = "메뉴 ID는 필수입니다")
    private final String menuId;
    
    public MenuDetailQuery(String menuId) {
        this.menuId = menuId;
    }
    
    public String getMenuId() {
        return menuId;
    }
}