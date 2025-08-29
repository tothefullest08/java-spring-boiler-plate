package harry.boilerplate.shop.domain.exception;

import harry.boilerplate.shop.command.domain.exception.MenuDomainException;
import harry.boilerplate.shop.command.domain.exception.MenuErrorCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MenuDomainException 테스트")
class MenuDomainExceptionTest {

    @Test
    @DisplayName("MenuDomainException 생성 및 ErrorCode 확인")
    void MenuDomainException_생성_및_ErrorCode_확인() {
        // Given
        MenuErrorCode errorCode = MenuErrorCode.MENU_NOT_FOUND;

        // When
        MenuDomainException exception = new MenuDomainException(errorCode);

        // Then
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("MenuDomainException 생성 시 원인 예외 포함")
    void MenuDomainException_생성_시_원인_예외_포함() {
        // Given
        MenuErrorCode errorCode = MenuErrorCode.MENU_NOT_FOUND;
        RuntimeException cause = new RuntimeException("원인 예외");

        // When
        MenuDomainException exception = new MenuDomainException(errorCode, cause);

        // Then
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("모든 MenuErrorCode에 대한 예외 생성 테스트")
    void 모든_MenuErrorCode에_대한_예외_생성_테스트() {
        // Given & When & Then
        for (MenuErrorCode errorCode : MenuErrorCode.values()) {
            MenuDomainException exception = new MenuDomainException(errorCode);
            
            assertThat(exception.getErrorCode()).isEqualTo(errorCode);
            assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
            assertThat(exception.getErrorCode().getCode()).isNotNull();
            assertThat(exception.getErrorCode().getCode()).isNotEmpty();
            assertThat(exception.getErrorCode().getCode()).startsWith("MENU-DOMAIN-");
        }
    }

    @Test
    @DisplayName("ErrorCode 형식 검증")
    void ErrorCode_형식_검증() {
        // Given & When & Then
        for (MenuErrorCode errorCode : MenuErrorCode.values()) {
            String code = errorCode.getCode();
            String message = errorCode.getMessage();
            
            // 코드 형식: MENU-DOMAIN-XXX
            assertThat(code).matches("MENU-DOMAIN-\\d{3}");
            
            // 메시지는 null이 아니고 비어있지 않음
            assertThat(message).isNotNull();
            assertThat(message).isNotEmpty();
        }
    }
}