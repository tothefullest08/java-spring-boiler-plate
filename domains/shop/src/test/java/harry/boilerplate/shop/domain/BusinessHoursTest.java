package harry.boilerplate.shop.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import static org.assertj.core.api.Assertions.*;

class BusinessHoursTest {
    
    @Test
    void 일반적인_영업시간_확인() {
        // Given
        BusinessHours businessHours = new BusinessHours(
            LocalTime.of(9, 0), 
            LocalTime.of(18, 0)
        );
        
        // When & Then
        assertThat(businessHours.isOpenAt(LocalTime.of(9, 0))).isTrue();
        assertThat(businessHours.isOpenAt(LocalTime.of(12, 0))).isTrue();
        assertThat(businessHours.isOpenAt(LocalTime.of(17, 59))).isTrue();
        assertThat(businessHours.isOpenAt(LocalTime.of(18, 0))).isFalse();
        assertThat(businessHours.isOpenAt(LocalTime.of(8, 59))).isFalse();
    }
    
    @Test
    void 자정을_넘는_영업시간_확인() {
        // Given - 22:00 ~ 02:00
        BusinessHours businessHours = new BusinessHours(
            LocalTime.of(22, 0), 
            LocalTime.of(2, 0)
        );
        
        // When & Then
        assertThat(businessHours.isOpenAt(LocalTime.of(22, 0))).isTrue();
        assertThat(businessHours.isOpenAt(LocalTime.of(23, 30))).isTrue();
        assertThat(businessHours.isOpenAt(LocalTime.of(1, 30))).isTrue();
        assertThat(businessHours.isOpenAt(LocalTime.of(2, 0))).isFalse();
        assertThat(businessHours.isOpenAt(LocalTime.of(12, 0))).isFalse();
    }
}