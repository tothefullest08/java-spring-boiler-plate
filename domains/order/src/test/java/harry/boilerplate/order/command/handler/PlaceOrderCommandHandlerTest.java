package harry.boilerplate.order.command.handler;

import harry.boilerplate.order.command.application.dto.PlaceOrderCommand;
import harry.boilerplate.order.command.application.handler.PlaceOrderCommandHandler;
import harry.boilerplate.order.command.domain.aggregate.Cart;
import harry.boilerplate.order.command.domain.aggregate.CartRepository;
import harry.boilerplate.order.command.domain.aggregate.Order;
import harry.boilerplate.order.command.domain.aggregate.OrderRepository;
import harry.boilerplate.order.command.domain.exception.CartDomainException;
import harry.boilerplate.order.command.domain.exception.CartErrorCode;
import harry.boilerplate.order.command.domain.exception.OrderDomainException;
import harry.boilerplate.order.command.domain.exception.OrderErrorCode;
import harry.boilerplate.order.command.domain.valueObject.MenuId;
import harry.boilerplate.order.command.domain.valueObject.OptionId;
import harry.boilerplate.order.command.domain.valueObject.ShopId;
import harry.boilerplate.order.command.domain.valueObject.UserId;
import harry.boilerplate.order.command.infrastructure.external.shop.ShopApiClient;
import harry.boilerplate.order.command.infrastructure.external.user.UserApiClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlaceOrderCommandHandler 테스트")
class PlaceOrderCommandHandlerTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserApiClient userApiClient;
    
    @Mock
    private ShopApiClient shopApiClient;

    @InjectMocks
    private PlaceOrderCommandHandler placeOrderCommandHandler;

    private Cart cart;
    private PlaceOrderCommand command;

    @BeforeEach
    void setUp() {
        UserId userId = UserId.of("user-1");
        ShopId shopId = ShopId.of("shop-1");
        cart = new Cart(userId);
        cart.addItem(shopId, MenuId.of("menu-1"), Arrays.asList(OptionId.of("option-1")), 2);
        
        command = new PlaceOrderCommand("user-1");
    }

    @Test
    @DisplayName("주문 생성 성공")
    void 주문_생성_성공() {
        // Given
        when(userApiClient.isValidUser(anyString())).thenReturn(true);
        when(shopApiClient.isShopOpen(anyString())).thenReturn(true);
        when(cartRepository.findByUserIdOptional(any(UserId.class))).thenReturn(Optional.of(cart));

        // When
        String orderId = placeOrderCommandHandler.handle(command);

        // Then
        assertThat(orderId).isNotNull();
        
        verify(userApiClient).isValidUser("user-1");
        verify(cartRepository).findByUserIdOptional(any(UserId.class));
        verify(orderRepository).save(any(Order.class));
        verify(cartRepository).save(cart);
        
        // 주문 후 장바구니가 비워졌는지 확인
        assertThat(cart.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("유효하지 않은 사용자로 주문 생성 시 예외 발생")
    void 유효하지_않은_사용자로_주문_생성_시_예외_발생() {
        // Given
        when(userApiClient.isValidUser(anyString())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> placeOrderCommandHandler.handle(command))
                .isInstanceOf(OrderDomainException.class)
                .extracting(e -> ((OrderDomainException) e).getErrorCode())
                .isEqualTo(OrderErrorCode.INVALID_USER_ID);

        verify(userApiClient).isValidUser("user-1");
        verify(cartRepository, never()).findByUserIdOptional(any(UserId.class));
        verify(orderRepository, never()).save(any(Order.class));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("장바구니가 존재하지 않을 때 주문 생성 시 예외 발생")
    void 장바구니가_존재하지_않을_때_주문_생성_시_예외_발생() {
        // Given
        when(userApiClient.isValidUser(anyString())).thenReturn(true);
        when(cartRepository.findByUserIdOptional(any(UserId.class))).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> placeOrderCommandHandler.handle(command))
                .isInstanceOf(CartDomainException.class)
                .extracting(e -> ((CartDomainException) e).getErrorCode())
                .isEqualTo(CartErrorCode.CART_NOT_FOUND);

        verify(userApiClient).isValidUser("user-1");
        verify(cartRepository).findByUserIdOptional(any(UserId.class));
        verify(orderRepository, never()).save(any(Order.class));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("빈 장바구니로 주문 생성 시 예외 발생")
    void 빈_장바구니로_주문_생성_시_예외_발생() {
        // Given
        UserId userId = UserId.of("user-1");
        Cart emptyCart = new Cart(userId);
        
        when(userApiClient.isValidUser(anyString())).thenReturn(true);
        when(cartRepository.findByUserIdOptional(any(UserId.class))).thenReturn(Optional.of(emptyCart));

        // When & Then
        assertThatThrownBy(() -> placeOrderCommandHandler.handle(command))
                .isInstanceOf(CartDomainException.class)
                .extracting(e -> ((CartDomainException) e).getErrorCode())
                .isEqualTo(CartErrorCode.EMPTY_CART);

        verify(userApiClient).isValidUser("user-1");
        verify(cartRepository).findByUserIdOptional(any(UserId.class));
        verify(orderRepository, never()).save(any(Order.class));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("null 명령어로 주문 생성 시 예외 발생")
    void null_명령어로_주문_생성_시_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> placeOrderCommandHandler.handle(null))
                .isInstanceOf(NullPointerException.class);

        verify(userApiClient, never()).isValidUser(anyString());
        verify(cartRepository, never()).findByUserIdOptional(any(UserId.class));
        verify(orderRepository, never()).save(any(Order.class));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("외부 API 호출 실패 시 예외 전파")
    void 외부_API_호출_실패_시_예외_전파() {
        // Given
        when(userApiClient.isValidUser(anyString())).thenThrow(new RuntimeException("사용자 API 오류"));

        // When & Then
        assertThatThrownBy(() -> placeOrderCommandHandler.handle(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("사용자 API 오류");

        verify(userApiClient).isValidUser("user-1");
        verify(cartRepository, never()).findByUserIdOptional(any(UserId.class));
        verify(orderRepository, never()).save(any(Order.class));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("주문 저장 실패 시 예외 전파")
    void 주문_저장_실패_시_예외_전파() {
        // Given
        when(userApiClient.isValidUser(anyString())).thenReturn(true);
        when(cartRepository.findByUserIdOptional(any(UserId.class))).thenReturn(Optional.of(cart));
        when(shopApiClient.isShopOpen(anyString())).thenReturn(true);
        doThrow(new RuntimeException("데이터베이스 오류")).when(orderRepository).save(any(Order.class));

        // When & Then
        assertThatThrownBy(() -> placeOrderCommandHandler.handle(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("데이터베이스 오류");

        verify(userApiClient).isValidUser("user-1");
        verify(cartRepository).findByUserIdOptional(any(UserId.class));
        verify(orderRepository).save(any(Order.class));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("장바구니 저장 실패 시 예외 전파")
    void 장바구니_저장_실패_시_예외_전파() {
        // Given
        when(userApiClient.isValidUser(anyString())).thenReturn(true);
        when(cartRepository.findByUserIdOptional(any(UserId.class))).thenReturn(Optional.of(cart));
        when(shopApiClient.isShopOpen(anyString())).thenReturn(true);
        doThrow(new RuntimeException("장바구니 저장 오류")).when(cartRepository).save(any(Cart.class));

        // When & Then
        assertThatThrownBy(() -> placeOrderCommandHandler.handle(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("장바구니 저장 오류");

        verify(userApiClient).isValidUser("user-1");
        verify(cartRepository).findByUserIdOptional(any(UserId.class));
        verify(orderRepository).save(any(Order.class));
        verify(cartRepository).save(cart);
    }

    @Test
    @DisplayName("주문 생성 후 도메인 이벤트 발행 확인")
    void 주문_생성_후_도메인_이벤트_발행_확인() {
        // Given
        when(userApiClient.isValidUser(anyString())).thenReturn(true);
        when(cartRepository.findByUserIdOptional(any(UserId.class))).thenReturn(Optional.of(cart));
        when(shopApiClient.isShopOpen(anyString())).thenReturn(true);

        // When
        String orderId = placeOrderCommandHandler.handle(command);

        // Then
        assertThat(orderId).isNotNull();
        
        verify(orderRepository).save(argThat(order -> {
            assertThat(order.hasDomainEvents()).isTrue();
            assertThat(order.getDomainEvents()).hasSize(1);
            return true;
        }));
    }

    @Test
    @DisplayName("주문 생성 시 장바구니 정보가 주문으로 정확히 복사됨")
    void 주문_생성_시_장바구니_정보가_주문으로_정확히_복사됨() {
        // Given
        when(userApiClient.isValidUser(anyString())).thenReturn(true);
        when(cartRepository.findByUserIdOptional(any(UserId.class))).thenReturn(Optional.of(cart));
        when(shopApiClient.isShopOpen(anyString())).thenReturn(true);

        // When
        placeOrderCommandHandler.handle(command);

        // Then
        verify(orderRepository).save(argThat(order -> {
            assertThat(order.getUserId()).isEqualTo(cart.getUserId());
            assertThat(order.getShopId()).isEqualTo(ShopId.of("shop-1")); // cart.getShopId() 대신 직접 값 사용
            assertThat(order.getOrderItems()).hasSize(1); // cart에서 생성된 OrderLineItem 1개
            assertThat(order.getTotalQuantity()).isEqualTo(2); // setUp에서 설정한 quantity
            return true;
        }));
    }
}