package harry.boilerplate.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * 에러 응답 표준 형식
 */
@Schema(description = "에러 응답")
public class ErrorResponse {
    
    @Schema(description = "에러 코드", example = "COMMON-SYSTEM-001")
    private final String errorCode;
    
    @Schema(description = "에러 메시지", example = "요청 처리 중 오류가 발생했습니다.")
    private final String message;
    
    @Schema(description = "에러 발생 시간", example = "2024-01-01T12:00:00Z")
    private final Instant timestamp;
    
    @Schema(description = "요청 경로", example = "/api/shops")
    private final String path;
    
    public ErrorResponse(String errorCode, String message, String path) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = Instant.now();
        this.path = path;
    }
    
    /**
     * 에러 응답 생성 팩토리 메서드
     */
    public static ErrorResponse of(String errorCode, String message, String path) {
        return new ErrorResponse(errorCode, message, path);
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public String getPath() {
        return path;
    }
}