package harry.boilerplate.user.command.domain.valueObject;

import harry.boilerplate.common.domain.entity.EntityId;

import java.util.UUID;

/**
 * 사용자 ID 값 객체
 * 사용자를 고유하게 식별하는 불변 값 객체
 */
public class UserId extends EntityId {
    
    public UserId() {
        super(UUID.randomUUID().toString());
    }
    
    public UserId(String value) {
        super(value);
    }
    
    public static UserId generate() {
        return new UserId();
    }
    
    public static UserId of(String value) {
        return new UserId(value);
    }
}