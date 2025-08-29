package harry.boilerplate.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Command 요청 결과 응답 표준 형식
 */
@Schema(description = "Command 요청 결과 응답")
public class CommandResultResponse {
    
    @Schema(description = "처리 상태", example = "SUCCESS")
    private final String status;
    
    @Schema(description = "결과 메시지", example = "요청이 성공적으로 처리되었습니다.")
    private final String message;
    
    @Schema(description = "생성/수정된 리소스의 ID", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef", nullable = true)
    private final String resourceId;
    
    public CommandResultResponse(String status, String message, String resourceId) {
        this.status = status;
        this.message = message;
        this.resourceId = resourceId;
    }
    
    /**
     * 성공 응답 생성 (리소스 ID 포함)
     */
    public static CommandResultResponse success(String message, String resourceId) {
        return new CommandResultResponse("SUCCESS", message, resourceId);
    }
    
    /**
     * 성공 응답 생성 (리소스 ID 없음)
     */
    public static CommandResultResponse success(String message) {
        return new CommandResultResponse("SUCCESS", message, null);
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getResourceId() {
        return resourceId;
    }
}