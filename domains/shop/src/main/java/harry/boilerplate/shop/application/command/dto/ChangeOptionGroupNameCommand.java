package harry.boilerplate.shop.application.command.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 옵션 그룹 이름 변경 명령 DTO
 */
public class ChangeOptionGroupNameCommand {
    
    @NotBlank(message = "메뉴 ID는 필수입니다")
    private final String menuId;
    
    @NotBlank(message = "옵션 그룹 ID는 필수입니다")
    private final String optionGroupId;
    
    @NotBlank(message = "새로운 옵션 그룹 이름은 필수입니다")
    @Size(max = 255, message = "옵션 그룹 이름은 255자 이하여야 합니다")
    private final String newName;
    
    public ChangeOptionGroupNameCommand(String menuId, String optionGroupId, String newName) {
        this.menuId = menuId;
        this.optionGroupId = optionGroupId;
        this.newName = newName;
    }
    
    public String getMenuId() {
        return menuId;
    }
    
    public String getOptionGroupId() {
        return optionGroupId;
    }
    
    public String getNewName() {
        return newName;
    }
}