package harry.boilerplate.shop.domain;

import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.common.domain.event.DomainEvent;
import harry.boilerplate.shop.domain.event.ShopClosedEvent;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import static org.assertj.core.api.Assertions.*;

class ShopTest {
    
    @Test
    void Shop_생성_성공() {
        // Given
        String name = "맛있는 식당";
        Money minOrderAmount = Money.of("15000");
        BusinessHours businessHours = new BusinessHours(
            LocalTime.of(9, 0), 
            LocalTime.of(22, 0)
        );
        
        // When
        Shop shop = new Shop(name, minOrderAmount, businessHours);
        
        // Then
        assertThat(shop.getName()).isEqualTo(name);
        assertThat(shop.getMinOrderAmount().getAmount()).isEqualByComparingTo(minOrderAmount.getAmount());
        assertThat(shop.getId()).isNotNull();
    }
    
    @Test
    void 영업시간_내_영업중_확인() {
        // Given
        BusinessHours businessHours = new BusinessHours(
            LocalTime.of(9, 0), 
            LocalTime.of(22, 0)
        );
        Shop shop = new Shop("테스트 식당", Money.of("10000"), businessHours);
        
        // When & Then
        assertThat(shop.isOpenAt(LocalTime.of(10, 0))).isTrue();
        assertThat(shop.isOpenAt(LocalTime.of(21, 59))).isTrue();
        assertThat(shop.isOpenAt(LocalTime.of(8, 59))).isFalse();
        assertThat(shop.isOpenAt(LocalTime.of(22, 0))).isFalse();
    }
    
    @Test
    void 가게_영업_종료_시_도메인_이벤트_발행() {
        // Given
        BusinessHours businessHours = new BusinessHours(
            LocalTime.of(9, 0), 
            LocalTime.of(22, 0)
        );
        Shop shop = new Shop("테스트 식당", Money.of("10000"), businessHours);
        String reason = "정기 휴무";
        
        // When
        shop.close(reason);
        
        // Then
        assertThat(shop.getDomainEvents()).hasSize(1);
        DomainEvent event = shop.getDomainEvents().get(0);
        assertThat(event).isInstanceOf(ShopClosedEvent.class);
        
        ShopClosedEvent shopClosedEvent = (ShopClosedEvent) event;
        assertThat(shopClosedEvent.getAggregateId()).isEqualTo(shop.getId().getValue());
        assertThat(shopClosedEvent.getAggregateType()).isEqualTo("Shop");
        assertThat(shopClosedEvent.getShopName()).isEqualTo(shop.getName());
        assertThat(shopClosedEvent.getReason()).isEqualTo(reason);
    }
    
    @Test
    void 가게_영업_종료_사유_없으면_예외_발생() {
        // Given
        BusinessHours businessHours = new BusinessHours(
            LocalTime.of(9, 0), 
            LocalTime.of(22, 0)
        );
        Shop shop = new Shop("테스트 식당", Money.of("10000"), businessHours);
        
        // When & Then
        assertThatThrownBy(() -> shop.close(null))
            .isInstanceOf(ShopDomainException.class)
            .extracting(e -> ((ShopDomainException) e).getErrorCode())
            .isEqualTo(ShopErrorCode.CLOSE_REASON_REQUIRED);
            
        assertThatThrownBy(() -> shop.close(""))
            .isInstanceOf(ShopDomainException.class)
            .extracting(e -> ((ShopDomainException) e).getErrorCode())
            .isEqualTo(ShopErrorCode.CLOSE_REASON_REQUIRED);
            
        assertThatThrownBy(() -> shop.close("   "))
            .isInstanceOf(ShopDomainException.class)
            .extracting(e -> ((ShopDomainException) e).getErrorCode())
            .isEqualTo(ShopErrorCode.CLOSE_REASON_REQUIRED);
    }
    
