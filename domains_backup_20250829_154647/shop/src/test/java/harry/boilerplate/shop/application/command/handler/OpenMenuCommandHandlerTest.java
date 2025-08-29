package harry.boilerplate.shop.application.command.handler;

import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.shop.application.command.dto.OpenMenuCommand;
import harry.boilerplate.shop.domain.aggregate.Menu;
import harry.boilerplate.shop.domain.aggregate.MenuRepository;
import harry.boilerplate.shop.domain.exception.MenuDomainException;
import harry.boilerplate.shop.domain.exception.MenuErrorCode;
import harry.boilerplate.shop.domain.valueObject.MenuId;
import harry.boilerplate.shop.domain.valueObject.ShopId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OpenMenuCommandHandler 테스트")
class OpenMenuCommandHandlerTest {

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private OpenMenuCommandHandler openMenuCommandHandler;

    private Menu menu;
    private OpenMenuCommand command;

    @BeforeEach
    void setUp() {
        ShopId shopId = ShopId.generate();
        menu = new Menu(shopId, "삼겹살", "맛있는 삼겹살", Money.of(new BigDecimal("15000")));
        
        // 메뉴 공개 조건을 만족하도록 설정
        menu.addOptionGroup("양 선택", true);
        menu.getOptionGroups().get(0).addOption(
            new harry.boilerplate.shop.domain.valueObject.Option("곱빼기", Money.of(new BigDecimal("2000")))
        );
        
        command = new OpenMenuCommand(menu.getId().getValue());
    }

    @Test
    @DisplayName("메뉴 공개 성공")
    void 메뉴_공개_성공() {
        // Given
        when(menuRepository.findById(any(MenuId.class))).thenReturn(menu);

        // When
        openMenuCommandHandler.handle(command);

        // Then
        assertThat(menu.isOpen()).isTrue();
        verify(menuRepository).findById(any(MenuId.class));
        verify(menuRepository).save(menu);
    }

    @Test
    @DisplayName("존재하지 않는 메뉴 공개 시 예외 발생")
    void 존재하지_않는_메뉴_공개_시_예외_발생() {
        // Given
        when(menuRepository.findById(any(MenuId.class))).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> openMenuCommandHandler.handle(command))
                .isInstanceOf(NullPointerException.class);

        verify(menuRepository).findById(any(MenuId.class));
        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @DisplayName("이미 공개된 메뉴 재공개 시 예외 발생")
    void 이미_공개된_메뉴_재공개_시_예외_발생() {
        // Given
        menu.open(); // 미리 공개 상태로 설정
        when(menuRepository.findById(any(MenuId.class))).thenReturn(menu);

        // When & Then
        assertThatThrownBy(() -> openMenuCommandHandler.handle(command))
                .isInstanceOf(MenuDomainException.class)
                .extracting(e -> ((MenuDomainException) e).getErrorCode())
                .isEqualTo(MenuErrorCode.MENU_ALREADY_OPEN);

        verify(menuRepository).findById(any(MenuId.class));
        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @DisplayName("공개 조건을 만족하지 않는 메뉴 공개 시 예외 발생")
    void 공개_조건을_만족하지_않는_메뉴_공개_시_예외_발생() {
        // Given
        ShopId shopId = ShopId.generate();
        Menu emptyMenu = new Menu(shopId, "빈메뉴", "설명", Money.of(new BigDecimal("10000")));
        OpenMenuCommand emptyMenuCommand = new OpenMenuCommand(emptyMenu.getId().getValue());
        
        when(menuRepository.findById(any(MenuId.class))).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> openMenuCommandHandler.handle(emptyMenuCommand))
                .isInstanceOf(NullPointerException.class);

        verify(menuRepository).findById(any(MenuId.class));
        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @DisplayName("null 명령어로 메뉴 공개 시 예외 발생")
    void null_명령어로_메뉴_공개_시_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> openMenuCommandHandler.handle(null))
                .isInstanceOf(NullPointerException.class);

        verify(menuRepository, never()).findById(any(MenuId.class));
        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @DisplayName("메뉴 저장 중 예외 발생 시 전파")
    void 메뉴_저장_중_예외_발생_시_전파() {
        // Given
        when(menuRepository.findById(any(MenuId.class))).thenReturn(menu);
        doThrow(new RuntimeException("데이터베이스 오류")).when(menuRepository).save(any(Menu.class));

        // When & Then
        assertThatThrownBy(() -> openMenuCommandHandler.handle(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("데이터베이스 오류");

        verify(menuRepository).findById(any(MenuId.class));
        verify(menuRepository).save(menu);
    }

    @Test
    @DisplayName("메뉴 공개 후 도메인 이벤트 발행 확인")
    void 메뉴_공개_후_도메인_이벤트_발행_확인() {
        // Given
        when(menuRepository.findById(any(MenuId.class))).thenReturn(menu);

        // When
        openMenuCommandHandler.handle(command);

        // Then
        assertThat(menu.isOpen()).isTrue();
        assertThat(menu.hasDomainEvents()).isTrue();
        assertThat(menu.getDomainEvents()).hasSize(1);
        verify(menuRepository).save(menu);
    }
}