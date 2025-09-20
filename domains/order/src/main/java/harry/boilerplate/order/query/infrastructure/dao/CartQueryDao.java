package harry.boilerplate.order.query.infrastructure.dao;

import harry.boilerplate.order.query.application.readModel.CartSummaryReadModel;
import harry.boilerplate.order.command.domain.valueObject.CartId;
import harry.boilerplate.order.command.domain.valueObject.UserId;

import java.util.Optional;

/**
 * 장바구니 조회 최적화를 위한 Query DAO 인터페이스
 */
public interface CartQueryDao {
    
    /**
     * 사용자별 장바구니 요약 정보 조회
     */
    Optional<CartSummaryReadModel> findCartSummaryByUserId(UserId userId);
    
    /**
     * 장바구니 ID로 요약 정보 조회
     */
    Optional<CartSummaryReadModel> findCartSummaryById(CartId cartId);
    
    /**
     * 사용자의 장바구니 존재 여부 확인
     */
    boolean existsCartByUserId(UserId userId);
    
    /**
     * 사용자의 장바구니 아이템 개수 조회
     */
    int getCartItemCountByUserId(UserId userId);
}