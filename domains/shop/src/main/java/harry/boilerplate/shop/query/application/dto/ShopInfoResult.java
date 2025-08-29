package harry.boilerplate.shop.query.application.dto;

import harry.boilerplate.shop.query.application.readModel.ShopDetailReadModel;

/**
 * 가게 정보 조회 결과 DTO
 */
public class ShopInfoResult {
    
    private final ShopDetailReadModel shop;
    
    public ShopInfoResult(ShopDetailReadModel shop) {
        this.shop = shop;
    }
    
    public ShopDetailReadModel getShop() {
        return shop;
    }
    
    public static ShopInfoResult from(ShopDetailReadModel shop) {
        return new ShopInfoResult(shop);
    }
}