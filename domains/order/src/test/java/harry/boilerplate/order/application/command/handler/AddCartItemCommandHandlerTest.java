package harry.boilerplate.order.command.application.handler;

import harry.boilerplate.order.command.application.dto.AddCartItemCommand;
import harry.boilerplate.order.command.domain.aggregate.Cart;
import harry.boilerplate.order.command.domain.aggregate.CartRepository;
import harry.boilerplate.order.command.domain.exception.CartDomainException;
import harry.boilerplate.order.command.domain.exception.CartErrorCode;
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
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddCartItemCommandHandler 테스트")
class AddCartItemCommandHandlerTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ShopApiClient shopApiClient;

    @Mock
    private UserApiClient userApiClient;

    @InjectMocks
    private AddCartItemCommandHandler addCartItemCommandHandler;

    private Cart cart;
    private AddCartItemCommand command;

    @BeforeEach
    void setUp() {
        UserId userId = UserId.of("user-1");
        cart = new Cart(userId);
        
        List<String> selectedOptionIds = Arrays.asList("option-1", "option-2");
        command = new AddCartItemCommand(
            "user-1", "shop-1", "menu-1", selectedOptionIds, 2
        );
    }

    @Test
    @DisplayName("장바구니 아이템 추가 성공 - 새 장바구니")
    void 장바구니_아이템_추가_성공_새_장바구니() {
        // Given
        when(userApiClient.isValidUser(anyString())).thenReturn(true);
        when(shopApiClient.isShopOpen(anyString())).thenReturn(true);
        
        // 메뉴 정보 모킹
        ShopApiClient.MenuInfoResponse menuInfo = mock(ShopApiClient.MenuInfoResponse.class);
        when(menuInfo.isOpen()).thenReturn(true);
        when(shopApiClient.getMenu(anyString(), anyString())).thenReturn(menuInfo);
        
        // 옵션 정보 모킹
        ShopApiClient.OptionInfoResponse option1 = mock(ShopApiClient.OptionInfoResponse.class);
        when(option1.getName()).thenReturn("option-1");
        ShopApiClient.OptionInfoResponse option2 = mock(ShopApiClient.OptionInfoResponse.class);
        when(option2.getName()).thenReturn("option-2");
        when(shopApiClient.getMenuOptions(anyString(), anyString())).thenReturn(List.of(option1, option2));
        
        when(cartRepository.findByUserIdOptional(any(UserId.class))).thenReturn(Optional.empty());

        // When
        addCartItemCommandHandler.handle(command);

        // Then
        verify(userApiClient).isValidUser("user-1");
        verify(shopApiClient).isShopOpen("shop-1");
        verify(shopApiClient).getMenu("shop-1", "menu-1");
        verify(cartRepository).findByUserIdOptional(any(UserId.class));
        verify(cartRepository).save(argThat(savedCart -> savedCart != null));
    }

    @Test
    @DisplayName("장바구니 아이템 추가 성공 - 기존 장바구니")
    void 장바구니_아이템_추가_성공_기존_장바구니() {
        // Given
        when(userApiClient.isValidUser(anyString())).thenReturn(true);
        when(shopApiClient.isShopOpen(anyString())).thenReturn(true);
        
        // 메뉴 정보 모킹
        ShopApiClient.MenuInfoResponse menuInfo = mock(ShopApiClient.MenuInfoResponse.class);
        when(menuInfo.isOpen()).thenReturn(true);
        when(shopApiClient.getMenu(anyString(), anyString())).thenReturn(menuInfo);
        
        // 옵션 정보 모킹
        ShopApiClient.OptionInfoResponse option1 = mock(ShopApiClient.OptionInfoResponse.class);
        when(option1.getName()).thenReturn("option-1");
        ShopApiClient.OptionInfoResponse option2 = mock(ShopApiClient.OptionInfoResponse.class);
        when(option2.getName()).thenReturn("option-2");
        when(shopApiClient.getMenuOptions(anyString(), anyString())).thenReturn(List.of(option1, option2));
        
        when(cartRepository.findByUserIdOptional(any(UserId.class))).thenReturn(Optional.of(cart));

        // When
        addCartItemCommandHandler.handle(command);

        // Then
        verify(userApiClient).isValidUser("user-1");
        verify(shopApiClient).isShopOpen("shop-1");
        verify(shopApiClient).getMenu("shop-1", "menu-1");
        verify(cartRepository).findByUserIdOptional(any(UserId.class));
        verify(cartRepository).save(argThat(savedCart -> savedCart != null));
        
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getTotalQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("유효하지 않은 사용자로 아이템 추가 시 예외 발생")
    void 유효하지_않은_사용자로_아이템_추가_시_예외_발생() {
        // Given
        when(userApiClient.isValidUser(anyString())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> addCartItemCommandHandler.handle(command))
                .isInstanceOf(CartDomainException.class)
                .extracting(e -> ((CartDomainException) e).getErrorCode())
                .isEqualTo(CartErrorCode.INVALID_USER_ID);

        verify(userApiClient).isValidUser("user-1");
        verify(shopApiClient, never()).isShopOpen(anyString());
        verify(cartRepository, never()).findByUserIdOptional(any(UserId.class));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("영업 중이 아닌 가게의 메뉴 추가 시 예외 발생")
    void 영업_중이_아닌_가게의_메뉴_추가_시_예외_발생() {
        // Given
        when(userApiClient.isValidUser(anyString())).thenReturn(true);
        when(shopApiClient.isShopOpen(anyString())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> addCartItemCommandHandler.handle(command))
                .isInstanceOf(CartDomainException.class)
                .extracting(e -> ((CartDomainException) e).getErrorCode())
                .isEqualTo(CartErrorCode.SHOP_NOT_OPEN);

        verify(userApiClient).isValidUser("user-1");
        verify(shopApiClient).isShopOpen("shop-1");
        verify(cartRepository, never()).findByUserIdOptional(any(UserId.class));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("다른 가게의 메뉴 추가 시 장바구니 초기화")
    void 다른_가게의_메뉴_추가_시_장바구니_초기화() {
        // Given
        ShopId existingShopId = ShopId.of("existing-shop");
        cart.addItem(existingShopId, MenuId.of("existing-menu"), Arrays.asList(OptionId.of("option-1")), 1);
        
        when(userApiClient.isValidUser(anyString())).thenReturn(true);
        when(shopApiClient.isShopOpen(anyString())).thenReturn(true);
        when(cartRepository.findByUserIdOptional(any(UserId.class))).thenReturn(Optional.of(cart));

        // 메뉴/옵션 정보 모킹
        ShopApiClient.MenuInfoResponse menuInfo = mock(ShopApiClient.MenuInfoResponse.class);
        when(menuInfo.isOpen()).thenReturn(true);
        when(shopApiClient.getMenu(anyString(), anyString())).thenReturn(menuInfo);

        ShopApiClient.OptionInfoResponse option1 = mock(ShopApiClient.OptionInfoResponse.class);
        when(option1.getName()).thenReturn("option-1");
        ShopApiClient.OptionInfoResponse option2 = mock(ShopApiClient.OptionInfoResponse.class);
        when(option2.getName()).thenReturn("option-2");
        when(shopApiClient.getMenuOptions(anyString(), anyString())).thenReturn(List.of(option1, option2));

        // When
        addCartItemCommandHandler.handle(command);

        // Then
        verify(cartRepository).save(argThat(savedCart -> savedCart != null));
        
        // 다른 가게의 메뉴이므로 장바구니가 초기화되고 새 아이템만 있어야 함
        assertThat(cart.getShopId()).isEqualTo(ShopId.of("shop-1"));
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getMenuId()).isEqualTo(MenuId.of("menu-1"));
    }

    @Test
    @DisplayName("동일한 메뉴와 옵션 조합 추가 시 수량 병합")
    void 동일한_메뉴와_옵션_조합_추가_시_수량_병합() {
        // Given
        ShopId shopId = ShopId.of("shop-1");
        MenuId menuId = MenuId.of("menu-1");
        List<OptionId> options = Arrays.asList(OptionId.of("option-1"), OptionId.of("option-2"));
        
        cart.addItem(shopId, menuId, options, 1); // 기존에 1개 있음
        
        when(userApiClient.isValidUser(anyString())).thenReturn(true);
        when(shopApiClient.isShopOpen(anyString())).thenReturn(true);
        when(cartRepository.findByUserIdOptional(any(UserId.class))).thenReturn(Optional.of(cart));

        // 메뉴/옵션 정보 모킹
        ShopApiClient.MenuInfoResponse menuInfo = mock(ShopApiClient.MenuInfoResponse.class);
        when(menuInfo.isOpen()).thenReturn(true);
        when(shopApiClient.getMenu(anyString(), anyString())).thenReturn(menuInfo);

        ShopApiClient.OptionInfoResponse option1 = mock(ShopApiClient.OptionInfoResponse.class);
        when(option1.getName()).thenReturn("option-1");
        ShopApiClient.OptionInfoResponse option2 = mock(ShopApiClient.OptionInfoResponse.class);
        when(option2.getName()).thenReturn("option-2");
        when(shopApiClient.getMenuOptions(anyString(), anyString())).thenReturn(List.of(option1, option2));

        // When
        addCartItemCommandHandler.handle(command); // 2개 추가

        // Then
        verify(cartRepository).save(argThat(savedCart -> savedCart != null));
        
        assertThat(cart.getItems()).hasSize(1); // 아이템은 1개지만
        assertThat(cart.getTotalQuantity()).isEqualTo(3); // 수량은 3개 (1 + 2)
    }

    @Test
    @DisplayName("null 명령어로 아이템 추가 시 예외 발생")
    void null_명령어로_아이템_추가_시_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> addCartItemCommandHandler.handle(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("AddCartItemCommand는 필수입니다");

        verify(userApiClient, never()).isValidUser(anyString());
        verify(shopApiClient, never()).isShopOpen(anyString());
        verify(cartRepository, never()).findByUserIdOptional(any(UserId.class));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("외부 API 호출 실패 시 예외 전파")
    void 외부_API_호출_실패_시_예외_전파() {
        // Given
        when(userApiClient.isValidUser(anyString())).thenThrow(new RuntimeException("사용자 API 오류"));

        // When & Then
        assertThatThrownBy(() -> addCartItemCommandHandler.handle(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("사용자 API 오류");

        verify(userApiClient).isValidUser("user-1");
        verify(shopApiClient, never()).isShopOpen(anyString());
        verify(cartRepository, never()).findByUserIdOptional(any(UserId.class));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("장바구니 저장 실패 시 예외 전파")
    void 장바구니_저장_실패_시_예외_전파() {
        // Given
        when(userApiClient.isValidUser(anyString())).thenReturn(true);
        when(shopApiClient.isShopOpen(anyString())).thenReturn(true);
        when(cartRepository.findByUserIdOptional(any(UserId.class))).thenReturn(Optional.of(cart));
        // 메뉴/옵션 정보 모킹
        ShopApiClient.MenuInfoResponse menuInfo = mock(ShopApiClient.MenuInfoResponse.class);
        when(menuInfo.isOpen()).thenReturn(true);
        when(shopApiClient.getMenu(anyString(), anyString())).thenReturn(menuInfo);
        ShopApiClient.OptionInfoResponse option1 = mock(ShopApiClient.OptionInfoResponse.class);
        when(option1.getName()).thenReturn("option-1");
        ShopApiClient.OptionInfoResponse option2 = mock(ShopApiClient.OptionInfoResponse.class);
        when(option2.getName()).thenReturn("option-2");
        when(shopApiClient.getMenuOptions(anyString(), anyString())).thenReturn(List.of(option1, option2));
        doThrow(new RuntimeException("데이터베이스 오류")).when(cartRepository).save(any(Cart.class));

        // When & Then
        assertThatThrownBy(() -> addCartItemCommandHandler.handle(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("데이터베이스 오류");

        verify(userApiClient).isValidUser("user-1");
        verify(shopApiClient).isShopOpen("shop-1");
        verify(cartRepository).findByUserIdOptional(any(UserId.class));
        verify(cartRepository).save(argThat(cart -> cart != null));
    }

    @Test
    @DisplayName("아이템 추가 후 도메인 이벤트 발행 확인")
    void 아이템_추가_후_도메인_이벤트_발행_확인() {
        // Given
        when(userApiClient.isValidUser(anyString())).thenReturn(true);
        when(shopApiClient.isShopOpen(anyString())).thenReturn(true);
        when(cartRepository.findByUserIdOptional(any(UserId.class))).thenReturn(Optional.of(cart));

        // 메뉴/옵션 정보 모킹
        ShopApiClient.MenuInfoResponse menuInfo = mock(ShopApiClient.MenuInfoResponse.class);
        when(menuInfo.isOpen()).thenReturn(true);
        when(shopApiClient.getMenu(anyString(), anyString())).thenReturn(menuInfo);
        ShopApiClient.OptionInfoResponse option1 = mock(ShopApiClient.OptionInfoResponse.class);
        when(option1.getName()).thenReturn("option-1");
        ShopApiClient.OptionInfoResponse option2 = mock(ShopApiClient.OptionInfoResponse.class);
        when(option2.getName()).thenReturn("option-2");
        when(shopApiClient.getMenuOptions(anyString(), anyString())).thenReturn(List.of(option1, option2));

        // When
        addCartItemCommandHandler.handle(command);

        // Then
        verify(cartRepository).save(argThat(savedCart -> savedCart != null));
        
        assertThat(cart.hasDomainEvents()).isTrue();
        assertThat(cart.getDomainEvents()).hasSize(1);
    }
}