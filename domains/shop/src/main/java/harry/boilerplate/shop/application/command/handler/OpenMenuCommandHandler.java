package harry.boilerplate.shop.application.command.handler;

import harry.boilerplate.shop.application.command.dto.OpenMenuCommand;
import harry.boilerplate.shop.domain.Menu;
import harry.boilerplate.shop.domain.MenuId;
import harry.boilerplate.shop.domain.MenuRepository;
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