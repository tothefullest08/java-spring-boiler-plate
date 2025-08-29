package harry.boilerplate.shop.domain;

import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.shop.domain.exception.MenuDomainException;
import harry.boilerplate.shop.domain.exception.MenuErrorCode;
import harry.boilerplate.shop.domain.valueObject.Option;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Option 값 객체 테스트")
class OptionTest {

    @Nested
    @DisplayName("옵션 생성 테스트")
    class OptionCreationTest {

        @Test
        @DisplayName("정상적인 옵션 생성 성공")
        void 옵션_생성_성공() {
            // Given & When
            Option option = new Option("곱빼기", Money.of(new BigDecimal("2000")));

            // Then
            assertThat(option.getName()).isEqualTo("곱빼기");
            assertThat(option.getPrice()).isEqualTo(Money.of(new BigDecimal("2000")));
            assertThat(option.isPaid()).isTrue();
            assertThat(option.isFree()).isFalse();
        }

        @Test
        @DisplayName("무료 옵션 생성 성공")
        void 무료_옵션_생성_성공() {
            // Given & When
            Option option = new Option("보통", Money.zero());

            // Then
            assertThat(option.getName()).isEqualTo("보통");
            assertThat(option.getPrice()).isEqualTo(Money.zero());
            assertThat(option.isPaid()).isFalse();
            assertThat(option.isFree()).isTrue();
        }

        @Test
        @DisplayName("옵션 이름이 null인 경우 예외 발생")
        void 옵션이름_null_예외() {
            // When & Then
            assertThatThrownBy(() -> new Option(null, Money.of(new BigDecimal("1000"))))
                    .isInstanceOf(MenuDomainException.class)
                    .extracting(e -> ((MenuDomainException) e).getErrorCode())
                    .isEqualTo(MenuErrorCode.CURRENT_OPTION_NAME_REQUIRED);
        }

        @Test
        @DisplayName("옵션 이름이 빈 문자열인 경우 예외 발생")
        void 옵션이름_빈문자열_예외() {
            // When & Then
            assertThatThrownBy(() -> new Option("", Money.of(new BigDecimal("1000"))))
                    .isInstanceOf(MenuDomainException.class)
                    .extracting(e -> ((MenuDomainException) e).getErrorCode())
                    .isEqualTo(MenuErrorCode.CURRENT_OPTION_NAME_REQUIRED);

            assertThatThrownBy(() -> new Option("   ", Money.of(new BigDecimal("1000"))))
                    .isInstanceOf(MenuDomainException.class)
                    .extracting(e -> ((MenuDomainException) e).getErrorCode())
                    .isEqualTo(MenuErrorCode.CURRENT_OPTION_NAME_REQUIRED);
        }

        @Test
        @DisplayName("옵션 가격이 null인 경우 예외 발생")
        void 옵션가격_null_예외() {
            // When & Then
            assertThatThrownBy(() -> new Option("곱빼기", null))
                    .isInstanceOf(MenuDomainException.class)
                    .extracting(e -> ((MenuDomainException) e).getErrorCode())
                    .isEqualTo(MenuErrorCode.CURRENT_OPTION_PRICE_REQUIRED);
        }

        @Test
        @DisplayName("옵션 가격이 음수인 경우 예외 발생")
        void 옵션가격_음수_예외() {
            // When & Then
            assertThatThrownBy(() -> new Option("곱빼기", Money.of(new BigDecimal("-1000"))))
                    .isInstanceOf(MenuDomainException.class)
                    .extracting(e -> ((MenuDomainException) e).getErrorCode())
                    .isEqualTo(MenuErrorCode.INVALID_BASE_PRICE);
        }

        @Test
        @DisplayName("옵션 이름 앞뒤 공백 제거")
        void 옵션이름_공백_제거() {
            // Given & When
            Option option = new Option("  곱빼기  ", Money.of(new BigDecimal("2000")));

            // Then
            assertThat(option.getName()).isEqualTo("곱빼기");
        }
    }

    @Nested
    @DisplayName("옵션 수정 테스트")
    class OptionModificationTest {

        @Test
        @DisplayName("옵션 이름 변경 성공")
        void 옵션_이름_변경_성공() {
            // Given
            Option originalOption = new Option("곱빼기", Money.of(new BigDecimal("2000")));

            // When
            Option updatedOption = originalOption.changeName("대곱빼기");

            // Then
            assertThat(updatedOption.getName()).isEqualTo("대곱빼기");
            assertThat(updatedOption.getPrice()).isEqualTo(Money.of(new BigDecimal("2000")));
            assertThat(originalOption.getName()).isEqualTo("곱빼기"); // 원본은 변경되지 않음
        }

        @Test
        @DisplayName("옵션 이름 변경 시 null인 경우 예외 발생")
        void 옵션_이름_변경_null_예외() {
            // Given
            Option option = new Option("곱빼기", Money.of(new BigDecimal("2000")));

            // When & Then
            assertThatThrownBy(() -> option.changeName(null))
                    .isInstanceOf(MenuDomainException.class)
                    .extracting(e -> ((MenuDomainException) e).getErrorCode())
                    .isEqualTo(MenuErrorCode.NEW_OPTION_NAME_REQUIRED);
        }

