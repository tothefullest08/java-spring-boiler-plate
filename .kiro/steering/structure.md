# 멀티 프로젝트 구조 및 CQRS 패턴 규칙

## 필수 준수 원칙
**모든 규칙은 선택 사항이 아닌 필수이며, 예외 없이 준수해야 합니다. 규칙 위반 시 즉시 코드 리뷰에서 반려됩니다.**

## 멀티 프로젝트 구조 (필수)

```
java-spring-boiler-plate/
├── domains/
│   ├── common/        # 공통 모듈 (아키텍처 패턴)
│   │   ├── build.gradle
│   │   └── src/main/java/harry/boilerplate/common/
│   │       ├── domain/
│   │       │   ├── entity/        # 기본 엔티티 클래스들
│   │       │   │   ├── AggregateRoot.java
│   │       │   │   ├── BaseEntity.java
│   │       │   │   ├── DomainEntity.java    # 새로 추가
│   │       │   │   ├── ValueObject.java
│   │       │   │   ├── EntityId.java
│   │       │   │   └── Money.java
│   │       │   └── event/         # 도메인 이벤트 패턴
│   │       │       └── DomainEvent.java
│   │       ├── exception/         # 공통 예외 처리
│   │       ├── response/          # 공통 응답 패턴
│   │       └── config/            # 공통 설정
│   ├── shop/          # Shop Context 독립 프로젝트
│   │   ├── build.gradle
│   │   ├── src/main/java/harry/boilerplate/shop/
│   │   │   └── domain/
│   │   │       ├── aggregate/     # 애그리게이트 루트들
│   │   │       │   ├── Menu.java
│   │   │       │   ├── Shop.java
│   │   │       │   ├── MenuRepository.java
│   │   │       │   ├── ShopRepository.java
│   │   │       │   ├── MenuDomainException.java
│   │   │       │   ├── ShopDomainException.java
│   │   │       │   ├── MenuErrorCode.java
│   │   │       │   └── ShopErrorCode.java
│   │   │       ├── entity/        # 도메인 엔티티들
│   │   │       │   └── OptionGroup.java
│   │   │       ├── valueobject/   # 값 객체들
│   │   │       │   ├── MenuId.java
│   │   │       │   ├── ShopId.java
│   │   │       │   ├── OptionGroupId.java
│   │   │       │   ├── Option.java
│   │   │       │   └── BusinessHours.java
│   │   │       └── event/         # 도메인 이벤트들
│   │   │           ├── MenuOpenedEvent.java
│   │   │           └── ShopClosedEvent.java
│   │   └── src/main/resources/application-shop.yml
│   ├── order/         # Order Context 독립 프로젝트  
│   │   ├── build.gradle
│   │   ├── src/main/java/harry/boilerplate/order/
│   │   └── src/main/resources/application-order.yml
│   └── user/          # User Context 독립 프로젝트
│       ├── build.gradle
│       ├── src/main/java/harry/boilerplate/user/
│       └── src/main/resources/application-user.yml
└── settings.gradle    # 멀티 프로젝트 설정
```

## CQRS 기반 패키지 구조 (필수)

