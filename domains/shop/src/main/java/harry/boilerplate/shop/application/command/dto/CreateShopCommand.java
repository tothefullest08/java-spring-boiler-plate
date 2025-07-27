package harry.boilerplate.shop.application.command.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 가게 생성 명령 DTO
 */
public class CreateShopCommand {
    
    @NotBlank(message = "가게 이름은 필수입니다")
    @Size(max = 255, message = "가게 이름은 255자 이하여야 합니다")
    private final String name;
    
    @NotNull(message = "최소 주문금액은 필수입니다")
    @Positive(message = "최소 주문금액은 양수여야 합니다")
    private final BigDecimal minOrderAmount;
    
    public CreateShopCommand(String name, BigDecimal minOrderAmount) {
        this.name = name;
        this.minOrderAmount = minOrderAmount;
    }
    
    public String getName() {
        return name;
    }
    
    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }
}