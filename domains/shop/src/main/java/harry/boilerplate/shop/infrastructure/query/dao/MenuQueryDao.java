package harry.boilerplate.shop.infrastructure.query.dao;

import harry.boilerplate.shop.application.query.readmodel.MenuBoardViewModel;
import harry.boilerplate.shop.application.query.readmodel.MenuDetailReadModel;
import harry.boilerplate.shop.application.query.readmodel.MenuSummaryReadModel;

import java.util.List;
import java.util.Optional;

/**
 * Menu Query DAO 인터페이스
 * Query 측면의 읽기 작업을 담당 (Table Data Gateway 패턴)
 */
public interface MenuQueryDao {
    
    /**
     * 가게의 메뉴판 조회 (UI 최적화)
     */
    MenuBoardViewModel getMenuBoard(String shopId);
    
    /**
     * 가게의 모든 메뉴 요약 정보 조회
     */
    List<MenuSummaryReadModel> findMenuSummariesByShopId(String shopId);
    
    /**
     * 가게의 공개된 메뉴 요약 정보 조회
     */
    List<MenuSummaryReadModel> findOpenMenuSummariesByShopId(String shopId);
    
    /**
     * 메뉴 상세 정보 조회
     */
    Optional<MenuDetailReadModel> findMenuDetail(String menuId);
    
    /**
     * 메뉴 존재 여부 확인
     */
    boolean existsMenu(String menuId);
    
    /**
     * 메뉴 이름으로 검색
     */
    List<MenuSummaryReadModel> searchMenusByName(String shopId, String nameKeyword);
}