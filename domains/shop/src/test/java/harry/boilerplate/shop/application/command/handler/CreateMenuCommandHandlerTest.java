package harry.boilerplate.shop.application.command.handler;

import harry.boilerplate.common.domain.entity.Money;
import harry.boilerplate.shop.command.application.dto.CreateMenuCommand;
import harry.boilerplate.shop.command.domain.aggregate.Menu;
import harry.boilerplate.shop.command.domain.aggregate.MenuRepository;
import harry.boilerplate.shop.command.domain.aggregate.Shop;
import harry.boilerplate.shop.command.domain.aggregate.ShopRepository;
import harry.boilerplate.shop.command.domain.exception.ShopDomainException;
import harry.boilerplate.shop.command.domain.exception.ShopErrorCode;
import harry.boilerplate.shop.command.domain.valueObject.ShopId;
import harry.boilerplate.shop.command.application.handler.CreateMenuCommandHandler;

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

import harry.boilerplate.shop.command.domain.valueObject.BusinessHours;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateMenuCommandHandler 테스트")
class CreateMenuCommandHandlerTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private ShopRepository shopRepository;

    @InjectMocks
    private CreateMenuCommandHandler createMenuCommandHandler;

    private Shop shop;
    private CreateMenuCommand command;

    private BusinessHours createDefaultBusinessHours() {
        Map<DayOfWeek, LocalTime[]> weeklyHours = Map.of(
            DayOfWeek.MONDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)},
            DayOfWeek.TUESDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)},
            DayOfWeek.WEDNESDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)},
            DayOfWeek.THURSDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)},
            DayOfWeek.FRIDAY, new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(22, 0)}
        );
        return new BusinessHours(weeklyHours);
    }

    @BeforeEach
    void setUp() {
        shop = new Shop("맛있는 가게", Money.of(new BigDecimal("15000")), createDefaultBusinessHours());
        command = new CreateMenuCommand(
            shop.getId().getValue(),
            "삼겹살",
            "맛있는 삼겹살",
            new BigDecimal("15000")
        );
    }

    @Test
    @DisplayName("메뉴 생성 성공")
    void 메뉴_생성_성공() {
        // Given
        when(shopRepository.existsById(any(ShopId.class))).thenReturn(true);

        // When
        String menuId = createMenuCommandHandler.handle(command);

        // Then
        assertThat(menuId).isNotNull();
        verify(shopRepository).existsById(any(ShopId.class));
        verify(menuRepository).save(any(Menu.class));
    }

    @Test
    @DisplayName("존재하지 않는 가게로 메뉴 생성 시 예외 발생")
    void 존재하지_않는_가게로_메뉴_생성_시_예외_발생() {
        // Given
        when(shopRepository.existsById(any(ShopId.class))).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> createMenuCommandHandler.handle(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 가게입니다");

        verify(shopRepository).existsById(any(ShopId.class));
        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @DisplayName("null 명령어로 메뉴 생성 시 예외 발생")
    void null_명령어로_메뉴_생성_시_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> createMenuCommandHandler.handle(null))
                .isInstanceOf(NullPointerException.class);

        verify(shopRepository, never()).existsById(any(ShopId.class));
        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @DisplayName("메뉴 저장 중 예외 발생 시 전파")
    void 메뉴_저장_중_예외_발생_시_전파() {
        // Given
        when(shopRepository.existsById(any(ShopId.class))).thenReturn(true);
        doThrow(new RuntimeException("데이터베이스 오류")).when(menuRepository).save(any(Menu.class));

        // When & Then
        assertThatThrownBy(() -> createMenuCommandHandler.handle(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("데이터베이스 오류");

        verify(shopRepository).existsById(any(ShopId.class));
        verify(menuRepository).save(any(Menu.class));
    }

    @Test
    @DisplayName("생성된 메뉴의 속성 검증")
    void 생성된_메뉴의_속성_검증() {
        // Given
        when(shopRepository.existsById(any(ShopId.class))).thenReturn(true);

        // When
        createMenuCommandHandler.handle(command);

        // Then
        verify(menuRepository).save(argThat(menu -> {
            assertThat(menu.getShopId()).isEqualTo(shop.getId());
            assertThat(menu.getName()).isEqualTo("삼겹살");
            assertThat(menu.getDescription()).isEqualTo("맛있는 삼겹살");
            assertThat(menu.getBasePrice()).isEqualTo(Money.of(new BigDecimal("15000")));
            assertThat(menu.isOpen()).isFalse();
            return true;
        }));
    }
}