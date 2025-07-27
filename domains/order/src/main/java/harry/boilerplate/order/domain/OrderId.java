package harry.boilerplate.order.domain;

import harry.boilerplate.common.domain.entity.EntityId;

import java.util.UUID;

/**
 * Order 애그리게이트의 식별자 ValueObject
 */
public class OrderId extends EntityId {
    
    public OrderId() {
        super(UUID.randomUUID().toString());
    }
    
    public OrderId(String value) {
        super(value);
    }
    
    public static OrderId of(String value) {
        return new OrderId(value);
    }
    
    public static OrderId generate() {
        return new OrderId();
    }
}