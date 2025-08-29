package harry.boilerplate.shop.query.presentation.controller;

import harry.boilerplate.shop.query.application.dto.MenuBoardQuery;
import harry.boilerplate.shop.query.application.dto.MenuBoardResult;
import harry.boilerplate.shop.query.application.dto.MenuDetailQuery;
import harry.boilerplate.shop.query.application.dto.MenuDetailResult;
import harry.boilerplate.shop.query.application.handler.MenuBoardQueryHandler;
import harry.boilerplate.shop.query.application.handler.MenuDetailQueryHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Menu Context Query API Controller
 * 메뉴 관련 조회(읽기) 작업을 처리하는 REST API
 */
@RestController
@RequestMapping("/api/shops/{shopId}/menus")
@Tag(name = "Menu Query API", description = "메뉴 관련 조회 API")
public class MenuQueryController {
    
    private final MenuBoardQueryHandler menuBoardQueryHandler;
    private final MenuDetailQueryHandler menuDetailQueryHandler;
    
    public MenuQueryController(
            MenuBoardQueryHandler menuBoardQueryHandler,
            MenuDetailQueryHandler menuDetailQueryHandler) {
        this.menuBoardQueryHandler = menuBoardQueryHandler;
        this.menuDetailQueryHandler = menuDetailQueryHandler;
    }
    
    /**
     * 메뉴보드 조회
     * Requirements: 4.1, 4.2, 4.3 - 고객이 가게의 메뉴보드를 조회할 수 있어야 함
     */
    @GetMapping
    @Operation(summary = "메뉴보드 조회", description = "가게의 메뉴보드를 조회합니다 (공개된 메뉴만)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "메뉴보드 조회 성공"),
        @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<MenuBoardResult> getMenuBoard(
            @Parameter(description = "가게 ID", required = true)
            @PathVariable String shopId) {
        
        MenuBoardQuery query = new MenuBoardQuery(shopId);
        MenuBoardResult result = menuBoardQueryHandler.handle(query);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 메뉴 상세 조회
     * Requirements: 4.4 - 고객이 메뉴 상세 정보를 조회할 수 있어야 함
     */
    @GetMapping("/{menuId}")
    @Operation(summary = "메뉴 상세 조회", description = "특정 메뉴의 상세 정보를 조회합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "메뉴 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<MenuDetailResult> getMenuDetail(
            @Parameter(description = "가게 ID", required = true)
            @PathVariable String shopId,
            @Parameter(description = "메뉴 ID", required = true)
            @PathVariable String menuId) {
        
        MenuDetailQuery query = new MenuDetailQuery(menuId);
        MenuDetailResult result = menuDetailQueryHandler.handle(query);
        
        return ResponseEntity.ok(result);
    }
}