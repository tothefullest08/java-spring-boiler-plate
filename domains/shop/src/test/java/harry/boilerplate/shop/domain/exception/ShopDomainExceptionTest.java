package harry.boilerplate.shop.domain.exception;

import harry.boilerplate.shop.command.domain.exception.ShopDomainException;
import harry.boilerplate.shop.command.domain.exception.ShopErrorCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ShopDomainException 테스트")
class ShopDomainExceptionTest {

    @Test
    @DisplayName("ShopDomainException 생성 및 ErrorCode 확인")
    void ShopDomainException_생성_및_ErrorCode_확인() {
        // Given
        ShopErrorCode errorCode = ShopErrorCode.SHOP_NOT_FOUND;

        // When
        ShopDomainException exception = new ShopDomainException(errorCode);

        // Then
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("ShopDomainException 생성 시 원인 예외 포함")
    void ShopDomainException_생성_시_원인_예외_포함() {
        // Given
        ShopErrorCode errorCode = ShopErrorCode.SHOP_NOT_FOUND;
        RuntimeException cause = new RuntimeException("원인 예외");

        // When
        ShopDomainException exception = new ShopDomainException(errorCode, cause);

        // Then
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("모든 ShopErrorCode에 대한 예외 생성 테스트")
    void 모든_ShopErrorCode에_대한_예외_생성_테스트() {
        // Given & When & Then
        for (ShopErrorCode errorCode : ShopErrorCode.values()) {
            ShopDomainException exception = new ShopDomainException(errorCode);
            
            assertThat(exception.getErrorCode()).isEqualTo(errorCode);
            assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
            assertThat(exception.getErrorCode().getCode()).isNotNull();
            assertThat(exception.getErrorCode().getCode()).isNotEmpty();
        }
    }
}