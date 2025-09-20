package harry.boilerplate.order.query.application.dto;

import harry.boilerplate.order.query.application.readModel.OrderHistoryReadModel;

import java.util.List;

/**
 * 주문 이력 조회 결과
 * Requirements: 6.3
 */
public class OrderHistoryResult {
    
    private final List<OrderHistoryReadModel> orders;
    private final int totalCount;
    private final int page;
    private final int size;
    private final boolean hasNext;
    
    public OrderHistoryResult(List<OrderHistoryReadModel> orders, int totalCount, int page, int size) {
        this.orders = orders != null ? List.copyOf(orders) : List.of();
        this.totalCount = totalCount;
        this.page = page;
        this.size = size;
        this.hasNext = (page + 1) * size < totalCount;
    }
    
    public static OrderHistoryResult from(List<OrderHistoryReadModel> orders, int totalCount, int page, int size) {
        return new OrderHistoryResult(orders, totalCount, page, size);
    }
    
    public static OrderHistoryResult empty(int page, int size) {
        return new OrderHistoryResult(List.of(), 0, page, size);
    }
    
    public List<OrderHistoryReadModel> getOrders() {
        return orders;
    }
    
    public int getTotalCount() {
        return totalCount;
    }
    
    public int getPage() {
        return page;
    }
    
    public int getSize() {
        return size;
    }
    
    public boolean hasNext() {
        return hasNext;
    }
    
    public boolean isEmpty() {
        return orders.isEmpty();
    }
}