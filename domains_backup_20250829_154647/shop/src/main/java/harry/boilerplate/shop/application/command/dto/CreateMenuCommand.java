package harry.boilerplate.shop.application.command.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 메뉴 생성 명령 DTO
 */
public class CreateMenuCommand {
    
    @NotBlank(message = "가게 ID는 필수입니다")
    private final String shopId;
    
    @NotBlank(message = "메뉴 이름은 필수입니다")
    @Size(max = 255, message = "메뉴 이름은 255자 이하여야 합니다")
    private final String name;
    
    @Size(max = 1000, message = "메뉴 설명은 1000자 이하여야 합니다")
    private final String description;
    
    @NotNull(message = "기본 가격은 필수입니다")
    @Positive(message = "기본 가격은 양수여야 합니다")
    private final BigDecimal basePrice;
    
    public CreateMenuCommand(String shopId, String name, String description, BigDecimal basePrice) {
        this.shopId = shopId;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
    }
    
    public String getShopId() {
        return shopId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public BigDecimal getBasePrice() {
        return basePrice;
    }
}