package harry.boilerplate.order.domain;

import harry.boilerplate.common.domain.entity.ValueObject;

import java.util.List;
import java.util.Objects;

/**
 * 장바구니 라인 아이템 ValueObject
 * 메뉴와 선택된 옵션, 수량 정보를 포함
 */
public class CartLineItem extends ValueObject {
    
    private final MenuId menuId;
    private final List<OptionId> selectedOptions;
    private final int quantity;
    
    public CartLineItem(MenuId menuId, List<OptionId> selectedOptions, int quantity) {
        if (menuId == null) {
            throw new IllegalArgumentException("메뉴 ID는 필수입니다");
        }
        if (selectedOptions == null) {
            throw new IllegalArgumentException("선택된 옵션 목록은 필수입니다");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다");
        }
        
        this.menuId = menuId;
        this.selectedOptions = List.copyOf(selectedOptions); // 불변 리스트로 복사
        this.quantity = quantity;
    }
    
    /**
     * 동일한 메뉴와 옵션 조합인지 확인
     */
    public boolean isSameMenuAndOptions(CartLineItem other) {
        if (other == null) {
            return false;
        }
        
        return this.menuId.equals(other.menuId) && 
               this.selectedOptions.equals(other.selectedOptions);
    }
    
    /**
     * 동일한 메뉴와 옵션 조합의 아이템을 수량으로 병합
     */
    public CartLineItem combine(CartLineItem other) {
        if (!isSameMenuAndOptions(other)) {
            throw new IllegalArgumentException("동일한 메뉴와 옵션 조합만 병합할 수 있습니다");
        }
        
        return new CartLineItem(this.menuId, this.selectedOptions, this.quantity + other.quantity);
    }
    
    /**
     * 수량 변경
     */
    public CartLineItem changeQuantity(int newQuantity) {
        return new CartLineItem(this.menuId, this.selectedOptions, newQuantity);
    }
    
    public MenuId getMenuId() {
        return menuId;
    }
    
    public List<OptionId> getSelectedOptions() {
        return selectedOptions;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    @Override
    protected boolean equalsByValue(Object other) {
        if (!(other instanceof CartLineItem)) return false;
        CartLineItem that = (CartLineItem) other;
        return quantity == that.quantity &&
               Objects.equals(menuId, that.menuId) &&
               Objects.equals(selectedOptions, that.selectedOptions);
    }
    
    @Override
    protected Object[] getEqualityComponents() {
        return new Object[]{menuId, selectedOptions, quantity};
    }
    
    @Override
    public String toString() {
        return "CartLineItem{" +
               "menuId=" + menuId +
               ", selectedOptions=" + selectedOptions +
               ", quantity=" + quantity +
               '}';
    }
}