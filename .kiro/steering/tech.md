# 기술 스택 및 CQRS 개발 규칙

## 필수 기술 스택
- **Java 21** - 프로젝트 필수 요구사항
- **Gradle** - 멀티 프로젝트 빌드 도구
- **Spring Boot 3.5.3** - 각 컨텍스트별 독립 애플리케이션 프레임워크
- **Spring Data JPA** - Command/Query 영속성 계층
- **Hibernate** - JPA 구현체 (커스텀 타입 지원)
- **MySQL 8.0** - Docker 컨테이너로 실행되는 메인 데이터베이스
- **Docker & Docker Compose** - 데이터베이스 컨테이너 관리

## 멀티 프로젝트 빌드 설정

### 루트 프로젝트 설정
```gradle
// settings.gradle
rootProject.name = 'java-spring-boiler-plate'
include 'domains:common'
include 'domains:shop'
include 'domains:order' 
include 'domains:user'
```

### Common 모듈 빌드 설정
```gradle
// domains/common/build.gradle
plugins {
    id 'java-library'
}

dependencies {
    api 'org.springframework.boot:spring-boot-starter-data-jpa'
    api 'org.springframework.security:spring-security-crypto'
    api 'io.swagger.core.v3:swagger-annotations:2.2.0'
    // 공통 라이브러리만 포함
}
```

### 개별 컨텍스트 빌드
```gradle
// domains/shop/build.gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.5.3'
}

dependencies {
    implementation project(':domains:common')  // Common 모듈만 의존
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    // 다른 바운디드 컨텍스트 의존성 금지
}
```

### 실행 포트 분리
```yaml
# domains/shop/src/main/resources/application-shop.yml
server:
  port: 8081

# domains/order/src/main/resources/application-order.yml  
server:
  port: 8082

# domains/user/src/main/resources/application-user.yml
server:
  port: 8083
```

## CQRS 아키텍처 레이어

### Presentation Layer
- **Command Controller**: POST, PUT, DELETE (쓰기 작업)
- **Query Controller**: GET (읽기 작업)

### Application Layer
- **Command Handler/Service**: 쓰기 작업 처리
- **Query Handler/Service**: 읽기 작업 처리

### Domain Layer
- 도메인 모델, 비즈니스 규칙, 도메인 서비스
- 인프라스트럭처에 대한 의존성 없음

### Infrastructure Layer
- **Command Repository**: 쓰기 최적화 (Repository 패턴)
- **Query DAO**: 읽기 최적화 (DAO 패턴)

## 주요 명령어

### Gradle 의존성 추가
```gradle
// 각 컨텍스트의 build.gradle에 MySQL 의존성 추가
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'mysql:mysql-connector-java:8.0.33'
    // 기타 의존성...
}
```

### UUID 기반 ID 설정
```java
// ValueObject ID 클래스 예시
public class ShopId extends ValueObject {
    private final String value;
    
    public ShopId() {
        this.value = UUID.randomUUID().toString();
    }
    
    public ShopId(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}

// JPA Entity에서 UUID 사용 예시
@Entity
@Table(name = "shop")
public class Shop extends AggregateRoot<Shop, ShopId> {
    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;
    
    @Column(name = "name")
    private String name;
    
    // 생성자에서 UUID 자동 생성
    public Shop() {
        this.id = UUID.randomUUID().toString();
    }
    
    @Override
    public ShopId getId() {
        return new ShopId(this.id);
    }
}
```

### Docker 및 애플리케이션 실행
```bash
# Docker 컨테이너 시작 (MySQL 데이터베이스)
docker-compose up -d

# Docker 컨테이너 상태 확인
docker-compose ps

# MySQL 접속 확인
docker exec -it food-delivery-mysql mysql -u food_user -p food_delivery_db

# 테이블 생성 확인
# MySQL> SHOW TABLES;
# MySQL> DESCRIBE shop;

# 전체 프로젝트 빌드
./gradlew build

# 특정 컨텍스트 빌드
./gradlew :domains:shop:build

# 특정 컨텍스트 실행 (MySQL 실행 후)
./gradlew :domains:shop:bootRun
./gradlew :domains:order:bootRun
./gradlew :domains:user:bootRun

# Docker 컨테이너 중지
docker-compose down

# Docker 컨테이너 및 볼륨 완전 삭제 (데이터 초기화)
docker-compose down -v
```

