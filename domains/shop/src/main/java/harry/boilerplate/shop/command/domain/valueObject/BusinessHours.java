package harry.boilerplate.shop.command.domain.valueObject;

import harry.boilerplate.common.domain.entity.ValueObject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.Map;

/**
 * 가게 영업시간 값 객체
 * 요일별 영업시간을 관리
 */
@Embeddable
public class BusinessHours extends ValueObject {
    
    @Column(name = "monday_open")
    private LocalTime mondayOpen;
    
    @Column(name = "monday_close")
    private LocalTime mondayClose;
    
    @Column(name = "tuesday_open")
    private LocalTime tuesdayOpen;
    
    @Column(name = "tuesday_close")
    private LocalTime tuesdayClose;
    
    @Column(name = "wednesday_open")
    private LocalTime wednesdayOpen;
    
    @Column(name = "wednesday_close")
    private LocalTime wednesdayClose;
    
    @Column(name = "thursday_open")
    private LocalTime thursdayOpen;
    
    @Column(name = "thursday_close")
    private LocalTime thursdayClose;
    
    @Column(name = "friday_open")
    private LocalTime fridayOpen;
    
    @Column(name = "friday_close")
    private LocalTime fridayClose;
    
    @Column(name = "saturday_open")
    private LocalTime saturdayOpen;
    
    @Column(name = "saturday_close")
    private LocalTime saturdayClose;
    
    @Column(name = "sunday_open")
    private LocalTime sundayOpen;
    
    @Column(name = "sunday_close")
    private LocalTime sundayClose;
    
    protected BusinessHours() {
        // JPA용 기본 생성자
    }
    
    public BusinessHours(Map<DayOfWeek, LocalTime[]> weeklyHours) {
        if (weeklyHours == null) {
            throw new IllegalArgumentException("영업시간 정보는 필수입니다");
        }
        
        setHoursFromMap(weeklyHours);
    }
    
    private void setHoursFromMap(Map<DayOfWeek, LocalTime[]> weeklyHours) {
        LocalTime[] monday = weeklyHours.get(DayOfWeek.MONDAY);
        if (monday != null && monday.length == 2) {
            this.mondayOpen = monday[0];
            this.mondayClose = monday[1];
        }
        
        LocalTime[] tuesday = weeklyHours.get(DayOfWeek.TUESDAY);
        if (tuesday != null && tuesday.length == 2) {
            this.tuesdayOpen = tuesday[0];
            this.tuesdayClose = tuesday[1];
        }
        
        LocalTime[] wednesday = weeklyHours.get(DayOfWeek.WEDNESDAY);
        if (wednesday != null && wednesday.length == 2) {
            this.wednesdayOpen = wednesday[0];
            this.wednesdayClose = wednesday[1];
        }
        
        LocalTime[] thursday = weeklyHours.get(DayOfWeek.THURSDAY);
        if (thursday != null && thursday.length == 2) {
            this.thursdayOpen = thursday[0];
            this.thursdayClose = thursday[1];
        }
        
        LocalTime[] friday = weeklyHours.get(DayOfWeek.FRIDAY);
        if (friday != null && friday.length == 2) {
            this.fridayOpen = friday[0];
            this.fridayClose = friday[1];
        }
        
        LocalTime[] saturday = weeklyHours.get(DayOfWeek.SATURDAY);
        if (saturday != null && saturday.length == 2) {
            this.saturdayOpen = saturday[0];
            this.saturdayClose = saturday[1];
        }
        
        LocalTime[] sunday = weeklyHours.get(DayOfWeek.SUNDAY);
        if (sunday != null && sunday.length == 2) {
            this.sundayOpen = sunday[0];
            this.sundayClose = sunday[1];
        }
    }
    
    /**
     * 특정 요일의 영업 여부 확인
     */
    public boolean isOpenOn(DayOfWeek dayOfWeek) {
        LocalTime[] hours = getHoursForDay(dayOfWeek);
        return hours[0] != null && hours[1] != null;
    }
    
