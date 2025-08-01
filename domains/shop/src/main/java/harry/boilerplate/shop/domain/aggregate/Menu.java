package harry.boilerplate.shop.domain.aggregate;

import harry.boilerplate.common.domain.entity.AggregateRoot;
import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.shop.domain.entity.OptionGroup;
import harry.boilerplate.shop.domain.event.MenuOpenedEvent;
import harry.boilerplate.shop.domain.valueobject.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Menu 애그리게이트 루트
 * 메뉴 정보와 옵션그룹 관리를 담당
 * Requirements: 10.5 - BaseEntity 상속으로 공통 필드 관리
 */
@Entity
@Table(name = "menu")
public class Menu extends AggregateRoot<Menu, MenuId> {

    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "shop_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private String shopId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "base_price", precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "is_open", nullable = false)
    private boolean open = false;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OptionGroup> optionGroups = new ArrayList<>();

    protected Menu() {
        // JPA 기본 생성자
    }

    public Menu(ShopId shopId, String name, String description, Money basePrice) {
        if (shopId == null) {
            throw new MenuDomainException(MenuErrorCode.SHOP_ID_REQUIRED);
        }
        if (name == null || name.trim().isEmpty()) {
            throw new MenuDomainException(MenuErrorCode.MENU_NAME_REQUIRED);
        }
        if (basePrice == null) {
            throw new MenuDomainException(MenuErrorCode.BASE_PRICE_REQUIRED);
        }
        if (basePrice.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new MenuDomainException(MenuErrorCode.INVALID_BASE_PRICE);
        }

        this.id = UUID.randomUUID().toString();
        this.shopId = shopId.getValue();
        this.name = name.trim();
        this.description = description != null ? description.trim() : null;
        this.basePrice = basePrice.getAmount();
        this.open = false; // 초기 상태는 비공개 (Requirement 2.2)
        this.optionGroups = new ArrayList<>();
    }

    @Override
    public MenuId getId() {
        return new MenuId(this.id);
    }

    /**
     * 메뉴 공개
     * Requirements 2.3: 옵션그룹 최소 1개, 필수 옵션그룹 1~3개, 유료 옵션그룹 최소 1개
     */
    public void open() {
        if (this.open) {
            throw new MenuDomainException(MenuErrorCode.MENU_ALREADY_OPEN);
        }

        validateOpenConditions();
        this.open = true;
        
        // 도메인 이벤트 발행 (Requirements 2.3)
        addDomainEvent(new MenuOpenedEvent(
            this.id,
            this.shopId,
            this.name,
            this.description
        ));
    }

    /**
     * 메뉴 공개 조건 검증
     */
    private void validateOpenConditions() {
        // 1. 옵션그룹이 최소 1개 이상 존재해야 함
        if (optionGroups.isEmpty()) {
            throw new MenuDomainException(MenuErrorCode.INSUFFICIENT_OPTION_GROUPS);
        }

        // 2. 필수 옵션그룹이 1~3개 범위에 있어야 함
        long requiredGroupCount = optionGroups.stream()
            .filter(OptionGroup::isRequired)
            .count();

        if (requiredGroupCount < 1 || requiredGroupCount > 3) {
            throw new MenuDomainException(MenuErrorCode.INVALID_REQUIRED_OPTION_GROUP_COUNT);
        }

        // 3. 유료 옵션그룹이 최소 1개 있어야 함
        boolean hasPaidOptionGroup = optionGroups.stream()
            .anyMatch(OptionGroup::hasPaidOptions);

        if (!hasPaidOptionGroup) {
            throw new MenuDomainException(MenuErrorCode.NO_PAID_OPTION_GROUP);
        }
    }

    /**
     * 옵션그룹 추가
     * Requirement 3.1: 동일한 이름의 옵션그룹이 존재하지 않을 때만 추가
     * Requirement 3.2: 공개 상태에서 필수 옵션그룹 추가 시 최대 3개 제한
     */
    public void addOptionGroup(String name, boolean required) {
        if (name == null || name.trim().isEmpty()) {
            throw new MenuDomainException(MenuErrorCode.NEW_OPTION_GROUP_NAME_REQUIRED);
        }
        
        // 동일한 이름의 옵션그룹 존재 확인
        boolean nameExists = optionGroups.stream()
            .anyMatch(existing -> existing.getName().equals(name.trim()));

        if (nameExists) {
            throw new MenuDomainException(MenuErrorCode.DUPLICATE_OPTION_GROUP_NAME);
        }

        // 메뉴가 공개된 상태에서 필수 옵션그룹 추가 시 최대 3개 제한 확인
        if (this.open && required) {
            long currentRequiredCount = optionGroups.stream()
                .filter(OptionGroup::isRequired)
                .count();

            if (currentRequiredCount >= 3) {
                throw new MenuDomainException(MenuErrorCode.MAX_REQUIRED_OPTION_GROUPS_EXCEEDED);
            }
        }

        // OptionGroup 생성 및 추가
        OptionGroup optionGroup = new OptionGroup(this, OptionGroupId.generate(), name, required);
        this.optionGroups.add(optionGroup);
    }

