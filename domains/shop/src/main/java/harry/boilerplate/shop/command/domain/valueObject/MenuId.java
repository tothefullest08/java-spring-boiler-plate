package harry.boilerplate.shop.command.domain.valueObject;

import harry.boilerplate.common.domain.entity.EntityId;

import java.util.UUID;

/**
 * Menu 애그리게이트의 식별자
 */
public class MenuId extends EntityId {
    
    public MenuId(String value) {
        super(value);
    }
    
    public static MenuId generate() {
        return new MenuId(UUID.randomUUID().toString());
    }
    
    public static MenuId of(String value) {
        return new MenuId(value);
    }
}