### 각 컨텍스트 내부 구조
```
domains/shop/src/main/java/harry/boilerplate/shop/
├── domain/                          # 도메인 레이어 (DDD 분류)
│   ├── aggregate/                   # 애그리게이트 루트들
│   │   ├── Menu.java               # Menu 애그리게이트 루트
│   │   ├── Shop.java               # Shop 애그리게이트 루트
│   │   ├── MenuRepository.java     # Menu Repository Interface
│   │   ├── ShopRepository.java     # Shop Repository Interface
│   │   ├── MenuDomainException.java
│   │   ├── ShopDomainException.java
│   │   ├── MenuErrorCode.java
│   │   └── ShopErrorCode.java
│   ├── entity/                      # 도메인 엔티티들 (DomainEntity 상속)
│   │   └── OptionGroup.java        # Menu 애그리게이트 내부 엔티티
│   ├── valueobject/                 # 값 객체들 (ValueObject 상속)
│   │   ├── MenuId.java             # Menu ID
│   │   ├── ShopId.java             # Shop ID
│   │   ├── OptionGroupId.java      # OptionGroup ID
│   │   ├── Option.java             # 옵션 값 객체
│   │   └── BusinessHours.java      # 영업시간 값 객체
│   └── event/                       # 도메인 이벤트들
│       ├── MenuOpenedEvent.java
│       └── ShopClosedEvent.java
├── application/
│   ├── command/                    # 명령 처리 (쓰기 작업)
│   │   ├── handler/                # Command Handler
│   │   │   ├── CreateMenuCommandHandler.java
│   │   │   └── UpdateMenuCommandHandler.java
│   │   ├── service/                # Command Service
│   │   │   └── MenuCommandService.java
│   │   └── dto/                    # Command DTO
│   │       ├── CreateMenuCommand.java
│   │       └── UpdateMenuCommand.java
│   └── query/                      # 조회 처리 (읽기 작업)
│       ├── handler/                # Query Handler
│       │   ├── MenuBoardQueryHandler.java
│       │   └── MenuDetailQueryHandler.java
│       ├── readmodel/              # CQRS Journey 스타일 Read Model
│       │   ├── MenuSummaryReadModel.java
│       │   ├── MenuDetailReadModel.java
│       │   └── MenuBoardViewModel.java
│       └── dto/                    # Query Request/Response DTO
│           ├── MenuBoardQuery.java
│           └── MenuBoardResult.java
├── infrastructure/
│   ├── command/                    # Command 영속성 (Repository 패턴)
│   │   ├── MenuRepositoryImpl.java # JPA 기반 Repository 구현체
│   │   └── ShopRepositoryImpl.java
│   └── query/                      # Query 영속성 (DAO 패턴)
│       ├── dao/                    # Table Data Gateway
│       │   ├── MenuQueryDaoImpl.java    # EntityManager 직접 사용
│       │   └── ShopQueryDaoImpl.java
│       └── mapper/                 # Entity → ReadModel 변환
│           ├── MenuReadModelMapper.java
│           └── ShopReadModelMapper.java
├── presentation/
│   ├── command/                    # 명령 API 컨트롤러
│   │   └── MenuCommandController.java
│   └── query/                      # 조회 API 컨트롤러  
│       └── MenuQueryController.java
└── ShopApplication.java            # Spring Boot 메인 클래스
```

## DDD 패턴 적용 규칙 (필수)

### 애그리게이트 설계 원칙
```java
// ✅ 필수: 애그리게이트 루트 상속
@Entity
@Table(name = "shop")
public class Shop extends AggregateRoot<Shop, ShopId> {
    // 애그리게이트 루트 로직
}

@Entity
@Table(name = "menu")
public class Menu extends AggregateRoot<Menu, MenuId> {
    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<OptionGroup> optionGroups = new ArrayList<>();
    
    // 애그리게이트 루트 로직
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
    
    // 도메인 엔티티 로직
}

// ✅ 값 객체: ValueObject 상속
@Embeddable
public class Option extends ValueObject {
    @Column(name = "option_name")
    private String name;
    
    @Column(name = "option_price")
    private BigDecimal price;
    
    // 값 객체 로직 (불변성 보장)
}
```

### ValueObject 불변성 (필수)
```java
public class Money extends ValueObject {
    private final BigDecimal amount;
    private final Currency currency;
    
    public Money(BigDecimal amount, Currency currency) {
        this.amount = amount;        // final 필드
        this.currency = currency;    // final 필드
    }
    
    // Getter만 제공, Setter 금지
    public BigDecimal getAmount() { return amount; }
    
    // 변경이 필요한 경우 새 인스턴스 반환
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount), this.currency);
    }
}
```

