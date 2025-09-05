package harry.boilerplate.order.command.domain.entity;

import harry.boilerplate.common.domain.entity.DomainEntity;
import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.order.command.domain.aggregate.Order;
import harry.boilerplate.order.command.domain.valueObject.MenuId;
import harry.boilerplate.order.command.domain.valueObject.OptionId;
import harry.boilerplate.order.command.domain.valueObject.SelectedOption;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * 주문 라인 아이템 도메인 엔티티
 * 주문 시점의 메뉴 정보와 선택된 옵션, 수량, 가격 정보를 포함
 */
@Entity
@Table(name = "order_line_item")
public class OrderLineItem extends DomainEntity<OrderLineItem, String> {

    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "menu_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private String menuId;

    @Column(name = "menu_name", nullable = false)
    private String menuName;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "order_item_selected_option", 
        joinColumns = @JoinColumn(name = "order_line_item_id")
    )
    private List<SelectedOption> selectedOptions;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "line_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal linePrice;

    // JPA 기본 생성자
    protected OrderLineItem() {
    }

    public OrderLineItem(MenuId menuId, String menuName, List<SelectedOption> selectedOptions,
            int quantity, Money linePrice) {
        if (menuId == null) {
            throw new IllegalArgumentException("메뉴 ID는 필수입니다");
        }
        if (menuName == null || menuName.trim().isEmpty()) {
            throw new IllegalArgumentException("메뉴 이름은 필수입니다");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다");
        }
        if (linePrice == null) {
            throw new IllegalArgumentException("라인 가격은 필수입니다");
        }

        this.id = UUID.randomUUID().toString();
        this.menuId = menuId.getValue();
        this.menuName = menuName.trim();
        this.selectedOptions = selectedOptions != null ? List.copyOf(selectedOptions) : List.of();
        this.quantity = quantity;
        this.linePrice = linePrice.getAmount();
    }

    public OrderLineItem(Order order, MenuId menuId, String menuName, List<SelectedOption> selectedOptions,
                          int quantity, Money linePrice) {
        this(menuId, menuName, selectedOptions, quantity, linePrice);
        if (order == null) {
            throw new IllegalArgumentException("Order는 필수입니다");
        }
        this.order = order;
    }

    /**
     * CartLineItem으로부터 OrderLineItem 생성
     */
    public static OrderLineItem fromCartLineItem(CartLineItem cartItem, String menuName,
            List<SelectedOption> selectedOptions, Money unitPrice) {
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
                selectedOptions != null ? selectedOptions : List.of(),
                cartItem.getQuantity(),
                linePrice);
    }

    /**
     * 라인 아이템의 총 가격 반환
     */
    public Money getTotalPrice() {
        return Money.of(linePrice);
    }

    @Override
    public String getId() {
        return id;
    }

    public MenuId getMenuId() {
        return MenuId.of(menuId);
    }

    public String getMenuName() {
        return menuName;
    }

    public List<SelectedOption> getSelectedOptions() {
        return List.copyOf(selectedOptions);
    }

    public List<String> getSelectedOptionNames() {
        return selectedOptions.stream()
                .map(SelectedOption::getOptionName)
                .toList();
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getLinePrice() {
        return Money.of(linePrice);
    }

    @Override
    public String toString() {
        return "OrderLineItem{" +
                "id='" + id + '\'' +
                ", menuId='" + menuId + '\'' +
                ", menuName='" + menuName + '\'' +
                ", selectedOptions=" + selectedOptions +
                ", quantity=" + quantity +
                ", linePrice=" + linePrice +
                '}';
    }
}