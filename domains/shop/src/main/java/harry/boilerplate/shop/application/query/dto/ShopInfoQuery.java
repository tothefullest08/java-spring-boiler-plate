package harry.boilerplate.shop.application.query.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 가게 정보 조회 쿼리 DTO
 */
public class ShopInfoQuery {
    
    @NotBlank(message = "가게 ID는 필수입니다")
    private final String shopId;
    
    public ShopInfoQuery(String shopId) {
        this.shopId = shopId;
    }
    
    public String getShopId() {
        return shopId;
    }
}