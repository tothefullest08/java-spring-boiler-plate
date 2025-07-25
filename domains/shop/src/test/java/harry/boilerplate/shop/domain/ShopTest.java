package harry.boilerplate.shop.domain;

import harry.boilerplate.common.domain.entity.Money;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import static org.assertj.core.api.Assertions.*;

class ShopTest {
    
    @Test
    void Shop_생성_성공() {
        // Given
        String name = "맛있는 식당";
        Money minOrderAmount = Money.of("15000");
        BusinessHours businessHours = new BusinessHours(
            LocalTime.of(9, 0), 
            LocalTime.of(22, 0)
        );
        
        // When
        Shop shop = new Shop(name, minOrderAmount, businessHours);
        
        // Then
        assertThat(shop.getName()).isEqualTo(name);
        assertThat(shop.getMinOrderAmount().getAmount()).isEqualByComparingTo(minOrderAmount.getAmount());
        assertThat(shop.getId()).isNotNull();
    }
    
    @Test
    void 영업시간_내_영업중_확인() {
        // Given
        BusinessHours businessHours = new BusinessHours(
            LocalTime.of(9, 0), 
            LocalTime.of(22, 0)
        );
        Shop shop = new Shop("테스트 식당", Money.of("10000"), businessHours);
        
        // When & Then
        assertThat(shop.isOpenAt(LocalTime.of(10, 0))).isTrue();
        assertThat(shop.isOpenAt(LocalTime.of(21, 59))).isTrue();
        assertThat(shop.isOpenAt(LocalTime.of(8, 59))).isFalse();
        assertThat(shop.isOpenAt(LocalTime.of(22, 0))).isFalse();
    }
}