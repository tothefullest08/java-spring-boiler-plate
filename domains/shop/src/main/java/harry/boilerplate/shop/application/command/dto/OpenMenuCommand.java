package harry.boilerplate.shop.application.command.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 메뉴 공개 명령 DTO
 */
public class OpenMenuCommand {
    
    @NotBlank(message = "메뉴 ID는 필수입니다")
    private final String menuId;
    
    public OpenMenuCommand(String menuId) {
        this.menuId = menuId;
    }
    
    public String getMenuId() {
        return menuId;
    }
}