package harry.boilerplate.shop.command.presentation.controller;

import harry.boilerplate.common.response.CommandResultResponse;
import harry.boilerplate.shop.command.application.handler.AddOptionGroupCommandHandler;
import harry.boilerplate.shop.command.application.handler.CreateMenuCommandHandler;
import harry.boilerplate.shop.command.application.handler.OpenMenuCommandHandler;
import harry.boilerplate.shop.command.application.dto.OpenMenuCommand;
import harry.boilerplate.shop.command.presentation.dto.AddOptionGroupRequest;
import harry.boilerplate.shop.command.presentation.dto.CreateMenuRequest;
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
 * Menu Context Command API Controller
 * 메뉴 관련 명령(쓰기) 작업을 처리하는 REST API
 */
@RestController
@RequestMapping("/api/shops/{shopId}/menus")
@Tag(name = "Menu Command API", description = "메뉴 관련 명령 API")
public class MenuCommandController {
    
    private final CreateMenuCommandHandler createMenuCommandHandler;
    private final OpenMenuCommandHandler openMenuCommandHandler;
    private final AddOptionGroupCommandHandler addOptionGroupCommandHandler;
    
    public MenuCommandController(
            CreateMenuCommandHandler createMenuCommandHandler,
            OpenMenuCommandHandler openMenuCommandHandler,
            AddOptionGroupCommandHandler addOptionGroupCommandHandler) {
        this.createMenuCommandHandler = createMenuCommandHandler;
        this.openMenuCommandHandler = openMenuCommandHandler;
        this.addOptionGroupCommandHandler = addOptionGroupCommandHandler;
    }
    
    /**
     * 메뉴 생성
     * Requirements: 2.1 - 가게 운영자가 메뉴를 등록할 수 있어야 함
     */
    @PostMapping
    @Operation(summary = "메뉴 생성", description = "가게에 새로운 메뉴를 생성합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "메뉴 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<CommandResultResponse> createMenu(
            @Parameter(description = "가게 ID", required = true)
            @PathVariable String shopId,
            @Valid @RequestBody CreateMenuRequest request) {
        
        String menuId = createMenuCommandHandler.handle(request.toCommand(shopId));
        
        CommandResultResponse response = CommandResultResponse.success(
            "메뉴가 성공적으로 생성되었습니다",
            menuId
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 메뉴 공개
     * Requirements: 2.3 - 메뉴 공개 조건을 검증하여 공개 상태로 변경
     */
    @PutMapping("/{menuId}/open")
    @Operation(summary = "메뉴 공개", description = "메뉴를 공개 상태로 변경합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "메뉴 공개 성공"),
        @ApiResponse(responseCode = "400", description = "메뉴 공개 조건 미충족"),
        @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<CommandResultResponse> openMenu(
            @Parameter(description = "가게 ID", required = true)
            @PathVariable String shopId,
            @Parameter(description = "메뉴 ID", required = true)
            @PathVariable String menuId) {
        
        openMenuCommandHandler.handle(new OpenMenuCommand(menuId));
        
        CommandResultResponse response = CommandResultResponse.success(
            "메뉴가 성공적으로 공개되었습니다"
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 옵션 그룹 추가
     * Requirements: 3.1 - 동일한 이름의 옵션그룹이 존재하지 않을 때만 추가
     */
    @PostMapping("/{menuId}/option-groups")
    @Operation(summary = "옵션 그룹 추가", description = "메뉴에 새로운 옵션 그룹을 추가합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "옵션 그룹 추가 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 또는 중복된 옵션 그룹 이름"),
        @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<CommandResultResponse> addOptionGroup(
            @Parameter(description = "가게 ID", required = true)
            @PathVariable String shopId,
            @Parameter(description = "메뉴 ID", required = true)
            @PathVariable String menuId,
            @Valid @RequestBody AddOptionGroupRequest request) {
        
        String optionGroupId = addOptionGroupCommandHandler.handle(request.toCommand(menuId));
        
        CommandResultResponse response = CommandResultResponse.success(
            "옵션 그룹이 성공적으로 추가되었습니다",
            optionGroupId
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}