package harry.boilerplate.shop.domain;

import harry.boilerplate.common.domain.entity.AggregateRoot;
import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.shop.domain.event.ShopClosedEvent;
import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Shop 애그리게이트 루트
 * 가게 정보와 영업시간 관리를 담당
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
    @AttributeOverrides({
            @AttributeOverride(name = "openTime", column = @Column(name = "open_time")),
            @AttributeOverride(name = "closeTime", column = @Column(name = "close_time"))
    })
    private BusinessHours businessHours;

    protected Shop() {
        // JPA 기본 생성자
    }

    public Shop(String name, Money minOrderAmount, BusinessHours businessHours) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
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
        this.businessHours = new BusinessHours(openTime, closeTime);
    }

    /**
     * 최소 주문 금액 변경
     */
    public void changeMinOrderAmount(Money newMinOrderAmount) {
        this.minOrderAmount = newMinOrderAmount != null ? newMinOrderAmount.getAmount() : null;
    }

    /**
     * 가게 영업 종료
     * Requirements 1.5: 가게 영업 상태 변경 시 이벤트 발행
     */
    public void close(String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("영업 종료 사유는 필수입니다");
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