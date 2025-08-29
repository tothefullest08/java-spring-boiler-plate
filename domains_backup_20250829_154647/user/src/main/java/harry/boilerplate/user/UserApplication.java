package harry.boilerplate.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * User Context Spring Boot 애플리케이션
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "harry.boilerplate.user",
    "harry.boilerplate.common"
})
@EntityScan(basePackages = {
    "harry.boilerplate.user.domain",
    "harry.boilerplate.common.domain"
})
public class UserApplication {
    
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "user");
        SpringApplication.run(UserApplication.class, args);
    }
}