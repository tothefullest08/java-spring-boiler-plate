package harry.boilerplate.order.domain;

import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.order.domain.entity.CartLineItem;
import harry.boilerplate.order.domain.entity.OrderLineItem;
import harry.boilerplate.order.domain.valueObject.*;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * OrderLineItem ValueObject 테스트
 */
class OrderLineItemTest {
    
    @Test
    void 주문_라인_아이템_생성_성공() {
        // Given
        MenuId menuId = MenuId.of("menu-1");
        String menuName = "삼겹살";
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"), OptionId.of("option-2"));
        List<String> optionNames = Arrays.asList("매운맛", "곱빼기");
        int quantity = 2;
        Money linePrice = Money.of(20000);
        
        // When
        OrderLineItem item = new OrderLineItem(menuId, menuName, options, optionNames, quantity, linePrice);
        
        // Then
        assertThat(item.getMenuId()).isEqualTo(menuId);
        assertThat(item.getMenuName()).isEqualTo(menuName);
        assertThat(item.getSelectedOptions()).containsExactlyElementsOf(options);
        assertThat(item.getSelectedOptionNames()).containsExactlyElementsOf(optionNames);
        assertThat(item.getQuantity()).isEqualTo(quantity);
        assertThat(item.getLinePrice()).isEqualTo(linePrice);
        assertThat(item.getTotalPrice()).isEqualTo(linePrice);
    }
    
    @Test
    void 주문_라인_아이템_생성_실패_메뉴ID_null() {
        // Given
        String menuName = "삼겹살";
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));
        List<String> optionNames = Arrays.asList("매운맛");
        Money linePrice = Money.of(10000);
        
        // When & Then
        assertThatThrownBy(() -> new OrderLineItem(null, menuName, options, optionNames, 1, linePrice))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("메뉴 ID는 필수입니다");
    }
    
    @Test
    void 주문_라인_아이템_생성_실패_메뉴이름_null() {
        // Given
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));
        List<String> optionNames = Arrays.asList("매운맛");
        Money linePrice = Money.of(10000);
        
        // When & Then
        assertThatThrownBy(() -> new OrderLineItem(menuId, null, options, optionNames, 1, linePrice))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("메뉴 이름은 필수입니다");
    }
    
    @Test
    void 주문_라인_아이템_생성_실패_메뉴이름_빈문자열() {
        // Given
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));
        List<String> optionNames = Arrays.asList("매운맛");
        Money linePrice = Money.of(10000);
        
        // When & Then
        assertThatThrownBy(() -> new OrderLineItem(menuId, "  ", options, optionNames, 1, linePrice))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("메뉴 이름은 필수입니다");
    }
    
    @Test
    void 주문_라인_아이템_생성_실패_수량_0이하() {
        // Given
        MenuId menuId = MenuId.of("menu-1");
        String menuName = "삼겹살";
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));
        List<String> optionNames = Arrays.asList("매운맛");
        Money linePrice = Money.of(10000);
        
        // When & Then
        assertThatThrownBy(() -> new OrderLineItem(menuId, menuName, options, optionNames, 0, linePrice))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("수량은 1개 이상이어야 합니다");
    }
    
    @Test
    void 주문_라인_아이템_생성_실패_가격_null() {
        // Given
        MenuId menuId = MenuId.of("menu-1");
        String menuName = "삼겹살";
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));
        List<String> optionNames = Arrays.asList("매운맛");
        
        // When & Then
        assertThatThrownBy(() -> new OrderLineItem(menuId, menuName, options, optionNames, 1, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("라인 가격은 필수입니다");
    }
    
    @Test
    void 장바구니_아이템으로부터_주문_라인_아이템_생성_성공() {
        // Given
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));
        CartLineItem cartItem = new CartLineItem(menuId, options, 2);
        
        String menuName = "삼겹살";
        List<String> optionNames = Arrays.asList("매운맛");
        Money unitPrice = Money.of(10000);
        
        // When
        OrderLineItem orderItem = OrderLineItem.fromCartLineItem(cartItem, menuName, optionNames, unitPrice);
        
        // Then
        assertThat(orderItem.getMenuId()).isEqualTo(menuId);
        assertThat(orderItem.getMenuName()).isEqualTo(menuName);
        assertThat(orderItem.getSelectedOptions()).containsExactlyElementsOf(options);
        assertThat(orderItem.getSelectedOptionNames()).containsExactlyElementsOf(optionNames);
        assertThat(orderItem.getQuantity()).isEqualTo(2);
        assertThat(orderItem.getLinePrice()).isEqualTo(Money.of(20000)); // 10000 * 2
    }
    
    @Test
    void 장바구니_아이템으로부터_주문_라인_아이템_생성_실패_장바구니_아이템_null() {
        // Given
        String menuName = "삼겹살";
        List<String> optionNames = Arrays.asList("매운맛");
        Money unitPrice = Money.of(10000);
        
        // When & Then
        assertThatThrownBy(() -> OrderLineItem.fromCartLineItem(null, menuName, optionNames, unitPrice))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("장바구니 아이템은 필수입니다");
    }
    
    @Test
    void 장바구니_아이템으로부터_주문_라인_아이템_생성_실패_단가_null() {
        // Given
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"));
        CartLineItem cartItem = new CartLineItem(menuId, options, 1);
        
        String menuName = "삼겹살";
        List<String> optionNames = Arrays.asList("매운맛");
        
        // When & Then
        assertThatThrownBy(() -> OrderLineItem.fromCartLineItem(cartItem, menuName, optionNames, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("단가는 필수입니다");
    }
}