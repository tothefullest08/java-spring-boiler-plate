package harry.boilerplate.order.application.query.readmodel;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * 장바구니 요약 정보를 위한 Read Model
 * UI 최적화된 불변 데이터 객체
 */
public class CartSummaryReadModel {
    
    private final String cartId;
    private final String userId;
    private final String shopId;
    private final String shopName;
    private final List<CartItemReadModel> items;
    private final BigDecimal totalPrice;
    private final int totalQuantity;
    private final Instant updatedAt;
    
    public CartSummaryReadModel(String cartId, String userId, String shopId, String shopName,
                               List<CartItemReadModel> items, BigDecimal totalPrice, 
                               int totalQuantity, Instant updatedAt) {
        this.cartId = cartId;
        this.userId = userId;
        this.shopId = shopId;
        this.shopName = shopName;
        this.items = List.copyOf(items != null ? items : List.of());
        this.totalPrice = totalPrice;
        this.totalQuantity = totalQuantity;
        this.updatedAt = updatedAt;
    }
    
    public String getCartId() {
        return cartId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getShopId() {
        return shopId;
    }
    
    public String getShopName() {
        return shopName;
    }
    
    public List<CartItemReadModel> getItems() {
        return items;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public int getTotalQuantity() {
        return totalQuantity;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    public int getItemCount() {
        return items.size();
    }
}