package harry.boilerplate.order.query.application.readmodel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 주문 요약 정보를 위한 Read Model
 * 주문 목록 화면에서 사용
 */
public class OrderSummaryReadModel {
    
    private final String orderId;
    private final String shopId;
    private final String shopName;
    private final BigDecimal totalPrice;
    private final int totalQuantity;
    private final LocalDateTime orderTime;
    private final String firstMenuName;
    private final int additionalMenuCount;
    
    public OrderSummaryReadModel(String orderId, String shopId, String shopName, 
                                BigDecimal totalPrice, int totalQuantity, LocalDateTime orderTime,
                                String firstMenuName, int additionalMenuCount) {
        this.orderId = orderId;
        this.shopId = shopId;
        this.shopName = shopName;
        this.totalPrice = totalPrice;
        this.totalQuantity = totalQuantity;
        this.orderTime = orderTime;
        this.firstMenuName = firstMenuName;
        this.additionalMenuCount = additionalMenuCount;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public String getShopId() {
        return shopId;
    }
    
    public String getShopName() {
        return shopName;
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
    
    public String getFirstMenuName() {
        return firstMenuName;
    }
    
    public int getAdditionalMenuCount() {
        return additionalMenuCount;
    }
    
    /**
     * 주문 요약 텍스트 생성 (예: "삼겹살 외 2개")
     */
    public String getOrderSummaryText() {
        if (additionalMenuCount > 0) {
            return firstMenuName + " 외 " + additionalMenuCount + "개";
        }
        return firstMenuName;
    }
}