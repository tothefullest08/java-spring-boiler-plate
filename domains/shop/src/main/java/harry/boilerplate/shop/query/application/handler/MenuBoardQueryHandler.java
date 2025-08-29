package harry.boilerplate.shop.query.application.handler;

import harry.boilerplate.shop.query.application.dto.MenuBoardQuery;
import harry.boilerplate.shop.query.application.dto.MenuBoardResult;
import harry.boilerplate.shop.query.application.readModel.MenuBoardViewModel;
import harry.boilerplate.shop.query.infrastructure.dao.MenuQueryDao;
import harry.boilerplate.shop.query.infrastructure.dao.ShopQueryDao;
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
        if (query == null) {
            throw new IllegalArgumentException("MenuBoardQuery는 필수입니다");
        }
        
        // 메뉴보드 조회
        MenuBoardViewModel viewModel = menuQueryDao.getMenuBoard(query.getShopId());
        
        if (viewModel == null) {
            return null;
        }
        
        return MenuBoardResult.from(viewModel);
    }
}