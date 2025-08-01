package harry.boilerplate.shop.domain.valueobject;

import harry.boilerplate.common.domain.entity.ValueObject;
import harry.boilerplate.shop.domain.aggregate.ShopDomainException;
import harry.boilerplate.shop.domain.aggregate.ShopErrorCode;
import jakarta.persistence.Embeddable;
import java.time.LocalTime;
import java.util.Objects;

/**
 * 영업시간을 나타내는 값 객체
 * 불변성을 보장하며 영업시간 관련 비즈니스 로직을 포함
 */
@Embeddable
public class BusinessHours extends ValueObject {

    @jakarta.persistence.Column(name = "open_time")
    private LocalTime openTime;
    
    @jakarta.persistence.Column(name = "close_time")
    private LocalTime closeTime;

    protected BusinessHours() {
        // JPA 기본 생성자
    }

    public BusinessHours(LocalTime openTime, LocalTime closeTime) {
        if (openTime == null || closeTime == null) {
            throw new ShopDomainException(ShopErrorCode.INVALID_OPERATING_HOURS);
        }
        if (openTime.equals(closeTime)) {
            throw new ShopDomainException(ShopErrorCode.INVALID_OPERATING_HOURS);
        }

        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    /**
     * 현재 시간이 영업시간 내인지 확인
     */
    public boolean isOpenAt(LocalTime currentTime) {
        if (currentTime == null) {
            return false;
        }

        // 일반적인 경우: 09:00 - 18:00
        if (openTime.isBefore(closeTime)) {
            return !currentTime.isBefore(openTime) && currentTime.isBefore(closeTime);
        }
        // 자정을 넘는 경우: 22:00 - 02:00
        else {
            return !currentTime.isBefore(openTime) || currentTime.isBefore(closeTime);
        }
    }

    /**
     * 영업시간을 조정한 새로운 BusinessHours 반환
     */
    public BusinessHours adjustHours(LocalTime newOpenTime, LocalTime newCloseTime) {
        return new BusinessHours(newOpenTime, newCloseTime);
    }

    public LocalTime getOpenTime() {
        return openTime;
    }

    public LocalTime getCloseTime() {
        return closeTime;
    }

    @Override
    protected boolean equalsByValue(Object other) {
        if (!(other instanceof BusinessHours)) {
            return false;
        }
        BusinessHours that = (BusinessHours) other;
        return Objects.equals(this.openTime, that.openTime) &&
                Objects.equals(this.closeTime, that.closeTime);
    }

    @Override
    protected Object[] getEqualityComponents() {
        return new Object[] { openTime, closeTime };
    }

    @Override
    public String toString() {
        return String.format("BusinessHours{openTime=%s, closeTime=%s}", openTime, closeTime);
    }
}