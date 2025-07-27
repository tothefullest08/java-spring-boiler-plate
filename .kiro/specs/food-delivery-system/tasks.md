# Implementation Plan

- [x] 1. 프로젝트 구조 및 공통 모듈 설정
  - 멀티 프로젝트 Gradle 설정 및 Common 모듈 기본 아키텍처 패턴 구현
  - Docker Compose MySQL 환경 설정 및 데이터베이스 스키마 생성
  - _Requirements: 10.1, 10.2, 10.3, 10.4_

- [ ] 2. Common 모듈 핵심 패턴 구현
  - [x] 2.1 도메인 기본 패턴 구현
    - DomainEvent 인터페이스, AggregateRoot, ValueObject 기본 클래스 작성
    - BaseEntity 패턴과 JPA 공통 설정 구현
    - _Requirements: 9.5_
  
  - [x] 2.2 에러 처리 패턴 구현
    - ErrorCode 인터페이스와 CommonSystemErrorCode enum 작성
    - DomainException, ApplicationException 기본 클래스 구현
    - GlobalExceptionHandler와 ErrorResponse 패턴 작성
    - _Requirements: 8.4, 8.5, 8.6_
  
  - [x] 2.3 공통 응답 패턴 구현
    - CommandResultResponse 클래스와 팩토리 메서드 구현
    - 공통 설정 클래스(PasswordEncoder, ObjectMapper) 작성
    - _Requirements: 9.1, 9.2_

- [ ] 3. Shop Context 도메인 모델 구현
  - [x] 3.1 Shop 애그리게이트 구현
    - Shop 엔티티와 ShopId ValueObject 작성
    - BusinessHours ValueObject와 영업시간 관리 로직 구현
    - Shop.isOpen() 메서드와 영업시간 조정 기능 구현
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5_
  
  - [x] 3.2 Menu 애그리게이트 구현
    - Menu 엔티티와 MenuId ValueObject 작성
    - OptionGroup, Option ValueObject 구조 구현
    - Menu.open() 메서드와 공개 조건 검증 로직 구현
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_
  
  - [x] 3.3 옵션 관리 비즈니스 로직 구현
    - Menu.addOptionGroup() 메서드와 중복 검증 로직 작성
    - Menu.changeOptionGroupName(), Menu.changeOptionName() 메서드 구현
    - 옵션그룹 삭제 시 최소 조건 보장 로직 구현
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5_

- [x] 4. Shop Context 도메인 이벤트 구현
  - [x] 4.1 Shop Context 도메인 이벤트 작성
    - MenuOpenedEvent 도메인 이벤트 클래스 구현
    - ShopClosedEvent 도메인 이벤트 클래스 구현
    - 각 이벤트에 필요한 비즈니스 데이터 포함
    - _Requirements: 2.3, 1.5_
  
  - [x] 4.2 애그리게이트에서 도메인 이벤트 발행
    - Menu.open() 메서드에서 MenuOpenedEvent 발행 로직 추가
    - Shop 영업 종료 시 ShopClosedEvent 발행 로직 추가
    - AggregateRoot.addDomainEvent() 메서드 활용
    - _Requirements: 2.3, 1.5_

- [x] 5. Shop Context 도메인 예외 처리 구현
  - ShopErrorCode, MenuErrorCode enum 작성
  - ShopDomainException, MenuDomainException 클래스 구현
  - 각 비즈니스 규칙 위반 시 적절한 예외 발생 로직 추가
  - _Requirements: 1.5, 2.4, 3.1, 3.2, 3.4_

- [x] 6. Shop Context 인프라스트럭처 레이어 구현
  - [x] 6.1 JPA 엔티티 매핑 구현
    - Shop, Menu JPA 엔티티 클래스 작성 (BaseEntity 상속)
    - OptionGroup, Option 임베디드 매핑 구현
    - 데이터베이스 테이블과 엔티티 매핑 설정
    - _Requirements: 10.5_
  
  - [x] 6.2 Command Repository 구현
    - ShopRepository, MenuRepository 인터페이스 정의
    - ShopRepositoryImpl, MenuRepositoryImpl JPA 구현체 작성
    - EntityManager 기반 CRUD 작업 구현
    - _Requirements: 9.1, 9.3_
  
  - [x] 6.3 Query DAO 구현
    - ShopQueryDao, MenuQueryDao 인터페이스 정의
    - 읽기 최적화된 쿼리 메서드 구현 (EntityManager 직접 사용)
    - Read Model 매핑 로직 구현
    - _Requirements: 9.2, 9.4_

