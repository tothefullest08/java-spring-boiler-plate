package harry.boilerplate.shop.application.query.readmodel;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * Shop 요약 정보 Read Model
 * 가게 목록 조회 시 사용되는 불변 데이터 객체
 */
public class ShopSummaryReadModel {
    private final String id;
    private final String name;
    private final BigDecimal minOrderAmount;
    private final LocalTime openTime;
    private final LocalTime closeTime;
    private final boolean isOpen;
    
    public ShopSummaryReadModel(String id, String name, BigDecimal minOrderAmount, 
                               LocalTime openTime, LocalTime closeTime, boolean isOpen) {
        this.id = id;
        this.name = name;
        this.minOrderAmount = minOrderAmount;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.isOpen = isOpen;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }
    
    public LocalTime getOpenTime() {
        return openTime;
    }
    
    public LocalTime getCloseTime() {
        return closeTime;
    }
    
    public boolean isOpen() {
        return isOpen;
    }
}