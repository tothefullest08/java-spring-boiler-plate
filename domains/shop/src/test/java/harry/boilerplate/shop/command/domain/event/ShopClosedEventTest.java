package harry.boilerplate.shop.command.domain.event;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ShopClosedEvent 테스트
 */
class ShopClosedEventTest {
    
    @Test
    void 가게_영업_종료_이벤트_생성_성공() {
        // Given
        String shopId = "shop-123";
        String shopName = "맛있는 가게";
        String reason = "정기 휴무";
        
        // When
        ShopClosedEvent event = new ShopClosedEvent(shopId, shopName, reason);
        
        // Then
        assertThat(event.getEventId()).isNotNull();
        assertThat(event.getOccurredAt()).isNotNull();
        assertThat(event.getAggregateId()).isEqualTo(shopId);
        assertThat(event.getAggregateType()).isEqualTo("Shop");
        assertThat(event.getVersion()).isEqualTo(1);
        assertThat(event.getShopName()).isEqualTo(shopName);
        assertThat(event.getReason()).isEqualTo(reason);
    }
    
    @Test
    void 이벤트_ID는_고유해야_함() {
        // Given
        String shopId = "shop-123";
        String shopName = "맛있는 가게";
        String reason = "정기 휴무";
        
        // When
        ShopClosedEvent event1 = new ShopClosedEvent(shopId, shopName, reason);
        ShopClosedEvent event2 = new ShopClosedEvent(shopId, shopName, reason);
        
        // Then
        assertThat(event1.getEventId()).isNotEqualTo(event2.getEventId());
    }
    
    @Test
    void toString_메서드_정상_동작() {
        // Given
        String shopId = "shop-123";
        String shopName = "맛있는 가게";
        String reason = "정기 휴무";
        
        // When
        ShopClosedEvent event = new ShopClosedEvent(shopId, shopName, reason);
        String result = event.toString();
        
        // Then
        assertThat(result).contains("ShopClosedEvent");
        assertThat(result).contains(shopId);
        assertThat(result).contains(shopName);
        assertThat(result).contains(reason);
    }
}