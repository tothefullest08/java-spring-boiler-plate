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
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("영업 종료 사유는 필수입니다");
            
        assertThatThrownBy(() -> shop.close(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("영업 종료 사유는 필수입니다");
            
        assertThatThrownBy(() -> shop.close("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("영업 종료 사유는 필수입니다");
    }
}