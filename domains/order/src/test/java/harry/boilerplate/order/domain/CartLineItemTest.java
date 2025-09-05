package harry.boilerplate.order.domain;

import harry.boilerplate.order.command.domain.entity.CartLineItem;
import harry.boilerplate.order.command.domain.valueObject.MenuId;
import harry.boilerplate.order.command.domain.valueObject.OptionId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * CartLineItem 도메인 엔티티 테스트
 */
@DisplayName("CartLineItem 테스트")
class CartLineItemTest {

    @Nested
    @DisplayName("CartLineItem 생성")
    class CreateCartLineItem {

        @Test
        @DisplayName("정상적인 CartLineItem 생성")
        void 정상적인_CartLineItem_생성() {
            // Given
            MenuId menuId = MenuId.of("menu-1");
            List<OptionId> selectedOptions = Arrays.asList(OptionId.of("option-1"));

            // When
            CartLineItem cartLineItem = new CartLineItem(menuId, selectedOptions, 2);

            // Then
            assertThat(cartLineItem.getId()).isNotNull();
            assertThat(cartLineItem.getMenuId()).isEqualTo(menuId);
            assertThat(cartLineItem.getSelectedOptions()).hasSize(1);
            assertThat(cartLineItem.getQuantity()).isEqualTo(2);
        }

