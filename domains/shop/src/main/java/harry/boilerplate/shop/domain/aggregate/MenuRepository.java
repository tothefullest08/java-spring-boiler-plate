package harry.boilerplate.shop.domain.aggregate;

import harry.boilerplate.shop.domain.valueobject.*;
import java.util.List;

/**
 * Menu 애그리게이트 Repository 인터페이스
 * Command 측면의 쓰기 작업을 담당
 */
public interface MenuRepository {
    
    /**
     * Menu 저장 (생성 또는 수정)
     */
    void save(Menu menu);
    
    /**
     * Menu ID로 조회
     */
    Menu findById(MenuId menuId);
    
    /**
     * Shop ID로 Menu 목록 조회
     */
    List<Menu> findByShopId(ShopId shopId);
    
    /**
     * Menu 존재 여부 확인
     */
    boolean existsById(MenuId menuId);
    
    /**
     * Menu 삭제
     */
    void delete(Menu menu);
    
    /**
     * Menu ID로 삭제
     */
    void deleteById(MenuId menuId);
    
    /**
     * Shop의 모든 Menu 삭제
     */
    void deleteByShopId(ShopId shopId);
}