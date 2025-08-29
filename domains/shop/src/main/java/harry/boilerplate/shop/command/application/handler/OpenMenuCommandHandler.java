package harry.boilerplate.shop.command.application.handler;

import harry.boilerplate.shop.command.application.dto.OpenMenuCommand;
import harry.boilerplate.shop.command.domain.aggregate.Menu;
import harry.boilerplate.shop.command.domain.aggregate.MenuRepository;
import harry.boilerplate.shop.command.domain.valueObject.MenuId;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 메뉴 공개 Command Handler
 * Requirements: 2.3 - 메뉴 공개 조건을 검증하여 공개 상태로 변경
 */
@Component
@Transactional
public class OpenMenuCommandHandler {
    
    private final MenuRepository menuRepository;
    
    public OpenMenuCommandHandler(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }
    
    /**
     * 메뉴 공개 처리
     * @param command 메뉴 공개 명령
     */
    public void handle(OpenMenuCommand command) {
        // 메뉴 조회
        MenuId menuId = new MenuId(command.getMenuId());
        Menu menu = menuRepository.findById(menuId);
        
        // 메뉴 공개 (도메인 로직에서 조건 검증)
        menu.open();
        
        // 저장
        menuRepository.save(menu);
    }
}