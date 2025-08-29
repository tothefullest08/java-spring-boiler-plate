package harry.boilerplate.user.application.command.handler;

import harry.boilerplate.user.application.command.dto.UpdateUserCommand;
import harry.boilerplate.user.domain.aggregate.User;
import harry.boilerplate.user.domain.aggregate.UserRepository;
import harry.boilerplate.user.domain.exception.UserDomainException;
import harry.boilerplate.user.domain.exception.UserErrorCode;
import harry.boilerplate.user.domain.valueObject.UserId;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 정보 수정 Command Handler
 * Requirements 7.1: 사용자 정보 수정 기능 구현
 */
@Component
@Transactional
public class UpdateUserCommandHandler {
    
    private final UserRepository userRepository;
    
    public UpdateUserCommandHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * 사용자 정보 수정 처리
     * 
     * @param command 사용자 수정 명령
     */
    public void handle(UpdateUserCommand command) {
        // 사용자 조회
        UserId userId = UserId.of(command.getUserId());
        User user = userRepository.find(userId);
        
        if (user == null) {
            throw new UserDomainException(UserErrorCode.USER_NOT_FOUND, 
                "사용자를 찾을 수 없습니다: " + command.getUserId());
        }
        
        // 이메일 변경 시 중복 검증 (자신의 이메일이 아닌 경우)
        if (!user.getEmail().equals(command.getEmail()) && 
            userRepository.existsByEmail(command.getEmail())) {
            throw new UserDomainException(UserErrorCode.DUPLICATE_EMAIL, 
                "이미 존재하는 이메일입니다: " + command.getEmail());
        }
        
        // 사용자 정보 수정
        user.changeName(command.getName());
        user.changeEmail(command.getEmail());
        
        // 변경사항 저장
        userRepository.save(user);
    }
}