package harry.boilerplate.shop.domain.valueobject;

import harry.boilerplate.common.domain.entity.EntityId;

/**
 * OptionGroup의 고유 식별자
 */
public class OptionGroupId extends EntityId {
    
    public OptionGroupId() {
        super();
    }
    
    public OptionGroupId(String value) {
        super(value);
    }
    
    /**
     * 새로운 OptionGroupId 생성
     */
    public static OptionGroupId generate() {
        return new OptionGroupId();
    }
}