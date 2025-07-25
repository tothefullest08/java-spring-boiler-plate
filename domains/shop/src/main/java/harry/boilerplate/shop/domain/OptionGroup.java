package harry.boilerplate.shop.domain;

import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.common.domain.entity.ValueObject;
import jakarta.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 옵션그룹 값 객체
 * 메뉴의 옵션들을 그룹화하여 관리
 */
@Embeddable
public class OptionGroup extends ValueObject {
    
    @Column(name = "option_group_id", nullable = false)
    private String id;
    
    @Column(name = "option_group_name", nullable = false)
    private String name;
    
    @Column(name = "is_required", nullable = false)
    private boolean required;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "menu_option", 
        joinColumns = @JoinColumn(name = "option_group_id", referencedColumnName = "option_group_id")
    )
    private List<Option> options = new ArrayList<>();
    
    protected OptionGroup() {
        // JPA 기본 생성자
    }
    
    public OptionGroup(OptionGroupId id, String name, boolean required) {
        if (id == null) {
            throw new MenuDomainException(MenuErrorCode.OPTION_GROUP_ID_REQUIRED);
        }
        if (name == null || name.trim().isEmpty()) {
            throw new MenuDomainException(MenuErrorCode.NEW_OPTION_GROUP_NAME_REQUIRED);
        }
        
        this.id = id.getValue();
        this.name = name.trim();
        this.required = required;
        this.options = new ArrayList<>();
    }
    
    public OptionGroup(OptionGroupId id, String name, boolean required, List<Option> options) {
        this(id, name, required);
        if (options != null) {
            this.options = new ArrayList<>(options);
        }
    }
    
    /**
     * 옵션 추가
     */
    public OptionGroup addOption(Option option) {
        if (option == null) {
            throw new MenuDomainException(MenuErrorCode.OPTION_GROUP_REQUIRED);
        }
        
        // 중복 옵션 확인 (이름과 가격이 모두 같은 경우)
        boolean exists = options.stream()
            .anyMatch(existingOption -> 
                existingOption.getName().equals(option.getName()) && 
                existingOption.getPrice().equals(option.getPrice()));
        
        if (exists) {
            throw new MenuDomainException(MenuErrorCode.DUPLICATE_OPTION_GROUP_NAME);
        }
        
        List<Option> newOptions = new ArrayList<>(this.options);
        newOptions.add(option);
        
        return new OptionGroup(new OptionGroupId(this.id), this.name, this.required, newOptions);
    }
    
    /**
     * 옵션 제거
     */
    public OptionGroup removeOption(String optionName, Money optionPrice) {
        if (optionName == null || optionName.trim().isEmpty()) {
            throw new MenuDomainException(MenuErrorCode.CURRENT_OPTION_NAME_REQUIRED);
        }
        if (optionPrice == null) {
            throw new MenuDomainException(MenuErrorCode.CURRENT_OPTION_PRICE_REQUIRED);
        }
        
        List<Option> newOptions = options.stream()
            .filter(option -> !(option.getName().equals(optionName.trim()) && 
                               option.getPrice().equals(optionPrice)))
            .collect(Collectors.toList());
        
        if (newOptions.size() == options.size()) {
            throw new MenuDomainException(MenuErrorCode.OPTION_NOT_FOUND);
        }
        
        return new OptionGroup(new OptionGroupId(this.id), this.name, this.required, newOptions);
    }
    
    /**
     * 옵션그룹 이름 변경
     */
    public OptionGroup changeName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new MenuDomainException(MenuErrorCode.NEW_OPTION_GROUP_NAME_REQUIRED);
        }
        
        return new OptionGroup(new OptionGroupId(this.id), newName, this.required, this.options);
    }
    
    /**
     * 옵션 이름 변경
     */
    public OptionGroup changeOptionName(String currentName, Money currentPrice, String newName) {
        if (currentName == null || currentName.trim().isEmpty()) {
            throw new MenuDomainException(MenuErrorCode.CURRENT_OPTION_NAME_REQUIRED);
        }
        if (currentPrice == null) {
            throw new MenuDomainException(MenuErrorCode.CURRENT_OPTION_PRICE_REQUIRED);
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new MenuDomainException(MenuErrorCode.NEW_OPTION_NAME_REQUIRED);
        }
        
        List<Option> newOptions = options.stream()
            .map(option -> {
                if (option.getName().equals(currentName.trim()) && 
                    option.getPrice().equals(currentPrice)) {
                    return option.changeName(newName);
                }
                return option;
            })
            .collect(Collectors.toList());
        
        // 변경된 옵션이 있는지 확인
        boolean changed = !newOptions.equals(options);
        if (!changed) {
            throw new MenuDomainException(MenuErrorCode.OPTION_NOT_FOUND);
        }
        
        return new OptionGroup(new OptionGroupId(this.id), this.name, this.required, newOptions);
    }
    
    /**
     * 유료 옵션이 있는지 확인
     */
    public boolean hasPaidOptions() {
        return options.stream().anyMatch(Option::isPaid);
    }
    
    /**
     * 옵션 개수 반환
     */
    public int getOptionCount() {
        return options.size();
    }
    
    /**
     * 빈 옵션그룹인지 확인
     */
    public boolean isEmpty() {
        return options.isEmpty();
    }
    
    // Getters
    public OptionGroupId getId() {
        return new OptionGroupId(id);
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public List<Option> getOptions() {
        return new ArrayList<>(options);
    }
    
    @Override
    protected boolean equalsByValue(Object other) {
        OptionGroup that = (OptionGroup) other;
        return required == that.required &&
               Objects.equals(id, that.id) &&
               Objects.equals(name, that.name) &&
               Objects.equals(options, that.options);
    }
    
    @Override
    protected Object[] getEqualityComponents() {
        return new Object[]{id, name, required, options};
    }
    
    @Override
    public String toString() {
        return "OptionGroup{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", required=" + required +
                ", options=" + options +
                '}';
    }
}