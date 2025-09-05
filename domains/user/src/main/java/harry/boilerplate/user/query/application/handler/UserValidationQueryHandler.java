package harry.boilerplate.user.query.application.handler;

import harry.boilerplate.user.query.application.dto.UserValidationQuery;
import harry.boilerplate.user.query.application.dto.UserValidationResult;
import harry.boilerplate.user.command.domain.aggregate.User;
import harry.boilerplate.user.command.domain.aggregate.UserRepository;
import harry.boilerplate.user.command.domain.valueObject.UserId;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 유효성 검증 Query Handler
 * Requirements 7.2: 사용자 유효성 검증 기능 구현
 */
@Component
@Transactional(readOnly = true)
public class UserValidationQueryHandler {
    
    private final UserRepository userRepository;
    
    public UserValidationQueryHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * 사용자 유효성 검증 처리
     * Requirements 7.2: 유효한 사용자 ID가 제공될 때 유효한 UserId를 반환
     * 
     * @param query 사용자 유효성 검증 쿼리
     * @return 사용자 유효성 검증 결과
     */
    public UserValidationResult handle(UserValidationQuery query) {
        try {
            UserId userId = UserId.of(query.getUserId());
            User user = userRepository.find(userId);
            
            if (user == null) {
                return UserValidationResult.invalid(query.getUserId(), "사용자를 찾을 수 없습니다");
            }
            
            if (!user.isValid()) {
                return UserValidationResult.invalid(query.getUserId(), "유효하지 않은 사용자 정보입니다");
            }
            
            return UserValidationResult.valid(query.getUserId());
            
        } catch (Exception e) {
            return UserValidationResult.invalid(query.getUserId(), "잘못된 사용자 ID 형식입니다");
        }
    }
}