- [ ] 6. Shop Context 애플리케이션 레이어 구현
  - [ ] 6.1 Command Handler 구현
    - CreateShopCommandHandler, UpdateShopCommandHandler 작성
    - CreateMenuCommandHandler, OpenMenuCommandHandler 작성
    - AddOptionGroupCommandHandler, ChangeOptionGroupNameCommandHandler 작성
    - _Requirements: 1.1, 2.1, 2.3, 3.1, 3.2_
  
  - [ ] 6.2 Query Handler 구현
    - ShopInfoQueryHandler, MenuBoardQueryHandler 작성
    - MenuDetailQueryHandler 구현
    - Read Model 생성 및 반환 로직 구현
    - _Requirements: 4.1, 4.2, 4.3, 4.4_
  
  - [ ] 6.3 Command/Query DTO 구현
    - Command DTO 클래스들 (CreateShopCommand, CreateMenuCommand 등) 작성
    - Query DTO 클래스들 (MenuBoardQuery, MenuBoardResult 등) 작성
    - 입력 검증 어노테이션 추가
    - _Requirements: 2.1, 4.1, 4.2, 4.3_

- [ ] 7. Shop Context API 레이어 구현
  - [ ] 7.1 Command Controller 구현
    - ShopCommandController (POST, PUT 엔드포인트) 작성
    - MenuCommandController (POST, PUT 엔드포인트) 작성
    - CommandResultResponse 표준 응답 형식 적용
    - _Requirements: 9.1, 10.5_
  
  - [ ] 7.2 Query Controller 구현
    - ShopQueryController (GET 엔드포인트) 작성
    - MenuQueryController (GET 엔드포인트) 작성
    - 메뉴보드 조회 API 구현
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 9.2_

- [ ] 8. Order Context 도메인 모델 구현
  - [ ] 8.1 Cart 애그리게이트 구현
    - Cart 엔티티와 CartId ValueObject 작성
    - CartLineItem ValueObject와 combine() 메서드 구현
    - Cart.start(), Cart.addItem() 메서드와 단일 가게 규칙 구현
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.6_
  
  - [ ] 8.2 Order 애그리게이트 구현
    - Order 엔티티와 OrderId ValueObject 작성
    - OrderLineItem ValueObject 구조 구현
    - Cart.placeOrder(), Order.getPrice() 메서드 구현
    - _Requirements: 6.1, 6.2, 6.3, 6.4_
  
  - [ ] 8.3 Money ValueObject 구현
    - Money 클래스와 불변성 보장 구현
    - 금액 계산 메서드들 (add, multiply 등) 작성
    - Cart.getTotalPrice() 메서드에서 Money 타입 반환 구현
    - _Requirements: 5.5, 6.3_

- [ ] 9. Order Context 도메인 이벤트 구현
  - [ ] 9.1 Order Context 도메인 이벤트 작성
    - OrderPlacedEvent 도메인 이벤트 클래스 구현
    - CartItemAddedEvent 도메인 이벤트 클래스 구현
    - 각 이벤트에 필요한 비즈니스 데이터 포함
    - _Requirements: 6.1, 5.1_
  
  - [ ] 9.2 애그리게이트에서 도메인 이벤트 발행
    - Order.from() 메서드에서 OrderPlacedEvent 발행 로직 추가
    - Cart.addItem() 메서드에서 CartItemAddedEvent 발행 로직 추가
    - AggregateRoot.addDomainEvent() 메서드 활용
    - _Requirements: 6.1, 5.1_

- [ ] 10. Order Context 외부 API 연동 구현
  - [ ] 10.1 Shop API Client 구현
    - ShopApiClient 인터페이스 정의
    - RestTemplate 기반 Shop Context API 호출 구현
    - 가게 영업 상태 확인, 메뉴 정보 조회 기능 구현
    - _Requirements: 8.1, 8.2, 8.3, 5.3_
  
  - [ ] 10.2 User API Client 구현
    - UserApiClient 인터페이스 정의
    - 사용자 유효성 검증 API 호출 구현
    - _Requirements: 8.4, 5.6_

- [ ] 11. Order Context 도메인 예외 처리 구현
  - CartErrorCode, OrderErrorCode enum 작성
  - CartDomainException, OrderDomainException 클래스 구현
  - 장바구니 및 주문 비즈니스 규칙 위반 시 예외 처리 로직 추가
  - _Requirements: 5.6, 6.4_

