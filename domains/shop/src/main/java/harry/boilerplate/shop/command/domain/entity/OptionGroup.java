package harry.boilerplate.shop.command.domain.entity;

import harry.boilerplate.common.domain.entity.DomainEntity;
import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.shop.command.domain.aggregate.Menu;

import harry.boilerplate.shop.command.domain.exception.MenuDomainException;
import harry.boilerplate.shop.command.domain.exception.MenuErrorCode;
import harry.boilerplate.shop.command.domain.valueObject.Option;
import harry.boilerplate.shop.command.domain.valueObject.OptionGroupId;
import jakarta.persistence.*;
import java.util.*;

/**
 * 옵션그룹 도메인 엔티티
 * 메뉴의 옵션들을 그룹화하여 관리
 */
@Entity
@Table(name = "option_group")
public class OptionGroup extends DomainEntity<OptionGroup, OptionGroupId> {
    
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
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "option_group_options",
        joinColumns = @JoinColumn(name = "option_group_id")
    )
    private List<Option> options = new ArrayList<>();
    
    protected OptionGroup() {
        // JPA 기본 생성자
    }
    
    public OptionGroup(Menu menu, OptionGroupId id, String name, boolean required) {
        if (menu == null) {
            throw new MenuDomainException(MenuErrorCode.MENU_REQUIRED);
        }
        if (id == null) {
            throw new MenuDomainException(MenuErrorCode.OPTION_GROUP_ID_REQUIRED);
        }
        if (name == null || name.trim().isEmpty()) {
            throw new MenuDomainException(MenuErrorCode.NEW_OPTION_GROUP_NAME_REQUIRED);
        }
        
        this.menu = menu;
        this.id = id.getValue();
        this.name = name.trim();
        this.required = required;
        this.options = new ArrayList<>();
    }
    
    // 테스트용 생성자 (Menu 없이 생성)
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
    
    /**
     * 옵션 추가
     */
    public void addOption(Option option) {
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
        
        this.options.add(option);
    }
    
    /**
     * 옵션 제거
     */
    public void removeOption(String optionName, Money optionPrice) {
        if (optionName == null || optionName.trim().isEmpty()) {
            throw new MenuDomainException(MenuErrorCode.CURRENT_OPTION_NAME_REQUIRED);
        }
        if (optionPrice == null) {
            throw new MenuDomainException(MenuErrorCode.CURRENT_OPTION_PRICE_REQUIRED);
        }
        
        Option targetOption = options.stream()
            .filter(option -> option.getName().equals(optionName.trim()) && 
                             option.getPrice().equals(optionPrice))
            .findFirst()
            .orElseThrow(() -> new MenuDomainException(MenuErrorCode.OPTION_NOT_FOUND));
        
        this.options.remove(targetOption);
    }
    
    /**
     * 옵션그룹 이름 변경
     */
    public void changeName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new MenuDomainException(MenuErrorCode.NEW_OPTION_GROUP_NAME_REQUIRED);
        }
        
        this.name = newName.trim();
    }
    
    /**
     * 옵션 이름 변경
     */
    public void changeOptionName(String currentName, Money currentPrice, String newName) {
        if (currentName == null || currentName.trim().isEmpty()) {
            throw new MenuDomainException(MenuErrorCode.CURRENT_OPTION_NAME_REQUIRED);
        }
        if (currentPrice == null) {
            throw new MenuDomainException(MenuErrorCode.CURRENT_OPTION_PRICE_REQUIRED);
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new MenuDomainException(MenuErrorCode.NEW_OPTION_NAME_REQUIRED);
        }
        
        // 기존 옵션 찾기
        int targetIndex = -1;
        Option targetOption = null;
        for (int i = 0; i < options.size(); i++) {
            Option option = options.get(i);
            if (option.getName().equals(currentName.trim()) && 
                option.getPrice().equals(currentPrice)) {
                targetIndex = i;
                targetOption = option;
                break;
            }
        }
        
        if (targetOption == null) {
            throw new MenuDomainException(MenuErrorCode.OPTION_NOT_FOUND);
        }
        
        // Value Object이므로 새로운 인스턴스로 교체
        Option newOption = targetOption.changeName(newName);
        options.set(targetIndex, newOption);
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
    
    @Override
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
    public String toString() {
        return "OptionGroup{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", required=" + required +
                ", options=" + options +
                '}';
    }
}