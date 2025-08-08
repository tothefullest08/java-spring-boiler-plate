package harry.boilerplate.order.domain;

import harry.boilerplate.order.domain.entity.CartLineItem;
import harry.boilerplate.order.domain.valueObject.*;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * CartLineItem ValueObject 테스트
 */
class CartLineItemTest {
    
    @Test
    void 장바구니_라인_아이템_생성_성공() {
        // Given
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"), OptionId.of("option-2"));
        int quantity = 2;
        
        // When
        CartLineItem item = new CartLineItem(menuId, options, quantity);
        
        // Then
        assertThat(item.getMenuId()).isEqualTo(menuId);
        assertThat(item.getSelectedOptions()).containsExactlyElementsOf(options);
        assertThat(item.getQuantity()).isEqualTo(quantity);
    }
    
    @Test
    void 장바구니_라인_아이템_생성_실패_메뉴ID_null() {
        // Given
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));
        
        // When & Then
        assertThatThrownBy(() -> new CartLineItem(null, options, 1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("메뉴 ID는 필수입니다");
    }
    
    @Test
    void 장바구니_라인_아이템_생성_실패_옵션_null() {
        // Given
        MenuId menuId = MenuId.of("menu-1");
        
        // When & Then
        assertThatThrownBy(() -> new CartLineItem(menuId, null, 1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("선택된 옵션 목록은 필수입니다");
    }
    
    @Test
    void 장바구니_라인_아이템_생성_실패_수량_0이하() {
        // Given
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));
        
        // When & Then
        assertThatThrownBy(() -> new CartLineItem(menuId, options, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("수량은 1개 이상이어야 합니다");
    }
    
    @Test
    void 동일한_메뉴와_옵션_조합_확인_성공() {
        // Given
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"), OptionId.of("option-2"));
        
        CartLineItem item1 = new CartLineItem(menuId, options, 1);
        CartLineItem item2 = new CartLineItem(menuId, options, 2);
        
        // When & Then
        assertThat(item1.isSameMenuAndOptions(item2)).isTrue();
    }
    
    @Test
    void 다른_메뉴_조합_확인_실패() {
        // Given
        MenuId menuId1 = MenuId.of("menu-1");
        MenuId menuId2 = MenuId.of("menu-2");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));
        
        CartLineItem item1 = new CartLineItem(menuId1, options, 1);
        CartLineItem item2 = new CartLineItem(menuId2, options, 1);
        
        // When & Then
        assertThat(item1.isSameMenuAndOptions(item2)).isFalse();
    }
    
    @Test
    void 다른_옵션_조합_확인_실패() {
        // Given
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options1 = Arrays.asList(OptionId.of("option-1"));
        List<OptionId> options2 = Arrays.asList(OptionId.of("option-2"));
        
        CartLineItem item1 = new CartLineItem(menuId, options1, 1);
        CartLineItem item2 = new CartLineItem(menuId, options2, 1);
        
        // When & Then
        assertThat(item1.isSameMenuAndOptions(item2)).isFalse();
    }
    
    @Test
    void 동일한_메뉴와_옵션_조합_병합_성공() {
        // Given
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));
        
        CartLineItem item1 = new CartLineItem(menuId, options, 2);
        CartLineItem item2 = new CartLineItem(menuId, options, 3);
        
        // When
        CartLineItem combined = item1.combine(item2);
        
        // Then
        assertThat(combined.getQuantity()).isEqualTo(5);
        assertThat(combined.getMenuId()).isEqualTo(menuId);
        assertThat(combined.getSelectedOptions()).containsExactlyElementsOf(options);
    }
    
    @Test
    void 다른_메뉴와_옵션_조합_병합_실패() {
        // Given
        MenuId menuId1 = MenuId.of("menu-1");
        MenuId menuId2 = MenuId.of("menu-2");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));
        
        CartLineItem item1 = new CartLineItem(menuId1, options, 1);
        CartLineItem item2 = new CartLineItem(menuId2, options, 1);
        
        // When & Then
        assertThatThrownBy(() -> item1.combine(item2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("동일한 메뉴와 옵션 조합만 병합할 수 있습니다");
    }
    
    @Test
    void 수량_변경_성공() {
        // Given
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));
        CartLineItem item = new CartLineItem(menuId, options, 1);
        
        // When
        item.changeQuantity(5);
        
        // Then
        assertThat(item.getQuantity()).isEqualTo(5);
        assertThat(item.getMenuId()).isEqualTo(menuId);
        assertThat(item.getSelectedOptions()).containsExactlyElementsOf(options);
    }
}