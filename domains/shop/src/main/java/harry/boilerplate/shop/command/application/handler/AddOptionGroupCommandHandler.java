package harry.boilerplate.shop.command.application.handler;

import harry.boilerplate.shop.command.application.dto.AddOptionGroupCommand;
import harry.boilerplate.shop.command.domain.aggregate.Menu;
import harry.boilerplate.shop.command.domain.aggregate.MenuRepository;
import harry.boilerplate.shop.command.domain.valueObject.MenuId;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        
        // 메뉴에 옵션 그룹 추가 (도메인 로직에서 중복 검증 및 OptionGroup 생성)
        menu.addOptionGroup(command.getName(), command.getIsRequired());
        
        // 저장
        menuRepository.save(menu);
        
        // 추가된 옵션그룹의 ID를 반환하기 위해 마지막 추가된 옵션그룹을 찾음
        return menu.getOptionGroups().stream()
            .filter(og -> og.getName().equals(command.getName()))
            .findFirst()
            .map(og -> og.getId().getValue())
            .orElseThrow(() -> new RuntimeException("옵션그룹 추가 실패"));
    }
}