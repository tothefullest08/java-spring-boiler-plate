package harry.boilerplate.shop.infrastructure.query.dao;

import harry.boilerplate.shop.application.query.readmodel.ShopDetailReadModel;
import harry.boilerplate.shop.application.query.readmodel.ShopSummaryReadModel;

import java.util.List;
import java.util.Optional;

/**
 * Shop Query DAO 인터페이스
 * Query 측면의 읽기 작업을 담당 (Table Data Gateway 패턴)
 */
public interface ShopQueryDao {
    
    /**
     * 모든 가게 요약 정보 조회
     */
    List<ShopSummaryReadModel> findAllShopSummaries();
    
    /**
     * 영업 중인 가게 요약 정보 조회
     */
    List<ShopSummaryReadModel> findOpenShopSummaries();
    
    /**
     * 가게 상세 정보 조회
     */
    Optional<ShopDetailReadModel> findShopDetail(String shopId);
    
    /**
     * 가게 존재 여부 확인
     */
    boolean existsShop(String shopId);
    
    /**
     * 가게 이름으로 검색
     */
    List<ShopSummaryReadModel> searchShopsByName(String nameKeyword);
}