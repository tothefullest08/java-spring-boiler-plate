package harry.boilerplate.shop.application.command.handler;

import harry.boilerplate.shop.application.command.dto.AddOptionGroupCommand;
import harry.boilerplate.shop.domain.Menu;
import harry.boilerplate.shop.domain.MenuId;
import harry.boilerplate.shop.domain.MenuRepository;
import harry.boilerplate.shop.domain.OptionGroup;
import harry.boilerplate.shop.domain.OptionGroupId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * 옵션 그룹 추가 Command Handler
 * Requirements: 3.1 - 동일한 이름의 옵션그룹이 존재하지 않을 때만 추가
 */
@Component
@Transactional
public class AddOptionGroupCommandHandler {
    
    private final MenuRepository menuRepository;
    
    public AddOptionGroupCommandHandler(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }
    
    /**
     * 옵션 그룹 추가 처리
     * @param command 옵션 그룹 추가 명령
     * @return 생성된 옵션 그룹 ID
     */
    public String handle(AddOptionGroupCommand command) {
        // 메뉴 조회
        MenuId menuId = new MenuId(command.getMenuId());
        Menu menu = menuRepository.findById(menuId);
        
        // 옵션 그룹 생성 (빈 옵션 리스트로 시작)
        OptionGroup optionGroup = new OptionGroup(
            new OptionGroupId(),
            command.getName(),
            command.getIsRequired(),
            new ArrayList<>()
        );
        
        // 메뉴에 옵션 그룹 추가 (도메인 로직에서 중복 검증)
        menu.addOptionGroup(optionGroup);
        
        // 저장
        menuRepository.save(menu);
        
        return optionGroup.getId().getValue();
    }
}