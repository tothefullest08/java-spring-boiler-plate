package harry.boilerplate.shop.domain.aggregate;

import harry.boilerplate.common.domain.entity.AggregateRoot;
import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.shop.domain.event.ShopClosedEvent;
import harry.boilerplate.shop.domain.valueobject.*;
import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Shop 애그리게이트 루트
 * 가게 정보와 영업시간 관리를 담당
 * Requirements: 10.5 - BaseEntity 상속으로 공통 필드 관리
 */
@Entity
@Table(name = "shop")
public class Shop extends AggregateRoot<Shop, ShopId> {

    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "min_order_amount", precision = 10, scale = 2)
    private java.math.BigDecimal minOrderAmount;

    @Embedded
    private BusinessHours businessHours;

    protected Shop() {
        // JPA 기본 생성자
    }

    public Shop(String name, Money minOrderAmount, BusinessHours businessHours) {
        if (name == null || name.trim().isEmpty()) {
            throw new ShopDomainException(ShopErrorCode.SHOP_NAME_REQUIRED);
        }
        if (minOrderAmount != null && minOrderAmount.getAmount().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new ShopDomainException(ShopErrorCode.INVALID_MIN_ORDER_AMOUNT);
        }

        this.id = UUID.randomUUID().toString();
        this.name = name.trim();
        this.minOrderAmount = minOrderAmount != null ? minOrderAmount.getAmount() : null;
        this.businessHours = businessHours;
    }

    @Override
    public ShopId getId() {
        return new ShopId(this.id);
    }

    /**
     * 현재 시간 기준으로 영업 중인지 확인
     */
    public boolean isOpen() {
        if (businessHours == null) {
            return false;
        }
        return businessHours.isOpenAt(LocalTime.now());
    }

    /**
     * 특정 시간에 영업 중인지 확인
     */
    public boolean isOpenAt(LocalTime time) {
        if (businessHours == null) {
            return false;
        }
        return businessHours.isOpenAt(time);
    }

    /**
     * 영업시간 조정
     */
    public void adjustBusinessHours(LocalTime openTime, LocalTime closeTime) {
        if (openTime == null || closeTime == null) {
            throw new ShopDomainException(ShopErrorCode.INVALID_OPERATING_HOURS);
        }
        if (openTime.isAfter(closeTime) || openTime.equals(closeTime)) {
            throw new ShopDomainException(ShopErrorCode.INVALID_OPERATING_HOURS);
        }

        this.businessHours = new BusinessHours(openTime, closeTime);
    }

    /**
     * 최소 주문 금액 변경
     */
    public void changeMinOrderAmount(Money newMinOrderAmount) {
        if (newMinOrderAmount != null && newMinOrderAmount.getAmount().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new ShopDomainException(ShopErrorCode.INVALID_MIN_ORDER_AMOUNT);
        }

        this.minOrderAmount = newMinOrderAmount != null ? newMinOrderAmount.getAmount() : null;
    }

    /**
     * 가게 영업 종료
     * Requirements 1.5: 가게 영업 상태 변경 시 이벤트 발행
     */
    public void close(String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new ShopDomainException(ShopErrorCode.CLOSE_REASON_REQUIRED);
        }

        // 도메인 이벤트 발행 (Requirements 1.5)
        addDomainEvent(new ShopClosedEvent(
            this.id,
            this.name,
            reason.trim()
        ));
    }

    // Getters
    public String getName() {
        return name;
    }

    public Money getMinOrderAmount() {
        return minOrderAmount != null ? Money.of(minOrderAmount) : Money.zero();
    }

    public BusinessHours getBusinessHours() {
        return businessHours;
    }
}