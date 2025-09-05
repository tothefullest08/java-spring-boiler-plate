package harry.boilerplate.order.command.application.dto;

/**
 * 주문 생성 Command
 * Requirements: 6.1, 6.2
 */
public class PlaceOrderCommand {
    
    private final String userId;
    
    public PlaceOrderCommand(String userId) {
        this.userId = userId;
    }
    
    public String getUserId() {
        return userId;
    }
}