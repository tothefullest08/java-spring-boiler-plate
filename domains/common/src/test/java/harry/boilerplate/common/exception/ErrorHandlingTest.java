package harry.boilerplate.common.exception;

import harry.boilerplate.common.response.ErrorResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 에러 처리 패턴 테스트
 */
class ErrorHandlingTest {

    @Test
    void ErrorCode_인터페이스_구현_테스트() {
        // Given
        TestErrorCode errorCode = TestErrorCode.TEST_ERROR;
        
        // Then
        assertThat(errorCode.getCode()).isEqualTo("TEST-DOMAIN-001");
        assertThat(errorCode.getMessage()).isEqualTo("테스트 에러입니다");
    }

    @Test
    void CommonSystemErrorCode_enum_테스트() {
        // Given
        CommonSystemErrorCode errorCode = CommonSystemErrorCode.VALIDATION_ERROR;
        
        // Then
        assertThat(errorCode.getCode()).isEqualTo("COMMON-SYSTEM-006");
        assertThat(errorCode.getMessage()).isEqualTo("Validation error");
    }

    @Test
    void DomainException_기본_동작_테스트() {
        // Given
        TestDomainException exception = new TestDomainException("테스트 도메인 예외");
        
        // Then
        assertThat(exception.getMessage()).isEqualTo("테스트 도메인 예외");
        assertThat(exception.getErrorCode()).isEqualTo(TestErrorCode.TEST_ERROR);
    }

    @Test
    void ApplicationException_기본_동작_테스트() {
        // Given
        TestApplicationException exception = new TestApplicationException("테스트 애플리케이션 예외");
        
        // Then
        assertThat(exception.getMessage()).isEqualTo("테스트 애플리케이션 예외");
        assertThat(exception.getErrorCode()).isEqualTo(TestErrorCode.TEST_ERROR);
    }

    @Test
    void ErrorResponse_생성_테스트() {
        // Given
        String errorCode = "TEST-001";
        String message = "테스트 에러 메시지";
        String path = "/api/test";
        
        // When
        ErrorResponse response = ErrorResponse.of(errorCode, message, path);
        
        // Then
        assertThat(response.getErrorCode()).isEqualTo(errorCode);
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getPath()).isEqualTo(path);
        assertThat(response.getTimestamp()).isNotNull();
    }

    // Test implementations
    private enum TestErrorCode implements ErrorCode {
        TEST_ERROR("TEST-DOMAIN-001", "테스트 에러입니다");
        
        private final String code;
        private final String message;
        
        TestErrorCode(String code, String message) {
            this.code = code;
            this.message = message;
        }
        
        @Override
        public String getCode() { return code; }
        
        @Override
        public String getMessage() { return message; }
    }

    private static class TestDomainException extends DomainException {
        public TestDomainException(String message) {
            super(message, null);
        }
        
        @Override
        public ErrorCode getErrorCode() {
            return TestErrorCode.TEST_ERROR;
        }
    }

    private static class TestApplicationException extends ApplicationException {
        public TestApplicationException(String message) {
            super(message);
        }
        
        @Override
        public ErrorCode getErrorCode() {
            return TestErrorCode.TEST_ERROR;
        }
    }
}