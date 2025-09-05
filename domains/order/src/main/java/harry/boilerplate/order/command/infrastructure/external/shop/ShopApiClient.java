package harry.boilerplate.order.command.infrastructure.external.shop;

import java.math.BigDecimal;
import java.util.List;

/**
 * Shop Context API Client 인터페이스
 * Requirements: 8.1, 8.2, 8.3, 5.3
 */
public interface ShopApiClient {

    /**
     * 가게 영업 상태 확인
     * @param shopId 가게 ID
     * @return 영업 중 여부
     */
    boolean isShopOpen(String shopId);

    /**
     * 메뉴 기본 정보 조회 (이름, 기본가 등)
     * @param shopId 가게 ID
     * @param menuId 메뉴 ID
     * @return 메뉴 정보
     */
    MenuInfoResponse getMenu(String shopId, String menuId);

    /**
     * 메뉴의 옵션 목록 조회
     * @param shopId 가게 ID
     * @param menuId 메뉴 ID
     * @return 옵션 정보 목록
     */
    List<OptionInfoResponse> getMenuOptions(String shopId, String menuId);

    /**
     * 메뉴 정보 응답
     */
    class MenuInfoResponse {
        private final String id;
        private final String name;
        private final String description;
        private final BigDecimal basePrice;
        private final boolean open;

        public MenuInfoResponse(String id, String name, String description, BigDecimal basePrice, boolean open) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.basePrice = basePrice;
            this.open = open;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public BigDecimal getBasePrice() { return basePrice; }
        public boolean isOpen() { return open; }
    }

    /**
     * 옵션 정보 응답
     */
    class OptionInfoResponse {
        private final String name;
        private final BigDecimal price;

        public OptionInfoResponse(String name, BigDecimal price) {
            this.name = name;
            this.price = price;
        }

        public String getName() { return name; }
        public BigDecimal getPrice() { return price; }
    }
}




