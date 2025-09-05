package harry.boilerplate.user.query.presentation.query;

import harry.boilerplate.user.query.application.dto.UserDetailQuery;
import harry.boilerplate.user.query.application.dto.UserDetailResult;
import harry.boilerplate.user.query.application.dto.UserValidationQuery;
import harry.boilerplate.user.query.application.dto.UserValidationResult;
import harry.boilerplate.user.query.application.handler.UserDetailQueryHandler;
import harry.boilerplate.user.query.application.handler.UserValidationQueryHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User Query Controller
 * Requirements 7.3, 7.4: 사용자 조회 및 유효성 검증 API 구현
 */
@RestController
@RequestMapping("/users")
@Tag(name = "User Query API", description = "사용자 조회 및 유효성 검증 API")
public class UserQueryController {
    
    private final UserDetailQueryHandler userDetailQueryHandler;
    private final UserValidationQueryHandler userValidationQueryHandler;
    
    public UserQueryController(UserDetailQueryHandler userDetailQueryHandler,
                              UserValidationQueryHandler userValidationQueryHandler) {
        this.userDetailQueryHandler = userDetailQueryHandler;
        this.userValidationQueryHandler = userValidationQueryHandler;
    }
    
    /**
     * 사용자 상세 정보 조회
     * Requirements 7.3: 사용자 상세 정보 조회 기능
     */
    @GetMapping("/{userId}")
    @Operation(summary = "사용자 상세 정보 조회", description = "사용자 ID로 상세 정보를 조회합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseEntity<UserDetailResult> getUserDetail(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable String userId) {
        
        UserDetailQuery query = new UserDetailQuery(userId);
        UserDetailResult result = userDetailQueryHandler.handle(query);
        
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 사용자 유효성 검증
     * Requirements 7.2, 7.4: 사용자 유효성 검증 기능
     */
    @GetMapping("/{userId}/validation")
    @Operation(summary = "사용자 유효성 검증", description = "사용자 ID의 유효성을 검증합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "유효성 검증 완료")
    })
    public ResponseEntity<UserValidationResult> validateUser(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable String userId) {
        
        UserValidationQuery query = new UserValidationQuery(userId);
        UserValidationResult result = userValidationQueryHandler.handle(query);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 사용자 존재 여부 확인 (간단한 검증)
     * Requirements 7.4: 사용자 존재 여부 확인 기능
     */
    @GetMapping("/{userId}/exists")
    @Operation(summary = "사용자 존재 여부 확인", description = "사용자 ID가 존재하는지 확인합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "존재 여부 확인 완료")
    })
    public ResponseEntity<Boolean> checkUserExists(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable String userId) {
        
        UserValidationQuery query = new UserValidationQuery(userId);
        UserValidationResult result = userValidationQueryHandler.handle(query);
        
        return ResponseEntity.ok(result.isValid());
    }
}