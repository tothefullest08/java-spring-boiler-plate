package harry.boilerplate.order.query.application.dto;

/**
 * 장바구니 요약 조회 Query
 * Requirements: 5.5
 */
public class CartSummaryQuery {
    
    private final String userId;
    
    public CartSummaryQuery(String userId) {
        this.userId = userId;
    }
    
    public String getUserId() {
        return userId;
    }
}