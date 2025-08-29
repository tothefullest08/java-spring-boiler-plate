package harry.boilerplate.shop.command.application.handler;

import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.shop.command.application.dto.CreateShopCommand;
import harry.boilerplate.shop.command.domain.aggregate.Shop;
import harry.boilerplate.shop.command.domain.aggregate.ShopRepository;
import harry.boilerplate.shop.command.domain.valueObject.BusinessHours;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.Map;

/**
 * 가게 생성 Command Handler
 * Requirements: 1.1 - 가게 운영자가 가게를 생성할 수 있어야 함
 */
@Component
@Transactional
public class CreateShopCommandHandler {

    private final ShopRepository shopRepository;

    public CreateShopCommandHandler(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    /**
     * 가게 생성 처리
     * 
     * @param command 가게 생성 명령
     * @return 생성된 가게 ID
     */
    public String handle(CreateShopCommand command) {
        // Money 객체 생성
        Money minOrderAmount = Money.of(command.getMinOrderAmount());

        // 기본 영업시간 설정 (09:00 ~ 22:00, 모든 요일 동일)
        Map<DayOfWeek, LocalTime[]> weeklyHours = new EnumMap<>(DayOfWeek.class);
        LocalTime openTime = LocalTime.of(9, 0);
        LocalTime closeTime = LocalTime.of(22, 0);
        
        for (DayOfWeek day : DayOfWeek.values()) {
            weeklyHours.put(day, new LocalTime[]{openTime, closeTime});
        }
        
        BusinessHours defaultBusinessHours = new BusinessHours(weeklyHours);

        // Shop 애그리게이트 생성
        Shop shop = new Shop(
                command.getName(),
                minOrderAmount,
                defaultBusinessHours);

        // 저장
        shopRepository.save(shop);

        return shop.getId().getValue();
    }
}