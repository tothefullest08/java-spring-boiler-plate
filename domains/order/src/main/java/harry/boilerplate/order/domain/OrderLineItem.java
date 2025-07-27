package harry.boilerplate.order.domain;

import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.common.domain.entity.ValueObject;

import java.util.List;
import java.util.Objects;

/**
 * 주문 라인 아이템 ValueObject
 * 주문 시점의 메뉴 정보와 선택된 옵션, 수량, 가격 정보를 포함
 */
public class OrderLineItem extends ValueObject {
    
    private final MenuId menuId;
    private final String menuName;
    private final List<OptionId> selectedOptions;
    private final List<String> selectedOptionNames;
    private final int quantity;
    private final Money linePrice;
    
    public OrderLineItem(MenuId menuId, String menuName, List<OptionId> selectedOptions, 
                        List<String> selectedOptionNames, int quantity, Money linePrice) {
        if (menuId == null) {
            throw new IllegalArgumentException("메뉴 ID는 필수입니다");
        }
        if (menuName == null || menuName.trim().isEmpty()) {
            throw new IllegalArgumentException("메뉴 이름은 필수입니다");
        }
        if (selectedOptions == null) {
            throw new IllegalArgumentException("선택된 옵션 목록은 필수입니다");
        }
        if (selectedOptionNames == null) {
            throw new IllegalArgumentException("선택된 옵션 이름 목록은 필수입니다");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다");
        }
        if (linePrice == null) {
            throw new IllegalArgumentException("라인 가격은 필수입니다");
        }
        
        this.menuId = menuId;
        this.menuName = menuName.trim();
        this.selectedOptions = List.copyOf(selectedOptions);
        this.selectedOptionNames = List.copyOf(selectedOptionNames);
        this.quantity = quantity;
        this.linePrice = linePrice;
    }
    
    /**
     * CartLineItem으로부터 OrderLineItem 생성
     */
    public static OrderLineItem fromCartLineItem(CartLineItem cartItem, String menuName, 
                                               List<String> optionNames, Money unitPrice) {
        if (cartItem == null) {
            throw new IllegalArgumentException("장바구니 아이템은 필수입니다");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("단가는 필수입니다");
        }
        
        Money linePrice = unitPrice.multiply(cartItem.getQuantity());
        
        return new OrderLineItem(
            cartItem.getMenuId(),
            menuName,
            cartItem.getSelectedOptions(),
            optionNames != null ? optionNames : List.of(),
            cartItem.getQuantity(),
            linePrice
        );
    }
    
    /**
     * 라인 아이템의 총 가격 반환
     */
    public Money getTotalPrice() {
        return linePrice;
    }
    
    public MenuId getMenuId() {
        return menuId;
    }
    
    public String getMenuName() {
        return menuName;
    }
    
    public List<OptionId> getSelectedOptions() {
        return selectedOptions;
    }
    
    public List<String> getSelectedOptionNames() {
        return selectedOptionNames;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public Money getLinePrice() {
        return linePrice;
    }
    
    @Override
    protected boolean equalsByValue(Object other) {
        if (!(other instanceof OrderLineItem)) return false;
        OrderLineItem that = (OrderLineItem) other;
        return quantity == that.quantity &&
               Objects.equals(menuId, that.menuId) &&
               Objects.equals(menuName, that.menuName) &&
               Objects.equals(selectedOptions, that.selectedOptions) &&
               Objects.equals(selectedOptionNames, that.selectedOptionNames) &&
               Objects.equals(linePrice, that.linePrice);
    }
    
    @Override
    protected Object[] getEqualityComponents() {
        return new Object[]{menuId, menuName, selectedOptions, selectedOptionNames, quantity, linePrice};
    }
    
    @Override
    public String toString() {
        return "OrderLineItem{" +
               "menuId=" + menuId +
               ", menuName='" + menuName + '\'' +
               ", selectedOptions=" + selectedOptions +
               ", selectedOptionNames=" + selectedOptionNames +
               ", quantity=" + quantity +
               ", linePrice=" + linePrice +
               '}';
    }
}