package harry.boilerplate.shop.domain;

import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.common.domain.event.DomainEvent;
import harry.boilerplate.shop.command.domain.aggregate.Menu;
import harry.boilerplate.shop.command.domain.exception.MenuDomainException;
import harry.boilerplate.shop.command.domain.exception.MenuErrorCode;
import harry.boilerplate.shop.command.domain.entity.OptionGroup;
import harry.boilerplate.shop.command.domain.event.MenuOpenedEvent;
import harry.boilerplate.shop.command.domain.valueObject.Option;
import harry.boilerplate.shop.command.domain.valueObject.OptionGroupId;
import harry.boilerplate.shop.command.domain.valueObject.ShopId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Menu 애그리게이트 테스트")
class MenuTest {

    private ShopId shopId;
    private Menu menu;

    @BeforeEach
    void setUp() {
        shopId = ShopId.generate();
        menu = new Menu(shopId, "삼겹살", "맛있는 삼겹살", Money.of(new BigDecimal("15000")));

        // 필수 유료 옵션그룹 추가
        menu.addOptionGroup("양 선택", true);
        OptionGroup requiredGroup = menu.getOptionGroups().get(0);
        requiredGroup.addOption(new Option("곱빼기", Money.of(new BigDecimal("2000"))));
        requiredGroup.addOption(new Option("보통", Money.zero()));

        // 선택 무료 옵션그룹 추가
        menu.addOptionGroup("매운맛 선택", false);
        OptionGroup optionalGroup = menu.getOptionGroups().get(1);
        optionalGroup.addOption(new Option("안맵게", Money.zero()));
        optionalGroup.addOption(new Option("보통맵게", Money.zero()));
    }

    @Nested
    @DisplayName("Menu 생성 테스트")
    class MenuCreationTest {

        @Test
        @DisplayName("정상적인 Menu 생성")
        void 정상적인_Menu_생성() {
            // When
            Menu menu = new Menu(shopId, "삼겹살", "맛있는 삼겹살", Money.of(new BigDecimal("15000")));

            // Then
            assertThat(menu.getId()).isNotNull();
            assertThat(menu.getShopId()).isEqualTo(shopId);
            assertThat(menu.getName()).isEqualTo("삼겹살");
            assertThat(menu.getDescription()).isEqualTo("맛있는 삼겹살");
            assertThat(menu.getBasePrice()).isEqualTo(Money.of(new BigDecimal("15000")));
            assertThat(menu.isOpen()).isFalse();
            assertThat(menu.getOptionGroups()).isEmpty();
        }

        @Test
        @DisplayName("ShopId가 null인 경우 예외 발생")
        void ShopId가_null인_경우_예외_발생() {
            // When & Then
            assertThatThrownBy(() -> new Menu(null, "삼겹살", "맛있는 삼겹살", Money.of(new BigDecimal("15000"))))
                    .isInstanceOf(MenuDomainException.class)
                    .extracting(e -> ((MenuDomainException) e).getErrorCode())
                    .isEqualTo(MenuErrorCode.SHOP_ID_REQUIRED);
        }

        @Test
        @DisplayName("메뉴 이름이 null인 경우 예외 발생")
        void 메뉴_이름이_null인_경우_예외_발생() {
            // When & Then
            assertThatThrownBy(() -> new Menu(shopId, null, "설명", Money.of(new BigDecimal("15000"))))
                    .isInstanceOf(MenuDomainException.class)
                    .extracting(e -> ((MenuDomainException) e).getErrorCode())
                    .isEqualTo(MenuErrorCode.MENU_NAME_REQUIRED);
        }

        @Test
        @DisplayName("메뉴 이름이 빈 문자열인 경우 예외 발생")
        void 메뉴_이름이_빈_문자열인_경우_예외_발생() {
            // When & Then
            assertThatThrownBy(() -> new Menu(shopId, "", "설명", Money.of(new BigDecimal("15000"))))
                    .isInstanceOf(MenuDomainException.class)
                    .extracting(e -> ((MenuDomainException) e).getErrorCode())
                    .isEqualTo(MenuErrorCode.MENU_NAME_REQUIRED);
        }

