package harry.boilerplate.order.application.query.readmodel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문 이력 정보를 위한 Read Model
 * UI 최적화된 불변 데이터 객체
 */
public class OrderHistoryReadModel {
    
    private final String orderId;
    private final String userId;
    private final String shopId;
    private final String shopName;
    private final List<OrderItemReadModel> orderItems;
    private final BigDecimal totalPrice;
    private final int totalQuantity;
    private final LocalDateTime orderTime;
    
    public OrderHistoryReadModel(String orderId, String userId, String shopId, String shopName,
                                List<OrderItemReadModel> orderItems, BigDecimal totalPrice,
                                int totalQuantity, LocalDateTime orderTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.shopId = shopId;
        this.shopName = shopName;
        this.orderItems = List.copyOf(orderItems != null ? orderItems : List.of());
        this.totalPrice = totalPrice;
        this.totalQuantity = totalQuantity;
        this.orderTime = orderTime;
    }
    
    public String getOrderId() {
        return orderId;
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
    
    public List<OrderItemReadModel> getOrderItems() {
        return orderItems;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public int getTotalQuantity() {
        return totalQuantity;
    }
    
    public LocalDateTime getOrderTime() {
        return orderTime;
    }
    
    public int getItemCount() {
        return orderItems.size();
    }
}