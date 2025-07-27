package harry.boilerplate.shop.application.command.handler;

import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.shop.application.command.dto.UpdateShopCommand;
import harry.boilerplate.shop.domain.Shop;
import harry.boilerplate.shop.domain.ShopId;
import harry.boilerplate.shop.domain.ShopRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 가게 정보 수정 Command Handler
 * Requirements: 1.1 - 가게 운영자가 가게 정보를 수정할 수 있어야 함
 */
@Component
@Transactional
public class UpdateShopCommandHandler {
    
    private final ShopRepository shopRepository;
    
    public UpdateShopCommandHandler(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }
    
    /**
     * 가게 정보 수정 처리
     * @param command 가게 수정 명령
     */
    public void handle(UpdateShopCommand command) {
        // 가게 조회
        ShopId shopId = new ShopId(command.getShopId());
        Shop shop = shopRepository.findById(shopId);
        
        // 최소 주문금액 변경
        Money newMinOrderAmount = Money.of(command.getMinOrderAmount());
        shop.changeMinOrderAmount(newMinOrderAmount);
        
        // 저장
        shopRepository.save(shop);
    }
}