- [ ] 12. Order Context 인프라스트럭처 레이어 구현
  - [ ] 12.1 JPA 엔티티 매핑 구현
    - Cart, Order JPA 엔티티 클래스 작성
    - CartLineItem, OrderLineItem 임베디드 매핑 구현
    - 선택된 옵션 정보 저장 구조 구현
    - _Requirements: 10.5_
  
  - [ ] 11.2 Command Repository 구현
    - CartRepository, OrderRepository 인터페이스 및 구현체 작성
    - 장바구니 및 주문 CRUD 작업 구현
    - _Requirements: 9.1, 9.3_
  
  - [ ] 11.3 Query DAO 구현
    - CartQueryDao, OrderQueryDao 인터페이스 및 구현체 작성
    - 사용자별 장바구니/주문 조회 최적화 쿼리 구현
    - _Requirements: 9.2, 9.4_

- [ ] 12. Order Context 애플리케이션 레이어 구현
  - [ ] 12.1 Command Handler 구현
    - AddCartItemCommandHandler, PlaceOrderCommandHandler 작성
    - 외부 API 호출을 통한 검증 로직 구현
    - 트랜잭션 처리 및 예외 처리 구현
    - _Requirements: 5.1, 5.2, 5.3, 6.1, 6.2_
  
  - [ ] 12.2 Query Handler 구현
    - CartSummaryQueryHandler, OrderHistoryQueryHandler 작성
    - Read Model 생성 및 반환 로직 구현
    - _Requirements: 5.5, 6.3_

- [ ] 13. Order Context API 레이어 구현
  - [ ] 13.1 Command Controller 구현
    - CartCommandController (POST, PUT 엔드포인트) 작성
    - OrderCommandController (POST 엔드포인트) 작성
    - _Requirements: 5.1, 6.1, 9.1_
  
  - [ ] 13.2 Query Controller 구현
    - CartQueryController, OrderQueryController (GET 엔드포인트) 작성
    - 사용자별 장바구니/주문 조회 API 구현
    - _Requirements: 5.5, 6.3, 9.2_

- [ ] 14. User Context 구현
  - [ ] 14.1 User 도메인 모델 구현
    - User 엔티티와 UserId ValueObject 작성
    - 사용자 유효성 검증 로직 구현
    - _Requirements: 7.1, 7.2_
  
  - [ ] 14.2 User Context 인프라스트럭처 구현
    - User JPA 엔티티 매핑 및 Repository 구현
    - UserQueryDao 구현
    - _Requirements: 7.3, 7.4_
  
  - [ ] 14.3 User Context 애플리케이션 및 API 구현
    - User Command/Query Handler 작성
    - User Command/Query Controller 구현
    - _Requirements: 7.1, 7.2, 7.3, 7.4_

- [ ] 15. 통합 테스트 구현
  - [ ] 15.1 단위 테스트 작성
    - 각 도메인 모델의 비즈니스 로직 단위 테스트 작성
    - Command/Query Handler 단위 테스트 작성
    - ValueObject 및 예외 처리 테스트 작성
    - _Requirements: 1.1, 2.3, 3.1, 5.1, 6.1_
  
  - [ ] 15.2 Repository 통합 테스트 작성
    - Testcontainers를 사용한 MySQL 통합 테스트 작성
    - Repository 구현체 CRUD 작업 테스트 작성
    - _Requirements: 9.1, 9.2_
  
  - [ ] 15.3 API 통합 테스트 작성
    - Controller 레이어 통합 테스트 작성
    - 각 엔드포인트별 성공/실패 시나리오 테스트 작성
    - _Requirements: 10.5_

- [ ] 16. 컨텍스트 간 통신 테스트 구현
  - [ ] 16.1 API Client 테스트 작성
    - ShopApiClient, UserApiClient 단위 테스트 작성
    - WireMock을 사용한 외부 API 호출 테스트 작성
    - _Requirements: 8.1, 8.2, 8.3, 8.4_
  
  - [ ] 16.2 End-to-End 테스트 작성
    - 전체 주문 플로우 E2E 테스트 작성
    - 멀티 컨텍스트 상호작용 시나리오 테스트 작성
    - _Requirements: 8.5, 8.6_

- [ ] 17. 애플리케이션 설정 및 배포 준비
  - [ ] 17.1 각 컨텍스트별 Spring Boot 설정 완성
    - application.yml 파일 설정 (포트, 데이터베이스 연결)
    - 각 컨텍스트별 메인 애플리케이션 클래스 작성
    - _Requirements: 10.2, 10.3, 10.4_
  
  - [ ] 17.2 빌드 및 실행 스크립트 작성
    - Gradle 빌드 태스크 최적화
    - Docker Compose 환경에서 전체 시스템 실행 검증
    - _Requirements: 10.1, 10.5_