    @Test
    void Shop_생성_시_이름_null_또는_빈문자열_예외() {
        // Given
        Money minOrderAmount = Money.of("15000");
        BusinessHours businessHours = new BusinessHours(
            LocalTime.of(9, 0), 
            LocalTime.of(22, 0)
        );
        
        // When & Then
        assertThatThrownBy(() -> new Shop(null, minOrderAmount, businessHours))
            .isInstanceOf(ShopDomainException.class)
            .extracting(e -> ((ShopDomainException) e).getErrorCode())
            .isEqualTo(ShopErrorCode.SHOP_NAME_REQUIRED);
            
        assertThatThrownBy(() -> new Shop("", minOrderAmount, businessHours))
            .isInstanceOf(ShopDomainException.class)
            .extracting(e -> ((ShopDomainException) e).getErrorCode())
            .isEqualTo(ShopErrorCode.SHOP_NAME_REQUIRED);
            
        assertThatThrownBy(() -> new Shop("   ", minOrderAmount, businessHours))
            .isInstanceOf(ShopDomainException.class)
            .extracting(e -> ((ShopDomainException) e).getErrorCode())
            .isEqualTo(ShopErrorCode.SHOP_NAME_REQUIRED);
    }
    
    @Test
    void Shop_생성_시_최소주문금액_음수_예외() {
        // Given
        String name = "맛있는 식당";
        Money invalidMinOrderAmount = Money.of("-1000");
        BusinessHours businessHours = new BusinessHours(
            LocalTime.of(9, 0), 
            LocalTime.of(22, 0)
        );
        
        // When & Then
        assertThatThrownBy(() -> new Shop(name, invalidMinOrderAmount, businessHours))
            .isInstanceOf(ShopDomainException.class)
            .extracting(e -> ((ShopDomainException) e).getErrorCode())
            .isEqualTo(ShopErrorCode.INVALID_MIN_ORDER_AMOUNT);
    }
    
    @Test
    void 영업시간_조정_시_잘못된_시간_예외() {
        // Given
        BusinessHours businessHours = new BusinessHours(
            LocalTime.of(9, 0), 
            LocalTime.of(22, 0)
        );
        Shop shop = new Shop("테스트 식당", Money.of("10000"), businessHours);
        
        // When & Then - null 시간
        assertThatThrownBy(() -> shop.adjustBusinessHours(null, LocalTime.of(22, 0)))
            .isInstanceOf(ShopDomainException.class)
            .extracting(e -> ((ShopDomainException) e).getErrorCode())
            .isEqualTo(ShopErrorCode.INVALID_OPERATING_HOURS);
            
        assertThatThrownBy(() -> shop.adjustBusinessHours(LocalTime.of(9, 0), null))
            .isInstanceOf(ShopDomainException.class)
            .extracting(e -> ((ShopDomainException) e).getErrorCode())
            .isEqualTo(ShopErrorCode.INVALID_OPERATING_HOURS);
            
        // When & Then - 시작시간이 종료시간보다 늦은 경우
        assertThatThrownBy(() -> shop.adjustBusinessHours(LocalTime.of(22, 0), LocalTime.of(9, 0)))
            .isInstanceOf(ShopDomainException.class)
            .extracting(e -> ((ShopDomainException) e).getErrorCode())
            .isEqualTo(ShopErrorCode.INVALID_OPERATING_HOURS);
            
        // When & Then - 시작시간과 종료시간이 같은 경우
        assertThatThrownBy(() -> shop.adjustBusinessHours(LocalTime.of(9, 0), LocalTime.of(9, 0)))
            .isInstanceOf(ShopDomainException.class)
            .extracting(e -> ((ShopDomainException) e).getErrorCode())
            .isEqualTo(ShopErrorCode.INVALID_OPERATING_HOURS);
    }
    
    @Test
    void 최소주문금액_변경_시_음수_예외() {
        // Given
        BusinessHours businessHours = new BusinessHours(
            LocalTime.of(9, 0), 
            LocalTime.of(22, 0)
        );
        Shop shop = new Shop("테스트 식당", Money.of("10000"), businessHours);
        
        // When & Then
        assertThatThrownBy(() -> shop.changeMinOrderAmount(Money.of("-5000")))
            .isInstanceOf(ShopDomainException.class)
            .extracting(e -> ((ShopDomainException) e).getErrorCode())
            .isEqualTo(ShopErrorCode.INVALID_MIN_ORDER_AMOUNT);
    }
}