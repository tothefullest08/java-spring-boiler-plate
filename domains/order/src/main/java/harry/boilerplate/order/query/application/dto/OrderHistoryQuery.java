package harry.boilerplate.order.query.application.dto;

/**
 * 주문 이력 조회 Query
 * Requirements: 6.3
 */
public class OrderHistoryQuery {
    
    private final String userId;
    private final int page;
    private final int size;
    
    public OrderHistoryQuery(String userId, int page, int size) {
        this.userId = userId;
        this.page = Math.max(0, page); // 최소 0
        this.size = Math.min(Math.max(1, size), 100); // 1~100 범위
    }
    
    public OrderHistoryQuery(String userId) {
        this(userId, 0, 20); // 기본값: 첫 페이지, 20개
    }
    
    public String getUserId() {
        return userId;
    }
    
    public int getPage() {
        return page;
    }
    
    public int getSize() {
        return size;
    }
    
    public int getOffset() {
        return page * size;
    }
}