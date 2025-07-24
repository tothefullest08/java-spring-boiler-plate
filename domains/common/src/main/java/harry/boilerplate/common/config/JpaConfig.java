package harry.boilerplate.common.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA 공통 설정 클래스
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "harry.boilerplate")
@EntityScan(basePackages = "harry.boilerplate")
public class JpaConfig {
    // JPA 관련 공통 설정은 여기에 추가
}