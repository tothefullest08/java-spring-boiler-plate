package harry.boilerplate.shop.domain.event;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MenuOpenedEvent 테스트
 */
class MenuOpenedEventTest {
    
    @Test
    void 메뉴_공개_이벤트_생성_성공() {
        // Given
        String menuId = "menu-123";
        String shopId = "shop-456";
        String menuName = "삼겹살";
        String description = "맛있는 삼겹살";
        
        // When
        MenuOpenedEvent event = new MenuOpenedEvent(menuId, shopId, menuName, description);
        
        // Then
        assertThat(event.getEventId()).isNotNull();
        assertThat(event.getOccurredAt()).isNotNull();
        assertThat(event.getAggregateId()).isEqualTo(menuId);
        assertThat(event.getAggregateType()).isEqualTo("Menu");
        assertThat(event.getVersion()).isEqualTo(1);
        assertThat(event.getShopId()).isEqualTo(shopId);
        assertThat(event.getMenuName()).isEqualTo(menuName);
        assertThat(event.getDescription()).isEqualTo(description);
    }
    
    @Test
    void 이벤트_ID는_고유해야_함() {
        // Given
        String menuId = "menu-123";
        String shopId = "shop-456";
        String menuName = "삼겹살";
        String description = "맛있는 삼겹살";
        
        // When
        MenuOpenedEvent event1 = new MenuOpenedEvent(menuId, shopId, menuName, description);
        MenuOpenedEvent event2 = new MenuOpenedEvent(menuId, shopId, menuName, description);
        
        // Then
        assertThat(event1.getEventId()).isNotEqualTo(event2.getEventId());
    }
    
    @Test
    void toString_메서드_정상_동작() {
        // Given
        String menuId = "menu-123";
        String shopId = "shop-456";
        String menuName = "삼겹살";
        String description = "맛있는 삼겹살";
        
        // When
        MenuOpenedEvent event = new MenuOpenedEvent(menuId, shopId, menuName, description);
        String result = event.toString();
        
        // Then
        assertThat(result).contains("MenuOpenedEvent");
        assertThat(result).contains(menuId);
        assertThat(result).contains(shopId);
        assertThat(result).contains(menuName);
        assertThat(result).contains(description);
    }
}