### Docker Compose 데이터베이스 설정
```yaml
# docker-compose.yml (프로젝트 루트)
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    container_name: food-delivery-mysql
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: food_delivery_db
      MYSQL_USER: food_user
      MYSQL_PASSWORD: food_password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./docker/mysql/init.sql:/docker-entrypoint-initdb.d/init.sql
    command: --default-authentication-plugin=mysql_native_password

volumes:
  mysql_data:
```

### 애플리케이션 데이터베이스 설정
```yaml
# domains/shop/src/main/resources/application-shop.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/food_delivery_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul
    username: food_user
    password: food_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
    show-sql: true

# domains/order/src/main/resources/application-order.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/food_delivery_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul
    username: food_user
    password: food_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
    show-sql: true

# domains/user/src/main/resources/application-user.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/food_delivery_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul
    username: food_user
    password: food_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
    show-sql: true
```

### 테이블 스키마 정의
```sql
-- docker/mysql/init.sql
-- Shop Context 테이블들
CREATE TABLE shop (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(255) NOT NULL,
    min_order_amount DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE menu (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    shop_id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    base_price DECIMAL(10,2),
    is_open BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (shop_id) REFERENCES shop(id)
);

CREATE TABLE option_group (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    menu_id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    is_required BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (menu_id) REFERENCES menu(id)
);

CREATE TABLE option (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    option_group_id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (option_group_id) REFERENCES option_group(id)
);

-- Order Context 테이블들
CREATE TABLE cart (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id VARCHAR(36) NOT NULL,
    shop_id VARCHAR(36),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cart_line_item (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    cart_id VARCHAR(36) NOT NULL,
    menu_id VARCHAR(36) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cart_id) REFERENCES cart(id)
);

CREATE TABLE order_table (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id VARCHAR(36) NOT NULL,
    shop_id VARCHAR(36) NOT NULL,
    total_price DECIMAL(10,2),
    order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_line_item (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    order_id VARCHAR(36) NOT NULL,
    menu_id VARCHAR(36) NOT NULL,
    menu_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    line_price DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES order_table(id)
);

-- User Context 테이블들
CREATE TABLE user (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 공통 아키텍처 패턴

### Common 패키지 구조 (필수)
```
domains/common/src/main/java/harry/boilerplate/common/
├── domain/
│   ├── entity/                    # 엔티티 관련 아키텍처 패턴
│   │   ├── AggregateRoot.java    # 애그리게이트 루트 기본 클래스
│   │   ├── BaseEntity.java       # JPA 엔티티 공통 필드
│   │   ├── ValueObject.java      # 값 객체 기본 클래스
│   │   ├── EntityId.java         # 엔티티 ID 기본 클래스
│   │   └── Money.java            # 금액 값 객체
│   └── event/                     # 도메인 이벤트 패턴
│       └── DomainEvent.java      # 도메인 이벤트 인터페이스
├── exception/                     # 공통 예외 처리 패턴
├── response/                      # 공통 응답 패턴
└── config/                        # 공통 설정
```

### DDD 분류 및 상속 구조 패턴 (필수)
```java
// ✅ Common 모듈: DomainEntity 추가
public abstract class DomainEntity<T extends DomainEntity<T, TID>, TID> extends BaseEntity {
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        return equals((T) other);
    }
    
    public boolean equals(T other) {
        if (other == null || getId() == null) return false;
        if (other.getClass().equals(getClass())) {
            return getId().equals(other.getId());
        }
        return super.equals(other);
    }
    
    @Override
    public int hashCode() {
        return getId() == null ? 0 : getId().hashCode();
    }
    
    public abstract TID getId();
}

// ✅ 애그리게이트 루트: AggregateRoot 상속
@Entity
@Table(name = "menu")
public class Menu extends AggregateRoot<Menu, MenuId> {
    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<OptionGroup> optionGroups = new ArrayList<>();
    
    public void addOptionGroup(String name, boolean required) {
        OptionGroup optionGroup = new OptionGroup(this, OptionGroupId.generate(), name, required);
        this.optionGroups.add(optionGroup);
    }
}

// ✅ 도메인 엔티티: DomainEntity 상속
@Entity
@Table(name = "option_group")
public class OptionGroup extends DomainEntity<OptionGroup, OptionGroupId> {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "option_group_options")
    private List<Option> options = new ArrayList<>();
    
    public void addOption(Option option) {
        this.options.add(option);
    }
}

// ✅ 값 객체: ValueObject 상속 (불변)
@Embeddable
public class Option extends ValueObject {
    @Column(name = "option_name")
    private String name;
    
    @Column(name = "option_price")
    private BigDecimal price;
    