        @Test
        @DisplayName("메뉴 이름이 공백만 있는 경우 예외 발생")
        void 메뉴_이름이_공백만_있는_경우_예외_발생() {
            // When & Then
            assertThatThrownBy(() -> new Menu(shopId, "   ", "설명", Money.of(new BigDecimal("15000"))))
                    .isInstanceOf(MenuDomainException.class)
                    .extracting(e -> ((MenuDomainException) e).getErrorCode())
                    .isEqualTo(MenuErrorCode.MENU_NAME_REQUIRED);
        }

        @Test
        @DisplayName("기본 가격이 null인 경우 예외 발생")
        void 기본_가격이_null인_경우_예외_발생() {
            // When & Then
            assertThatThrownBy(() -> new Menu(shopId, "삼겹살", "설명", null))
                    .isInstanceOf(MenuDomainException.class)
                    .extracting(e -> ((MenuDomainException) e).getErrorCode())
                    .isEqualTo(MenuErrorCode.BASE_PRICE_REQUIRED);
        }

        @Test
        @DisplayName("기본 가격이 음수인 경우 예외 발생")
        void 기본_가격이_음수인_경우_예외_발생() {
            // When & Then
            assertThatThrownBy(() -> new Menu(shopId, "삼겹살", "설명", Money.of(new BigDecimal("-1000"))))
                    .isInstanceOf(MenuDomainException.class)
                    .extracting(e -> ((MenuDomainException) e).getErrorCode())
                    .isEqualTo(MenuErrorCode.INVALID_BASE_PRICE);
        }
    }

    @Nested
    @DisplayName("옵션그룹 관리 테스트")
    class OptionGroupManagementTest {

        @Test
        @DisplayName("옵션그룹 추가 성공")
        void 옵션그룹_추가_성공() {
            // Given
            Menu newMenu = new Menu(shopId, "새메뉴", "설명", Money.of(new BigDecimal("10000")));

            // When
            newMenu.addOptionGroup("사이즈 선택", true);

            // Then
            assertThat(newMenu.getOptionGroups()).hasSize(1);
            assertThat(newMenu.getOptionGroups().get(0).getName()).isEqualTo("사이즈 선택");
            assertThat(newMenu.getOptionGroups().get(0).isRequired()).isTrue();
        }

        @Test
        @DisplayName("중복된 이름의 옵션그룹 추가 시 예외 발생")
        void 중복된_이름의_옵션그룹_추가_시_예외_발생() {
            // When & Then
            assertThatThrownBy(() -> menu.addOptionGroup("양 선택", false))
                    .isInstanceOf(MenuDomainException.class)
                    .extracting(e -> ((MenuDomainException) e).getErrorCode())
                    .isEqualTo(MenuErrorCode.DUPLICATE_OPTION_GROUP_NAME);
        }

        @Test
        @DisplayName("옵션그룹 이름 변경 성공")
        void 옵션그룹_이름_변경_성공() {
            // Given
            OptionGroupId optionGroupId = menu.getOptionGroups().get(0).getId();

            // When
            menu.changeOptionGroupName(optionGroupId, "새로운 양 선택");

            // Then
            assertThat(menu.getOptionGroups().get(0).getName()).isEqualTo("새로운 양 선택");
        }

        @Test
        @DisplayName("옵션 이름 변경 성공")
        void 옵션_이름_변경_성공() {
            // Given
            OptionGroupId optionGroupId = menu.getOptionGroups().get(0).getId();

            // When
            menu.changeOptionName(optionGroupId, "곱빼기", Money.of(new BigDecimal("2000")), "대곱빼기");

            // Then
            OptionGroup optionGroup = menu.getOptionGroups().get(0);
            Option changedOption = optionGroup.getOptions().stream()
                    .filter(option -> option.getName().equals("대곱빼기"))
                    .findFirst()
                    .orElseThrow();
            assertThat(changedOption.getName()).isEqualTo("대곱빼기");
            assertThat(changedOption.getPrice()).isEqualTo(Money.of(new BigDecimal("2000")));
        }
    }

