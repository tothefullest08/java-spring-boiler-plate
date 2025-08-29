package harry.boilerplate.shop.query.presentation.controller;

import harry.boilerplate.shop.query.application.dto.ShopInfoQuery;
import harry.boilerplate.shop.query.application.dto.ShopInfoResult;
import harry.boilerplate.shop.query.application.handler.ShopInfoQueryHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Shop Context Query API Controller
 * 가게 관련 조회(읽기) 작업을 처리하는 REST API
 */
@RestController
@RequestMapping("/api/shops")
@Tag(name = "Shop Query API", description = "가게 관련 조회 API")
public class ShopQueryController {
    
    private final ShopInfoQueryHandler shopInfoQueryHandler;
    
    public ShopQueryController(ShopInfoQueryHandler shopInfoQueryHandler) {
        this.shopInfoQueryHandler = shopInfoQueryHandler;
    }
    
    /**
     * 가게 정보 조회
     * Requirements: 4.1 - 고객이 가게 정보를 조회할 수 있어야 함
     */
    @GetMapping("/{shopId}")
    @Operation(summary = "가게 정보 조회", description = "가게의 상세 정보를 조회합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "가게 정보 조회 성공"),
        @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ShopInfoResult> getShopInfo(
            @Parameter(description = "가게 ID", required = true)
            @PathVariable String shopId) {
        
        ShopInfoQuery query = new ShopInfoQuery(shopId);
        ShopInfoResult result = shopInfoQueryHandler.handle(query);
        
        return ResponseEntity.ok(result);
    }
}