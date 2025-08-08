package harry.boilerplate.order.domain.entity;

import harry.boilerplate.common.domain.entity.DomainEntity;
import harry.boilerplate.order.domain.valueObject.MenuId;
import harry.boilerplate.order.domain.valueObject.OptionId;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

/**
 * 장바구니 라인 아이템 도메인 엔티티
 * 메뉴와 선택된 옵션, 수량 정보를 포함
 */
@Entity
@Table(name = "cart_line_item")
public class CartLineItem extends DomainEntity<CartLineItem, String> {
    
    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;
    
    @Column(name = "menu_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private String menuId;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "cart_line_item_options", joinColumns = @JoinColumn(name = "cart_line_item_id"))
    @Column(name = "option_id", columnDefinition = "VARCHAR(36)")
    private List<String> selectedOptionIds;
    
    @Column(name = "quantity", nullable = false)
    private int quantity;
    
    // JPA 기본 생성자
    protected CartLineItem() {
    }
    
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
        
        this.id = UUID.randomUUID().toString();
        this.menuId = menuId.getValue();
        this.selectedOptionIds = selectedOptions.stream()
            .map(OptionId::getValue)
            .toList();
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
               this.selectedOptionIds.equals(other.selectedOptionIds);
    }
    
    /**
     * 동일한 메뉴와 옵션 조합의 아이템을 수량으로 병합
     */
    public CartLineItem combine(CartLineItem other) {
        if (!isSameMenuAndOptions(other)) {
            throw new IllegalArgumentException("동일한 메뉴와 옵션 조합만 병합할 수 있습니다");
        }
        
        List<OptionId> optionIds = this.selectedOptionIds.stream()
            .map(OptionId::of)
            .toList();
        
        return new CartLineItem(MenuId.of(this.menuId), optionIds, this.quantity + other.quantity);
    }
    
    /**
     * 수량 변경
     */
    public void changeQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다");
        }
        this.quantity = newQuantity;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    public MenuId getMenuId() {
        return MenuId.of(menuId);
    }
    
    public List<OptionId> getSelectedOptions() {
        return selectedOptionIds.stream()
            .map(OptionId::of)
            .toList();
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    @Override
    public String toString() {
        return "CartLineItem{" +
               "id='" + id + '\'' +
               ", menuId='" + menuId + '\'' +
               ", selectedOptionIds=" + selectedOptionIds +
               ", quantity=" + quantity +
               '}';
    }
}