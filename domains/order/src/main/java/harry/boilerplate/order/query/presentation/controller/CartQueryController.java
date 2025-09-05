package harry.boilerplate.order.query.presentation.controller;

import harry.boilerplate.order.query.application.dto.CartSummaryQuery;
import harry.boilerplate.order.query.application.dto.CartSummaryResult;
import harry.boilerplate.order.query.application.handler.CartSummaryQueryHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 장바구니 Query API Controller
 * Requirements: 5.5, 9.2
 */
@RestController
@RequestMapping("/api/v1/carts")
@Tag(name = "Cart Query API", description = "장바구니 조회 API")
public class CartQueryController {
    
    private final CartSummaryQueryHandler cartSummaryQueryHandler;
    
    public CartQueryController(CartSummaryQueryHandler cartSummaryQueryHandler) {
        this.cartSummaryQueryHandler = cartSummaryQueryHandler;
    }
    
    /**
     * 사용자 장바구니 요약 조회
     * Requirements: 5.5
     */
    @GetMapping
    @Operation(
        summary = "장바구니 요약 조회",
        description = "사용자의 현재 장바구니 요약 정보를 조회합니다. 장바구니가 비어있는 경우 빈 결과를 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "장바구니 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<CartSummaryResult> getCartSummary(
            @Parameter(description = "사용자 ID", required = true, example = "user-123")
            @RequestHeader("X-User-Id") String userId) {
        
        // Query 생성 및 Handler 호출
        CartSummaryQuery query = new CartSummaryQuery(userId);
        CartSummaryResult result = cartSummaryQueryHandler.handle(query);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 장바구니 상세 조회 (향후 구현 예정)
     * Requirements: 5.5
     */
    @GetMapping("/details")
    @Operation(
        summary = "장바구니 상세 조회",
        description = "사용자의 장바구니 상세 정보를 조회합니다. 각 아이템의 옵션 정보와 가격 계산 내역을 포함합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "장바구니 상세 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "사용자 또는 장바구니를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Object> getCartDetails(
            @Parameter(description = "사용자 ID", required = true, example = "user-123")
            @RequestHeader("X-User-Id") String userId) {
        
        // TODO: CartDetailQueryHandler 구현 필요
        // 현재는 기본 응답만 반환
        return ResponseEntity.ok("장바구니 상세 정보 (구현 예정)");
    }
    
    /**
     * 장바구니 아이템 개수 조회 (향후 구현 예정)
     * Requirements: 5.5
     */
    @GetMapping("/count")
    @Operation(
        summary = "장바구니 아이템 개수 조회",
        description = "사용자의 장바구니에 담긴 총 아이템 개수를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "아이템 개수 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Object> getCartItemCount(
            @Parameter(description = "사용자 ID", required = true, example = "user-123")
            @RequestHeader("X-User-Id") String userId) {
        
        // TODO: CartItemCountQueryHandler 구현 필요
        // 현재는 기본 응답만 반환
        return ResponseEntity.ok("{ \"count\": 0 }");
    }
}