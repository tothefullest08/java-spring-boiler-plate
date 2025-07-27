package harry.boilerplate.shop.application.query.handler;

import harry.boilerplate.shop.application.query.dto.MenuBoardQuery;
import harry.boilerplate.shop.application.query.dto.MenuBoardResult;
import harry.boilerplate.shop.application.query.readmodel.MenuBoardViewModel;
import harry.boilerplate.shop.infrastructure.query.dao.MenuQueryDao;
import harry.boilerplate.shop.infrastructure.query.dao.ShopQueryDao;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 메뉴보드 조회 Query Handler
 * Requirements: 4.1, 4.2, 4.3 - 고객이 가게의 메뉴보드를 조회할 수 있어야 함
 */
@Component
@Transactional(readOnly = true)
public class MenuBoardQueryHandler {
    
    private final MenuQueryDao menuQueryDao;
    private final ShopQueryDao shopQueryDao;
    
    public MenuBoardQueryHandler(MenuQueryDao menuQueryDao, ShopQueryDao shopQueryDao) {
        this.menuQueryDao = menuQueryDao;
        this.shopQueryDao = shopQueryDao;
    }
    
    /**
     * 메뉴보드 조회 처리
     * @param query 메뉴보드 조회 쿼리
     * @return 메뉴보드 조회 결과
     */
    public MenuBoardResult handle(MenuBoardQuery query) {
        // 가게 존재 확인
        if (!shopQueryDao.existsShop(query.getShopId())) {
            throw new IllegalArgumentException("존재하지 않는 가게입니다: " + query.getShopId());
        }
        
        try {
            // 메뉴보드 조회
            MenuBoardViewModel viewModel = menuQueryDao.getMenuBoard(query.getShopId());
            return MenuBoardResult.from(viewModel);
        } catch (Exception e) {
            // 가게에 메뉴가 없는 경우 빈 결과 반환
            return MenuBoardResult.empty(query.getShopId());
        }
    }
}