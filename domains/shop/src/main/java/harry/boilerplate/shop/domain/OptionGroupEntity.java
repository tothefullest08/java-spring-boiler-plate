package harry.boilerplate.shop.domain;

import harry.boilerplate.common.domain.entity.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * OptionGroup JPA 엔티티
 * 데이터베이스 테이블과 매핑되는 엔티티
 */
@Entity
@Table(name = "option_group")
public class OptionGroupEntity extends BaseEntity {
    
    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "is_required", nullable = false)
    private boolean required;
    
    @OneToMany(mappedBy = "optionGroup", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OptionEntity> optionEntities = new ArrayList<>();
    
    protected OptionGroupEntity() {
        // JPA 기본 생성자
    }
    
    public OptionGroupEntity(Menu menu, OptionGroup optionGroup) {
        this.id = optionGroup.getId().getValue();
        this.menu = menu;
        this.name = optionGroup.getName();
        this.required = optionGroup.isRequired();
        
        // Option 엔티티들 생성
        this.optionEntities = new ArrayList<>();
        for (Option option : optionGroup.getOptions()) {
            this.optionEntities.add(new OptionEntity(this, option));
        }
    }
    
    /**
     * 도메인 객체로 변환
     */
    public OptionGroup toDomainObject() {
        List<Option> options = new ArrayList<>();
        for (OptionEntity optionEntity : optionEntities) {
            options.add(optionEntity.toDomainObject());
        }
        
        return new OptionGroup(
            new OptionGroupId(this.id),
            this.name,
            this.required,
            options
        );
    }
    
    /**
     * 도메인 객체로부터 업데이트
     */
    public void updateFromDomainObject(OptionGroup optionGroup) {
        this.name = optionGroup.getName();
        this.required = optionGroup.isRequired();
        
        // 기존 옵션 엔티티들 제거
        this.optionEntities.clear();
        
        // 새로운 옵션 엔티티들 추가
        for (Option option : optionGroup.getOptions()) {
            this.optionEntities.add(new OptionEntity(this, option));
        }
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public Menu getMenu() {
        return menu;
    }
    
    public void setMenu(Menu menu) {
        this.menu = menu;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public List<OptionEntity> getOptionEntities() {
        return optionEntities;
    }
    
    public void setOptionEntities(List<OptionEntity> optionEntities) {
        this.optionEntities = optionEntities;
    }
}