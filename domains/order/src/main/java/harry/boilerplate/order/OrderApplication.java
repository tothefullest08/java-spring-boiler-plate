package harry.boilerplate.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Order Context Spring Boot 애플리케이션
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "harry.boilerplate.order",
    "harry.boilerplate.common"
})
@EntityScan(basePackages = {
    "harry.boilerplate.order.command.domain",
    "harry.boilerplate.common.domain"
})
public class OrderApplication {
    
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "order");
        SpringApplication.run(OrderApplication.class, args);
    }
}
