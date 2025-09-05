package harry.boilerplate.user.query.infrastructure.dao;

import harry.boilerplate.user.query.application.readmodel.UserDetailReadModel;
import harry.boilerplate.user.query.application.readmodel.UserSummaryReadModel;

import java.util.List;
import java.util.Optional;

/**
 * User Query DAO 인터페이스
 * Query 측면의 읽기 최적화된 데이터 접근 계층
 * Requirements 7.4: UserQueryDao 구현
 */
public interface UserQueryDao {
    
    /**
     * 사용자 ID로 상세 정보 조회
     * 
     * @param userId 사용자 ID
     * @return 사용자 상세 정보 (존재하지 않으면 Optional.empty())
     */
    Optional<UserDetailReadModel> findUserDetailById(String userId);
    
    /**
     * 사용자 ID로 상세 정보 조회 (Query Handler 용)
     * 
     * @param userId 사용자 ID
     * @return 사용자 상세 정보 (존재하지 않으면 null)
     */
    UserDetailReadModel findUserDetail(String userId);
    
    /**
     * 이메일로 사용자 요약 정보 조회
     * 
     * @param email 이메일
     * @return 사용자 요약 정보 (존재하지 않으면 Optional.empty())
     */
    Optional<UserSummaryReadModel> findUserSummaryByEmail(String email);
    
    /**
     * 모든 사용자 요약 정보 조회
     * 
     * @return 사용자 요약 정보 목록
     */
    List<UserSummaryReadModel> findAllUserSummaries();
    
    /**
     * 사용자 이름으로 검색
     * 
     * @param namePattern 이름 패턴 (LIKE 검색)
     * @return 검색된 사용자 요약 정보 목록
     */
    List<UserSummaryReadModel> findUserSummariesByNamePattern(String namePattern);
    
    /**
     * 사용자 존재 여부 확인
     * 
     * @param userId 사용자 ID
     * @return 존재 여부
     */
    boolean existsById(String userId);
    
    /**
     * 이메일 중복 확인
     * 
     * @param email 이메일
     * @return 중복 여부
     */
    boolean existsByEmail(String email);
    
    /**
     * 전체 사용자 수 조회
     * 
     * @return 사용자 수
     */
    long countAllUsers();
}