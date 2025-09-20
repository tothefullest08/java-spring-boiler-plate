package harry.boilerplate.common.command.misc.response;

import org.junit.jupiter.api.Test;

import harry.boilerplate.common.response.CommandResultResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CommandResultResponse 테스트
 */
class CommandResultResponseTest {
    
    @Test
    void 성공_응답_생성_리소스ID_포함() {
        // Given
        String message = "메뉴가 성공적으로 생성되었습니다";
        String resourceId = "menu-123";
        
        // When
        CommandResultResponse response = CommandResultResponse.success(message, resourceId);
        
        // Then
        assertThat(response.getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getResourceId()).isEqualTo(resourceId);
        assertThat(response.getTimestamp()).isNotBlank();
    }
    
    @Test
    void 성공_응답_생성_리소스ID_없음() {
        // Given
        String message = "요청이 성공적으로 처리되었습니다";
        
        // When
        CommandResultResponse response = CommandResultResponse.success(message);
        
        // Then
        assertThat(response.getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getResourceId()).isNull();
        assertThat(response.getTimestamp()).isNotBlank();
    }
    
    @Test
    void 생성자를_통한_응답_생성() {
        // Given
        String status = "SUCCESS";
        String message = "처리 완료";
        String resourceId = "resource-456";
        
        // When
        CommandResultResponse response = new CommandResultResponse(status, message, resourceId);
        
        // Then
        assertThat(response.getStatus()).isEqualTo(status);
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getResourceId()).isEqualTo(resourceId);
        assertThat(response.getTimestamp()).isNotBlank();
    }
    
    @Test
    void 팩토리_메서드_null_처리() {
        // Given
        String message = "테스트 메시지";
        
        // When
        CommandResultResponse responseWithNull = CommandResultResponse.success(message, null);
        CommandResultResponse responseWithoutId = CommandResultResponse.success(message);
        
        // Then
        assertThat(responseWithNull.getResourceId()).isNull();
        assertThat(responseWithoutId.getResourceId()).isNull();
        assertThat(responseWithNull.getStatus()).isEqualTo(responseWithoutId.getStatus());
        assertThat(responseWithNull.getMessage()).isEqualTo(responseWithoutId.getMessage());
    }
}