    // 불변 객체 - 변경 시 새 인스턴스 반환
    public Option changeName(String newName) {
        return new Option(newName, Money.of(this.price));
    }
}
```

### Domain Event Pattern (필수)

#### 바운디드 컨텍스트 격리 원칙
- **Common 패키지**: 아키텍처 패턴(인터페이스)만 제공
- **각 Context**: 구체적인 비즈니스 도메인 이벤트 구현

```java
// ✅ Common 패키지: 아키텍처 패턴만
// domains/common/src/main/java/harry/boilerplate/common/domain/event/DomainEvent.java
public interface DomainEvent {
    UUID getEventId();
    Instant getOccurredAt();
    String getAggregateId();
    String getAggregateType();
    int getVersion();
}

// ✅ Order Context: 비즈니스 도메인 이벤트
// domains/order/src/main/java/harry/boilerplate/order/domain/event/OrderPlacedEvent.java
public class OrderPlacedEvent implements DomainEvent {
    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final String aggregateId;
    private final String aggregateType = "Order";
    private final int version = 1;
    
    // 비즈니스 데이터
    private final String userId;
    private final String shopId;
    private final BigDecimal totalAmount;
    
    public OrderPlacedEvent(String orderId, String userId, String shopId, BigDecimal totalAmount) {
        this.aggregateId = orderId;
        this.userId = userId;
        this.shopId = shopId;
        this.totalAmount = totalAmount;
    }
    
    // getters...
}

// ✅ Shop Context: 비즈니스 도메인 이벤트
// domains/shop/src/main/java/harry/boilerplate/shop/domain/event/MenuOpenedEvent.java
public class MenuOpenedEvent implements DomainEvent {
    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final String aggregateId;
    private final String aggregateType = "Menu";
    private final int version = 1;
    
    // 비즈니스 데이터
    private final String shopId;
    private final String menuName;
    
    public MenuOpenedEvent(String menuId, String shopId, String menuName) {
        this.aggregateId = menuId;
        this.shopId = shopId;
        this.menuName = menuName;
    }
    
    // getters...
}
```

#### 도메인 이벤트 사용 규칙 (필수)
```java
// ✅ 애그리게이트에서 이벤트 발행
public class Order extends AggregateRoot<Order, OrderId> {
    public void place() {
        // 비즈니스 로직 수행
        this.status = OrderStatus.PLACED;
        this.orderTime = LocalDateTime.now();
        
        // 도메인 이벤트 발행
        addDomainEvent(new OrderPlacedEvent(
            this.getId().getValue(),
            this.userId.getValue(),
            this.shopId.getValue(),
            this.totalPrice.getAmount()
        ));
    }
}

// ✅ 이벤트 핸들러에서 처리
@EventHandler
public class OrderPlacedEventHandler {
    public void handle(OrderPlacedEvent event) {
        // 다른 바운디드 컨텍스트에 알림
        // 예: 재고 차감, 알림 발송 등
    }
}
```

### Error Handling Pattern (필수)
```java
// ErrorCode 인터페이스
public interface ErrorCode {
    /**
     * 에러 코드 형식: {DOMAIN}-{LAYER}-{CODE}
     * 예시: USER-DOMAIN-001, SHOP-APP-002
     */
    String getCode();
    String getMessage();
}

// 공통 시스템 에러 코드
public enum CommonSystemErrorCode implements ErrorCode {
    INTERNAL_SERVER_ERROR("COMMON-SYSTEM-001", "Internal server error"),
    INVALID_REQUEST("COMMON-SYSTEM-002", "Invalid request"),
    RESOURCE_NOT_FOUND("COMMON-SYSTEM-003", "Resource not found"),
    UNAUTHORIZED("COMMON-SYSTEM-004", "Unauthorized"),
    FORBIDDEN("COMMON-SYSTEM-005", "Forbidden"),
    VALIDATION_ERROR("COMMON-SYSTEM-006", "Validation error"),
    CONFLICT("COMMON-SYSTEM-007", "Conflict"),
    OPTIMISTIC_LOCK_ERROR("COMMON-SYSTEM-010", "Optimistic lock error");
    
    private final String code;
    private final String message;
    
    CommonSystemErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    @Override
    public String getCode() { return code; }
    
    @Override
    public String getMessage() { return message; }
}

// 도메인 예외 기본 클래스
public abstract class DomainException extends RuntimeException {
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public abstract ErrorCode getErrorCode();
}

