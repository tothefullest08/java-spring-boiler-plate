package harry.boilerplate.order.domain;

import harry.boilerplate.common.domain.entity.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Money ValueObject 테스트 (Order Context에서 사용)
 */
class MoneyTest {
    
    @Test
    void Money_생성_성공() {
        // Given & When
        Money money = Money.of(10000);
        
        // Then
        assertThat(money.getAmount()).isEqualTo(new BigDecimal("10000.00"));
        assertThat(money.getCurrency().getCurrencyCode()).isEqualTo("KRW");
    }
    
    @Test
    void Money_zero_생성_성공() {
        // Given & When
        Money zero = Money.zero();
        
        // Then
        assertThat(zero.getAmount()).isEqualTo(BigDecimal.ZERO.setScale(2));
        assertThat(zero.isZero()).isTrue();
        assertThat(zero.isPositive()).isFalse();
    }
    
    @Test
    void Money_덧셈_성공() {
        // Given
        Money money1 = Money.of(10000);
        Money money2 = Money.of(5000);
        
        // When
        Money result = money1.add(money2);
        
        // Then
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("15000.00"));
    }
    
    @Test
    void Money_곱셈_성공() {
        // Given
        Money money = Money.of(10000);
        
        // When
        Money result = money.multiply(3);
        
        // Then
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("30000.00"));
    }
    
    @Test
    void Money_비교_성공() {
        // Given
        Money money1 = Money.of(10000);
        Money money2 = Money.of(5000);
        Money money3 = Money.of(10000);
        
        // When & Then
        assertThat(money1.isGreaterThan(money2)).isTrue();
        assertThat(money2.isLessThan(money1)).isTrue();
        assertThat(money1.equals(money3)).isTrue();
    }
    
    @Test
    void Money_불변성_확인() {
        // Given
        Money original = Money.of(10000);
        
        // When
        Money added = original.add(Money.of(5000));
        Money multiplied = original.multiply(2);
        
        // Then
        assertThat(original.getAmount()).isEqualTo(new BigDecimal("10000.00")); // 원본 변경 안됨
        assertThat(added.getAmount()).isEqualTo(new BigDecimal("15000.00"));
        assertThat(multiplied.getAmount()).isEqualTo(new BigDecimal("20000.00"));
    }
}