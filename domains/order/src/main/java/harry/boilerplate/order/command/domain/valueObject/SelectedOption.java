package harry.boilerplate.order.command.domain.valueObject;

import harry.boilerplate.common.domain.entity.ValueObject;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 선택된 옵션 정보를 저장하는 값 객체
 * 주문 시점의 옵션 정보를 스냅샷으로 저장
 */
@Embeddable
public class SelectedOption extends ValueObject {
    
    @Column(name = "option_id", columnDefinition = "VARCHAR(36)")
    private String optionId;
    
    @Column(name = "option_name", nullable = false)
    private String optionName;
    
    @Column(name = "option_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal optionPrice;
    
    // JPA 기본 생성자
    protected SelectedOption() {
    }
    
    public SelectedOption(OptionId optionId, String optionName, BigDecimal optionPrice) {
        if (optionId == null) {
            throw new IllegalArgumentException("옵션 ID는 필수입니다");
        }
        if (optionName == null || optionName.trim().isEmpty()) {
            throw new IllegalArgumentException("옵션 이름은 필수입니다");
        }
        if (optionPrice == null) {
            throw new IllegalArgumentException("옵션 가격은 필수입니다");
        }
        
        this.optionId = optionId.getValue();
        this.optionName = optionName.trim();
        this.optionPrice = optionPrice;
    }
    
    public OptionId getOptionId() {
        return OptionId.of(optionId);
    }
    
    public String getOptionName() {
        return optionName;
    }
    
    public BigDecimal getOptionPrice() {
        return optionPrice;
    }
    
    @Override
    protected Object[] getEqualityComponents() {
        return new Object[]{optionId, optionName, optionPrice};
    }
    
    @Override
    protected boolean equalsByValue(Object other) {
        if (!(other instanceof SelectedOption)) {
            return false;
        }
        SelectedOption that = (SelectedOption) other;
        return Objects.equals(this.optionId, that.optionId) &&
               Objects.equals(this.optionName, that.optionName) &&
               Objects.equals(this.optionPrice, that.optionPrice);
    }
    
    @Override
    public String toString() {
        return "SelectedOption{" +
               "optionId='" + optionId + '\'' +
               ", optionName='" + optionName + '\'' +
               ", optionPrice=" + optionPrice +
               '}';
    }
}