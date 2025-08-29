package harry.boilerplate.shop.application.query.readmodel;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;

/**
 * Shop 상세 정보 Read Model
 * 가게 상세 조회 시 사용되는 불변 데이터 객체
 */
public class ShopDetailReadModel {
    private final String id;
    private final String name;
    private final BigDecimal minOrderAmount;
    private final LocalTime openTime;
    private final LocalTime closeTime;
    private final boolean isOpen;
    private final Instant createdAt;
    private final Instant updatedAt;
    
    public ShopDetailReadModel(String id, String name, BigDecimal minOrderAmount,
                              LocalTime openTime, LocalTime closeTime, boolean isOpen,
                              Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.minOrderAmount = minOrderAmount;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.isOpen = isOpen;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
}