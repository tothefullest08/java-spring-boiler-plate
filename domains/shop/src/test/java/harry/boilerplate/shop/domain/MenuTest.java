package harry.boilerplate.shop.domain;

import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.shop.domain.Menu.MenuDomainException;
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
    private OptionGroup requiredPaidOptionGroup;
    private OptionGroup optionalFreeOptionGroup;

    @BeforeEach
    void setUp() {
        shopId = new ShopId();
        menu = new Menu(shopId, "삼겹살", "맛있는 삼겹살", Money.of(new BigDecimal("15000")));
        
        // 필수 유료 옵션그룹 생성
        OptionGroupId requiredGroupId = new OptionGroupId();
        Option paidOption = new Option("곱빼기", Money.of(new BigDecimal("2000")));
        requiredPaidOptionGroup = new OptionGroup(requiredGroupId, "양 선택", true)
            .addOption(paidOption)
            .addOption(new Option("보통", Money.zero()));
        
        // 선택 무료 옵션그룹 생성
        OptionGroupId optionalGroupId = new OptionGroupId();
        optionalFreeOptionGroup = new OptionGroup(optionalGroupId, "매운맛 선택", false)
            .addOption(new Option("안맵게", Money.zero()))
            .addOption(new Option("보통맵게", Money.zero()));
    }

    @Nested
    @DisplayName("메뉴 생성 테스트")
    class MenuCreationTest {

        @Test
        @DisplayName("정상적인 메뉴 생성 성공")
        void 메뉴_생성_성공() {
            // Given & When
            Menu menu = new Menu(shopId, "삼겹살", "맛있는 삼겹살", Money.of(new BigDecimal("15000")));

            // Then
            assertThat(menu.getId()).isNotNull();
            assertThat(menu.getShopId()).isEqualTo(shopId);
            assertThat(menu.getName()).isEqualTo("삼겹살");
            assertThat(menu.getDescription()).isEqualTo("맛있는 삼겹살");
            assertThat(menu.getBasePrice()).isEqualTo(Money.of(new BigDecimal("15000")));
            assertThat(menu.isOpen()).isFalse(); // 초기 상태는 비공개
            assertThat(menu.getOptionGroups()).isEmpty();
        }

        @Test
        @DisplayName("가게 ID가 null인 경우 예외 발생")
        void 가게ID_null_예외() {
            // When & Then
            assertThatThrownBy(() -> new Menu(null, "삼겹살", "맛있는 삼겹살", Money.of(new BigDecimal("15000"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가게 ID는 필수입니다");
        }

        @Test
        @DisplayName("메뉴 이름이 null이거나 빈 문자열인 경우 예외 발생")
        void 메뉴이름_null_또는_빈문자열_예외() {
            // When & Then
            assertThatThrownBy(() -> new Menu(shopId, null, "설명", Money.of(new BigDecimal("15000"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("메뉴 이름은 필수입니다");

            assertThatThrownBy(() -> new Menu(shopId, "", "설명", Money.of(new BigDecimal("15000"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("메뉴 이름은 필수입니다");

            assertThatThrownBy(() -> new Menu(shopId, "   ", "설명", Money.of(new BigDecimal("15000"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("메뉴 이름은 필수입니다");
        }

        @Test
        @DisplayName("기본 가격이 null이거나 음수인 경우 예외 발생")
        void 기본가격_null_또는_음수_예외() {
            // When & Then
            assertThatThrownBy(() -> new Menu(shopId, "삼겹살", "설명", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기본 가격은 필수입니다");

            assertThatThrownBy(() -> new Menu(shopId, "삼겹살", "설명", Money.of(new BigDecimal("-1000"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기본 가격은 0 이상이어야 합니다");
        }
    }

    @Nested
    @DisplayName("메뉴 공개 테스트")
    class MenuOpenTest {

        @Test
        @DisplayName("모든 조건을 만족할 때 메뉴 공개 성공")
        void 메뉴_공개_성공_모든_조건_만족() {
            // Given
            menu.addOptionGroup(requiredPaidOptionGroup);

            // When
            menu.open();

            // Then
            assertThat(menu.isOpen()).isTrue();
        }

        @Test
        @DisplayName("이미 공개된 메뉴를 다시 공개하려 할 때 예외 발생")
        void 이미_공개된_메뉴_재공개_예외() {
            // Given
            menu.addOptionGroup(requiredPaidOptionGroup);
            menu.open();

            // When & Then
            assertThatThrownBy(() -> menu.open())
                .isInstanceOf(MenuDomainException.class)
                .extracting(e -> ((MenuDomainException) e).getErrorCode())
                .isEqualTo(MenuErrorCode.MENU_ALREADY_OPEN);
        }

        @Test
        @DisplayName("옵션그룹이 없을 때 메뉴 공개 실패")
        void 옵션그룹_없음_공개_실패() {
            // When & Then
            assertThatThrownBy(() -> menu.open())
                .isInstanceOf(MenuDomainException.class)
                .extracting(e -> ((MenuDomainException) e).getErrorCode())
                .isEqualTo(MenuErrorCode.INSUFFICIENT_OPTION_GROUPS);
        }

        @Test
        @DisplayName("필수 옵션그룹이 없을 때 메뉴 공개 실패")
        void 필수_옵션그룹_없음_공개_실패() {
            // Given - 선택 옵션그룹만 추가
            menu.addOptionGroup(optionalFreeOptionGroup);

            // When & Then
            assertThatThrownBy(() -> menu.open())
                .isInstanceOf(MenuDomainException.class)
                .extracting(e -> ((MenuDomainException) e).getErrorCode())
                .isEqualTo(MenuErrorCode.INVALID_REQUIRED_OPTION_GROUP_COUNT);
        }

        @Test
        @DisplayName("필수 옵션그룹이 3개를 초과할 때 메뉴 공개 실패")
        void 필수_옵션그룹_초과_공개_실패() {
            // Given - 4개의 필수 옵션그룹 추가
            for (int i = 1; i <= 4; i++) {
                OptionGroupId groupId = new OptionGroupId();
                OptionGroup requiredGroup = new OptionGroup(groupId, "필수그룹" + i, true)
                    .addOption(new Option("옵션" + i, Money.of(new BigDecimal("1000"))));
                menu.addOptionGroup(requiredGroup);
            }

            // When & Then
            assertThatThrownBy(() -> menu.open())
                .isInstanceOf(MenuDomainException.class)
                .extracting(e -> ((MenuDomainException) e).getErrorCode())
                .isEqualTo(MenuErrorCode.INVALID_REQUIRED_OPTION_GROUP_COUNT);
        }

        @Test
        @DisplayName("유료 옵션그룹이 없을 때 메뉴 공개 실패")
        void 유료_옵션그룹_없음_공개_실패() {
            // Given - 필수이지만 무료 옵션그룹만 추가
            OptionGroupId groupId = new OptionGroupId();
            OptionGroup requiredFreeGroup = new OptionGroup(groupId, "무료 필수그룹", true)
                .addOption(new Option("무료옵션", Money.zero()));
            menu.addOptionGroup(requiredFreeGroup);

            // When & Then
            assertThatThrownBy(() -> menu.open())
                .isInstanceOf(MenuDomainException.class)
                .extracting(e -> ((MenuDomainException) e).getErrorCode())
                .isEqualTo(MenuErrorCode.NO_PAID_OPTION_GROUP);
        }
    }

    @Nested
    @DisplayName("옵션그룹 관리 테스트")
    class OptionGroupManagementTest {

        @Test
        @DisplayName("옵션그룹 추가 성공")
        void 옵션그룹_추가_성공() {
            // When
            menu.addOptionGroup(requiredPaidOptionGroup);

            // Then
            assertThat(menu.getOptionGroups()).hasSize(1);
            assertThat(menu.getOptionGroups().get(0).getName()).isEqualTo("양 선택");
        }

        @Test
        @DisplayName("동일한 이름의 옵션그룹 추가 시 예외 발생")
        void 동일한_이름_옵션그룹_추가_예외() {
            // Given
            menu.addOptionGroup(requiredPaidOptionGroup);
            
            OptionGroupId duplicateGroupId = new OptionGroupId();
            OptionGroup duplicateGroup = new OptionGroup(duplicateGroupId, "양 선택", false);

            // When & Then
            assertThatThrownBy(() -> menu.addOptionGroup(duplicateGroup))
                .isInstanceOf(MenuDomainException.class)
                .extracting(e -> ((MenuDomainException) e).getErrorCode())
                .isEqualTo(MenuErrorCode.DUPLICATE_OPTION_GROUP_NAME);
        }

        @Test
        @DisplayName("공개된 메뉴에서 필수 옵션그룹이 3개일 때 추가 필수 옵션그룹 추가 시 예외 발생")
        void 공개된_메뉴_필수_옵션그룹_최대개수_초과_예외() {
            // Given - 3개의 필수 옵션그룹 추가 후 메뉴 공개
            for (int i = 1; i <= 3; i++) {
                OptionGroupId groupId = new OptionGroupId();
                OptionGroup requiredGroup = new OptionGroup(groupId, "필수그룹" + i, true)
                    .addOption(new Option("유료옵션" + i, Money.of(new BigDecimal("1000"))));
                menu.addOptionGroup(requiredGroup);
            }
            menu.open();

            // When & Then
            OptionGroupId newGroupId = new OptionGroupId();
            OptionGroup newRequiredGroup = new OptionGroup(newGroupId, "새필수그룹", true);
            
            assertThatThrownBy(() -> menu.addOptionGroup(newRequiredGroup))
                .isInstanceOf(MenuDomainException.class)
                .extracting(e -> ((MenuDomainException) e).getErrorCode())
                .isEqualTo(MenuErrorCode.MAX_REQUIRED_OPTION_GROUPS_EXCEEDED);
        }

        @Test
        @DisplayName("옵션그룹 이름 변경 성공")
        void 옵션그룹_이름_변경_성공() {
            // Given
            menu.addOptionGroup(requiredPaidOptionGroup);
            OptionGroupId groupId = requiredPaidOptionGroup.getId();

            // When
            menu.changeOptionGroupName(groupId, "새로운 양 선택");

            // Then
            OptionGroup updatedGroup = menu.getOptionGroups().stream()
                .filter(group -> group.getId().equals(groupId))
                .findFirst()
                .orElseThrow();
            assertThat(updatedGroup.getName()).isEqualTo("새로운 양 선택");
        }

        @Test
        @DisplayName("존재하지 않는 옵션그룹 이름 변경 시 예외 발생")
        void 존재하지_않는_옵션그룹_이름_변경_예외() {
            // Given
            OptionGroupId nonExistentId = new OptionGroupId();

            // When & Then
            assertThatThrownBy(() -> menu.changeOptionGroupName(nonExistentId, "새이름"))
                .isInstanceOf(MenuDomainException.class)
                .extracting(e -> ((MenuDomainException) e).getErrorCode())
                .isEqualTo(MenuErrorCode.OPTION_GROUP_NOT_FOUND);
        }

        @Test
        @DisplayName("옵션 이름 변경 성공")
        void 옵션_이름_변경_성공() {
            // Given
            menu.addOptionGroup(requiredPaidOptionGroup);
            OptionGroupId groupId = requiredPaidOptionGroup.getId();

            // When
            menu.changeOptionName(groupId, "곱빼기", Money.of(new BigDecimal("2000")), "대곱빼기");

            // Then
            OptionGroup updatedGroup = menu.getOptionGroups().stream()
                .filter(group -> group.getId().equals(groupId))
                .findFirst()
                .orElseThrow();
            
            boolean hasUpdatedOption = updatedGroup.getOptions().stream()
                .anyMatch(option -> option.getName().equals("대곱빼기") && 
                                  option.getPrice().equals(Money.of(new BigDecimal("2000"))));
            assertThat(hasUpdatedOption).isTrue();
        }

        @Test
        @DisplayName("옵션그룹 삭제 성공")
        void 옵션그룹_삭제_성공() {
            // Given - 비공개 상태에서 옵션그룹 추가
            menu.addOptionGroup(requiredPaidOptionGroup);
            menu.addOptionGroup(optionalFreeOptionGroup);
            OptionGroupId groupIdToRemove = optionalFreeOptionGroup.getId();

            // When
            menu.removeOptionGroup(groupIdToRemove);

            // Then
            assertThat(menu.getOptionGroups()).hasSize(1);
            assertThat(menu.getOptionGroups().get(0).getId()).isEqualTo(requiredPaidOptionGroup.getId());
        }

        @Test
        @DisplayName("공개된 메뉴에서 최소 조건 위반 시 옵션그룹 삭제 실패")
        void 공개된_메뉴_최소조건_위반_삭제_실패() {
            // Given - 최소 조건만 만족하는 상태로 메뉴 공개
            menu.addOptionGroup(requiredPaidOptionGroup);
            menu.open();

            // When & Then - 유일한 옵션그룹 삭제 시도
            assertThatThrownBy(() -> menu.removeOptionGroup(requiredPaidOptionGroup.getId()))
                .isInstanceOf(MenuDomainException.class)
                .extracting(e -> ((MenuDomainException) e).getErrorCode())
                .isEqualTo(MenuErrorCode.CANNOT_DELETE_REQUIRED_OPTION_GROUP);
        }

        @Test
        @DisplayName("중복된 이름으로 옵션그룹 이름 변경 시 예외 발생")
        void 중복된_이름으로_옵션그룹_이름_변경_예외() {
            // Given - 두 개의 옵션그룹 추가
            menu.addOptionGroup(requiredPaidOptionGroup);
            menu.addOptionGroup(optionalFreeOptionGroup);
            OptionGroupId groupId = requiredPaidOptionGroup.getId();

            // When & Then - 다른 옵션그룹과 동일한 이름으로 변경 시도
            assertThatThrownBy(() -> menu.changeOptionGroupName(groupId, "매운맛 선택"))
                .isInstanceOf(MenuDomainException.class)
                .extracting(e -> ((MenuDomainException) e).getErrorCode())
                .isEqualTo(MenuErrorCode.DUPLICATE_OPTION_GROUP_NAME);
        }

        @Test
        @DisplayName("존재하지 않는 옵션 이름 변경 시 예외 발생")
        void 존재하지_않는_옵션_이름_변경_예외() {
            // Given
            menu.addOptionGroup(requiredPaidOptionGroup);
            OptionGroupId groupId = requiredPaidOptionGroup.getId();

            // When & Then - 존재하지 않는 옵션 이름으로 변경 시도
            assertThatThrownBy(() -> menu.changeOptionName(groupId, "존재하지않는옵션", Money.of(new BigDecimal("1000")), "새이름"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("변경할 옵션을 찾을 수 없습니다: 존재하지않는옵션");
        }

        @Test
        @DisplayName("공개된 메뉴에서 필수 옵션그룹 삭제 후 필수 옵션그룹이 없어질 때 삭제 실패")
        void 공개된_메뉴_필수_옵션그룹_삭제_후_필수그룹_없음_실패() {
            // Given - 필수 옵션그룹 1개와 선택 옵션그룹 1개 추가 후 공개
            OptionGroupId optionalGroupId = new OptionGroupId();
            OptionGroup optionalPaidGroup = new OptionGroup(optionalGroupId, "선택 유료그룹", false)
                .addOption(new Option("유료선택", Money.of(new BigDecimal("1000"))));
            
            menu.addOptionGroup(requiredPaidOptionGroup);
            menu.addOptionGroup(optionalPaidGroup);
            menu.open();

            // When & Then - 유일한 필수 옵션그룹 삭제 시도
            assertThatThrownBy(() -> menu.removeOptionGroup(requiredPaidOptionGroup.getId()))
                .isInstanceOf(MenuDomainException.class)
                .extracting(e -> ((MenuDomainException) e).getErrorCode())
                .isEqualTo(MenuErrorCode.CANNOT_DELETE_REQUIRED_OPTION_GROUP);
        }

        @Test
        @DisplayName("공개된 메뉴에서 유료 옵션그룹 삭제 후 유료 옵션그룹이 없어질 때 삭제 실패")
        void 공개된_메뉴_유료_옵션그룹_삭제_후_유료그룹_없음_실패() {
            // Given - 필수 무료 옵션그룹과 선택 유료 옵션그룹 추가 후 공개
            OptionGroupId requiredFreeGroupId = new OptionGroupId();
            OptionGroup requiredFreeGroup = new OptionGroup(requiredFreeGroupId, "필수 무료그룹", true)
                .addOption(new Option("무료옵션", Money.zero()));
            
            OptionGroupId optionalPaidGroupId = new OptionGroupId();
            OptionGroup optionalPaidGroup = new OptionGroup(optionalPaidGroupId, "선택 유료그룹", false)
                .addOption(new Option("유료옵션", Money.of(new BigDecimal("1000"))));
            
            menu.addOptionGroup(requiredFreeGroup);
            menu.addOptionGroup(optionalPaidGroup);
            menu.open();

            // When & Then - 유일한 유료 옵션그룹 삭제 시도
            assertThatThrownBy(() -> menu.removeOptionGroup(optionalPaidGroupId))
                .isInstanceOf(MenuDomainException.class)
                .extracting(e -> ((MenuDomainException) e).getErrorCode())
                .isEqualTo(MenuErrorCode.CANNOT_DELETE_REQUIRED_OPTION_GROUP);
        }

        @Test
        @DisplayName("공개된 메뉴에서 조건을 만족하는 옵션그룹 삭제 성공")
        void 공개된_메뉴_조건_만족_옵션그룹_삭제_성공() {
            // Given - 필수 유료 옵션그룹 2개와 선택 옵션그룹 1개 추가 후 공개
            OptionGroupId requiredGroup2Id = new OptionGroupId();
            OptionGroup requiredPaidGroup2 = new OptionGroup(requiredGroup2Id, "필수 유료그룹2", true)
                .addOption(new Option("유료옵션2", Money.of(new BigDecimal("1500"))));
            
            menu.addOptionGroup(requiredPaidOptionGroup);
            menu.addOptionGroup(requiredPaidGroup2);
            menu.addOptionGroup(optionalFreeOptionGroup);
            menu.open();

            // When - 선택 옵션그룹 삭제
            menu.removeOptionGroup(optionalFreeOptionGroup.getId());

            // Then - 삭제 성공 확인
            assertThat(menu.getOptionGroups()).hasSize(2);
            assertThat(menu.getOptionGroups().stream()
                .noneMatch(group -> group.getId().equals(optionalFreeOptionGroup.getId()))).isTrue();
        }
    }
}