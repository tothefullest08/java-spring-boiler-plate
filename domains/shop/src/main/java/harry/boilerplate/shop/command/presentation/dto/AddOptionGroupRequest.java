package harry.boilerplate.shop.command.presentation.dto;

import harry.boilerplate.shop.command.application.dto.AddOptionGroupCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 옵션 그룹 추가 요청 DTO
 */
@Schema(description = "옵션 그룹 추가 요청")
public class AddOptionGroupRequest {
    
    @Schema(description = "옵션 그룹 이름", example = "고기 선택", required = true)
    @NotBlank(message = "옵션 그룹 이름은 필수입니다")
    @Size(max = 255, message = "옵션 그룹 이름은 255자 이하여야 합니다")
    private String name;
    
    @Schema(description = "필수 여부", example = "true", required = true)
    @NotNull(message = "필수 여부는 필수입니다")
    private Boolean isRequired;
    
    // 기본 생성자 (Jackson 직렬화용)
    public AddOptionGroupRequest() {}
    
    public AddOptionGroupRequest(String name, Boolean isRequired) {
        this.name = name;
        this.isRequired = isRequired;
    }
    
    /**
     * Command DTO로 변환
     */
    public AddOptionGroupCommand toCommand(String menuId) {
        return new AddOptionGroupCommand(menuId, name, isRequired);
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Boolean getIsRequired() {
        return isRequired;
    }
    
    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }
}