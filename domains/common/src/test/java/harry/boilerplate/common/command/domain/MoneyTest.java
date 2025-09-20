package harry.boilerplate.common.command.domain;

import org.junit.jupiter.api.Test;

import harry.boilerplate.common.domain.entity.Money;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Money 값 객체 테스트
 */
class MoneyTest {

    @Test
    void Money_생성_테스트() {
        // Given & When
        Money money = Money.of("1000");
        
        // Then
        assertThat(money.getAmount()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(money.getCurrency()).isEqualTo(Currency.getInstance("KRW"));
    }

    @Test
    void Money_덧셈_테스트() {
        // Given
        Money money1 = Money.of("1000");
        Money money2 = Money.of("500");
        
        // When
        Money result = money1.add(money2);
        
        // Then
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("1500.00"));
    }

    @Test
    void Money_뺄셈_테스트() {
        // Given
        Money money1 = Money.of("1000");
        Money money2 = Money.of("300");
        
        // When
        Money result = money1.subtract(money2);
        
        // Then
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("700.00"));
    }

    @Test
    void Money_곱셈_테스트() {
        // Given
        Money money = Money.of("100");
        
        // When
        Money result = money.multiply(3);
        
        // Then
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("300.00"));
    }

    @Test
    void Money_비교_테스트() {
        // Given
        Money money1 = Money.of("1000");
        Money money2 = Money.of("500");
        Money money3 = Money.of("1000");
        
        // Then
        assertThat(money1.isGreaterThan(money2)).isTrue();
        assertThat(money2.isLessThan(money1)).isTrue();
        assertThat(money1.isGreaterThanOrEqual(money3)).isTrue();
    }

    @Test
    void Money_동등성_테스트() {
        // Given
        Money money1 = Money.of("1000");
        Money money2 = Money.of("1000");
        Money money3 = Money.of("500");
        
        // Then
        assertThat(money1).isEqualTo(money2);
        assertThat(money1).isNotEqualTo(money3);
        assertThat(money1.hashCode()).isEqualTo(money2.hashCode());
    }

    @Test
    void Money_zero_테스트() {
        // Given & When
        Money zero = Money.zero();
        
        // Then
        assertThat(zero.isZero()).isTrue();
        assertThat(zero.isPositive()).isFalse();
    }

    @Test
    void Money_null_검증_테스트() {
        // Then
        assertThatThrownBy(() -> new Money(null, Currency.getInstance("KRW")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Amount cannot be null");
            
        assertThatThrownBy(() -> new Money(BigDecimal.TEN, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Currency cannot be null");
    }

    @Test
    void Money_다른_통화_연산_예외_테스트() {
        // Given
        Money krw = new Money(BigDecimal.valueOf(1000), Currency.getInstance("KRW"));
        Money usd = new Money(BigDecimal.valueOf(1000), Currency.getInstance("USD"));
        
        // Then
        assertThatThrownBy(() -> krw.add(usd))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Cannot operate on different currencies");
    }
}