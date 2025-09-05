package harry.boilerplate.order.query.presentation.controller;

import harry.boilerplate.order.query.application.dto.OrderHistoryQuery;
import harry.boilerplate.order.query.application.dto.OrderHistoryResult;
import harry.boilerplate.order.query.application.handler.OrderHistoryQueryHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 주문 Query API Controller
 * Requirements: 6.3, 9.2
 */
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order Query API", description = "주문 조회 API")
public class OrderQueryController {
    
    private final OrderHistoryQueryHandler orderHistoryQueryHandler;
    
    public OrderQueryController(OrderHistoryQueryHandler orderHistoryQueryHandler) {
        this.orderHistoryQueryHandler = orderHistoryQueryHandler;
    }
    
    /**
     * 사용자 주문 이력 조회 (페이징)
     * Requirements: 6.3
     */
    @GetMapping
    @Operation(
        summary = "주문 이력 조회",
        description = "사용자의 주문 이력을 페이징하여 조회합니다. 최신 주문부터 정렬되어 반환됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "주문 이력 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (잘못된 페이징 파라미터 등)"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<OrderHistoryResult> getOrderHistory(
            @Parameter(description = "사용자 ID", required = true, example = "user-123")
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기 (1~100)", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        // Query 생성 및 Handler 호출
        OrderHistoryQuery query = new OrderHistoryQuery(userId, page, size);
        OrderHistoryResult result = orderHistoryQueryHandler.handle(query);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 특정 주문 상세 조회 (향후 구현 예정)
     * Requirements: 6.3
     */
    @GetMapping("/{orderId}")
    @Operation(
        summary = "주문 상세 조회",
        description = "특정 주문의 상세 정보를 조회합니다. 주문 아이템, 옵션, 가격 정보 등을 포함합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "주문 상세 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "주문 조회 권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Object> getOrderDetail(
            @Parameter(description = "사용자 ID", required = true, example = "user-123")
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "주문 ID", required = true, example = "order-789")
            @PathVariable String orderId) {
        
        // TODO: OrderDetailQueryHandler 구현 필요
        // 현재는 기본 응답만 반환
        return ResponseEntity.ok("주문 상세 정보 (구현 예정)");
    }
    
    /**
     * 주문 상태별 조회 (향후 구현 예정)
     * Requirements: 6.3
     */
    @GetMapping("/status/{status}")
    @Operation(
        summary = "주문 상태별 조회",
        description = "특정 상태의 주문들을 조회합니다. (예: 진행중, 완료, 취소 등)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "상태별 주문 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 상태값 등)"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Object> getOrdersByStatus(
            @Parameter(description = "사용자 ID", required = true, example = "user-123")
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "주문 상태", required = true, example = "PREPARING")
            @PathVariable String status,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기 (1~100)", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        // TODO: OrdersByStatusQueryHandler 구현 필요
        // 현재는 기본 응답만 반환
        return ResponseEntity.ok("상태별 주문 목록 (구현 예정)");
    }
    
    /**
     * 주문 통계 조회 (향후 구현 예정)
     * Requirements: 6.3
     */
    @GetMapping("/statistics")
    @Operation(
        summary = "주문 통계 조회",
        description = "사용자의 주문 통계 정보를 조회합니다. (총 주문 수, 총 주문 금액, 자주 주문하는 가게 등)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "주문 통계 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Object> getOrderStatistics(
            @Parameter(description = "사용자 ID", required = true, example = "user-123")
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "통계 기간 (일)", example = "30")
            @RequestParam(defaultValue = "30") int days) {
        
        // TODO: OrderStatisticsQueryHandler 구현 필요
        // 현재는 기본 응답만 반환
        return ResponseEntity.ok("주문 통계 정보 (구현 예정)");
    }
}