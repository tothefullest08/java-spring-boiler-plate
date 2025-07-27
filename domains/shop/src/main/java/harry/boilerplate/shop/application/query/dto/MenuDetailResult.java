package harry.boilerplate.shop.application.query.dto;

import harry.boilerplate.shop.application.query.readmodel.MenuDetailReadModel;

/**
 * 메뉴 상세 조회 결과 DTO
 */
public class MenuDetailResult {
    
    private final MenuDetailReadModel menu;
    
    public MenuDetailResult(MenuDetailReadModel menu) {
        this.menu = menu;
    }
    
    public MenuDetailReadModel getMenu() {
        return menu;
    }
    
    public static MenuDetailResult from(MenuDetailReadModel menu) {
        return new MenuDetailResult(menu);
    }
}