package harry.boilerplate.shop.application.command.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 옵션 그룹 추가 명령 DTO
 */
public class AddOptionGroupCommand {
    
    @NotBlank(message = "메뉴 ID는 필수입니다")
    private final String menuId;
    
    @NotBlank(message = "옵션 그룹 이름은 필수입니다")
    @Size(max = 255, message = "옵션 그룹 이름은 255자 이하여야 합니다")
    private final String name;
    
    @NotNull(message = "필수 여부는 필수입니다")
    private final Boolean isRequired;
    
    public AddOptionGroupCommand(String menuId, String name, Boolean isRequired) {
        this.menuId = menuId;
        this.name = name;
        this.isRequired = isRequired;
    }
    
    public String getMenuId() {
        return menuId;
    }
    
    public String getName() {
        return name;
    }
    
    public Boolean getIsRequired() {
        return isRequired;
    }
}