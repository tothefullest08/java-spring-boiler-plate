package harry.boilerplate.shop.query.application.handler;

import harry.boilerplate.shop.query.application.dto.MenuDetailQuery;
import harry.boilerplate.shop.query.application.dto.MenuDetailResult;
import harry.boilerplate.shop.query.application.readModel.MenuDetailReadModel;
import harry.boilerplate.shop.query.infrastructure.dao.MenuQueryDao;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 메뉴 상세 조회 Query Handler
 * Requirements: 4.4 - 고객이 메뉴 상세 정보를 조회할 수 있어야 함
 */
@Component
@Transactional(readOnly = true)
public class MenuDetailQueryHandler {
    
    private final MenuQueryDao menuQueryDao;
    
    public MenuDetailQueryHandler(MenuQueryDao menuQueryDao) {
        this.menuQueryDao = menuQueryDao;
    }
    
    /**
     * 메뉴 상세 조회 처리
     * @param query 메뉴 상세 조회 쿼리
     * @return 메뉴 상세 조회 결과
     */
    public MenuDetailResult handle(MenuDetailQuery query) {
        MenuDetailReadModel menu = menuQueryDao.findMenuDetail(query.getMenuId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다: " + query.getMenuId()));
        
        return MenuDetailResult.from(menu);
    }
}