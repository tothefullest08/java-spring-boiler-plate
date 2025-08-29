package harry.boilerplate.user.application.command.handler;

import harry.boilerplate.user.application.command.dto.CreateUserCommand;
import harry.boilerplate.user.domain.aggregate.User;
import harry.boilerplate.user.domain.aggregate.UserRepository;
import harry.boilerplate.user.domain.exception.UserDomainException;
import harry.boilerplate.user.domain.exception.UserErrorCode;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 생성 Command Handler
 * Requirements 7.1: 사용자 생성 기능 구현
 */
@Component
@Transactional
public class CreateUserCommandHandler {
    
    private final UserRepository userRepository;
    
    public CreateUserCommandHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * 사용자 생성 처리
     * 
     * @param command 사용자 생성 명령
     * @return 생성된 사용자 ID
     */
    public String handle(CreateUserCommand command) {
        // 이메일 중복 검증
        if (userRepository.existsByEmail(command.getEmail())) {
            throw new UserDomainException(UserErrorCode.DUPLICATE_EMAIL, 
                "이미 존재하는 이메일입니다: " + command.getEmail());
        }
        
        // 사용자 생성 (도메인 이벤트 자동 발행)
        User user = User.create(command.getName(), command.getEmail());
        
        // 사용자 저장
        userRepository.save(user);
        
        return user.getId().getValue();
    }
}