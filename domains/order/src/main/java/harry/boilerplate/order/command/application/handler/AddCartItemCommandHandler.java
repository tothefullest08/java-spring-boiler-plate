package harry.boilerplate.order.command.application.handler;

import harry.boilerplate.order.command.application.dto.AddCartItemCommand;
import harry.boilerplate.order.command.domain.aggregate.Cart;
import harry.boilerplate.order.command.domain.aggregate.CartRepository;
import harry.boilerplate.order.command.domain.valueObject.*;
import harry.boilerplate.order.command.domain.exception.CartDomainException;
import harry.boilerplate.order.command.domain.exception.CartErrorCode;
import harry.boilerplate.order.command.infrastructure.external.shop.ShopApiClient;
import harry.boilerplate.order.command.infrastructure.external.user.UserApiClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 장바구니 아이템 추가 Command Handler
 * Requirements: 5.1, 5.2, 5.3
 */
@Component
@Transactional
public class AddCartItemCommandHandler {

    private final CartRepository cartRepository;
    private final ShopApiClient shopApiClient;
    private final UserApiClient userApiClient;

    public AddCartItemCommandHandler(CartRepository cartRepository,
            ShopApiClient shopApiClient,
            UserApiClient userApiClient) {
        this.cartRepository = cartRepository;
        this.shopApiClient = shopApiClient;
        this.userApiClient = userApiClient;
    }

    /**
     * 장바구니 아이템 추가 처리
     * 1. 사용자 유효성 검증
     * 2. 가게 영업 상태 확인
     * 3. 메뉴 유효성 검증
     * 4. 장바구니 조회 또는 생성
     * 5. 아이템 추가
     */
    public void handle(AddCartItemCommand command) {
        // 입력 검증
        validateCommand(command);

        // 1. 사용자 유효성 검증
        if (!userApiClient.isValidUser(command.getUserId())) {
            throw new CartDomainException(CartErrorCode.INVALID_USER_ID);
        }

        // 2. 가게 영업 상태 확인 (Requirements: 5.3)
        if (!shopApiClient.isShopOpen(command.getShopId())) {
            throw new CartDomainException(CartErrorCode.SHOP_NOT_OPEN);
        }

        // 3. 메뉴 유효성 검증
        ShopApiClient.MenuInfoResponse menuInfo = shopApiClient.getMenu(
                command.getShopId(), command.getMenuId());

        if (menuInfo == null || !menuInfo.isOpen()) {
            throw new CartDomainException(CartErrorCode.MENU_NOT_AVAILABLE);
        }

        // 4. 옵션 유효성 검증 (선택된 옵션이 있는 경우)
        if (!command.getSelectedOptionIds().isEmpty()) {
            List<ShopApiClient.OptionInfoResponse> availableOptions = shopApiClient.getMenuOptions(command.getShopId(),
                    command.getMenuId());

            validateSelectedOptions(command.getSelectedOptionIds(), availableOptions);
        }

        // 5. 장바구니 조회 또는 생성
        UserId userId = UserId.of(command.getUserId());
        Cart cart = cartRepository.findByUserIdOptional(userId)
                .orElse(new Cart(userId));

        // 6. 아이템 추가 (단일 가게 규칙 적용)
        ShopId shopId = ShopId.of(command.getShopId());
        MenuId menuId = MenuId.of(command.getMenuId());
        List<OptionId> selectedOptions = command.getSelectedOptionIds().stream()
                .map(OptionId::of)
                .collect(Collectors.toList());

        cart.addItem(shopId, menuId, selectedOptions, command.getQuantity());

        // 7. 장바구니 저장
        try {
            cartRepository.save(cart);
        } catch (Exception e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }

    /**
     * Command 입력 검증
     */
    private void validateCommand(AddCartItemCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("AddCartItemCommand는 필수입니다");
        }
        if (command.getUserId() == null || command.getUserId().trim().isEmpty()) {
            throw new CartDomainException(CartErrorCode.INVALID_USER_ID);
        }
        if (command.getShopId() == null || command.getShopId().trim().isEmpty()) {
            throw new CartDomainException(CartErrorCode.INVALID_SHOP_ID);
        }
        if (command.getMenuId() == null || command.getMenuId().trim().isEmpty()) {
            throw new CartDomainException(CartErrorCode.INVALID_MENU_ID);
        }
        if (command.getQuantity() <= 0) {
            throw new CartDomainException(CartErrorCode.INVALID_QUANTITY);
        }
    }

    /**
     * 선택된 옵션들의 유효성 검증
     */
    private void validateSelectedOptions(List<String> selectedOptionIds,
            List<ShopApiClient.OptionInfoResponse> availableOptions) {
        if (availableOptions == null || availableOptions.isEmpty()) {
            if (!selectedOptionIds.isEmpty()) {
                throw new CartDomainException(CartErrorCode.INVALID_OPTION_SELECTION);
            }
            return;
        }

        // 선택된 옵션이 실제 메뉴의 옵션에 포함되는지 확인
        // 실제 구현에서는 더 정교한 옵션 검증 로직이 필요할 수 있음
        // (예: 필수 옵션 선택 여부, 옵션 그룹별 선택 제한 등)
        for (String selectedOptionId : selectedOptionIds) {
            boolean optionExists = availableOptions.stream()
                    .anyMatch(option -> option.getName().equals(selectedOptionId)); // 임시로 이름으로 비교

            if (!optionExists) {
                throw new CartDomainException(CartErrorCode.INVALID_OPTION_SELECTION);
            }
        }
    }
}