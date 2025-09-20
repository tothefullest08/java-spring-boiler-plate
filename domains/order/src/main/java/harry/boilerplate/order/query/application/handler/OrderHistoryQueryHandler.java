package harry.boilerplate.order.query.application.handler;

import harry.boilerplate.order.query.application.dto.OrderHistoryQuery;
import harry.boilerplate.order.query.application.dto.OrderHistoryResult;
import harry.boilerplate.order.query.application.readModel.OrderHistoryReadModel;
import harry.boilerplate.order.command.domain.valueObject.UserId;
import harry.boilerplate.order.query.infrastructure.dao.OrderQueryDao;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 주문 이력 조회 Query Handler
 * Requirements: 6.3
 */
@Component
@Transactional(readOnly = true)
public class OrderHistoryQueryHandler {
    
    private final OrderQueryDao orderQueryDao;
    
    public OrderHistoryQueryHandler(OrderQueryDao orderQueryDao) {
        this.orderQueryDao = orderQueryDao;
    }
    
    /**
     * 사용자의 주문 이력 조회 (페이징 지원)
     * 주문이 없는 경우 빈 결과 반환
     */
    public OrderHistoryResult handle(OrderHistoryQuery query) {
        // 입력 검증
        validateQuery(query);
        
        // 사용자 ID로 주문 이력 조회
        UserId userId = UserId.of(query.getUserId());
        
        // 페이징된 주문 이력 조회
        List<OrderHistoryReadModel> orders = orderQueryDao.findOrderHistoryByUserId(
            userId, query.getPage(), query.getSize());
        
        // 총 주문 개수 조회 (페이징 정보 계산용)
        long totalCount = orderQueryDao.countOrdersByUserId(userId);
        
        if (orders.isEmpty()) {
            return OrderHistoryResult.empty(query.getPage(), query.getSize());
        } else {
            return OrderHistoryResult.from(orders, (int) totalCount, query.getPage(), query.getSize());
        }
    }
    
    /**
     * Query 입력 검증
     */
    private void validateQuery(OrderHistoryQuery query) {
        if (query.getUserId() == null || query.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다");
        }
        if (query.getPage() < 0) {
            throw new IllegalArgumentException("페이지 번호는 0 이상이어야 합니다");
        }
        if (query.getSize() <= 0 || query.getSize() > 100) {
            throw new IllegalArgumentException("페이지 크기는 1~100 범위여야 합니다");
        }
    }
}