### DDD 분류 및 상속 구조 (필수)
```java
// ✅ 애그리게이트 루트: AggregateRoot 상속
public abstract class AggregateRoot<T extends AggregateRoot<T, TID>, TID> extends BaseEntity {
    // 도메인 이벤트 발행 기능
    // 애그리게이트 경계 관리
}

// ✅ 도메인 엔티티: DomainEntity 상속 (애그리게이트 내부 엔티티)
public abstract class DomainEntity<T extends DomainEntity<T, TID>, TID> extends BaseEntity {
    // ID 기반 동등성 비교
    // 생명주기 관리
}

// ✅ 값 객체: ValueObject 상속 (불변 객체)
public abstract class ValueObject {
    // 값 기반 동등성 비교
    // 불변성 보장
}

// ✅ 디렉토리 분류 규칙
domains/shop/src/main/java/harry/boilerplate/shop/domain/
├── aggregate/     # 애그리게이트 루트 + Repository + Exception + ErrorCode
├── entity/        # 도메인 엔티티 (DomainEntity 상속)
├── valueobject/   # 값 객체 (ValueObject 상속, ID 클래스 포함)
└── event/         # 도메인 이벤트

// ❌ 금지: JPA 엔티티와 도메인 엔티티 분리
// - 변환 로직 불필요
// - 코드 복잡도 감소
// - 성능 오버헤드 제거
```

## CQRS 명명 규칙 (필수)

### Command Handler 명명
```java
public class CreateMenuCommandHandler
public class AddCartItemCommandHandler
public class PlaceOrderCommandHandler
```

### Query Handler 명명
```java
public class MenuBoardQueryHandler
public class OrderHistoryQueryHandler
public class CartSummaryQueryHandler
```

### Command/Query DTO 명명
```java
// Command DTO
public class CreateMenuCommand
public class AddCartItemCommand

// Query DTO
public class MenuBoardQuery
public class MenuBoardResult
```

### Read Model 명명
```java
public class MenuSummaryReadModel      # 불변 데이터 객체
public class MenuDetailReadModel       # 불변 데이터 객체
public class MenuBoardViewModel        # UI 최적화된 뷰 모델
```

## 컨텍스트 간 상호작용 규칙 (필수)

### 의존성 격리 (절대 금지)
```gradle
// ❌ 금지: 다른 컨텍스트 프로젝트 의존
dependencies {
    implementation project(':domains:shop')  // 금지
}

// ✅ 권장: Common 모듈과 외부 라이브러리만 의존
dependencies {
    implementation project(':domains:common')  // Common 모듈만 허용
    implementation 'org.springframework.boot:spring-boot-starter-web'
}
```

### API 기반 통신 (필수)
```java
// ❌ 금지: 직접 Repository 호출
shopRepository.find(shopId);

// ✅ 권장: API 클라이언트 사용
shopApiClient.getShop(shopId);
```

### CQRS API 구조 (필수)
```java
// Shop Context Command API
// POST /shops/{shopId}/menus - 메뉴 생성
// PUT /shops/{shopId}/menus/{menuId} - 메뉴 수정

// Shop Context Query API  
// GET /shops/{shopId} - 가게 정보 조회
// GET /shops/{shopId}/menus - 메뉴 목록 조회
```

## CQRS Infrastructure 구현 (필수)

### Command Repository 구현
```java
@Repository
@Transactional
public class MenuRepositoryImpl implements MenuRepository {
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Menu find(MenuId menuId) {
        return entityManager.find(Menu.class, menuId);
    }
    
    @Override
    public void save(Menu menu) {
        if (menu.getId() == null) {
            entityManager.persist(menu);
        } else {
            entityManager.merge(menu);
        }
    }
}
```

### Query DAO 구현
```java
@Repository
@Transactional(readOnly = true)
public class MenuQueryDaoImpl implements MenuQueryDao {
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<MenuSummaryReadModel> findMenuSummariesByShopId(Long shopId) {
        List<Menu> menus = entityManager.createQuery(
            "SELECT m FROM Menu m WHERE m.shopId = :shopId AND m.open = true", Menu.class)
            .setParameter("shopId", shopId)
            .setHint(QueryHints.READ_ONLY, true)
            .getResultList();
            
        return menus.stream()
            .map(mapper::toSummaryReadModel)
            .collect(Collectors.toList());
    }
}
```

## 공통 아키텍처 패턴 (필수)

### Domain Event Pattern (필수)

#### Common 패키지 - 아키텍처 패턴만 제공
```java
// domains/common/src/main/java/harry/boilerplate/common/domain/event/DomainEvent.java
// ✅ 필수: 모든 도메인 이벤트가 구현해야 하는 인터페이스 (Common 패키지)
public interface DomainEvent {
    UUID getEventId();
    Instant getOccurredAt();
    String getAggregateId();
    String getAggregateType();
    int getVersion();
}
```

