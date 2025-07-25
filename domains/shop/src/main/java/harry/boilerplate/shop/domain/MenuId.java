package harry.boilerplate.shop.domain;

import harry.boilerplate.common.domain.entity.EntityId;

/**
 * Menu 애그리게이트의 고유 식별자
 */
public class MenuId extends EntityId {
    
    public MenuId() {
        super();
    }
    
    public MenuId(String value) {
        super(value);
    }
}