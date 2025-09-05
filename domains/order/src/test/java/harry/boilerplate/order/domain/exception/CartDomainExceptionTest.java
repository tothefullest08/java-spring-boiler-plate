package harry.boilerplate.order.command.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CartDomainException 테스트")
class CartDomainExceptionTest {

    @Test
    @DisplayName("CartDomainException 생성 및 ErrorCode 확인")
    void CartDomainException_생성_및_ErrorCode_확인() {
        // Given
        CartErrorCode errorCode = CartErrorCode.CART_NOT_FOUND;

        // When
        CartDomainException exception = new CartDomainException(errorCode);

        // Then
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("CartDomainException 생성 시 원인 예외 포함")
    void CartDomainException_생성_시_원인_예외_포함() {
        // Given
        CartErrorCode errorCode = CartErrorCode.CART_NOT_FOUND;
        RuntimeException cause = new RuntimeException("원인 예외");

        // When
        CartDomainException exception = new CartDomainException(errorCode, cause);

        // Then
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("모든 CartErrorCode에 대한 예외 생성 테스트")
    void 모든_CartErrorCode에_대한_예외_생성_테스트() {
        // Given & When & Then
        for (CartErrorCode errorCode : CartErrorCode.values()) {
            CartDomainException exception = new CartDomainException(errorCode);
            
            assertThat(exception.getErrorCode()).isEqualTo(errorCode);
            assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
            assertThat(exception.getErrorCode().getCode()).isNotNull();
            assertThat(exception.getErrorCode().getCode()).isNotEmpty();
            assertThat(exception.getErrorCode().getCode()).startsWith("CART-DOMAIN-");
        }
    }

    @Test
    @DisplayName("ErrorCode 형식 검증")
    void ErrorCode_형식_검증() {
        // Given & When & Then
        for (CartErrorCode errorCode : CartErrorCode.values()) {
            String code = errorCode.getCode();
            String message = errorCode.getMessage();
            
            // 코드 형식: CART-DOMAIN-XXX
            assertThat(code).matches("CART-DOMAIN-\\d{3}");
            
            // 메시지는 null이 아니고 비어있지 않음
            assertThat(message).isNotNull();
            assertThat(message).isNotEmpty();
        }
    }
}