package harry.boilerplate.common.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.format.DateTimeParseException;

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
    @Schema(description = "응답 생성 시각 (ISO-8601)", example = "2025-09-20T11:20:30Z")
    private final String timestamp;

    private CommandResultResponse(String status, String message, String resourceId, Instant timestamp) {
        this.status = status;
        this.message = message;
        this.resourceId = resourceId;
        this.timestamp = timestamp != null ? timestamp.toString() : Instant.now().toString();
    }

    public CommandResultResponse(String status, String message, String resourceId) {
        this(status, message, resourceId, Instant.now());
    }

    @JsonCreator
    public CommandResultResponse(
        @JsonProperty("status") String status,
        @JsonProperty("message") String message,
        @JsonProperty("resourceId") String resourceId,
        @JsonProperty("timestamp") String timestamp
    ) {
        this(status, message, resourceId, parseTimestamp(timestamp));
    }
    
    /**
     * 성공 응답 생성 (리소스 ID 포함)
     */
    public static CommandResultResponse success(String message, String resourceId) {
        return new CommandResultResponse("SUCCESS", message, resourceId, Instant.now());
    }
    
    /**
     * 성공 응답 생성 (리소스 ID 없음)
     */
    public static CommandResultResponse success(String message) {
        return new CommandResultResponse("SUCCESS", message, null, Instant.now());
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

    public String getTimestamp() {
        return timestamp;
    }

    private static Instant parseTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(timestamp);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
