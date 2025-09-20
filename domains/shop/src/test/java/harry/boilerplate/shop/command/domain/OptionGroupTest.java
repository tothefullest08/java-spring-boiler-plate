package harry.boilerplate.shop.command.domain;

import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.shop.command.domain.exception.MenuDomainException;
import harry.boilerplate.shop.command.domain.exception.MenuErrorCode;
import harry.boilerplate.shop.command.domain.entity.OptionGroup;
import harry.boilerplate.shop.command.domain.valueObject.Option;
import harry.boilerplate.shop.command.domain.valueObject.OptionGroupId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("OptionGroup 도메인 엔티티 테스트")
class OptionGroupTest {

    private OptionGroupId optionGroupId;
    private OptionGroup optionGroup;
    private Option paidOption;
    private Option freeOption;

    @BeforeEach
    void setUp() {
        optionGroupId = OptionGroupId.generate();
        paidOption = new Option("곱빼기", Money.of(new BigDecimal("2000")));
        freeOption = new Option("보통", Money.zero());
        optionGroup = new OptionGroup(optionGroupId, "양 선택", true);
    }

    @Nested
    @DisplayName("OptionGroup 생성 테스트")
    class OptionGroupCreationTest {

        @Test
        @DisplayName("정상적인 OptionGroup 생성")
        void 정상적인_OptionGroup_생성() {
            // When
            OptionGroup group = new OptionGroup(optionGroupId, "양 선택", true);

            // Then
            assertThat(group.getId()).isEqualTo(optionGroupId);
            assertThat(group.getName()).isEqualTo("양 선택");
            assertThat(group.isRequired()).isTrue();
            assertThat(group.getOptions()).isEmpty();
        }

        @Test
        @DisplayName("OptionGroupId가 null인 경우 예외 발생")
        void OptionGroupId가_null인_경우_예외_발생() {
            // When & Then
            assertThatThrownBy(() -> new OptionGroup(null, "양 선택", true))
                .isInstanceOf(MenuDomainException.class)
                .extracting(e -> ((MenuDomainException) e).getErrorCode())
                .isEqualTo(MenuErrorCode.OPTION_GROUP_ID_REQUIRED);
        }

        @Test
        @DisplayName("이름이 null인 경우 예외 발생")
        void 이름이_null인_경우_예외_발생() {
            // When & Then
            assertThatThrownBy(() -> new OptionGroup(optionGroupId, null, true))
                .isInstanceOf(MenuDomainException.class)
                .extracting(e -> ((MenuDomainException) e).getErrorCode())
                .isEqualTo(MenuErrorCode.NEW_OPTION_GROUP_NAME_REQUIRED);
        }

        @Test
        @DisplayName("이름이 빈 문자열인 경우 예외 발생")
        void 이름이_빈_문자열인_경우_예외_발생() {
            // When & Then
            assertThatThrownBy(() -> new OptionGroup(optionGroupId, "", true))
                .isInstanceOf(MenuDomainException.class)
                .extracting(e -> ((MenuDomainException) e).getErrorCode())
                .isEqualTo(MenuErrorCode.NEW_OPTION_GROUP_NAME_REQUIRED);
        }

        @Test
        @DisplayName("이름이 공백만 있는 경우 예외 발생")
        void 이름이_공백만_있는_경우_예외_발생() {
            // When & Then
            assertThatThrownBy(() -> new OptionGroup(optionGroupId, "   ", true))
                .isInstanceOf(MenuDomainException.class)
                .extracting(e -> ((MenuDomainException) e).getErrorCode())
                .isEqualTo(MenuErrorCode.NEW_OPTION_GROUP_NAME_REQUIRED);
        }
    }

    @Nested
    @DisplayName("옵션 관리 테스트")
    class OptionManagementTest {

        @Test
        @DisplayName("옵션 추가 성공")
        void 옵션_추가_성공() {
            // When
            optionGroup.addOption(paidOption);

            // Then
            assertThat(optionGroup.getOptions()).hasSize(1);
            assertThat(optionGroup.getOptions().get(0).getName()).isEqualTo("곱빼기");
            assertThat(optionGroup.getOptions().get(0).getPrice()).isEqualTo(Money.of(new BigDecimal("2000")));
        }

        @Test
        @DisplayName("중복 옵션 추가 시 예외 발생")
        void 중복_옵션_추가_시_예외_발생() {
            // Given
            optionGroup.addOption(paidOption);

            // When & Then
            assertThatThrownBy(() -> optionGroup.addOption(paidOption))
                .isInstanceOf(MenuDomainException.class)
                .extracting(e -> ((MenuDomainException) e).getErrorCode())
                .isEqualTo(MenuErrorCode.DUPLICATE_OPTION_GROUP_NAME);
        }

