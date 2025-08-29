package harry.boilerplate.order.application.command.dto;

import java.util.List;

/**
 * 장바구니 아이템 추가 Command
 * Requirements: 5.1, 5.2, 5.3
 */
public class AddCartItemCommand {
    
    private final String userId;
    private final String shopId;
    private final String menuId;
    private final List<String> selectedOptionIds;
    private final int quantity;
    
    public AddCartItemCommand(String userId, String shopId, String menuId, 
                             List<String> selectedOptionIds, int quantity) {
        this.userId = userId;
        this.shopId = shopId;
        this.menuId = menuId;
        this.selectedOptionIds = selectedOptionIds != null ? List.copyOf(selectedOptionIds) : List.of();
        this.quantity = quantity;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getShopId() {
        return shopId;
    }
    
    public String getMenuId() {
        return menuId;
    }
    
    public List<String> getSelectedOptionIds() {
        return selectedOptionIds;
    }
    
    public int getQuantity() {
        return quantity;
    }
}