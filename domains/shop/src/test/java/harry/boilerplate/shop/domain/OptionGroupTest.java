package harry.boilerplate.shop.domain;

import harry.boilerplate.common.domain.entity.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("OptionGroup 값 객체 테스트")
class OptionGroupTest {

    private OptionGroupId optionGroupId;
    private OptionGroup optionGroup;
    private Option paidOption;
    private Option freeOption;

    @BeforeEach
    void setUp() {
        optionGroupId = new OptionGroupId();
        paidOption = new Option("곱빼기", Money.of(new BigDecimal("2000")));
        freeOption = new Option("보통", Money.zero());
        optionGroup = new OptionGroup(optionGroupId, "양 선택", true);
    }

    @Nested
    @DisplayName("옵션그룹 생성 테스트")
    class OptionGroupCreationTest {

        @Test
        @DisplayName("정상적인 옵션그룹 생성 성공")
        void 옵션그룹_생성_성공() {
            // Given & When
            OptionGroup group = new OptionGroup(optionGroupId, "양 선택", true);

            // Then
            assertThat(group.getId()).isEqualTo(optionGroupId);
            assertThat(group.getName()).isEqualTo("양 선택");
            assertThat(group.isRequired()).isTrue();
            assertThat(group.getOptions()).isEmpty();
            assertThat(group.isEmpty()).isTrue();
            assertThat(group.getOptionCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("옵션그룹 ID가 null인 경우 예외 발생")
        void 옵션그룹ID_null_예외() {
            // When & Then
            assertThatThrownBy(() -> new OptionGroup(null, "양 선택", true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("옵션그룹 ID는 필수입니다");
        }

        @Test
        @DisplayName("옵션그룹 이름이 null이거나 빈 문자열인 경우 예외 발생")
        void 옵션그룹이름_null_또는_빈문자열_예외() {
            // When & Then
            assertThatThrownBy(() -> new OptionGroup(optionGroupId, null, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("옵션그룹 이름은 필수입니다");

            assertThatThrownBy(() -> new OptionGroup(optionGroupId, "", true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("옵션그룹 이름은 필수입니다");

            assertThatThrownBy(() -> new OptionGroup(optionGroupId, "   ", true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("옵션그룹 이름은 필수입니다");
        }
    }

    @Nested
    @DisplayName("옵션 관리 테스트")
    class OptionManagementTest {

        @Test
        @DisplayName("옵션 추가 성공")
        void 옵션_추가_성공() {
            // When
            OptionGroup updatedGroup = optionGroup.addOption(paidOption);

            // Then
            assertThat(updatedGroup.getOptions()).hasSize(1);
            assertThat(updatedGroup.getOptions().get(0)).isEqualTo(paidOption);
            assertThat(updatedGroup.isEmpty()).isFalse();
            assertThat(updatedGroup.getOptionCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("중복 옵션 추가 시 예외 발생")
        void 중복_옵션_추가_예외() {
            // Given
            OptionGroup groupWithOption = optionGroup.addOption(paidOption);

            // When & Then
            assertThatThrownBy(() -> groupWithOption.addOption(paidOption))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("동일한 이름과 가격의 옵션이 이미 존재합니다: 곱빼기");
        }

        @Test
        @DisplayName("옵션 제거 성공")
        void 옵션_제거_성공() {
            // Given
            OptionGroup groupWithOptions = optionGroup.addOption(paidOption).addOption(freeOption);

            // When
            OptionGroup updatedGroup = groupWithOptions.removeOption("곱빼기", Money.of(new BigDecimal("2000")));

            // Then
            assertThat(updatedGroup.getOptions()).hasSize(1);
            assertThat(updatedGroup.getOptions().get(0)).isEqualTo(freeOption);
        }

        @Test
        @DisplayName("존재하지 않는 옵션 제거 시 예외 발생")
        void 존재하지_않는_옵션_제거_예외() {
            // Given
            OptionGroup groupWithOption = optionGroup.addOption(paidOption);

            // When & Then
            assertThatThrownBy(() -> groupWithOption.removeOption("존재하지않는옵션", Money.zero()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("제거할 옵션을 찾을 수 없습니다: 존재하지않는옵션");
        }

        @Test
        @DisplayName("옵션그룹 이름 변경 성공")
        void 옵션그룹_이름_변경_성공() {
            // When
            OptionGroup updatedGroup = optionGroup.changeName("새로운 양 선택");

            // Then
            assertThat(updatedGroup.getName()).isEqualTo("새로운 양 선택");
            assertThat(updatedGroup.getId()).isEqualTo(optionGroup.getId());
            assertThat(updatedGroup.isRequired()).isEqualTo(optionGroup.isRequired());
        }

        @Test
        @DisplayName("옵션 이름 변경 성공")
        void 옵션_이름_변경_성공() {
            // Given
            OptionGroup groupWithOption = optionGroup.addOption(paidOption);

            // When
            OptionGroup updatedGroup = groupWithOption.changeOptionName("곱빼기", Money.of(new BigDecimal("2000")), "대곱빼기");

            // Then
            Option updatedOption = updatedGroup.getOptions().stream()
                .filter(option -> option.getName().equals("대곱빼기"))
                .findFirst()
                .orElseThrow();
            assertThat(updatedOption.getPrice()).isEqualTo(Money.of(new BigDecimal("2000")));
        }

        @Test
        @DisplayName("존재하지 않는 옵션 이름 변경 시 예외 발생")
        void 존재하지_않는_옵션_이름_변경_예외() {
            // Given
            OptionGroup groupWithOption = optionGroup.addOption(paidOption);

            // When & Then
            assertThatThrownBy(() -> groupWithOption.changeOptionName("존재하지않는옵션", Money.zero(), "새이름"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("변경할 옵션을 찾을 수 없습니다: 존재하지않는옵션");
        }
    }

    @Nested
    @DisplayName("유료 옵션 확인 테스트")
    class PaidOptionTest {

        @Test
        @DisplayName("유료 옵션이 있는 경우 hasPaidOptions 반환 true")
        void 유료_옵션_있음_true() {
            // Given
            OptionGroup groupWithPaidOption = optionGroup.addOption(paidOption);

            // When & Then
            assertThat(groupWithPaidOption.hasPaidOptions()).isTrue();
        }

        @Test
        @DisplayName("무료 옵션만 있는 경우 hasPaidOptions 반환 false")
        void 무료_옵션만_있음_false() {
            // Given
            OptionGroup groupWithFreeOption = optionGroup.addOption(freeOption);

            // When & Then
            assertThat(groupWithFreeOption.hasPaidOptions()).isFalse();
        }

        @Test
        @DisplayName("옵션이 없는 경우 hasPaidOptions 반환 false")
        void 옵션_없음_false() {
            // When & Then
            assertThat(optionGroup.hasPaidOptions()).isFalse();
        }

        @Test
        @DisplayName("유료와 무료 옵션이 모두 있는 경우 hasPaidOptions 반환 true")
        void 유료_무료_옵션_모두_있음_true() {
            // Given
            OptionGroup groupWithBothOptions = optionGroup.addOption(paidOption).addOption(freeOption);

            // When & Then
            assertThat(groupWithBothOptions.hasPaidOptions()).isTrue();
        }
    }
}