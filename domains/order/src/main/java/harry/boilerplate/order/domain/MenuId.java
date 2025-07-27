package harry.boilerplate.order.domain;

import harry.boilerplate.common.domain.entity.EntityId;

import java.util.UUID;

/**
 * Menu 식별자 ValueObject (Order Context에서 사용)
 */
public class MenuId extends EntityId {
    
    public MenuId() {
        super(UUID.randomUUID().toString());
    }
    
    public MenuId(String value) {
        super(value);
    }
    
    public static MenuId of(String value) {
        return new MenuId(value);
    }
    
    public static MenuId generate() {
        return new MenuId();
    }
}