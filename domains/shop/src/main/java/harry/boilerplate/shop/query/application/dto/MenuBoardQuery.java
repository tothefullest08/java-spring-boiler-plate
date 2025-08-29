package harry.boilerplate.shop.query.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 메뉴보드 조회 쿼리 DTO
 */
public class MenuBoardQuery {
    
    @NotBlank(message = "가게 ID는 필수입니다")
    private final String shopId;
    
    public MenuBoardQuery(String shopId) {
        this.shopId = shopId;
    }
    
    public String getShopId() {
        return shopId;
    }
}