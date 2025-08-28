package harry.boilerplate.order.domain;

import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.order.domain.entity.OrderLineItem;
import harry.boilerplate.order.domain.valueObject.MenuId;
import harry.boilerplate.order.domain.valueObject.OptionId;
import harry.boilerplate.order.domain.valueObject.SelectedOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * OrderLineItem 도메인 엔티티 테스트
 */
@DisplayName("OrderLineItem 테스트")
class OrderLineItemTest {

    @Nested
    @DisplayName("OrderLineItem 생성")
    class CreateOrderLineItem {

        @Test
        @DisplayName("정상적인 OrderLineItem 생성")
        void 정상적인_OrderLineItem_생성() {
            // Given
            MenuId menuId = MenuId.of("menu-1");
            String menuName = "삼겹살";
            List<SelectedOption> selectedOptions = Arrays.asList(
                new SelectedOption(OptionId.of("option-1"), "매운맛", new BigDecimal("1000"))
            );
            int quantity = 2;
            Money linePrice = Money.of(new BigDecimal("10000"));

            // When
            OrderLineItem orderLineItem = new OrderLineItem(
                menuId, menuName, selectedOptions, quantity, linePrice
            );

            // Then
            assertThat(orderLineItem.getId()).isNotNull();
            assertThat(orderLineItem.getMenuId()).isEqualTo(menuId);
            assertThat(orderLineItem.getMenuName()).isEqualTo(menuName);
            assertThat(orderLineItem.getSelectedOptions()).hasSize(1);
            assertThat(orderLineItem.getSelectedOptionNames()).containsExactly("매운맛");
            assertThat(orderLineItem.getQuantity()).isEqualTo(2);
            assertThat(orderLineItem.getLinePrice()).isEqualTo(linePrice);
        }

