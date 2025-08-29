package harry.boilerplate.order.application.command.handler;

import harry.boilerplate.order.application.command.dto.PlaceOrderCommand;
import harry.boilerplate.order.domain.aggregate.Cart;
import harry.boilerplate.order.domain.aggregate.CartRepository;
import harry.boilerplate.order.domain.aggregate.Order;
import harry.boilerplate.order.domain.aggregate.OrderRepository;
import harry.boilerplate.order.domain.valueObject.UserId;
import harry.boilerplate.order.domain.exception.CartDomainException;
import harry.boilerplate.order.domain.exception.CartErrorCode;
import harry.boilerplate.order.domain.exception.OrderDomainException;
import harry.boilerplate.order.domain.exception.OrderErrorCode;
import harry.boilerplate.order.infrastructure.external.shop.ShopApiClient;
import harry.boilerplate.order.infrastructure.external.user.UserApiClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 주문 생성 Command Handler
 * Requirements: 6.1, 6.2
 */
@Component
@Transactional
public class PlaceOrderCommandHandler {
    
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ShopApiClient shopApiClient;
    private final UserApiClient userApiClient;
    
    public PlaceOrderCommandHandler(CartRepository cartRepository,
                                  OrderRepository orderRepository,
                                  ShopApiClient shopApiClient,
                                  UserApiClient userApiClient) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.shopApiClient = shopApiClient;
        this.userApiClient = userApiClient;
    }
    
    /**
     * 주문 생성 처리
     * 1. 사용자 유효성 검증
     * 2. 장바구니 조회 및 검증
     * 3. 가게 영업 상태 재확인
     * 4. 최소 주문금액 검증
     * 5. 주문 생성
     * 6. 장바구니 정리
     */
    public String handle(PlaceOrderCommand command) {
        // 입력 검증
        validateCommand(command);
        
        // 1. 사용자 유효성 검증
        if (!userApiClient.isValidUser(command.getUserId())) {
            throw new OrderDomainException(OrderErrorCode.INVALID_USER_ID);
        }
        
        // 2. 장바구니 조회 및 검증
        UserId userId = UserId.of(command.getUserId());
        Cart cart = cartRepository.findByUserIdOptional(userId)
            .orElseThrow(() -> new CartDomainException(CartErrorCode.CART_NOT_FOUND));
        
        if (cart.isEmpty()) {
            throw new CartDomainException(CartErrorCode.EMPTY_CART);
        }
        
        if (cart.getShopId() == null) {
            throw new CartDomainException(CartErrorCode.INVALID_SHOP_ID);
        }
        
        // 3. 가게 영업 상태 재확인 (주문 시점에 다시 확인)
        if (!shopApiClient.isShopOpen(cart.getShopId().getValue())) {
            throw new OrderDomainException(OrderErrorCode.SHOP_NOT_OPEN);
        }
        
        // 4. 최소 주문금액 검증 (Requirements: 6.4)
        // TODO: Shop Context API를 통해 가게의 최소 주문금액을 조회하여 검증
        // 현재는 기본 검증만 수행
        if (cart.getTotalPrice().getAmount().compareTo(java.math.BigDecimal.valueOf(5000)) < 0) {
            throw new OrderDomainException(OrderErrorCode.MINIMUM_ORDER_AMOUNT_NOT_MET);
        }
        
        // 5. 주문 생성 (Requirements: 6.1, 6.2)
        Order order = cart.placeOrder(); // Cart.placeOrder()는 내부적으로 Order.fromCart() 호출
        
        // 6. 주문 저장
        orderRepository.save(order);
        
        // 7. 장바구니 업데이트 (placeOrder()에서 이미 clear() 호출됨)
        cartRepository.save(cart);
        
        return order.getId().getValue();
    }
    
    /**
     * Command 입력 검증
     */
    private void validateCommand(PlaceOrderCommand command) {
        if (command.getUserId() == null || command.getUserId().trim().isEmpty()) {
            throw new OrderDomainException(OrderErrorCode.INVALID_USER_ID);
        }
    }
}