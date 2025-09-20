package harry.boilerplate.order.query.infrastructure.dao;

import harry.boilerplate.order.query.application.readModel.OrderHistoryReadModel;
import harry.boilerplate.order.query.application.readModel.OrderSummaryReadModel;
import harry.boilerplate.order.command.domain.valueObject.OrderId;
import harry.boilerplate.order.command.domain.valueObject.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 주문 조회 최적화를 위한 Query DAO 인터페이스
 */
public interface OrderQueryDao {
    
    /**
     * 사용자별 주문 이력 조회 (페이징)
     */
    List<OrderHistoryReadModel> findOrderHistoryByUserId(UserId userId, int page, int size);
    
    /**
     * 주문 ID로 상세 정보 조회
     */
    Optional<OrderHistoryReadModel> findOrderHistoryById(OrderId orderId);
    
    /**
     * 사용자별 주문 요약 정보 조회 (최근 주문들)
     */
    List<OrderSummaryReadModel> findRecentOrdersByUserId(UserId userId, int limit);
    
    /**
     * 특정 기간 내 사용자 주문 조회
     */
    List<OrderHistoryReadModel> findOrderHistoryByUserIdAndDateRange(
        UserId userId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 사용자의 총 주문 개수 조회
     */
    long countOrdersByUserId(UserId userId);
    
    /**
     * 사용자의 특정 가게 주문 이력 조회
     */
    List<OrderHistoryReadModel> findOrderHistoryByUserIdAndShopId(UserId userId, String shopId);
}