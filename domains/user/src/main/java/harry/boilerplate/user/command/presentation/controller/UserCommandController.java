package harry.boilerplate.user.command.presentation.command;

import harry.boilerplate.common.response.CommandResultResponse;
import harry.boilerplate.user.command.application.handler.CreateUserCommandHandler;
import harry.boilerplate.user.command.application.handler.UpdateUserCommandHandler;
import harry.boilerplate.user.command.presentation.dto.CreateUserRequest;
import harry.boilerplate.user.command.presentation.dto.UpdateUserRequest;

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
 * User Command Controller
 * Requirements 7.1: 사용자 생성 및 수정 API 구현
 */
@RestController
@RequestMapping("/users")
@Tag(name = "User Command API", description = "사용자 생성 및 수정 API")
public class UserCommandController {
    
    private final CreateUserCommandHandler createUserCommandHandler;
    private final UpdateUserCommandHandler updateUserCommandHandler;
    
    public UserCommandController(CreateUserCommandHandler createUserCommandHandler,
                                UpdateUserCommandHandler updateUserCommandHandler) {
        this.createUserCommandHandler = createUserCommandHandler;
        this.updateUserCommandHandler = updateUserCommandHandler;
    }
    
    /**
     * 사용자 생성
     * Requirements 7.1: 사용자 생성 기능
     */
    @PostMapping
    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "사용자 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "409", description = "이메일 중복")
    })
    public ResponseEntity<CommandResultResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        
        String userId = createUserCommandHandler.handle(request.toCommand());
        
        CommandResultResponse response = CommandResultResponse.success(
            "사용자가 성공적으로 생성되었습니다", userId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 사용자 정보 수정
     * Requirements 7.1: 사용자 정보 수정 기능
     */
    @PutMapping("/{userId}")
    @Operation(summary = "사용자 정보 수정", description = "기존 사용자의 정보를 수정합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "이메일 중복")
    })
    public ResponseEntity<CommandResultResponse> updateUser(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest request) {
        
        updateUserCommandHandler.handle(request.toCommand(userId));
        
        CommandResultResponse response = CommandResultResponse.success(
            "사용자 정보가 성공적으로 수정되었습니다");
        
        return ResponseEntity.ok(response);
    }
}