        @Test
        @DisplayName("옵션 이름 변경 시 빈 문자열인 경우 예외 발생")
        void 옵션_이름_변경_빈문자열_예외() {
            // Given
            Option option = new Option("곱빼기", Money.of(new BigDecimal("2000")));

            // When & Then
            assertThatThrownBy(() -> option.changeName(""))
                    .isInstanceOf(MenuDomainException.class)
                    .extracting(e -> ((MenuDomainException) e).getErrorCode())
                    .isEqualTo(MenuErrorCode.NEW_OPTION_NAME_REQUIRED);

            assertThatThrownBy(() -> option.changeName("   "))
                    .isInstanceOf(MenuDomainException.class)
                    .extracting(e -> ((MenuDomainException) e).getErrorCode())
                    .isEqualTo(MenuErrorCode.NEW_OPTION_NAME_REQUIRED);
        }

        @Test
        @DisplayName("옵션 가격 변경 성공")
        void 옵션_가격_변경_성공() {
            // Given
            Option originalOption = new Option("곱빼기", Money.of(new BigDecimal("2000")));

            // When
            Option updatedOption = originalOption.changePrice(Money.of(new BigDecimal("3000")));

            // Then
            assertThat(updatedOption.getName()).isEqualTo("곱빼기");
            assertThat(updatedOption.getPrice()).isEqualTo(Money.of(new BigDecimal("3000")));
            assertThat(originalOption.getPrice()).isEqualTo(Money.of(new BigDecimal("2000"))); // 원본은 변경되지 않음
        }

        @Test
        @DisplayName("옵션 가격 변경 시 null인 경우 예외 발생")
        void 옵션_가격_변경_null_예외() {
            // Given
            Option option = new Option("곱빼기", Money.of(new BigDecimal("2000")));

            // When & Then
            assertThatThrownBy(() -> option.changePrice(null))
                    .isInstanceOf(MenuDomainException.class)
                    .extracting(e -> ((MenuDomainException) e).getErrorCode())
                    .isEqualTo(MenuErrorCode.NEW_OPTION_PRICE_REQUIRED);
        }
    }

    @Nested
    @DisplayName("옵션 유료/무료 확인 테스트")
    class OptionPriceTypeTest {

        @Test
        @DisplayName("유료 옵션 확인")
        void 유료_옵션_확인() {
            // Given
            Option paidOption1 = new Option("곱빼기", Money.of(new BigDecimal("2000")));
            Option paidOption2 = new Option("특곱빼기", Money.of(new BigDecimal("0.01")));

            // When & Then
            assertThat(paidOption1.isPaid()).isTrue();
            assertThat(paidOption1.isFree()).isFalse();
            assertThat(paidOption2.isPaid()).isTrue();
            assertThat(paidOption2.isFree()).isFalse();
        }

        @Test
        @DisplayName("무료 옵션 확인")
        void 무료_옵션_확인() {
            // Given
            Option freeOption = new Option("보통", Money.zero());

            // When & Then
            assertThat(freeOption.isPaid()).isFalse();
            assertThat(freeOption.isFree()).isTrue();
        }
    }

    @Nested
    @DisplayName("옵션 동등성 테스트")
    class OptionEqualityTest {

        @Test
        @DisplayName("동일한 이름과 가격의 옵션은 동등함")
        void 동일한_옵션_동등성() {
            // Given
            Option option1 = new Option("곱빼기", Money.of(new BigDecimal("2000")));
            Option option2 = new Option("곱빼기", Money.of(new BigDecimal("2000")));

            // When & Then
            assertThat(option1).isEqualTo(option2);
            assertThat(option1.hashCode()).isEqualTo(option2.hashCode());
        }

        @Test
        @DisplayName("다른 이름의 옵션은 동등하지 않음")
        void 다른_이름_옵션_비동등성() {
            // Given
            Option option1 = new Option("곱빼기", Money.of(new BigDecimal("2000")));
            Option option2 = new Option("대곱빼기", Money.of(new BigDecimal("2000")));

            // When & Then
            assertThat(option1).isNotEqualTo(option2);
        }

        @Test
        @DisplayName("다른 가격의 옵션은 동등하지 않음")
        void 다른_가격_옵션_비동등성() {
            // Given
            Option option1 = new Option("곱빼기", Money.of(new BigDecimal("2000")));
            Option option2 = new Option("곱빼기", Money.of(new BigDecimal("3000")));

            // When & Then
            assertThat(option1).isNotEqualTo(option2);
        }

        @Test
        @DisplayName("null과 비교 시 동등하지 않음")
        void null과_비교_비동등성() {
            // Given
            Option option = new Option("곱빼기", Money.of(new BigDecimal("2000")));

            // When & Then
            assertThat(option).isNotEqualTo(null);
        }

        @Test
        @DisplayName("다른 타입과 비교 시 동등하지 않음")
        void 다른_타입과_비교_비동등성() {
            // Given
            Option option = new Option("곱빼기", Money.of(new BigDecimal("2000")));
            String notOption = "곱빼기";

            // When & Then
            assertThat(option).isNotEqualTo(notOption);
        }
    }

    @Nested
    @DisplayName("옵션 문자열 표현 테스트")
    class OptionStringRepresentationTest {

        @Test
        @DisplayName("toString 메서드 정상 동작")
        void toString_정상_동작() {
            // Given
            Option option = new Option("곱빼기", Money.of(new BigDecimal("2000")));

            // When
            String result = option.toString();

            // Then
            assertThat(result).contains("곱빼기");
            assertThat(result).contains("2000");
        }
    }
}