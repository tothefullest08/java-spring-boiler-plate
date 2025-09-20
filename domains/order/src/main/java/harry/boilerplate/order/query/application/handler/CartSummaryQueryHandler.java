package harry.boilerplate.order.query.application.handler;

import harry.boilerplate.order.query.application.dto.CartSummaryQuery;
import harry.boilerplate.order.query.application.dto.CartSummaryResult;
import harry.boilerplate.order.query.application.readModel.CartSummaryReadModel;
import harry.boilerplate.order.command.domain.valueObject.UserId;
import harry.boilerplate.order.query.infrastructure.dao.CartQueryDao;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 장바구니 요약 조회 Query Handler
 * Requirements: 5.5
 */
@Component
@Transactional(readOnly = true)
public class CartSummaryQueryHandler {
    
    private final CartQueryDao cartQueryDao;
    
    public CartSummaryQueryHandler(CartQueryDao cartQueryDao) {
        this.cartQueryDao = cartQueryDao;
    }
    
    /**
     * 사용자의 장바구니 요약 정보 조회
     * 장바구니가 없거나 비어있는 경우 빈 결과 반환
     */
    public CartSummaryResult handle(CartSummaryQuery query) {
        // 입력 검증
        validateQuery(query);
        
        // 사용자 ID로 장바구니 요약 정보 조회
        UserId userId = UserId.of(query.getUserId());
        Optional<CartSummaryReadModel> cartSummary = cartQueryDao.findCartSummaryByUserId(userId);
        
        if (cartSummary.isPresent()) {
            return CartSummaryResult.from(cartSummary.get());
        } else {
            return CartSummaryResult.empty();
        }
    }
    
    /**
     * Query 입력 검증
     */
    private void validateQuery(CartSummaryQuery query) {
        if (query.getUserId() == null || query.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다");
        }
    }
}