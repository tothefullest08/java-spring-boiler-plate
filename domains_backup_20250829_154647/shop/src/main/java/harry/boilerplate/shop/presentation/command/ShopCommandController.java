package harry.boilerplate.shop.presentation.command;

import harry.boilerplate.common.response.CommandResultResponse;
import harry.boilerplate.shop.application.command.handler.CreateShopCommandHandler;
import harry.boilerplate.shop.application.command.handler.UpdateShopCommandHandler;
import harry.boilerplate.shop.presentation.command.dto.CreateShopRequest;
import harry.boilerplate.shop.presentation.command.dto.UpdateShopRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Shop Context Command API Controller
 * 가게 관련 명령(쓰기) 작업을 처리하는 REST API
 */
@RestController
@RequestMapping("/api/shops")
@Tag(name = "Shop Command API", description = "가게 관련 명령 API")
public class ShopCommandController {
    
    private final CreateShopCommandHandler createShopCommandHandler;
    private final UpdateShopCommandHandler updateShopCommandHandler;
    
    public ShopCommandController(
            CreateShopCommandHandler createShopCommandHandler,
            UpdateShopCommandHandler updateShopCommandHandler) {
        this.createShopCommandHandler = createShopCommandHandler;
        this.updateShopCommandHandler = updateShopCommandHandler;
    }
    
    /**
     * 가게 생성
     * Requirements: 1.1 - 가게 운영자가 가게를 생성할 수 있어야 함
     */
    @PostMapping
    @Operation(summary = "가게 생성", description = "새로운 가게를 생성합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "가게 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<CommandResultResponse> createShop(
            @Valid @RequestBody CreateShopRequest request) {
        
        String shopId = createShopCommandHandler.handle(request.toCommand());
        
        CommandResultResponse response = CommandResultResponse.success(
            "가게가 성공적으로 생성되었습니다",
            shopId
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 가게 정보 수정
     * Requirements: 1.1 - 가게 운영자가 가게 정보를 수정할 수 있어야 함
     */
    @PutMapping("/{shopId}")
    @Operation(summary = "가게 정보 수정", description = "기존 가게의 정보를 수정합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "가게 정보 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<CommandResultResponse> updateShop(
            @Parameter(description = "가게 ID", required = true)
            @PathVariable String shopId,
            @Valid @RequestBody UpdateShopRequest request) {
        
        updateShopCommandHandler.handle(request.toCommand(shopId));
        
        CommandResultResponse response = CommandResultResponse.success(
            "가게 정보가 성공적으로 수정되었습니다"
        );
        
        return ResponseEntity.ok(response);
    }
}