package harry.boilerplate.order.application.query.readmodel;

import java.math.BigDecimal;
import java.util.List;

/**
 * 주문 아이템 정보를 위한 Read Model
 */
public class OrderItemReadModel {
    
    private final String orderLineItemId;
    private final String menuId;
    private final String menuName;
    private final List<SelectedOptionReadModel> selectedOptions;
    private final int quantity;
    private final BigDecimal linePrice;
    
    public OrderItemReadModel(String orderLineItemId, String menuId, String menuName,
                             List<SelectedOptionReadModel> selectedOptions, int quantity, 
                             BigDecimal linePrice) {
        this.orderLineItemId = orderLineItemId;
        this.menuId = menuId;
        this.menuName = menuName;
        this.selectedOptions = List.copyOf(selectedOptions != null ? selectedOptions : List.of());
        this.quantity = quantity;
        this.linePrice = linePrice;
    }
    
    public String getOrderLineItemId() {
        return orderLineItemId;
    }
    
    public String getMenuId() {
        return menuId;
    }
    
    public String getMenuName() {
        return menuName;
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