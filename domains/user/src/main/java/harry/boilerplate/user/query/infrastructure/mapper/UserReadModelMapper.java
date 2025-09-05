package harry.boilerplate.user.query.infrastructure.mapper;

import harry.boilerplate.user.query.application.readmodel.UserDetailReadModel;
import harry.boilerplate.user.query.application.readmodel.UserSummaryReadModel;
import harry.boilerplate.user.command.domain.aggregate.User;
import org.springframework.stereotype.Component;

/**
 * User Entity를 Read Model로 변환하는 매퍼
 * Entity → ReadModel 변환 로직을 담당
 * Requirements 7.4: UserQueryDao 구현
 */
@Component
public class UserReadModelMapper {
    
    /**
     * User 엔티티를 UserSummaryReadModel로 변환
     * 
     * @param user User 엔티티
     * @return UserSummaryReadModel
     */
    public UserSummaryReadModel toSummaryReadModel(User user) {
        if (user == null) {
            return null;
        }
        
        return new UserSummaryReadModel(
            user.getId().getValue(),
            user.getName(),
            user.getEmail(),
            user.getCreatedAt().toString()
        );
    }
    
    /**
     * User 엔티티를 UserDetailReadModel로 변환
     * 
     * @param user User 엔티티
     * @return UserDetailReadModel
     */
    public UserDetailReadModel toDetailReadModel(User user) {
        if (user == null) {
            return null;
        }
        
        return new UserDetailReadModel(
            user.getId().getValue(),
            user.getName(),
            user.getEmail(),
            user.isValid(),
            user.getCreatedAt().toString(),
            user.getUpdatedAt().toString()
        );
    }
}