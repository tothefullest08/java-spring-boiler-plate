package harry.boilerplate.shop.domain.aggregate;

import harry.boilerplate.shop.domain.valueobject.*;

/**
 * Shop 애그리게이트 Repository 인터페이스
 * Command 측면의 쓰기 작업을 담당
 */
public interface ShopRepository {
    
    /**
     * Shop 저장 (생성 또는 수정)
     */
    void save(Shop shop);
    
    /**
     * Shop ID로 조회
     */
    Shop findById(ShopId shopId);
    
    /**
     * Shop 존재 여부 확인
     */
    boolean existsById(ShopId shopId);
    
    /**
     * Shop 삭제
     */
    void delete(Shop shop);
    
    /**
     * Shop ID로 삭제
     */
    void deleteById(ShopId shopId);
}