        @Test
        @DisplayName("MenuId가 null인 경우 예외 발생")
        void MenuId가_null인_경우_예외_발생() {
            // When & Then
            assertThatThrownBy(() -> new OrderLineItem(
                null, "삼겹살", Arrays.asList(), 1, Money.of(new BigDecimal("10000"))
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("메뉴 ID는 필수입니다");
        }

        @Test
        @DisplayName("메뉴 이름이 null인 경우 예외 발생")
        void 메뉴_이름이_null인_경우_예외_발생() {
            // When & Then
            assertThatThrownBy(() -> new OrderLineItem(
                MenuId.of("menu-1"), null, Arrays.asList(), 1, Money.of(new BigDecimal("10000"))
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("메뉴 이름은 필수입니다");
        }

        @Test
        @DisplayName("메뉴 이름이 빈 문자열인 경우 예외 발생")
        void 메뉴_이름이_빈_문자열인_경우_예외_발생() {
            // When & Then
            assertThatThrownBy(() -> new OrderLineItem(
                MenuId.of("menu-1"), "", Arrays.asList(), 1, Money.of(new BigDecimal("10000"))
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("메뉴 이름은 필수입니다");
        }

        @Test
        @DisplayName("수량이 0 이하인 경우 예외 발생")
        void 수량이_0_이하인_경우_예외_발생() {
            // When & Then
            assertThatThrownBy(() -> new OrderLineItem(
                MenuId.of("menu-1"), "삼겹살", Arrays.asList(), 0, Money.of(new BigDecimal("10000"))
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("수량은 1개 이상이어야 합니다");
        }

        @Test
        @DisplayName("음수 수량인 경우 예외 발생")
        void 음수_수량인_경우_예외_발생() {
            // When & Then
            assertThatThrownBy(() -> new OrderLineItem(
                MenuId.of("menu-1"), "삼겹살", Arrays.asList(), -1, Money.of(new BigDecimal("10000"))
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("수량은 1개 이상이어야 합니다");
        }

        @Test
        @DisplayName("라인 가격이 null인 경우 예외 발생")
        void 라인_가격이_null인_경우_예외_발생() {
            // When & Then
            assertThatThrownBy(() -> new OrderLineItem(
                MenuId.of("menu-1"), "삼겹살", Arrays.asList(), 1, null
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("라인 가격은 필수입니다");
        }

        @Test
        @DisplayName("선택된 옵션이 null인 경우도 빈 목록으로 처리")
        void 선택된_옵션이_null인_경우도_빈_목록으로_처리() {
            // When
            OrderLineItem orderLineItem = new OrderLineItem(
                MenuId.of("menu-1"), "삼겹살", null, 1, Money.of(new BigDecimal("10000"))
            );

            // Then
            assertThat(orderLineItem.getSelectedOptions()).isEmpty();
            assertThat(orderLineItem.getSelectedOptionNames()).isEmpty();
        }
    }

    @Nested
    @DisplayName("메뉴 검증")
    class MenuValidation {

        @Test
        @DisplayName("특정 메뉴인지 확인")
        void 특정_메뉴인지_확인() {
            // Given
            MenuId menuId = MenuId.of("menu-1");
            OrderLineItem orderLineItem = new OrderLineItem(
                menuId, "삼겹살", Arrays.asList(), 1, Money.of(new BigDecimal("10000"))
            );

            // Then
            assertThat(orderLineItem.getMenuId()).isEqualTo(menuId);
        }
    }

    @Nested
    @DisplayName("옵션 관련 기능")
    class OptionFeatures {

        @Test
        @DisplayName("옵션이 있는 아이템과 없는 아이템 구분")
        void 옵션이_있는_아이템과_없는_아이템_구분() {
            // Given
            List<SelectedOption> optionsWithValues = Arrays.asList(
                new SelectedOption(OptionId.of("option-1"), "매운맛", new BigDecimal("1000"))
            );
            OrderLineItem itemWithOptions = new OrderLineItem(
                MenuId.of("menu-1"), "삼겹살", optionsWithValues, 1, Money.of(new BigDecimal("10000"))
            );
            OrderLineItem itemWithoutOptions = new OrderLineItem(
                MenuId.of("menu-2"), "냉면", null, 1, Money.of(new BigDecimal("8000"))
            );

            // Then
            assertThat(itemWithOptions.getSelectedOptions()).hasSize(1);
            assertThat(itemWithoutOptions.getSelectedOptions()).isEmpty();
        }

        @Test
        @DisplayName("단가 계산 기능")
        void 단가_계산_기능() {
            // Given
            OrderLineItem orderLineItem = new OrderLineItem(
                MenuId.of("menu-1"), "삼겹살", Arrays.asList(), 2, Money.of(new BigDecimal("20000"))
            );

            // When
            Money totalPrice = orderLineItem.getTotalPrice();

            // Then
            assertThat(totalPrice).isEqualTo(Money.of(new BigDecimal("20000")));
        }
    }

    @Nested
    @DisplayName("옵션 목록 검증")
    class OptionListValidation {

        @Test
        @DisplayName("여러 옵션이 있는 OrderLineItem 생성")
        void 여러_옵션이_있는_OrderLineItem_생성() {
            // Given
            MenuId menuId = MenuId.of("menu-1");
            List<SelectedOption> optionIds = Arrays.asList(
                new SelectedOption(OptionId.of("option-1"), "매운맛", new BigDecimal("1000")),
                new SelectedOption(OptionId.of("option-2"), "김치", new BigDecimal("500"))
            );

            // When
            OrderLineItem orderLineItem = new OrderLineItem(
                menuId, "삼겹살", optionIds, 1, Money.of(new BigDecimal("10000"))
            );

            // Then
            assertThat(orderLineItem.getSelectedOptions()).hasSize(2);
            assertThat(orderLineItem.getSelectedOptionNames()).hasSize(2);
            assertThat(orderLineItem.getSelectedOptionNames()).containsExactly("매운맛", "김치");
        }
    }
}