        @Test
        @DisplayName("옵션 제거 성공")
        void 옵션_제거_성공() {
            // Given
            optionGroup.addOption(paidOption);
            optionGroup.addOption(freeOption);

            // When
            optionGroup.removeOption("곱빼기", Money.of(new BigDecimal("2000")));

            // Then
            assertThat(optionGroup.getOptions()).hasSize(1);
            assertThat(optionGroup.getOptions().get(0).getName()).isEqualTo("보통");
        }

        @Test
        @DisplayName("존재하지 않는 옵션 제거 시 예외 발생")
        void 존재하지_않는_옵션_제거_시_예외_발생() {
            // Given
            optionGroup.addOption(paidOption);

            // When & Then
            assertThatThrownBy(() -> optionGroup.removeOption("존재하지않음", Money.zero()))
                .isInstanceOf(MenuDomainException.class)
                .extracting(e -> ((MenuDomainException) e).getErrorCode())
                .isEqualTo(MenuErrorCode.OPTION_NOT_FOUND);
        }

        @Test
        @DisplayName("옵션그룹 이름 변경 성공")
        void 옵션그룹_이름_변경_성공() {
            // When
            optionGroup.changeName("새로운 양 선택");

            // Then
            assertThat(optionGroup.getName()).isEqualTo("새로운 양 선택");
        }

        @Test
        @DisplayName("옵션 이름 변경 성공")
        void 옵션_이름_변경_성공() {
            // Given
            optionGroup.addOption(paidOption);

            // When
            optionGroup.changeOptionName("곱빼기", Money.of(new BigDecimal("2000")), "대곱빼기");

            // Then
            Option updatedOption = optionGroup.getOptions().stream()
                .filter(option -> option.getName().equals("대곱빼기"))
                .findFirst()
                .orElseThrow();
            assertThat(updatedOption.getName()).isEqualTo("대곱빼기");
            assertThat(updatedOption.getPrice()).isEqualTo(Money.of(new BigDecimal("2000")));
        }

        @Test
        @DisplayName("존재하지 않는 옵션 이름 변경 시 예외 발생")
        void 존재하지_않는_옵션_이름_변경_시_예외_발생() {
            // Given
            optionGroup.addOption(paidOption);

            // When & Then
            assertThatThrownBy(() -> optionGroup.changeOptionName("존재하지않음", Money.zero(), "새이름"))
                .isInstanceOf(MenuDomainException.class)
                .extracting(e -> ((MenuDomainException) e).getErrorCode())
                .isEqualTo(MenuErrorCode.OPTION_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("유료 옵션 테스트")
    class PaidOptionTest {

        @Test
        @DisplayName("유료 옵션이 있는 경우 hasPaidOptions는 true")
        void 유료_옵션이_있는_경우_hasPaidOptions는_true() {
            // Given
            optionGroup.addOption(paidOption);

            // When & Then
            assertThat(optionGroup.hasPaidOptions()).isTrue();
        }

        @Test
        @DisplayName("무료 옵션만 있는 경우 hasPaidOptions는 false")
        void 무료_옵션만_있는_경우_hasPaidOptions는_false() {
            // Given
            optionGroup.addOption(freeOption);

            // When & Then
            assertThat(optionGroup.hasPaidOptions()).isFalse();
        }

        @Test
        @DisplayName("옵션이 없는 경우 hasPaidOptions는 false")
        void 옵션이_없는_경우_hasPaidOptions는_false() {
            // When & Then
            assertThat(optionGroup.hasPaidOptions()).isFalse();
        }

        @Test
        @DisplayName("유료와 무료 옵션이 모두 있는 경우 hasPaidOptions는 true")
        void 유료와_무료_옵션이_모두_있는_경우_hasPaidOptions는_true() {
            // Given
            optionGroup.addOption(paidOption);
            optionGroup.addOption(freeOption);

            // When & Then
            assertThat(optionGroup.hasPaidOptions()).isTrue();
        }
    }

    @Nested
    @DisplayName("옵션 개수 테스트")
    class OptionCountTest {

        @Test
        @DisplayName("옵션 개수 반환")
        void 옵션_개수_반환() {
            // Given
            optionGroup.addOption(paidOption);
            optionGroup.addOption(freeOption);

            // When & Then
            assertThat(optionGroup.getOptionCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("빈 옵션그룹 확인")
        void 빈_옵션그룹_확인() {
            // When & Then
            assertThat(optionGroup.isEmpty()).isTrue();
            assertThat(optionGroup.getOptionCount()).isEqualTo(0);
        }
    }
}