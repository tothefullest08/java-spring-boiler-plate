package harry.boilerplate.shop.presentation.command.dto;

import harry.boilerplate.shop.application.command.dto.CreateMenuCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 메뉴 생성 요청 DTO
 */
@Schema(description = "메뉴 생성 요청")
public class CreateMenuRequest {
    
    @Schema(description = "메뉴 이름", example = "삼겹살", required = true)
    @NotBlank(message = "메뉴 이름은 필수입니다")
    @Size(max = 255, message = "메뉴 이름은 255자 이하여야 합니다")
    private String name;
    
    @Schema(description = "메뉴 설명", example = "맛있는 삼겹살입니다")
    @Size(max = 1000, message = "메뉴 설명은 1000자 이하여야 합니다")
    private String description;
    
    @Schema(description = "기본 가격", example = "18000", required = true)
    @NotNull(message = "기본 가격은 필수입니다")
    @Positive(message = "기본 가격은 양수여야 합니다")
    private BigDecimal basePrice;
    
    // 기본 생성자 (Jackson 직렬화용)
    public CreateMenuRequest() {}
    
    public CreateMenuRequest(String name, String description, BigDecimal basePrice) {
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
    }
    
    /**
     * Command DTO로 변환
     */
    public CreateMenuCommand toCommand(String shopId) {
        return new CreateMenuCommand(shopId, name, description, basePrice);
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getBasePrice() {
        return basePrice;
    }
    
    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }
}