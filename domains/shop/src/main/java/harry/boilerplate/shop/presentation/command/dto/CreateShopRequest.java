package harry.boilerplate.shop.presentation.command.dto;

import harry.boilerplate.shop.application.command.dto.CreateShopCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 가게 생성 요청 DTO
 */
@Schema(description = "가게 생성 요청")
public class CreateShopRequest {
    
    @Schema(description = "가게 이름", example = "맛있는 삼겹살집", required = true)
    @NotBlank(message = "가게 이름은 필수입니다")
    @Size(max = 255, message = "가게 이름은 255자 이하여야 합니다")
    private String name;
    
    @Schema(description = "최소 주문금액", example = "15000", required = true)
    @NotNull(message = "최소 주문금액은 필수입니다")
    @Positive(message = "최소 주문금액은 양수여야 합니다")
    private BigDecimal minOrderAmount;
    
    // 기본 생성자 (Jackson 직렬화용)
    public CreateShopRequest() {}
    
    public CreateShopRequest(String name, BigDecimal minOrderAmount) {
        this.name = name;
        this.minOrderAmount = minOrderAmount;
    }
    
    /**
     * Command DTO로 변환
     */
    public CreateShopCommand toCommand() {
        return new CreateShopCommand(name, minOrderAmount);
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }
    
    public void setMinOrderAmount(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }
}