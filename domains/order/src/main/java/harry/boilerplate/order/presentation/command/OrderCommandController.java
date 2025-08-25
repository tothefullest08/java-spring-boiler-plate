package harry.boilerplate.order.presentation.command;

import harry.boilerplate.common.response.CommandResultResponse;
import harry.boilerplate.order.application.command.handler.PlaceOrderCommandHandler;
import harry.boilerplate.order.presentation.command.dto.PlaceOrderRequest;
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
 * 주문 Command API Controller
 * Requirements: 6.1, 9.1
 */
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order Command API", description = "주문 명령 API")
public class OrderCommandController {
    
    private final PlaceOrderCommandHandler placeOrderCommandHandler;
    
    public OrderCommandController(PlaceOrderCommandHandler placeOrderCommandHandler) {
        this.placeOrderCommandHandler = placeOrderCommandHandler;
    }
    
    /**
     * 주문 생성
     * Requirements: 6.1, 6.2
     */
    @PostMapping
    @Operation(
        summary = "주문 생성",
        description = "사용자의 장바구니 내용으로 주문을 생성합니다. 주문 생성 후 장바구니는 비워집니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "주문 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (빈 장바구니, 최소 주문금액 미달 등)"),
        @ApiResponse(responseCode = "404", description = "사용자 또는 장바구니를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<CommandResultResponse> placeOrder(
            @Parameter(description = "사용자 ID", required = true, example = "user-123")
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "주문 생성 요청", required = true)
            @Valid @RequestBody PlaceOrderRequest request) {
        
        // Command Handler 호출
        String orderId = placeOrderCommandHandler.handle(request.toCommand(userId));
        
        // 성공 응답 반환
        CommandResultResponse response = CommandResultResponse.success(
            "주문이 성공적으로 생성되었습니다",
            orderId
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 주문 취소
     * Requirements: 6.5 (향후 구현 예정)
     */
    @DeleteMapping("/{orderId}")
    @Operation(
        summary = "주문 취소",
        description = "생성된 주문을 취소합니다. 취소 가능한 상태의 주문만 취소할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "주문 취소 성공"),
        @ApiResponse(responseCode = "400", description = "취소할 수 없는 주문 상태"),
        @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "주문 취소 권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<CommandResultResponse> cancelOrder(
            @Parameter(description = "사용자 ID", required = true, example = "user-123")
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "주문 ID", required = true, example = "order-789")
            @PathVariable String orderId) {
        
        // TODO: CancelOrderCommandHandler 구현 필요
        // 현재는 기본 응답만 반환
        CommandResultResponse response = CommandResultResponse.success(
            "주문이 취소되었습니다"
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 주문 상태 변경
     * Requirements: 6.6 (향후 구현 예정)
     */
    @PutMapping("/{orderId}/status")
    @Operation(
        summary = "주문 상태 변경",
        description = "주문의 상태를 변경합니다. (예: 접수, 조리중, 배송중, 완료 등)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "주문 상태 변경 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 상태 변경 요청"),
        @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "상태 변경 권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<CommandResultResponse> updateOrderStatus(
            @Parameter(description = "사용자 ID", required = true, example = "user-123")
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "주문 ID", required = true, example = "order-789")
            @PathVariable String orderId,
            @Parameter(description = "새로운 주문 상태", required = true, example = "PREPARING")
            @RequestParam String status) {
        
        // TODO: UpdateOrderStatusCommandHandler 구현 필요
        // 현재는 기본 응답만 반환
        CommandResultResponse response = CommandResultResponse.success(
            "주문 상태가 변경되었습니다"
        );
        
        return ResponseEntity.ok(response);
    }
}