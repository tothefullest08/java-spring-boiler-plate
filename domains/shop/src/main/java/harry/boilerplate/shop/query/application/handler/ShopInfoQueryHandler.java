package harry.boilerplate.shop.query.application.handler;

import harry.boilerplate.shop.query.application.dto.ShopInfoQuery;
import harry.boilerplate.shop.query.application.dto.ShopInfoResult;
import harry.boilerplate.shop.query.application.readModel.ShopDetailReadModel;
import harry.boilerplate.shop.query.infrastructure.dao.ShopQueryDao;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 가게 정보 조회 Query Handler
 * Requirements: 4.1 - 고객이 가게 정보를 조회할 수 있어야 함
 */
@Component
@Transactional(readOnly = true)
public class ShopInfoQueryHandler {
    
    private final ShopQueryDao shopQueryDao;
    
    public ShopInfoQueryHandler(ShopQueryDao shopQueryDao) {
        this.shopQueryDao = shopQueryDao;
    }
    
    /**
     * 가게 정보 조회 처리
     * @param query 가게 정보 조회 쿼리
     * @return 가게 정보 조회 결과
     */
    public ShopInfoResult handle(ShopInfoQuery query) {
        ShopDetailReadModel shop = shopQueryDao.findShopDetail(query.getShopId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다: " + query.getShopId()));
        
        return ShopInfoResult.from(shop);
    }
}