package harry.boilerplate.order.command.infrastructure.external.user;

/**
 * User Context API Client 인터페이스
 * Requirements: 8.4, 5.6
 */
public interface UserApiClient {

    /**
     * 사용자 유효성 검증
     * @param userId 사용자 ID
     * @return 유효 사용자 여부
     */
    boolean isValidUser(String userId);
}