#### 각 바운디드 컨텍스트 - 구체적인 비즈니스 이벤트 구현
```java
// ✅ 올바른 위치: domains/order/src/main/java/harry/boilerplate/order/domain/event/OrderPlacedEvent.java
public class OrderPlacedEvent implements DomainEvent {
    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final String aggregateId;
    private final String aggregateType = "Order";
    private final int version = 1;
    
    private final String userId;
    private final String shopId;
    
    public OrderPlacedEvent(String orderId, String userId, String shopId) {
        this.aggregateId = orderId;
        this.userId = userId;
        this.shopId = shopId;
    }
    
    // getters...
}

// ✅ 올바른 위치: domains/shop/src/main/java/harry/boilerplate/shop/domain/event/MenuOpenedEvent.java
public class MenuOpenedEvent implements DomainEvent {
    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final String aggregateId;
    private final String aggregateType = "Menu";
    private final int version = 1;
    
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

#### 도메인 이벤트 위치 규칙 (절대 준수)
```
✅ 올바른 구조:
domains/
├── common/                           # 아키텍처 패턴만
│   └── domain/
│       ├── entity/                   # 엔티티 관련 패턴
│       │   ├── AggregateRoot.java
│       │   ├── BaseEntity.java
│       │   ├── ValueObject.java
│       │   ├── EntityId.java
│       │   └── Money.java
│       └── event/                    # 도메인 이벤트 패턴
│           └── DomainEvent.java     # 인터페이스만
├── shop/                            # Shop Context 비즈니스 이벤트
│   └── domain/
│       └── event/
│           ├── MenuOpenedEvent.java
│           └── ShopClosedEvent.java
├── order/                           # Order Context 비즈니스 이벤트
│   └── domain/
│       └── event/
│           ├── OrderPlacedEvent.java
│           └── CartItemAddedEvent.java
└── user/                            # User Context 비즈니스 이벤트
    └── domain/
        └── event/
            └── UserRegisteredEvent.java

❌ 금지된 구조:
domains/
└── common/
    └── domain/
        └── event/                   # 구체적인 비즈니스 이벤트 금지!
            ├── OrderPlacedEvent.java    # ❌ 바운디드 컨텍스트 위반
            └── MenuOpenedEvent.java     # ❌ 의존성 격리 위반
```

### Error Handling Pattern (필수)
```java
// 에러 코드 형식: {DOMAIN}-{LAYER}-{CODE}
public interface ErrorCode {
    String getCode();    // 예: "SHOP-DOMAIN-001"
    String getMessage(); // 예: "메뉴를 찾을 수 없습니다"
}

// 공통 시스템 에러
public enum CommonSystemErrorCode implements ErrorCode {
    INTERNAL_SERVER_ERROR("COMMON-SYSTEM-001", "Internal server error"),
    RESOURCE_NOT_FOUND("COMMON-SYSTEM-003", "Resource not found"),
    VALIDATION_ERROR("COMMON-SYSTEM-006", "Validation error");
}

// 도메인별 예외 기본 클래스
public abstract class DomainException extends RuntimeException {
    public abstract ErrorCode getErrorCode();
}
```

### Base Entity Pattern (필수)
```java
// domains/common/src/main/java/harry/boilerplate/common/domain/entity/BaseEntity.java
// 모든 JPA 엔티티가 상속해야 하는 기본 클래스
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
}
```

### Command Result Response Pattern (필수)
```java
// 모든 Command API의 표준 응답 형식
@Schema(description = "Command 요청 결과 응답")
public class CommandResultResponse {
    @Schema(description = "처리 상태", example = "SUCCESS")
    private final String status;
    
    @Schema(description = "결과 메시지")
    private final String message;
    
    @Schema(description = "생성/수정된 리소스의 ID", nullable = true)
    private final String resourceId;
    
