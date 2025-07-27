package harry.boilerplate.shop.application.command.handler;

import harry.boilerplate.shop.application.command.dto.ChangeOptionGroupNameCommand;
import harry.boilerplate.shop.domain.Menu;
import harry.boilerplate.shop.domain.MenuId;
import harry.boilerplate.shop.domain.MenuRepository;
import harry.boilerplate.shop.domain.OptionGroupId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 옵션 그룹 이름 변경 Command Handler
 * Requirements: 3.3 - 새로운 이름이 동일 메뉴 내에서 유일할 때만 변경
 */
@Component
@Transactional
public class ChangeOptionGroupNameCommandHandler {
    
    private final MenuRepository menuRepository;
    
    public ChangeOptionGroupNameCommandHandler(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }
    
    /**
     * 옵션 그룹 이름 변경 처리
     * @param command 옵션 그룹 이름 변경 명령
     */
    public void handle(ChangeOptionGroupNameCommand command) {
        // 메뉴 조회
        MenuId menuId = new MenuId(command.getMenuId());
        Menu menu = menuRepository.findById(menuId);
        
        // 옵션 그룹 이름 변경 (도메인 로직에서 중복 검증)
        OptionGroupId optionGroupId = new OptionGroupId(command.getOptionGroupId());
        menu.changeOptionGroupName(optionGroupId, command.getNewName());
        
        // 저장
        menuRepository.save(menu);
    }
}