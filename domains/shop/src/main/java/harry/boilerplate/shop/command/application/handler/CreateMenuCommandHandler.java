package harry.boilerplate.shop.command.application.handler;

import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.shop.command.application.dto.CreateMenuCommand;
import harry.boilerplate.shop.command.domain.aggregate.Menu;
import harry.boilerplate.shop.command.domain.aggregate.MenuRepository;
import harry.boilerplate.shop.command.domain.aggregate.ShopRepository;
import harry.boilerplate.shop.command.domain.valueObject.ShopId;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 메뉴 생성 Command Handler
 * Requirements: 2.1 - 가게 운영자가 메뉴를 등록할 수 있어야 함
 */
@Component
@Transactional
public class CreateMenuCommandHandler {
    
    private final MenuRepository menuRepository;
    private final ShopRepository shopRepository;
    
    public CreateMenuCommandHandler(MenuRepository menuRepository, ShopRepository shopRepository) {
        this.menuRepository = menuRepository;
        this.shopRepository = shopRepository;
    }
    
    /**
     * 메뉴 생성 처리
     * @param command 메뉴 생성 명령
     * @return 생성된 메뉴 ID
     */
    public String handle(CreateMenuCommand command) {
        // 가게 존재 확인
        ShopId shopId = new ShopId(command.getShopId());
        if (!shopRepository.existsById(shopId)) {
            throw new IllegalArgumentException("존재하지 않는 가게입니다: " + command.getShopId());
        }
        
        // Money 객체 생성
        Money basePrice = Money.of(command.getBasePrice());
        
        // Menu 애그리게이트 생성
        Menu menu = new Menu(
            shopId,
            command.getName(),
            command.getDescription(),
            basePrice
        );
        
        // 저장
        menuRepository.save(menu);
        
        return menu.getId().getValue();
    }
}