package harry.boilerplate.order.domain.aggregate;

import harry.boilerplate.common.domain.entity.AggregateRoot;
import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.order.domain.event.CartItemAddedEvent;
import harry.boilerplate.order.domain.entity.CartLineItem;
import harry.boilerplate.order.domain.valueObject.*;
import harry.boilerplate.order.domain.exception.CartDomainException;
import harry.boilerplate.order.domain.exception.CartErrorCode;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Cart 애그리게이트 루트
 * 사용자의 장바구니를 관리하며 단일 가게 규칙을 적용
 */
@Entity
@Table(name = "cart")
public class Cart extends AggregateRoot<Cart, CartId> {

    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "user_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private String userId;

    @Column(name = "shop_id", columnDefinition = "VARCHAR(36)")
    private String shopId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartLineItem> items;
    
    // 기본 생성자 (JPA용)
    protected Cart() {
        this.items = new ArrayList<>();
    }
    
    // 새 장바구니 생성
    public Cart(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다");
        }
        this.id = CartId.generate().getValue();
        this.userId = userId.getValue();
        this.shopId = null;
        this.items = new ArrayList<>();
    }
    
    // 기존 장바구니 복원 (Repository용)
    public Cart(CartId id, UserId userId, ShopId shopId, List<CartLineItem> items) {
        this.id = id != null ? id.getValue() : null;
        this.userId = userId != null ? userId.getValue() : null;
        this.shopId = shopId != null ? shopId.getValue() : null;
        this.items = new ArrayList<>(items != null ? items : new ArrayList<>());
    }
    
    /**
     * 다른 가게의 메뉴 추가 시 장바구니 초기화
     */
    public void start(ShopId newShopId) {
        if (newShopId == null) {
            throw new IllegalArgumentException("가게 ID는 필수입니다");
        }
        this.shopId = newShopId.getValue();
        this.items.clear();
    }
    
    /**
     * 장바구니에 메뉴 아이템 추가
     * 단일 가게 규칙 적용 및 동일 메뉴+옵션 조합 병합
     */
    public void addItem(MenuId menuId, List<OptionId> selectedOptions, int quantity) {
        if (menuId == null) {
            throw new CartDomainException(CartErrorCode.INVALID_MENU_ID);
        }
        if (quantity <= 0) {
            throw new CartDomainException(CartErrorCode.INVALID_QUANTITY);
        }
        CartLineItem newItem = new CartLineItem(this, menuId, selectedOptions, quantity);
        
        // 동일한 메뉴와 옵션 조합이 있는지 확인
        Optional<CartLineItem> existingItem = items.stream()
            .filter(item -> item.isSameMenuAndOptions(newItem))
            .findFirst();
        
        if (existingItem.isPresent()) {
            // 기존 아이템과 병합
            CartLineItem combinedItem = existingItem.get().combine(newItem);
            items.remove(existingItem.get());
            items.add(combinedItem);
        } else {
            // 새 아이템 추가
            items.add(newItem);
        }
        
        // 도메인 이벤트 발행
        addDomainEvent(new CartItemAddedEvent(
            this.id,
            this.userId,
            this.shopId,
            menuId.getValue(),
            quantity
        ));
    }
    
    /**
     * 장바구니에 메뉴 아이템 추가 (가게 검증 포함)
     * 다른 가게의 메뉴인 경우 자동으로 start() 호출
     */
    public void addItem(ShopId itemShopId, MenuId menuId, List<OptionId> selectedOptions, int quantity) {
        if (itemShopId == null) {
            throw new IllegalArgumentException("가게 ID는 필수입니다");
        }
        
        // 장바구니가 비어있거나 같은 가게인 경우
        if (this.shopId == null || this.shopId.equals(itemShopId.getValue())) {
            if (this.shopId == null) {
                this.shopId = itemShopId.getValue();
            }
            addItem(menuId, selectedOptions, quantity);
        } else {
            // 다른 가게의 메뉴인 경우 장바구니 초기화 후 추가
            start(itemShopId);
            addItem(menuId, selectedOptions, quantity);
        }
    }
    
    /**
     * 장바구니 총 금액 계산
     * 실제 구현에서는 Shop Context API를 통해 메뉴와 옵션 가격을 조회해야 함
     */
    public Money getTotalPrice() {
        if (isEmpty()) {
            return Money.zero();
        }
        
        // TODO: Shop Context API를 통해 실제 가격 계산
        // 현재는 기본 가격으로 계산 (메뉴당 10,000원으로 가정)
        Money total = Money.zero();
        for (CartLineItem item : items) {
            Money itemPrice = Money.of(10000).multiply(item.getQuantity());
            total = total.add(itemPrice);
        }
        
        return total;
    }
    
    /**
     * 장바구니가 비어있는지 확인
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    /**
     * 장바구니 아이템 개수
     */
    public int getItemCount() {
        return items.size();
    }
    
    /**
     * 장바구니 총 수량
     */
    public int getTotalQuantity() {
        return items.stream()
            .mapToInt(CartLineItem::getQuantity)
            .sum();
    }
    
    /**
     * 특정 아이템 제거
     */
    public void removeItem(MenuId menuId, List<OptionId> selectedOptions) {
        CartLineItem targetItem = new CartLineItem(menuId, selectedOptions, 1);
        items.removeIf(item -> item.isSameMenuAndOptions(targetItem));
    }
    
    /**
     * 장바구니 비우기
     */
    public void clear() {
        this.items.clear();
        this.shopId = null;
    }
    
    /**
     * 장바구니로부터 주문 생성
     */
    public Order placeOrder() {
        if (isEmpty()) {
            throw new CartDomainException(CartErrorCode.EMPTY_CART);
        }
        if (shopId == null) {
            throw new CartDomainException(CartErrorCode.DIFFERENT_SHOP_MENU, "가게가 선택되지 않았습니다");
        }
        
        Order order = Order.fromCart(this);
        
        // 주문 완료 후 장바구니 비우기
        clear();
        
        return order;
    }
    
    @Override
    public CartId getId() {
        return CartId.of(id);
    }
    
    public UserId getUserId() {
        return UserId.of(userId);
    }
    
    public ShopId getShopId() {
        return shopId != null ? ShopId.of(shopId) : null;
    }
    
    public List<CartLineItem> getItems() {
        return List.copyOf(items); // 불변 리스트 반환
    }
}