    @Nested
    @DisplayName("메뉴 공개 테스트")
    class MenuOpenTest {

        @Test
        @DisplayName("조건을 만족하는 메뉴 공개 성공")
        void 조건을_만족하는_메뉴_공개_성공() {
            // When
            menu.open();

            // Then
            assertThat(menu.isOpen()).isTrue();

            // 도메인 이벤트 발행 확인
            DomainEvent event = menu.getDomainEvents().get(0);
            assertThat(event).isInstanceOf(MenuOpenedEvent.class);
            MenuOpenedEvent menuOpenedEvent = (MenuOpenedEvent) event;
            assertThat(menuOpenedEvent.getAggregateId()).isEqualTo(menu.getId().getValue());
            assertThat(menuOpenedEvent.getMenuName()).isEqualTo("삼겹살");
        }

        @Test
        @DisplayName("이미 공개된 메뉴 재공개 시 예외 발생")
        void 이미_공개된_메뉴_재공개_시_예외_발생() {
            // Given
            menu.open();

            // When & Then
            assertThatThrownBy(() -> menu.open())
                    .isInstanceOf(MenuDomainException.class)
                    .extracting(e -> ((MenuDomainException) e).getErrorCode())
                    .isEqualTo(MenuErrorCode.MENU_ALREADY_OPEN);
        }

        @Test
        @DisplayName("옵션그룹이 없는 메뉴 공개 시 예외 발생")
        void 옵션그룹이_없는_메뉴_공개_시_예외_발생() {
            // Given
            Menu emptyMenu = new Menu(shopId, "빈메뉴", "설명", Money.of(new BigDecimal("10000")));

            // When & Then
            assertThatThrownBy(() -> emptyMenu.open())
                    .isInstanceOf(MenuDomainException.class)
                    .extracting(e -> ((MenuDomainException) e).getErrorCode())
                    .isEqualTo(MenuErrorCode.INSUFFICIENT_OPTION_GROUPS);
        }

        @Test
        @DisplayName("필수 옵션그룹이 없는 메뉴 공개 시 예외 발생")
        void 필수_옵션그룹이_없는_메뉴_공개_시_예외_발생() {
            // Given
            Menu menuWithoutRequired = new Menu(shopId, "메뉴", "설명", Money.of(new BigDecimal("10000")));
            menuWithoutRequired.addOptionGroup("선택사항", false);

            // When & Then
            assertThatThrownBy(() -> menuWithoutRequired.open())
                    .isInstanceOf(MenuDomainException.class)
                    .extracting(e -> ((MenuDomainException) e).getErrorCode())
                    .isEqualTo(MenuErrorCode.INVALID_REQUIRED_OPTION_GROUP_COUNT);
        }

        @Test
        @DisplayName("유료 옵션그룹이 없는 메뉴 공개 시 예외 발생")
        void 유료_옵션그룹이_없는_메뉴_공개_시_예외_발생() {
            // Given
            Menu menuWithoutPaid = new Menu(shopId, "메뉴", "설명", Money.of(new BigDecimal("10000")));
            menuWithoutPaid.addOptionGroup("무료옵션", true);
            OptionGroup freeGroup = menuWithoutPaid.getOptionGroups().get(0);
            freeGroup.addOption(new Option("무료", Money.zero()));

            // When & Then
            assertThatThrownBy(() -> menuWithoutPaid.open())
                    .isInstanceOf(MenuDomainException.class)
                    .extracting(e -> ((MenuDomainException) e).getErrorCode())
                    .isEqualTo(MenuErrorCode.NO_PAID_OPTION_GROUP);
        }
    }
}