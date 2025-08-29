package harry.boilerplate.order.application.query.dto;

import harry.boilerplate.order.application.query.readmodel.CartSummaryReadModel;

/**
 * 장바구니 요약 조회 결과
 * Requirements: 5.5
 */
public class CartSummaryResult {
    
    private final CartSummaryReadModel cartSummary;
    
    public CartSummaryResult(CartSummaryReadModel cartSummary) {
        this.cartSummary = cartSummary;
    }
    
    public static CartSummaryResult from(CartSummaryReadModel cartSummary) {
        return new CartSummaryResult(cartSummary);
    }
    
    public static CartSummaryResult empty() {
        return new CartSummaryResult(null);
    }
    
    public CartSummaryReadModel getCartSummary() {
        return cartSummary;
    }
    
    public boolean isEmpty() {
        return cartSummary == null;
    }
}