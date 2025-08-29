package harry.boilerplate.shop.application.query.dto;

import harry.boilerplate.shop.application.query.readmodel.ShopDetailReadModel;

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