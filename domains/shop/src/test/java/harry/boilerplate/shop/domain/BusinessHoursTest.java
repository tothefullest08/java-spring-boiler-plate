package harry.boilerplate.shop.domain;

import harry.boilerplate.shop.domain.valueObject.BusinessHours;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DisplayName("BusinessHours 값 객체 테스트")
class BusinessHoursTest {

    @Nested
    @DisplayName("BusinessHours 생성 테스트")
    class BusinessHoursCreationTest {

        @Test
        @DisplayName("정상적인 BusinessHours 생성")
        void 정상적인_BusinessHours_생성() {
            // Given
            Map<DayOfWeek, LocalTime[]> weeklyHours = new EnumMap<>(DayOfWeek.class);
            weeklyHours.put(DayOfWeek.MONDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)});
            weeklyHours.put(DayOfWeek.TUESDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)});

            // When
            BusinessHours businessHours = new BusinessHours(weeklyHours);

            // Then
            assertThat(businessHours.isOpenOn(DayOfWeek.MONDAY)).isTrue();
            assertThat(businessHours.isOpenOn(DayOfWeek.TUESDAY)).isTrue();
            assertThat(businessHours.isOpenOn(DayOfWeek.WEDNESDAY)).isFalse();
        }

        @Test
        @DisplayName("전일 영업 BusinessHours 생성")
        void 전일_영업_BusinessHours_생성() {
            // Given
            Map<DayOfWeek, LocalTime[]> weeklyHours = new EnumMap<>(DayOfWeek.class);
            for (DayOfWeek day : DayOfWeek.values()) {
                weeklyHours.put(day, new LocalTime[]{LocalTime.of(0, 0), LocalTime.of(23, 59)});
            }

            // When
            BusinessHours businessHours = new BusinessHours(weeklyHours);

            // Then
            for (DayOfWeek day : DayOfWeek.values()) {
                assertThat(businessHours.isOpenOn(day)).isTrue();
            }
        }

        @Test
        @DisplayName("영업시간 정보가 null인 경우 예외 발생")
        void 영업시간_정보가_null인_경우_예외_발생() {
            // When & Then
            assertThatThrownBy(() -> new BusinessHours(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("영업시간 정보는 필수입니다");
        }

        @Test
        @DisplayName("빈 영업시간 정보로 생성")
        void 빈_영업시간_정보로_생성() {
            // Given
            Map<DayOfWeek, LocalTime[]> emptyHours = new EnumMap<>(DayOfWeek.class);

            // When
            BusinessHours businessHours = new BusinessHours(emptyHours);

            // Then
            for (DayOfWeek day : DayOfWeek.values()) {
                assertThat(businessHours.isOpenOn(day)).isFalse();
            }
        }
    }

    @Nested
    @DisplayName("BusinessHours 동등성 테스트")
    class BusinessHoursEqualityTest {

        @Test
        @DisplayName("같은 영업시간의 BusinessHours는 동등함")
        void 같은_영업시간의_BusinessHours는_동등함() {
            // Given
            Map<DayOfWeek, LocalTime[]> weeklyHours1 = new EnumMap<>(DayOfWeek.class);
            weeklyHours1.put(DayOfWeek.MONDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)});
            
            Map<DayOfWeek, LocalTime[]> weeklyHours2 = new EnumMap<>(DayOfWeek.class);
            weeklyHours2.put(DayOfWeek.MONDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)});
            
            BusinessHours hours1 = new BusinessHours(weeklyHours1);
            BusinessHours hours2 = new BusinessHours(weeklyHours2);

            // Then
            assertThat(hours1).isEqualTo(hours2);
            assertThat(hours1.hashCode()).isEqualTo(hours2.hashCode());
        }

        @Test
        @DisplayName("다른 영업시간의 BusinessHours는 동등하지 않음")
        void 다른_영업시간의_BusinessHours는_동등하지_않음() {
            // Given
            Map<DayOfWeek, LocalTime[]> weeklyHours1 = new EnumMap<>(DayOfWeek.class);
            weeklyHours1.put(DayOfWeek.MONDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)});
            
            Map<DayOfWeek, LocalTime[]> weeklyHours2 = new EnumMap<>(DayOfWeek.class);
            weeklyHours2.put(DayOfWeek.MONDAY, new LocalTime[]{LocalTime.of(10, 0), LocalTime.of(22, 0)});
            
            BusinessHours hours1 = new BusinessHours(weeklyHours1);
            BusinessHours hours2 = new BusinessHours(weeklyHours2);

            // Then
            assertThat(hours1).isNotEqualTo(hours2);
        }

        @Test
        @DisplayName("다른 요일 설정의 BusinessHours는 동등하지 않음")
        void 다른_요일_설정의_BusinessHours는_동등하지_않음() {
            // Given
            Map<DayOfWeek, LocalTime[]> weeklyHours1 = new EnumMap<>(DayOfWeek.class);
            weeklyHours1.put(DayOfWeek.MONDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)});
            
            Map<DayOfWeek, LocalTime[]> weeklyHours2 = new EnumMap<>(DayOfWeek.class);
            weeklyHours2.put(DayOfWeek.TUESDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)});
            
            BusinessHours hours1 = new BusinessHours(weeklyHours1);
            BusinessHours hours2 = new BusinessHours(weeklyHours2);

            // Then
            assertThat(hours1).isNotEqualTo(hours2);
        }
    }

    @Nested
    @DisplayName("BusinessHours 비즈니스 로직 테스트")
    class BusinessHoursBusinessLogicTest {

        @Test
        @DisplayName("영업시간 내 시간 확인 - 영업 중")
        void 영업시간_내_시간_확인_영업_중() {
            // Given
            Map<DayOfWeek, LocalTime[]> weeklyHours = new EnumMap<>(DayOfWeek.class);
            weeklyHours.put(DayOfWeek.MONDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)});
            BusinessHours businessHours = new BusinessHours(weeklyHours);
            LocalTime currentTime = LocalTime.of(15, 30);

            // When & Then
            assertThat(businessHours.isOpenAt(DayOfWeek.MONDAY, currentTime)).isTrue();
        }

        @Test
        @DisplayName("영업시간 전 시간 확인 - 영업 전")
        void 영업시간_전_시간_확인_영업_전() {
            // Given
            Map<DayOfWeek, LocalTime[]> weeklyHours = new EnumMap<>(DayOfWeek.class);
            weeklyHours.put(DayOfWeek.MONDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)});
            BusinessHours businessHours = new BusinessHours(weeklyHours);
            LocalTime currentTime = LocalTime.of(8, 30);

            // When & Then
            assertThat(businessHours.isOpenAt(DayOfWeek.MONDAY, currentTime)).isFalse();
        }

        @Test
        @DisplayName("영업시간 후 시간 확인 - 영업 종료")
        void 영업시간_후_시간_확인_영업_종료() {
            // Given
            Map<DayOfWeek, LocalTime[]> weeklyHours = new EnumMap<>(DayOfWeek.class);
            weeklyHours.put(DayOfWeek.MONDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)});
            BusinessHours businessHours = new BusinessHours(weeklyHours);
            LocalTime currentTime = LocalTime.of(22, 30);

            // When & Then
            assertThat(businessHours.isOpenAt(DayOfWeek.MONDAY, currentTime)).isFalse();
        }

        @Test
        @DisplayName("오픈 시간 정각 확인 - 영업 중")
        void 오픈_시간_정각_확인_영업_중() {
            // Given
            Map<DayOfWeek, LocalTime[]> weeklyHours = new EnumMap<>(DayOfWeek.class);
            weeklyHours.put(DayOfWeek.MONDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)});
            BusinessHours businessHours = new BusinessHours(weeklyHours);
            LocalTime currentTime = LocalTime.of(9, 0);

            // When & Then
            assertThat(businessHours.isOpenAt(DayOfWeek.MONDAY, currentTime)).isTrue();
        }

        @Test
        @DisplayName("마감 시간 정각 확인 - 영업 종료")
        void 마감_시간_정각_확인_영업_종료() {
            // Given
            Map<DayOfWeek, LocalTime[]> weeklyHours = new EnumMap<>(DayOfWeek.class);
            weeklyHours.put(DayOfWeek.MONDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)});
            BusinessHours businessHours = new BusinessHours(weeklyHours);
            LocalTime currentTime = LocalTime.of(22, 0);

            // When & Then
            assertThat(businessHours.isOpenAt(DayOfWeek.MONDAY, currentTime)).isFalse();
        }

        @Test
        @DisplayName("휴무일 확인")
        void 휴무일_확인() {
            // Given
            Map<DayOfWeek, LocalTime[]> weeklyHours = new EnumMap<>(DayOfWeek.class);
            weeklyHours.put(DayOfWeek.MONDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)});
            BusinessHours businessHours = new BusinessHours(weeklyHours);

            // When & Then
            assertThat(businessHours.isOpenOn(DayOfWeek.MONDAY)).isTrue();
            assertThat(businessHours.isOpenOn(DayOfWeek.SUNDAY)).isFalse();
            assertThat(businessHours.isOpenAt(DayOfWeek.SUNDAY, LocalTime.of(15, 0))).isFalse();
        }

        @Test
        @DisplayName("전체 영업시간 정보 조회")
        void 전체_영업시간_정보_조회() {
            // Given
            Map<DayOfWeek, LocalTime[]> weeklyHours = new EnumMap<>(DayOfWeek.class);
            weeklyHours.put(DayOfWeek.MONDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)});
            weeklyHours.put(DayOfWeek.TUESDAY, new LocalTime[]{LocalTime.of(10, 0), LocalTime.of(21, 0)});
            BusinessHours businessHours = new BusinessHours(weeklyHours);

            // When
            Map<DayOfWeek, LocalTime[]> retrievedHours = businessHours.getWeeklyHours();

            // Then
            assertThat(retrievedHours.get(DayOfWeek.MONDAY)[0]).isEqualTo(LocalTime.of(9, 0));
            assertThat(retrievedHours.get(DayOfWeek.MONDAY)[1]).isEqualTo(LocalTime.of(22, 0));
            assertThat(retrievedHours.get(DayOfWeek.TUESDAY)[0]).isEqualTo(LocalTime.of(10, 0));
            assertThat(retrievedHours.get(DayOfWeek.TUESDAY)[1]).isEqualTo(LocalTime.of(21, 0));
            assertThat(retrievedHours.get(DayOfWeek.WEDNESDAY)[0]).isNull();
            assertThat(retrievedHours.get(DayOfWeek.WEDNESDAY)[1]).isNull();
        }
    }
}