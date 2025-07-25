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
            throw new IllegalArgumentException("옵션그룹 ID는 필수입니다");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("옵션그룹 이름은 필수입니다");
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
            throw new IllegalArgumentException("옵션은 필수입니다");
        }
        
        // 중복 옵션 확인 (이름과 가격이 모두 같은 경우)
        boolean exists = options.stream()
            .anyMatch(existingOption -> 
                existingOption.getName().equals(option.getName()) && 
                existingOption.getPrice().equals(option.getPrice()));
        
        if (exists) {
            throw new IllegalArgumentException("동일한 이름과 가격의 옵션이 이미 존재합니다: " + option.getName());
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
            throw new IllegalArgumentException("옵션 이름은 필수입니다");
        }
        if (optionPrice == null) {
            throw new IllegalArgumentException("옵션 가격은 필수입니다");
        }
        
        List<Option> newOptions = options.stream()
            .filter(option -> !(option.getName().equals(optionName.trim()) && 
                               option.getPrice().equals(optionPrice)))
            .collect(Collectors.toList());
        
        if (newOptions.size() == options.size()) {
            throw new IllegalArgumentException("제거할 옵션을 찾을 수 없습니다: " + optionName);
        }
        
        return new OptionGroup(new OptionGroupId(this.id), this.name, this.required, newOptions);
    }
    
    /**
     * 옵션그룹 이름 변경
     */
    public OptionGroup changeName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("새로운 옵션그룹 이름은 필수입니다");
        }
        
        return new OptionGroup(new OptionGroupId(this.id), newName, this.required, this.options);
    }
    
    /**
     * 옵션 이름 변경
     */
    public OptionGroup changeOptionName(String currentName, Money currentPrice, String newName) {
        if (currentName == null || currentName.trim().isEmpty()) {
            throw new IllegalArgumentException("현재 옵션 이름은 필수입니다");
        }
        if (currentPrice == null) {
            throw new IllegalArgumentException("현재 옵션 가격은 필수입니다");
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("새로운 옵션 이름은 필수입니다");
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
            throw new IllegalArgumentException("변경할 옵션을 찾을 수 없습니다: " + currentName);
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