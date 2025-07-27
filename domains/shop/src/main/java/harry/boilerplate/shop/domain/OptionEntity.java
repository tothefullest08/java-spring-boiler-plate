package harry.boilerplate.shop.domain;

import harry.boilerplate.common.domain.entity.BaseEntity;
import harry.boilerplate.common.domain.entity.Money;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Option JPA 엔티티
 * 데이터베이스 테이블과 매핑되는 엔티티
 */
@Entity
@Table(name = "option")
public class OptionEntity extends BaseEntity {
    
    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_group_id", nullable = false)
    private OptionGroupEntity optionGroup;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;
    
    protected OptionEntity() {
        // JPA 기본 생성자
    }
    
    public OptionEntity(OptionGroupEntity optionGroup, Option option) {
        this.id = UUID.randomUUID().toString();
        this.optionGroup = optionGroup;
        this.name = option.getName();
        this.price = option.getPrice().getAmount();
    }
    
    /**
     * 도메인 객체로 변환
     */
    public Option toDomainObject() {
        return new Option(this.name, Money.of(this.price));
    }
    
    /**
     * 도메인 객체로부터 업데이트
     */
    public void updateFromDomainObject(Option option) {
        this.name = option.getName();
        this.price = option.getPrice().getAmount();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public OptionGroupEntity getOptionGroup() {
        return optionGroup;
    }
    
    public void setOptionGroup(OptionGroupEntity optionGroup) {
        this.optionGroup = optionGroup;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}