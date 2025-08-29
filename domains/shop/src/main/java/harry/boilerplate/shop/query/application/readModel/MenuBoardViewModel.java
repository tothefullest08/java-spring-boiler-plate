package harry.boilerplate.shop.query.application.readModel;

import java.util.List;

/**
 * Menu Board View Model
 * 메뉴판 UI에 최적화된 뷰 모델
 */
public class MenuBoardViewModel {
    private final String shopId;
    private final String shopName;
    private final boolean shopIsOpen;
    private final List<MenuSummaryReadModel> openMenus;
    private final List<MenuSummaryReadModel> closedMenus;
    private final int totalMenuCount;
    private final int openMenuCount;
    
    public MenuBoardViewModel(String shopId, String shopName, boolean shopIsOpen,
                             List<MenuSummaryReadModel> openMenus, List<MenuSummaryReadModel> closedMenus) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.shopIsOpen = shopIsOpen;
        this.openMenus = openMenus;
        this.closedMenus = closedMenus;
        this.totalMenuCount = openMenus.size() + closedMenus.size();
        this.openMenuCount = openMenus.size();
    }
    
    public String getShopId() {
        return shopId;
    }
    
    public String getShopName() {
        return shopName;
    }
    
    public boolean isShopOpen() {
        return shopIsOpen;
    }
    
    public List<MenuSummaryReadModel> getOpenMenus() {
        return openMenus;
    }
    
    public List<MenuSummaryReadModel> getClosedMenus() {
        return closedMenus;
    }
    
    public int getTotalMenuCount() {
        return totalMenuCount;
    }
    
    public int getOpenMenuCount() {
        return openMenuCount;
    }
}