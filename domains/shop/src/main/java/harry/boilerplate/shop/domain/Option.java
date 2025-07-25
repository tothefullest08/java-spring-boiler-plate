package harry.boilerplate.shop.domain;

import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.common.domain.entity.ValueObject;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 옵션 값 객체
 * 옵션그룹 내의 개별 옵션을 나타냄
 */
@Embeddable
public class Option extends ValueObject {
    
    @Column(name = "option_name", nullable = false)
    private String name;
    
    @Column(name = "option_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;
    
    protected Option() {
        // JPA 기본 생성자
    }
    
    public Option(String name, Money price) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("옵션 이름은 필수입니다");
        }
        if (price == null) {
            throw new IllegalArgumentException("옵션 가격은 필수입니다");
        }
        if (price.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("옵션 가격은 0 이상이어야 합니다");
        }
        
        this.name = name.trim();
        this.price = price.getAmount();
    }
    
    /**
     * 옵션 이름 변경
     */
    public Option changeName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("새로운 옵션 이름은 필수입니다");
        }
        return new Option(newName, Money.of(this.price));
    }
    
    /**
     * 옵션 가격 변경
     */
    public Option changePrice(Money newPrice) {
        if (newPrice == null) {
            throw new IllegalArgumentException("새로운 옵션 가격은 필수입니다");
        }
        return new Option(this.name, newPrice);
    }
    
    /**
     * 유료 옵션인지 확인
     */
    public boolean isPaid() {
        return price.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * 무료 옵션인지 확인
     */
    public boolean isFree() {
        return price.compareTo(BigDecimal.ZERO) == 0;
    }
    
    // Getters
    public String getName() {
        return name;
    }
    
    public Money getPrice() {
        return Money.of(price);
    }
    
    @Override
    protected boolean equalsByValue(Object other) {
        Option option = (Option) other;
        return Objects.equals(name, option.name) && 
               Objects.equals(price, option.price);
    }
    
    @Override
    protected Object[] getEqualityComponents() {
        return new Object[]{name, price};
    }
    
    @Override
    public String toString() {
        return "Option{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}