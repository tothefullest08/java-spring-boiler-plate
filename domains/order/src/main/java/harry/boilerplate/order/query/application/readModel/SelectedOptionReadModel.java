package harry.boilerplate.order.query.application.readmodel;

import java.math.BigDecimal;

/**
 * 선택된 옵션 정보를 위한 Read Model
 */
public class SelectedOptionReadModel {
    
    private final String optionId;
    private final String optionName;
    private final BigDecimal optionPrice;
    
    public SelectedOptionReadModel(String optionId, String optionName, BigDecimal optionPrice) {
        this.optionId = optionId;
        this.optionName = optionName;
        this.optionPrice = optionPrice;
    }
    
    public String getOptionId() {
        return optionId;
    }
    
    public String getOptionName() {
        return optionName;
    }
    
    public BigDecimal getOptionPrice() {
        return optionPrice;
    }
}