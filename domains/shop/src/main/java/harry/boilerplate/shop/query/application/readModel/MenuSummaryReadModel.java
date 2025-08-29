package harry.boilerplate.shop.query.application.readModel;

import java.math.BigDecimal;

/**
 * Menu 요약 정보 Read Model
 * 메뉴 목록 조회 시 사용되는 불변 데이터 객체
 */
public class MenuSummaryReadModel {
    private final String id;
    private final String shopId;
    private final String name;
    private final String description;
    private final BigDecimal basePrice;
    private final boolean isOpen;
    private final int optionGroupCount;
    
    public MenuSummaryReadModel(String id, String shopId, String name, String description,
                               BigDecimal basePrice, boolean isOpen, int optionGroupCount) {
        this.id = id;
        this.shopId = shopId;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.isOpen = isOpen;
        this.optionGroupCount = optionGroupCount;
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
    
    public int getOptionGroupCount() {
        return optionGroupCount;
    }
}