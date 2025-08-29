package harry.boilerplate.shop.command.domain.valueObject;

import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.common.domain.entity.ValueObject;
import harry.boilerplate.shop.command.domain.exception.MenuDomainException;
import harry.boilerplate.shop.command.domain.exception.MenuErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import java.math.BigDecimal;
import java.util.List;

/**
 * 메뉴 옵션 값 객체
 * 불변 객체로 구현
 */
@Embeddable
public class Option extends ValueObject {
    
    @Column(name = "option_name")
    private String name;
    
    @Embedded
    private Money price;
    
    protected Option() {
        // JPA용 기본 생성자
    }
    
    public Option(String name, Money price) {
        if (name == null || name.trim().isEmpty()) {
            throw new MenuDomainException(MenuErrorCode.CURRENT_OPTION_NAME_REQUIRED);
        }
        if (price == null) {
            throw new MenuDomainException(MenuErrorCode.CURRENT_OPTION_PRICE_REQUIRED);
        }
        if (price.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new MenuDomainException(MenuErrorCode.INVALID_BASE_PRICE);
        }
        
        this.name = name.trim();
        this.price = price;
    }
    
    /**
     * 옵션 이름 변경 (새 인스턴스 반환)
     */
    public Option changeName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new MenuDomainException(MenuErrorCode.NEW_OPTION_NAME_REQUIRED);
        }
        return new Option(newName, this.price);
    }
    
    /**
     * 옵션 가격 변경 (새 인스턴스 반환)
     */
    public Option changePrice(Money newPrice) {
        if (newPrice == null) {
            throw new MenuDomainException(MenuErrorCode.NEW_OPTION_PRICE_REQUIRED);
        }
        return new Option(this.name, newPrice);
    }
    
    public String getName() {
        return name;
    }
    
    public Money getPrice() {
        return price;
    }
    
    /**
     * 유료 옵션인지 확인
     */
    public boolean isPaid() {
        return price != null && price.getAmount().compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * 무료 옵션인지 확인
     */
    public boolean isFree() {
        return !isPaid();
    }
    
    @Override
    protected Object[] getEqualityComponents() {
        return new Object[]{name, price};
    }
    
    @Override
    protected boolean equalsByValue(Object other) {
        Option that = (Option) other;
        return java.util.Objects.equals(this.name, that.name) &&
               java.util.Objects.equals(this.price, that.price);
    }
    
    @Override
    public String toString() {
        return "Option{" +
               "name='" + name + '\'' +
               ", price=" + price +
               '}';
    }
}