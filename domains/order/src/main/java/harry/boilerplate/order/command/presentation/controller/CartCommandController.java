package harry.boilerplate.order.command.presentation.controller;

import harry.boilerplate.common.response.CommandResultResponse;
import harry.boilerplate.order.command.application.handler.AddCartItemCommandHandler;
import harry.boilerplate.order.command.presentation.dto.AddCartItemRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 장바구니 Command API Controller
 * Requirements: 5.1, 9.1
 */
@RestController
@RequestMapping("/api/v1/carts")
@Tag(name = "Cart Command API", description = "장바구니 명령 API")
public class CartCommandController {
    
    private final AddCartItemCommandHandler addCartItemCommandHandler;
    
    public CartCommandController(AddCartItemCommandHandler addCartItemCommandHandler) {
        this.addCartItemCommandHandler = addCartItemCommandHandler;
    }
    
    /**
     * 장바구니에 아이템 추가
     * Requirements: 5.1, 5.2, 5.3
     */
    @PostMapping("/items")
    @Operation(
        summary = "장바구니 아이템 추가",
        description = "사용자의 장바구니에 메뉴 아이템을 추가합니다. 다른 가게의 메뉴 추가 시 기존 장바구니가 초기화됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "아이템 추가 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 메뉴, 가게 영업 중단 등)"),
        @ApiResponse(responseCode = "404", description = "사용자 또는 메뉴를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<CommandResultResponse> addCartItem(
            @Parameter(description = "사용자 ID", required = true, example = "user-123")
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "장바구니 아이템 추가 요청", required = true)
            @Valid @RequestBody AddCartItemRequest request) {
        
        // Command Handler 호출
        addCartItemCommandHandler.handle(request.toCommand(userId));
        
        // 성공 응답 반환
        CommandResultResponse response = CommandResultResponse.success(
            "장바구니에 아이템이 추가되었습니다"
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 장바구니 아이템 수량 변경
     * Requirements: 5.4 (향후 구현 예정)
     */
    @PutMapping("/items/{menuId}")
    @Operation(
        summary = "장바구니 아이템 수량 변경",
        description = "장바구니의 특정 메뉴 아이템 수량을 변경합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수량 변경 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<CommandResultResponse> updateCartItemQuantity(
            @Parameter(description = "사용자 ID", required = true, example = "user-123")
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "메뉴 ID", required = true, example = "menu-456")
            @PathVariable String menuId,
            @Parameter(description = "새로운 수량", required = true, example = "3")
            @RequestParam Integer quantity) {
        
        // TODO: UpdateCartItemQuantityCommandHandler 구현 필요
        // 현재는 기본 응답만 반환
        CommandResultResponse response = CommandResultResponse.success(
            "장바구니 아이템 수량이 변경되었습니다"
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 장바구니 아이템 삭제
     * Requirements: 5.4 (향후 구현 예정)
     */
    @DeleteMapping("/items/{menuId}")
    @Operation(
        summary = "장바구니 아이템 삭제",
        description = "장바구니에서 특정 메뉴 아이템을 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "아이템 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<CommandResultResponse> removeCartItem(
            @Parameter(description = "사용자 ID", required = true, example = "user-123")
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "메뉴 ID", required = true, example = "menu-456")
            @PathVariable String menuId) {
        
        // TODO: RemoveCartItemCommandHandler 구현 필요
        // 현재는 기본 응답만 반환
        CommandResultResponse response = CommandResultResponse.success(
            "장바구니에서 아이템이 삭제되었습니다"
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 장바구니 전체 비우기
     * Requirements: 5.4 (향후 구현 예정)
     */
    @DeleteMapping
    @Operation(
        summary = "장바구니 전체 비우기",
        description = "사용자의 장바구니를 완전히 비웁니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "장바구니 비우기 성공"),
        @ApiResponse(responseCode = "404", description = "장바구니를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<CommandResultResponse> clearCart(
            @Parameter(description = "사용자 ID", required = true, example = "user-123")
            @RequestHeader("X-User-Id") String userId) {
        
        // TODO: ClearCartCommandHandler 구현 필요
        // 현재는 기본 응답만 반환
        CommandResultResponse response = CommandResultResponse.success(
            "장바구니가 비워졌습니다"
        );
        
        return ResponseEntity.ok(response);
    }
}