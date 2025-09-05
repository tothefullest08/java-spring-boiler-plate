package harry.boilerplate.order.command.infrastructure.external.shop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.List;

/**
 * Shop Context API Client 구현체 (RestTemplate 기반)
 */
@Component
public class ShopApiClientImpl implements ShopApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ShopApiClientImpl(RestTemplate restTemplate,
                             @Value("${shop.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public boolean isShopOpen(String shopId) {
        String url = String.format("%s/api/shops/%s", baseUrl, shopId);

        ResponseEntity<ShopEnvelope> response;
        int attempts = 0;
        while (true) {
            try {
                response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ShopEnvelope>() {}
                );
                break;
            } catch (RestClientException e) {
                if (++attempts >= 2) {
                    throw e;
                }
                try { Thread.sleep(200); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            }
        }

        ShopEnvelope body = response.getBody();
        if (body == null || body.getShop() == null) {
            return false;
        }
        Object open = body.getShop().getOpen();
        return parseBoolean(open);
    }

    @Override
    public MenuInfoResponse getMenu(String shopId, String menuId) {
        String url = String.format("%s/api/shops/%s/menus/%s", baseUrl, shopId, menuId);

        ResponseEntity<MenuEnvelope> response;
        int attempts = 0;
        while (true) {
            try {
                response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<MenuEnvelope>() {}
                );
                break;
            } catch (RestClientException e) {
                if (++attempts >= 2) {
                    throw e;
                }
                try { Thread.sleep(200); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            }
        }

        MenuEnvelope body = response.getBody();
        if (body == null || body.getMenu() == null) {
            return null;
        }

        MenuDto menu = body.getMenu();
        BigDecimal basePrice = toBigDecimal(menu.getBasePrice());
        boolean open = parseBoolean(menu.getOpen());

        return new MenuInfoResponse(menu.getId(), menu.getName(), menu.getDescription(), basePrice, open);
    }

    @Override
    public List<OptionInfoResponse> getMenuOptions(String shopId, String menuId) {
        String url = String.format("%s/api/shops/%s/menus/%s/options", baseUrl, shopId, menuId);

        ResponseEntity<MenuEnvelope> response;
        int attempts = 0;
        while (true) {
            try {
                response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<MenuEnvelope>() {}
                );
                break;
            } catch (RestClientException e) {
                if (++attempts >= 2) {
                    throw e;
                }
                try { Thread.sleep(200); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            }
        }

        MenuEnvelope body = response.getBody();
        if (body == null || body.getMenu() == null) {
            return List.of();
        }

        MenuDto menu = body.getMenu();
        List<OptionGroupDto> groups = menu.getOptionGroups();
        if (groups == null) return List.of();

        return groups.stream()
            .filter(g -> g.getOptions() != null)
            .flatMap(g -> g.getOptions().stream())
            .map(opt -> new OptionInfoResponse(opt.getName(), toBigDecimal(opt.getPrice())))
            .toList();
    }

    private boolean parseBoolean(Object value) {
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof String) return Boolean.parseBoolean((String) value);
        return false;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Number) return BigDecimal.valueOf(((Number) value).doubleValue());
        return new BigDecimal(value.toString());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @SuppressWarnings("unused")
    private static class ShopEnvelope {
        private ShopDto shop;

        public ShopDto getShop() { return shop; }
        public void setShop(ShopDto shop) { this.shop = shop; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @SuppressWarnings("unused")
    private static class ShopDto {
        private Object open;

        public Object getOpen() { return open; }
        public void setOpen(Object open) { this.open = open; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @SuppressWarnings("unused")
    private static class MenuEnvelope {
        private MenuDto menu;

        public MenuDto getMenu() { return menu; }
        public void setMenu(MenuDto menu) { this.menu = menu; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @SuppressWarnings("unused")
    private static class MenuDto {
        private String id;
        private String name;
        private String description;
        private Object basePrice;
        private Object open;
        private List<OptionGroupDto> optionGroups;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Object getBasePrice() { return basePrice; }
        public void setBasePrice(Object basePrice) { this.basePrice = basePrice; }
        public Object getOpen() { return open; }
        public void setOpen(Object open) { this.open = open; }
        public List<OptionGroupDto> getOptionGroups() { return optionGroups; }
        public void setOptionGroups(List<OptionGroupDto> optionGroups) { this.optionGroups = optionGroups; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @SuppressWarnings("unused")
    private static class OptionGroupDto {
        private List<OptionDto> options;

        public List<OptionDto> getOptions() { return options; }
        public void setOptions(List<OptionDto> options) { this.options = options; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @SuppressWarnings("unused")
    private static class OptionDto {
        private String name;
        private Object price;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Object getPrice() { return price; }
        public void setPrice(Object price) { this.price = price; }
    }
}