    /**
     * 특정 요일과 시간에 영업 중인지 확인
     */
    public boolean isOpenAt(DayOfWeek dayOfWeek, LocalTime time) {
        if (!isOpenOn(dayOfWeek)) {
            return false;
        }
        
        LocalTime[] hours = getHoursForDay(dayOfWeek);
        LocalTime openTime = hours[0];
        LocalTime closeTime = hours[1];
        
        return !time.isBefore(openTime) && time.isBefore(closeTime);
    }
    
    private LocalTime[] getHoursForDay(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> new LocalTime[]{mondayOpen, mondayClose};
            case TUESDAY -> new LocalTime[]{tuesdayOpen, tuesdayClose};
            case WEDNESDAY -> new LocalTime[]{wednesdayOpen, wednesdayClose};
            case THURSDAY -> new LocalTime[]{thursdayOpen, thursdayClose};
            case FRIDAY -> new LocalTime[]{fridayOpen, fridayClose};
            case SATURDAY -> new LocalTime[]{saturdayOpen, saturdayClose};
            case SUNDAY -> new LocalTime[]{sundayOpen, sundayClose};
        };
    }
    
    /**
     * 전체 영업시간 정보를 Map으로 반환
     */
    public Map<DayOfWeek, LocalTime[]> getWeeklyHours() {
        Map<DayOfWeek, LocalTime[]> weeklyHours = new EnumMap<>(DayOfWeek.class);
        
        weeklyHours.put(DayOfWeek.MONDAY, new LocalTime[]{mondayOpen, mondayClose});
        weeklyHours.put(DayOfWeek.TUESDAY, new LocalTime[]{tuesdayOpen, tuesdayClose});
        weeklyHours.put(DayOfWeek.WEDNESDAY, new LocalTime[]{wednesdayOpen, wednesdayClose});
        weeklyHours.put(DayOfWeek.THURSDAY, new LocalTime[]{thursdayOpen, thursdayClose});
        weeklyHours.put(DayOfWeek.FRIDAY, new LocalTime[]{fridayOpen, fridayClose});
        weeklyHours.put(DayOfWeek.SATURDAY, new LocalTime[]{saturdayOpen, saturdayClose});
        weeklyHours.put(DayOfWeek.SUNDAY, new LocalTime[]{sundayOpen, sundayClose});
        
        return weeklyHours;
    }
    
    @Override
    protected Object[] getEqualityComponents() {
        return new Object[]{
            mondayOpen, mondayClose,
            tuesdayOpen, tuesdayClose,
            wednesdayOpen, wednesdayClose,
            thursdayOpen, thursdayClose,
            fridayOpen, fridayClose,
            saturdayOpen, saturdayClose,
            sundayOpen, sundayClose
        };
    }
    
    @Override
    protected boolean equalsByValue(Object other) {
        BusinessHours that = (BusinessHours) other;
        return java.util.Objects.equals(this.mondayOpen, that.mondayOpen) &&
               java.util.Objects.equals(this.mondayClose, that.mondayClose) &&
               java.util.Objects.equals(this.tuesdayOpen, that.tuesdayOpen) &&
               java.util.Objects.equals(this.tuesdayClose, that.tuesdayClose) &&
               java.util.Objects.equals(this.wednesdayOpen, that.wednesdayOpen) &&
               java.util.Objects.equals(this.wednesdayClose, that.wednesdayClose) &&
               java.util.Objects.equals(this.thursdayOpen, that.thursdayOpen) &&
               java.util.Objects.equals(this.thursdayClose, that.thursdayClose) &&
               java.util.Objects.equals(this.fridayOpen, that.fridayOpen) &&
               java.util.Objects.equals(this.fridayClose, that.fridayClose) &&
               java.util.Objects.equals(this.saturdayOpen, that.saturdayOpen) &&
               java.util.Objects.equals(this.saturdayClose, that.saturdayClose) &&
               java.util.Objects.equals(this.sundayOpen, that.sundayOpen) &&
               java.util.Objects.equals(this.sundayClose, that.sundayClose);
    }
}