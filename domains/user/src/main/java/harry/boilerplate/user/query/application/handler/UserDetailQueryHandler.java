package harry.boilerplate.user.query.application.handler;

import harry.boilerplate.user.query.application.dto.UserDetailQuery;
import harry.boilerplate.user.query.application.dto.UserDetailResult;
import harry.boilerplate.user.query.application.readmodel.UserDetailReadModel;
import harry.boilerplate.user.query.infrastructure.dao.UserQueryDao;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 상세 정보 조회 Query Handler
 * Requirements 7.3: 사용자 상세 정보 조회 기능 구현
 */
@Component
@Transactional(readOnly = true)
public class UserDetailQueryHandler {
    
    private final UserQueryDao userQueryDao;
    
    public UserDetailQueryHandler(UserQueryDao userQueryDao) {
        this.userQueryDao = userQueryDao;
    }
    
    /**
     * 사용자 상세 정보 조회 처리
     * 
     * @param query 사용자 상세 조회 쿼리
     * @return 사용자 상세 정보 결과
     */
    public UserDetailResult handle(UserDetailQuery query) {
        UserDetailReadModel readModel = userQueryDao.findUserDetail(query.getUserId());
        
        if (readModel == null) {
            return null; // 사용자가 존재하지 않는 경우
        }
        
        return UserDetailResult.from(readModel);
    }
}