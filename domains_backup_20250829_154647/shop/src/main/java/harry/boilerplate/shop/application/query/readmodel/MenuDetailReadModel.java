package harry.boilerplate.shop.application.query.readmodel;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Menu 상세 정보 Read Model
 * 메뉴 상세 조회 시 사용되는 불변 데이터 객체
 */
public class MenuDetailReadModel {
    private final String id;
    private final String shopId;
    private final String name;
    private final String description;
    private final BigDecimal basePrice;
    private final boolean isOpen;
    private final List<OptionGroupReadModel> optionGroups;
    private final Instant createdAt;
    private final Instant updatedAt;
    
    public MenuDetailReadModel(String id, String shopId, String name, String description,
                              BigDecimal basePrice, boolean isOpen, List<OptionGroupReadModel> optionGroups,
                              Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.shopId = shopId;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.isOpen = isOpen;
        this.optionGroups = optionGroups;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public String getId() {
        return id;
    }
    
    public String getShopId() {
        return shopId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public BigDecimal getBasePrice() {
        return basePrice;
    }
    
    public boolean isOpen() {
        return isOpen;
    }
    
    public List<OptionGroupReadModel> getOptionGroups() {
        return optionGroups;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
}