package harry.boilerplate.shop.application.query.readmodel;

import java.math.BigDecimal;

/**
 * Option Read Model
 * 옵션 조회 시 사용되는 불변 데이터 객체
 */
public class OptionReadModel {
    private final String name;
    private final BigDecimal price;
    private final boolean isPaid;
    
    public OptionReadModel(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
        this.isPaid = price.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public String getName() {
        return name;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public boolean isPaid() {
        return isPaid;
    }
}