    /**
     * 옵션그룹 이름 변경
     * Requirement 3.3: 새로운 이름이 동일 메뉴 내에서 유일할 때만 변경
     */
    public void changeOptionGroupName(OptionGroupId optionGroupId, String newName) {
        if (optionGroupId == null) {
            throw new MenuDomainException(MenuErrorCode.OPTION_GROUP_ID_REQUIRED);
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new MenuDomainException(MenuErrorCode.NEW_OPTION_GROUP_NAME_REQUIRED);
        }

        // 옵션그룹 찾기
        OptionGroup targetGroup = findOptionGroup(optionGroupId);

        // 새로운 이름이 다른 옵션그룹과 중복되는지 확인
        boolean nameExists = optionGroups.stream()
            .filter(group -> !group.getId().equals(optionGroupId))
            .anyMatch(group -> group.getName().equals(newName.trim()));

        if (nameExists) {
            throw new MenuDomainException(MenuErrorCode.DUPLICATE_OPTION_GROUP_NAME);
        }

        // 옵션그룹 이름 변경
        targetGroup.changeName(newName);
    }

    /**
     * 옵션 이름 변경
     * Requirement 3.5: 현재 이름과 가격으로 옵션을 식별하여 이름 변경
     */
    public void changeOptionName(OptionGroupId optionGroupId, String currentName, Money currentPrice, String newName) {
        if (optionGroupId == null) {
            throw new MenuDomainException(MenuErrorCode.OPTION_GROUP_ID_REQUIRED);
        }
        if (currentName == null || currentName.trim().isEmpty()) {
            throw new MenuDomainException(MenuErrorCode.CURRENT_OPTION_NAME_REQUIRED);
        }
        if (currentPrice == null) {
            throw new MenuDomainException(MenuErrorCode.CURRENT_OPTION_PRICE_REQUIRED);
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new MenuDomainException(MenuErrorCode.NEW_OPTION_NAME_REQUIRED);
        }

        // 옵션그룹 찾기
        OptionGroup targetGroup = findOptionGroup(optionGroupId);

        // 옵션 이름 변경
        targetGroup.changeOptionName(currentName, currentPrice, newName);
    }

    /**
     * 옵션그룹 삭제
     * Requirement 3.4: 메뉴가 공개된 상태에서는 최소 조건을 만족해야 함
     */
    public void removeOptionGroup(OptionGroupId optionGroupId) {
        if (optionGroupId == null) {
            throw new MenuDomainException(MenuErrorCode.OPTION_GROUP_ID_REQUIRED);
        }

        // 옵션그룹 찾기
        OptionGroup targetGroup = findOptionGroup(optionGroupId);

        // 메뉴가 공개된 상태에서는 삭제 후에도 최소 조건을 만족해야 함
        if (this.open) {
            validateRemovalConditions(targetGroup);
        }

        // 옵션그룹 제거
        this.optionGroups.removeIf(group -> group.getId().equals(optionGroupId));
    }

    /**
     * 옵션그룹 삭제 시 최소 조건 검증
     */
    private void validateRemovalConditions(OptionGroup groupToRemove) {
        List<OptionGroup> remainingGroups = optionGroups.stream()
            .filter(group -> !group.getId().equals(groupToRemove.getId()))
            .collect(Collectors.toList());

        // 1. 최소 1개의 옵션그룹이 남아있어야 함
        if (remainingGroups.isEmpty()) {
            throw new MenuDomainException(MenuErrorCode.CANNOT_DELETE_REQUIRED_OPTION_GROUP);
        }

        // 2. 최소 1개의 필수 옵션그룹이 남아있어야 함
        long remainingRequiredCount = remainingGroups.stream()
            .filter(OptionGroup::isRequired)
            .count();

        if (remainingRequiredCount < 1) {
            throw new MenuDomainException(MenuErrorCode.CANNOT_DELETE_REQUIRED_OPTION_GROUP);
        }

        // 3. 최소 1개의 유료 옵션그룹이 남아있어야 함
        boolean hasRemainingPaidGroup = remainingGroups.stream()
            .anyMatch(OptionGroup::hasPaidOptions);

        if (!hasRemainingPaidGroup) {
            throw new MenuDomainException(MenuErrorCode.CANNOT_DELETE_REQUIRED_OPTION_GROUP);
        }
    }

    /**
     * 옵션그룹 찾기
     */
    private OptionGroup findOptionGroup(OptionGroupId optionGroupId) {
        return optionGroups.stream()
            .filter(group -> group.getId().equals(optionGroupId))
            .findFirst()
            .orElseThrow(() -> new MenuDomainException(MenuErrorCode.OPTION_GROUP_NOT_FOUND));
    }

    // Getters
    public ShopId getShopId() {
        return new ShopId(shopId);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Money getBasePrice() {
        return Money.of(basePrice);
    }

    public boolean isOpen() {
        return open;
    }

    public List<OptionGroup> getOptionGroups() {
        return new ArrayList<>(optionGroups);
    }
}