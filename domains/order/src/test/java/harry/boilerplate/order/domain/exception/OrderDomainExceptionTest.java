package harry.boilerplate.order.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("OrderDomainException 테스트")
class OrderDomainExceptionTest {

    @Test
    @DisplayName("OrderDomainException 생성 및 ErrorCode 확인")
    void OrderDomainException_생성_및_ErrorCode_확인() {
        // Given
        OrderErrorCode errorCode = OrderErrorCode.ORDER_NOT_FOUND;

        // When
        OrderDomainException exception = new OrderDomainException(errorCode);

        // Then
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("OrderDomainException 생성 시 원인 예외 포함")
    void OrderDomainException_생성_시_원인_예외_포함() {
        // Given
        OrderErrorCode errorCode = OrderErrorCode.ORDER_NOT_FOUND;
        RuntimeException cause = new RuntimeException("원인 예외");

        // When
        OrderDomainException exception = new OrderDomainException(errorCode, cause);

        // Then
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("모든 OrderErrorCode에 대한 예외 생성 테스트")
    void 모든_OrderErrorCode에_대한_예외_생성_테스트() {
        // Given & When & Then
        for (OrderErrorCode errorCode : OrderErrorCode.values()) {
            OrderDomainException exception = new OrderDomainException(errorCode);
            
            assertThat(exception.getErrorCode()).isEqualTo(errorCode);
            assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
            assertThat(exception.getErrorCode().getCode()).isNotNull();
            assertThat(exception.getErrorCode().getCode()).isNotEmpty();
            assertThat(exception.getErrorCode().getCode()).startsWith("ORDER-DOMAIN-");
        }
    }

    @Test
    @DisplayName("ErrorCode 형식 검증")
    void ErrorCode_형식_검증() {
        // Given & When & Then
        for (OrderErrorCode errorCode : OrderErrorCode.values()) {
            String code = errorCode.getCode();
            String message = errorCode.getMessage();
            
            // 코드 형식: ORDER-DOMAIN-XXX
            assertThat(code).matches("ORDER-DOMAIN-\\d{3}");
            
            // 메시지는 null이 아니고 비어있지 않음
            assertThat(message).isNotNull();
            assertThat(message).isNotEmpty();
        }
    }
}