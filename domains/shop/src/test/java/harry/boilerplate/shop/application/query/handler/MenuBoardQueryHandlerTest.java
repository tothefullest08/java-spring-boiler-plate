package harry.boilerplate.shop.application.query.handler;

import harry.boilerplate.shop.query.application.dto.MenuBoardQuery;
import harry.boilerplate.shop.query.application.dto.MenuBoardResult;
import harry.boilerplate.shop.query.application.handler.MenuBoardQueryHandler;
import harry.boilerplate.shop.query.application.readModel.MenuBoardViewModel;
import harry.boilerplate.shop.query.application.readModel.MenuSummaryReadModel;
import harry.boilerplate.shop.query.infrastructure.dao.MenuQueryDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MenuBoardQueryHandler 테스트")
class MenuBoardQueryHandlerTest {

    @Mock
    private MenuQueryDao menuQueryDao;

    @InjectMocks
    private MenuBoardQueryHandler menuBoardQueryHandler;

    private MenuBoardQuery query;
    private MenuBoardViewModel viewModel;

    @BeforeEach
    void setUp() {
        query = new MenuBoardQuery("shop-1");

        MenuSummaryReadModel menu1 = new MenuSummaryReadModel(
            "menu-1", "shop-1", "삼겹살", "맛있는 삼겹살", new BigDecimal("15000"), true, 2
        );
        MenuSummaryReadModel menu2 = new MenuSummaryReadModel(
            "menu-2", "shop-1", "냉면", "시원한 냉면", new BigDecimal("8000"), true, 1
        );

        viewModel = new MenuBoardViewModel(
            "shop-1", "맛있는 가게", true, 
            Arrays.asList(menu1, menu2), // 열린 메뉴들
            Arrays.asList() // 닫힌 메뉴들
        );
    }

    @Test
    @DisplayName("메뉴보드 조회 성공")
    void 메뉴보드_조회_성공() {
        // Given
        when(menuQueryDao.getMenuBoard(anyString())).thenReturn(viewModel);

        // When
        MenuBoardResult result = menuBoardQueryHandler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getShopId()).isEqualTo("shop-1");
        assertThat(result.getShopName()).isEqualTo("맛있는 가게");
        assertThat(result.isShopOpen()).isTrue();
        assertThat(result.getMenus()).hasSize(2);

        assertThat(result.getMenus().get(0).getName()).isEqualTo("삼겹살");
        assertThat(result.getMenus().get(1).getName()).isEqualTo("냉면");

        verify(menuQueryDao).getMenuBoard("shop-1");
    }

    @Test
    @DisplayName("메뉴가 없는 가게의 메뉴보드 조회")
    void 메뉴가_없는_가게의_메뉴보드_조회() {
        // Given
        MenuBoardViewModel emptyMenuViewModel = new MenuBoardViewModel(
            "shop-1", "메뉴 없는 가게", true,
            Arrays.asList(), // 열린 메뉴 없음
            Arrays.asList()  // 닫힌 메뉴 없음
        );
        when(menuQueryDao.getMenuBoard(anyString())).thenReturn(emptyMenuViewModel);

        // When
        MenuBoardResult result = menuBoardQueryHandler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getShopId()).isEqualTo("shop-1");
        assertThat(result.getShopName()).isEqualTo("메뉴 없는 가게");
        assertThat(result.isShopOpen()).isTrue();
        assertThat(result.getMenus()).isEmpty();

        verify(menuQueryDao).getMenuBoard("shop-1");
    }

    @Test
    @DisplayName("존재하지 않는 가게 조회 시 null 반환")
    void 존재하지_않는_가게_조회_시_null_반환() {
        // Given
        when(menuQueryDao.getMenuBoard(anyString())).thenReturn(null);

        // When
        MenuBoardResult result = menuBoardQueryHandler.handle(query);

        // Then
        assertThat(result).isNull();

        verify(menuQueryDao).getMenuBoard("shop-1");
    }

    @Test
    @DisplayName("null query로 조회 시 예외 발생")
    void null_query로_조회_시_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> menuBoardQueryHandler.handle(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("MenuBoardQuery는 필수입니다");

        verify(menuQueryDao, never()).getMenuBoard(anyString());
    }

    @Test
    @DisplayName("DAO 조회 중 예외 발생 시 전파")
    void DAO_조회_중_예외_발생_시_전파() {
        // Given
        when(menuQueryDao.getMenuBoard(anyString())).thenThrow(new RuntimeException("데이터베이스 오류"));

        // When & Then
        assertThatThrownBy(() -> menuBoardQueryHandler.handle(query))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("데이터베이스 오류");

        verify(menuQueryDao).getMenuBoard("shop-1");
    }

    @Test
    @DisplayName("영업 중이 아닌 가게의 메뉴보드 조회")
    void 영업_중이_아닌_가게의_메뉴보드_조회() {
        // Given
        MenuSummaryReadModel closedMenu = new MenuSummaryReadModel(
            "menu-1", "shop-1", "삼겹살", "맛있는 삼겹살", new BigDecimal("15000"), false, 2
        );
        
        MenuBoardViewModel closedShopViewModel = new MenuBoardViewModel(
            "shop-1", "문 닫은 가게", false,
            Arrays.asList(), // 열린 메뉴 없음
            Arrays.asList(closedMenu) // 닫힌 메뉴만 있음
        );
        when(menuQueryDao.getMenuBoard(anyString())).thenReturn(closedShopViewModel);

        // When
        MenuBoardResult result = menuBoardQueryHandler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getShopId()).isEqualTo("shop-1");
        assertThat(result.getShopName()).isEqualTo("문 닫은 가게");
        assertThat(result.isShopOpen()).isFalse();
        assertThat(result.getMenus()).isEmpty(); // 영업 중이 아니므로 메뉴는 표시되지 않음

        verify(menuQueryDao).getMenuBoard("shop-1");
    }

    @Test
    @DisplayName("MenuBoardViewModel에서 MenuBoardResult로 정확한 매핑")
    void MenuBoardViewModel에서_MenuBoardResult로_정확한_매핑() {
        // Given
        MenuSummaryReadModel openMenu = new MenuSummaryReadModel(
            "menu-1", "shop-1", "삼겹살", "맛있는 삼겹살", new BigDecimal("15000"), true, 2
        );
        MenuSummaryReadModel closedMenu = new MenuSummaryReadModel(
            "menu-2", "shop-1", "냉면", "시원한 냉면", new BigDecimal("8000"), false, 1
        );
        
        MenuBoardViewModel complexViewModel = new MenuBoardViewModel(
            "shop-1", "복합 메뉴 가게", true,
            Arrays.asList(openMenu), // 열린 메뉴 1개
            Arrays.asList(closedMenu) // 닫힌 메뉴 1개
        );
        when(menuQueryDao.getMenuBoard(anyString())).thenReturn(complexViewModel);

        // When
        MenuBoardResult result = menuBoardQueryHandler.handle(query);

        // Then
        // 원본 ViewModel과 결과 Result의 모든 필드가 정확히 매핑되었는지 확인
        assertThat(result.getShopId()).isEqualTo(complexViewModel.getShopId());
        assertThat(result.getShopName()).isEqualTo(complexViewModel.getShopName());
        assertThat(result.isShopOpen()).isEqualTo(complexViewModel.isShopOpen());
        assertThat(result.getMenus()).hasSize(1); // 열린 메뉴만 표시됨
    }
}