    // 팩토리 메서드
    public static CommandResultResponse success(String message, String resourceId) {
        return new CommandResultResponse("SUCCESS", message, resourceId);
    }
}
```

## 금지 사항 (절대 금지)

### 애그리게이트 경계 위반
```java
// ❌ 절대 금지: 직접 수정
menu.getOptionGroups().get(0).setName("new name");

// ✅ 필수: 애그리게이트 루트를 통한 수정
menu.changeOptionGroupName(optionGroupId, "new name");
```

### 예외 처리 규칙 위반
```java
// ❌ 금지: ErrorCode 없는 예외
throw new RuntimeException("에러 발생");

// ✅ 필수: ErrorCode를 가진 구조화된 예외
throw new MenuNotFoundException(MenuErrorCode.MENU_NOT_FOUND);
```

### 트랜잭션 분리 (필수)
```java
@Transactional                    // Command Handler/Repository
@Transactional(readOnly = true)   // Query Handler/DAO
```

## 필수 메서드 패턴

### Shop Context 필수 메서드
```java
// 영업 상태 확인
Shop.isOpen()

// 메뉴 공개/비공개
Menu.open()

// 옵션그룹 관리
Menu.addOptionGroup()
Menu.changeOptionGroupName()
Menu.changeOptionName()
```

### Order Context 필수 메서드
```java
// 장바구니 관리
Cart.start()        // 다른 가게 메뉴 추가 시 초기화
Cart.addItem()      // 아이템 추가
Cart.getTotalPrice() // 총액 계산

// 주문 처리
Cart.placeOrder()   // 주문 생성
Order.getPrice()    // 주문 금액 계산

// 아이템 병합
CartLineItem.combine() // 동일 메뉴+옵션 조합 병합
```

## 필수 코딩 규칙

### Rule 68: 모든 예외는 ErrorCode를 가져야 함
```java
// ✅ 필수: 구조화된 예외 처리
public class MenuNotFoundException extends DomainException {
    public MenuNotFoundException(String menuId) {
        super("메뉴를 찾을 수 없습니다: " + menuId, null);
    }
    
    @Override
    public ErrorCode getErrorCode() {
        return MenuErrorCode.MENU_NOT_FOUND; // "MENU-DOMAIN-001"
    }
}
```

### 에러 코드 형식 규칙
```java
// 형식: {DOMAIN}-{LAYER}-{CODE}
public enum MenuErrorCode implements ErrorCode {
    MENU_NOT_FOUND("MENU-DOMAIN-001", "메뉴를 찾을 수 없습니다"),
    MENU_ALREADY_OPEN("MENU-DOMAIN-002", "이미 공개된 메뉴입니다"),
    INVALID_MENU_REQUEST("MENU-APP-001", "잘못된 메뉴 요청입니다");
}
```

### JPA 엔티티 BaseEntity 상속 (필수)
```java
// ✅ 필수: BaseEntity 상속
@Entity
@Table(name = "menu")
public class Menu extends BaseEntity {  // BaseEntity 상속 필수
    @Id
    private String id;
    
    // 비즈니스 필드들...
}
```

### 도메인 이벤트 구현 (필수)
```java
// ✅ 필수: DomainEvent 인터페이스 구현
public class MenuOpenedEvent implements DomainEvent {
    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final String aggregateId;
    private final String aggregateType = "Menu";
    private final int version = 1;
    
    // 필수 메서드 구현...
}
```

### Command 응답 표준화 (필수)
```java
// ✅ 필수: CommandResultResponse 사용
@PostMapping("/menus")
public CommandResultResponse createMenu(@RequestBody CreateMenuRequest request) {
    String menuId = menuCommandService.createMenu(request.toCommand());
    return CommandResultResponse.success("메뉴가 생성되었습니다", menuId);
}
```

## CQRS 테스트 구조 (필수)

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

### 예외 처리 테스트 (필수)
```java
@Test
void 메뉴_생성_실패_ErrorCode_확인() {
    // Given
    CreateMenuCommand invalidCommand = new CreateMenuCommand(null, "");
    
    // When & Then
    MenuDomainException exception = assertThrows(MenuDomainException.class, 
        () -> createMenuCommandHandler.handle(invalidCommand));
    
    assertThat(exception.getErrorCode().getCode()).isEqualTo("MENU-DOMAIN-003");
}
```