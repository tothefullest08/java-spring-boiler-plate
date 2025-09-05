package harry.boilerplate.order.presentation.command.dto;

import harry.boilerplate.order.application.command.dto.AddCartItemCommand;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * 장바구니 아이템 추가 요청 DTO
 * Requirements: 5.1, 5.2, 5.3
 */
@Schema(description = "장바구니 아이템 추가 요청")
public class AddCartItemRequest {
    
    @Schema(description = "가게 ID", example = "shop-123", required = true)
    @NotBlank(message = "가게 ID는 필수입니다")
    private String shopId;
    
    @Schema(description = "메뉴 ID", example = "menu-456", required = true)
    @NotBlank(message = "메뉴 ID는 필수입니다")
    private String menuId;
    
    @Schema(description = "선택된 옵션 ID 목록", example = "[\"option-1\", \"option-2\"]")
    private List<String> selectedOptionIds;
    
    @Schema(description = "수량", example = "2", required = true)
    @NotNull(message = "수량은 필수입니다")
    @Positive(message = "수량은 1 이상이어야 합니다")
    private Integer quantity;
    
    // 기본 생성자 (Jackson 직렬화용)
    public AddCartItemRequest() {}
    
    public AddCartItemRequest(String shopId, String menuId, List<String> selectedOptionIds, Integer quantity) {
        this.shopId = shopId;
        this.menuId = menuId;
        this.selectedOptionIds = selectedOptionIds;
        this.quantity = quantity;
    }
    
    /**
     * Command 객체로 변환
     */
    public AddCartItemCommand toCommand(String userId) {
        return new AddCartItemCommand(
            userId,
            this.shopId,
            this.menuId,
            this.selectedOptionIds,
            this.quantity
        );
    }
    
    // Getters and Setters
    public String getShopId() {
        return shopId;
    }
    
    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
    
    public String getMenuId() {
        return menuId;
    }
    
    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
    
    public List<String> getSelectedOptionIds() {
        return selectedOptionIds;
    }
    
    public void setSelectedOptionIds(List<String> selectedOptionIds) {
        this.selectedOptionIds = selectedOptionIds;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}