        @Test
        @DisplayName("MenuId가 null인 경우 예외 발생")
        void MenuId가_null인_경우_예외_발생() {
            // Given
            List<OptionId> selectedOptions = Arrays.asList(OptionId.of("option-1"));

            // When & Then
            assertThatThrownBy(() -> new CartLineItem(null, selectedOptions, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("메뉴 ID는 필수입니다");
        }

        @Test
        @DisplayName("수량이 0 이하인 경우 예외 발생")
        void 수량이_0_이하인_경우_예외_발생() {
            // Given
            MenuId menuId = MenuId.of("menu-1");
            List<OptionId> selectedOptions = Arrays.asList(OptionId.of("option-1"));

            // When & Then
            assertThatThrownBy(() -> new CartLineItem(menuId, selectedOptions, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("수량은 1개 이상이어야 합니다");

            assertThatThrownBy(() -> new CartLineItem(menuId, selectedOptions, -1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("수량은 1개 이상이어야 합니다");
        }

        @Test
        @DisplayName("빈 옵션 목록으로 생성")
        void 빈_옵션_목록으로_생성() {
            // Given
            MenuId menuId = MenuId.of("menu-1");
            List<OptionId> emptyOptions = Arrays.asList();

            // When
            CartLineItem cartLineItem = new CartLineItem(menuId, emptyOptions, 1);

            // Then
            assertThat(cartLineItem.getSelectedOptions()).isEmpty();
        }
    }

    @Nested
    @DisplayName("CartLineItem 병합")
    class CombineCartLineItem {

        @Test
        @DisplayName("동일한 메뉴와 옵션 조합 아이템 병합 성공")
        void 동일한_메뉴와_옵션_조합_아이템_병합_성공() {
            // Given
            MenuId menuId = MenuId.of("menu-1");
            List<OptionId> selectedOptions = Arrays.asList(OptionId.of("option-1"));

            // When
            CartLineItem item1 = new CartLineItem(menuId, selectedOptions, 2);
            CartLineItem item2 = new CartLineItem(menuId, selectedOptions, 3);
            CartLineItem combinedItem = item1.combine(item2);

            // Then
            assertThat(combinedItem.getMenuId()).isEqualTo(menuId);
            assertThat(combinedItem.getQuantity()).isEqualTo(5);
            assertThat(combinedItem.getSelectedOptions()).containsExactlyElementsOf(selectedOptions);
        }

        @Test
        @DisplayName("다른 메뉴 아이템 병합 시 예외 발생")
        void 다른_메뉴_아이템_병합_시_예외_발생() {
            // Given
            MenuId menuId1 = MenuId.of("menu-1");
            MenuId menuId2 = MenuId.of("menu-2");
            List<OptionId> selectedOptions = Arrays.asList(OptionId.of("option-1"));

            CartLineItem item1 = new CartLineItem(menuId1, selectedOptions, 2);
            CartLineItem item2 = new CartLineItem(menuId2, selectedOptions, 3);

            // When & Then
            assertThatThrownBy(() -> item1.combine(item2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("동일한 메뉴와 옵션 조합만 병합할 수 있습니다");
        }

        @Test
        @DisplayName("다른 옵션 조합 아이템 병합 시 예외 발생")
        void 다른_옵션_조합_아이템_병합_시_예외_발생() {
            // Given
            MenuId menuId = MenuId.of("menu-1");
            List<OptionId> selectedOptions1 = Arrays.asList(OptionId.of("option-1"));
            List<OptionId> selectedOptions2 = Arrays.asList(OptionId.of("option-2"));

            CartLineItem item1 = new CartLineItem(menuId, selectedOptions1, 2);
            CartLineItem item2 = new CartLineItem(menuId, selectedOptions2, 3);

            // When & Then
            assertThatThrownBy(() -> item1.combine(item2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("동일한 메뉴와 옵션 조합만 병합할 수 있습니다");
        }

        @Test
        @DisplayName("null 아이템과 병합 시 예외 발생")
        void null_아이템과_병합_시_예외_발생() {
            // Given
            MenuId menuId = MenuId.of("menu-1");
            List<OptionId> selectedOptions = Arrays.asList(OptionId.of("option-1"));
            CartLineItem item = new CartLineItem(menuId, selectedOptions, 2);

            // When & Then
            assertThatThrownBy(() -> item.combine(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("동일한 메뉴와 옵션 조합만 병합할 수 있습니다");
        }
    }

    @Nested
    @DisplayName("아이템 동일성 검증")
    class ItemEquality {

        @Test
        @DisplayName("동일한 메뉴와 옵션 조합 확인")
        void 동일한_메뉴와_옵션_조합_확인() {
            // Given
            MenuId menuId = MenuId.of("menu-1");
            List<OptionId> selectedOptions = Arrays.asList(OptionId.of("option-1"));

            // When
            CartLineItem item1 = new CartLineItem(menuId, selectedOptions, 2);
            CartLineItem item2 = new CartLineItem(menuId, selectedOptions, 3);

            // Then
            assertThat(item1.isSameMenuAndOptions(item2)).isTrue();
        }

        @Test
        @DisplayName("다른 메뉴 확인")
        void 다른_메뉴_확인() {
            // Given
            MenuId menuId1 = MenuId.of("menu-1");
            List<OptionId> selectedOptions = Arrays.asList(OptionId.of("option-1"));

            // When
            CartLineItem item1 = new CartLineItem(menuId1, selectedOptions, 2);

            // Then
            assertThat(item1.isSameMenuAndOptions(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("수량 변경")
    class ChangeQuantity {

        @Test
        @DisplayName("수량 변경 성공")
        void 수량_변경_성공() {
            // Given
            MenuId menuId = MenuId.of("menu-1");
            List<OptionId> selectedOptions = Arrays.asList(OptionId.of("option-1"));
            CartLineItem item = new CartLineItem(menuId, selectedOptions, 2);

            // When
            item.changeQuantity(5);

            // Then
            assertThat(item.getQuantity()).isEqualTo(5);
        }

        @Test
        @DisplayName("수량 0 이하로 변경 시 예외 발생")
        void 수량_0_이하로_변경_시_예외_발생() {
            // Given
            MenuId menuId = MenuId.of("menu-1");
            List<OptionId> selectedOptions = Arrays.asList(OptionId.of("option-1"));
            CartLineItem item = new CartLineItem(menuId, selectedOptions, 2);

            // When & Then
            assertThatThrownBy(() -> item.changeQuantity(0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("수량은 1개 이상이어야 합니다");

            assertThatThrownBy(() -> item.changeQuantity(-1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("수량은 1개 이상이어야 합니다");
        }
    }
}