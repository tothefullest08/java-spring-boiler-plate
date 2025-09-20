package harry.boilerplate.order.query.application.readModel;

import java.math.BigDecimal;
import java.util.List;

/**
 * 장바구니 아이템 정보를 위한 Read Model
 */
public class CartItemReadModel {
    
    private final String cartLineItemId;
    private final String menuId;
    private final String menuName;
    private final BigDecimal menuPrice;
    private final List<SelectedOptionReadModel> selectedOptions;
    private final int quantity;
    private final BigDecimal linePrice;
    
    public CartItemReadModel(String cartLineItemId, String menuId, String menuName, 
                            BigDecimal menuPrice, List<SelectedOptionReadModel> selectedOptions,
                            int quantity, BigDecimal linePrice) {
        this.cartLineItemId = cartLineItemId;
        this.menuId = menuId;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.selectedOptions = List.copyOf(selectedOptions != null ? selectedOptions : List.of());
        this.quantity = quantity;
        this.linePrice = linePrice;
    }
    
    public String getCartLineItemId() {
        return cartLineItemId;
    }
    
    public String getMenuId() {
        return menuId;
    }
    
    public String getMenuName() {
        return menuName;
    }
    
    public BigDecimal getMenuPrice() {
        return menuPrice;
    }
    
    public List<SelectedOptionReadModel> getSelectedOptions() {
        return selectedOptions;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public BigDecimal getLinePrice() {
        return linePrice;
    }
}