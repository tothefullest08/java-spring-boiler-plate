package harry.boilerplate.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Shop Context Spring Boot 애플리케이션
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "harry.boilerplate.shop",
    "harry.boilerplate.common"
})
@EntityScan(basePackages = {
    "harry.boilerplate.shop.domain",
    "harry.boilerplate.common.domain"
})
public class ShopApplication {
    
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "shop");
        SpringApplication.run(ShopApplication.class, args);
    }
}