package harry.boilerplate.order.domain.valueObject;

import harry.boilerplate.common.domain.entity.EntityId;

import java.util.UUID;

/**
 * Order 애그리게이트의 식별자
 */
public class OrderId extends EntityId {
    
    public OrderId(String value) {
        super(value);
    }
    
    public static OrderId generate() {
        return new OrderId(UUID.randomUUID().toString());
    }
    
    public static OrderId of(String value) {
        return new OrderId(value);
    }
}