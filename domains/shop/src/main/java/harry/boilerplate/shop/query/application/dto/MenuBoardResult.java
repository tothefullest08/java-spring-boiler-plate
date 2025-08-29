package harry.boilerplate.shop.query.application.dto;

import harry.boilerplate.shop.query.application.readModel.MenuBoardViewModel;
import harry.boilerplate.shop.query.application.readModel.MenuSummaryReadModel;

import java.util.List;

/**
 * 메뉴보드 조회 결과 DTO
 */
public class MenuBoardResult {
    
    private final String shopId;
    private final String shopName;
    private final boolean isShopOpen;
    private final List<MenuSummaryReadModel> menus;
    private final int totalMenuCount;
    private final int openMenuCount;
    
    public MenuBoardResult(String shopId, String shopName, boolean isShopOpen, 
                          List<MenuSummaryReadModel> menus, int totalMenuCount, int openMenuCount) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.isShopOpen = isShopOpen;
        this.menus = menus;
        this.totalMenuCount = totalMenuCount;
        this.openMenuCount = openMenuCount;
    }
    
    public String getShopId() {
        return shopId;
    }
    
    public String getShopName() {
        return shopName;
    }
    
    public boolean isShopOpen() {
        return isShopOpen;
    }
    
    public List<MenuSummaryReadModel> getMenus() {
        return menus;
    }
    
    public int getTotalMenuCount() {
        return totalMenuCount;
    }
    
    public int getOpenMenuCount() {
        return openMenuCount;
    }
    
    public static MenuBoardResult from(MenuBoardViewModel viewModel) {
        List<MenuSummaryReadModel> allMenus = viewModel.getOpenMenus();
        return new MenuBoardResult(
            viewModel.getShopId(),
            viewModel.getShopName(),
            viewModel.isShopOpen(),
            allMenus,
            viewModel.getTotalMenuCount(),
            viewModel.getOpenMenuCount()
        );
    }
    
    public static MenuBoardResult empty(String shopId) {
        return new MenuBoardResult(shopId, "", false, List.of(), 0, 0);
    }
}