// 애플리케이션 예외 기본 클래스
public abstract class ApplicationException extends RuntimeException {
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public abstract ErrorCode getErrorCode();
}
```

### Base Entity Pattern (필수)
```java
// domains/common/src/main/java/harry/boilerplate/common/domain/entity/BaseEntity.java
// JPA 엔티티 공통 필드
@MappedSuperclass
public abstract class BaseEntity {
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
    
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
```

### Command Result Response Pattern (필수)
```java
@Schema(description = "Command 요청 결과 응답")
public class CommandResultResponse {
    @Schema(description = "처리 상태", example = "SUCCESS")
    private final String status;
    
    @Schema(description = "결과 메시지", example = "요청이 성공적으로 처리되었습니다.")
    private final String message;
    
    @Schema(description = "생성/수정된 리소스의 ID", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef", nullable = true)
    private final String resourceId;
    
    public CommandResultResponse(String status, String message, String resourceId) {
        this.status = status;
        this.message = message;
        this.resourceId = resourceId;
    }
    
    // 성공 응답 팩토리 메서드
    public static CommandResultResponse success(String message, String resourceId) {
        return new CommandResultResponse("SUCCESS", message, resourceId);
    }
    
    public static CommandResultResponse success(String message) {
        return new CommandResultResponse("SUCCESS", message, null);
    }
    
    // getters...
}
```

### Configuration Pattern (필수)
```java
// 공통 설정
@Configuration
public class CommonConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
```

## CQRS 성능 최적화

### Command 측면 최적화
```java
@Transactional
public class CreateMenuCommandHandler {
    public void handle(CreateMenuCommand command) {
        // 쓰기 트랜잭션 최적화
        Menu menu = new Menu(command.getShopId(), command.getName());
        menuRepository.save(menu);
    }
}
```

### Query 측면 최적화
```java
@Transactional(readOnly = true)
public class MenuQueryDaoImpl implements MenuQueryDao {
    public List<MenuSummaryReadModel> findMenuSummaries(Long shopId) {
        return entityManager.createQuery(
            "SELECT m FROM Menu m WHERE m.shopId = :shopId", Menu.class)
            .setParameter("shopId", shopId)
            .setHint(QueryHints.READ_ONLY, true)
            .setHint(QueryHints.CACHEABLE, true)
            .getResultList();
    }
}
```

## 예외 처리 규칙

### Command 예외 처리
```java
@Transactional
public class CreateMenuCommandHandler {
    public void handle(CreateMenuCommand command) {
        try {
            Menu menu = new Menu(command.getShopId(), command.getName());
            menuRepository.save(menu);
        } catch (DomainException e) {
            throw new IllegalArgumentException("메뉴 생성 실패: " + e.getMessage());
        }
    }
}
```

### Query 예외 처리
```java
@Transactional(readOnly = true)
public class MenuBoardQueryHandler {
    public MenuBoardResult handle(MenuBoardQuery query) {
        try {
            MenuBoardViewModel viewModel = menuQueryDao.getMenuBoard(query.getShopId());
            return MenuBoardResult.from(viewModel);
        } catch (EntityNotFoundException e) {
            return MenuBoardResult.empty();
        }
    }
}
```

## CQRS 테스트 전략

### Command 테스트
```java
@Test
void 메뉴_생성_성공() {
    // Given
    CreateMenuCommand command = new CreateMenuCommand(shopId, "삼겹살", "맛있는 삼겹살");
    
    // When
    createMenuCommandHandler.handle(command);
    
    // Then
    verify(menuRepository).save(any(Menu.class));
}
```

### Query 테스트
```java
@Test
void 메뉴보드_조회_성공() {
    // Given
    MenuBoardQuery query = new MenuBoardQuery(shopId);
    
    // When
    MenuBoardResult result = menuBoardQueryHandler.handle(query);
    
    // Then
    assertThat(result.getMenuItems()).isNotEmpty();
}
```

## 보안 및 검증

### Command 입력 검증
```java
public class CreateMenuCommand {
    @NotNull(message = "가게 ID는 필수입니다")
    private Long shopId;
    
    @NotBlank(message = "메뉴 이름은 필수입니다")
    @Size(max = 100, message = "메뉴 이름은 100자 이하여야 합니다")
    private String name;
}
```

### Query 보안
```java
@Transactional(readOnly = true)
public List<OrderSummaryReadModel> getUserOrders(Long userId, String currentUserName) {
    // 사용자 본인의 주문만 조회 가능
    if (!isAuthorizedUser(userId, currentUserName)) {
        throw new AccessDeniedException("권한이 없습니다");
    }
    return orderQueryDao.findOrdersByUserId(userId);
}
```