package harry.boilerplate.order.domain.valueObject;

import harry.boilerplate.common.domain.entity.EntityId;

import java.util.UUID;

/**
 * Menu의 식별자 (Order Context에서 사용)
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