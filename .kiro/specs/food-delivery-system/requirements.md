# Requirements Document

## Introduction

DDD와 CQRS 패턴을 적용한 멀티 프로젝트 구조의 음식 주문 시스템입니다. 3개의 독립적인 바운디드 컨텍스트(Shop, Order, User)로 구성되며, 각각 별도의 Spring Boot 애플리케이션으로 실행됩니다. 각 컨텍스트는 완전한 의존성 격리를 통해 독립적으로 배포 가능하며, API 기반 통신을 통해 상호작용합니다.

## Requirements

### Requirement 1: Shop Context - 가게 운영 관리

**User Story:** As a 가게 운영자, I want 가게의 영업 상태를 관리하고 확인할 수 있기를, so that 고객들이 정확한 영업 정보를 확인할 수 있다

#### Acceptance Criteria

1. WHEN 가게 운영자가 요일별 운영시간을 설정 THEN 시스템 SHALL 각 요일에 대해 독립적인 운영시간을 저장한다
2. WHEN 운영시간이 설정되지 않은 요일이 있을 때 THEN 시스템 SHALL 해당 요일을 휴무로 처리한다
3. WHEN Shop.isOpen() 메서드가 호출될 때 THEN 시스템 SHALL 현재 시간 기준으로 영업 여부를 판단하여 반환한다
4. WHEN 가게 운영자가 특정 요일의 영업시간을 조정할 때 THEN 시스템 SHALL 시간 단위로 정확한 조정을 허용한다
5. IF 운영시간이 설정되지 않은 요일에 대해 조정 요청이 있을 때 THEN 시스템 SHALL 조정을 거부하고 적절한 오류 메시지를 반환한다

### Requirement 2: Shop Context - 메뉴 관리

**User Story:** As a 가게 운영자, I want 메뉴를 등록하고 공개/비공개를 관리할 수 있기를, so that 고객들에게 적절한 메뉴만 노출할 수 있다

#### Acceptance Criteria

1. WHEN 가게 운영자가 메뉴를 등록할 때 THEN 시스템 SHALL 메뉴 기본정보(이름, 설명)를 필수로 요구한다
2. WHEN 메뉴가 등록될 때 THEN 시스템 SHALL 초기 상태를 비공개로 설정한다
3. WHEN Menu.open() 메서드가 호출될 때 AND 옵션그룹이 최소 1개 이상 존재할 때 AND 필수 옵션그룹이 1~3개 범위에 있을 때 AND 유료 옵션그룹이 최소 1개 있을 때 THEN 시스템 SHALL 메뉴를 공개 상태로 변경한다
4. IF 메뉴 공개 조건을 만족하지 않을 때 THEN 시스템 SHALL 공개를 거부하고 구체적인 오류 메시지를 반환한다
5. WHEN 메뉴가 공개된 후 THEN 시스템 SHALL 고객에게 해당 메뉴를 노출한다

### Requirement 3: Shop Context - 옵션 관리

**User Story:** As a 가게 운영자, I want 메뉴의 옵션그룹과 옵션을 관리할 수 있기를, so that 다양한 메뉴 구성을 제공할 수 있다

#### Acceptance Criteria

1. WHEN Menu.addOptionGroup() 메서드가 호출될 때 AND 동일한 이름의 옵션그룹이 존재하지 않을 때 THEN 시스템 SHALL 새로운 옵션그룹을 추가한다
2. IF 메뉴가 공개 상태이고 필수 옵션그룹을 추가할 때 AND 이미 3개의 필수 옵션그룹이 존재할 때 THEN 시스템 SHALL 추가를 거부하고 오류 메시지를 반환한다
3. WHEN Menu.changeOptionGroupName() 메서드가 호출될 때 AND 새로운 이름이 동일 메뉴 내에서 유일할 때 THEN 시스템 SHALL 옵션그룹 이름을 변경한다
4. WHEN 옵션그룹 삭제 요청이 있을 때 AND 메뉴가 공개된 상태일 때 AND 삭제 후에도 최소 조건(옵션그룹 1개, 필수 옵션그룹 1개, 유료 옵션그룹 1개)을 만족할 때 THEN 시스템 SHALL 옵션그룹을 삭제한다
5. WHEN Menu.changeOptionName() 메서드가 호출될 때 THEN 시스템 SHALL 현재 이름과 가격으로 옵션을 식별하여 이름을 변경한다

### Requirement 4: Shop Context - 메뉴보드 조회

**User Story:** As a 고객, I want 가게의 메뉴보드를 조회할 수 있기를, so that 주문할 메뉴를 선택할 수 있다

#### Acceptance Criteria

1. WHEN 고객이 특정 가게의 메뉴보드를 조회할 때 THEN 시스템 SHALL 공개된 메뉴 목록만 반환한다
2. WHEN 메뉴보드가 조회될 때 THEN 시스템 SHALL 가게 ID, 이름, 영업 상태, 최소 주문금액을 포함한다
3. WHEN 메뉴 정보가 반환될 때 THEN 시스템 SHALL 메뉴 ID, 이름, 기본가격, 설명을 포함한다
4. IF 가게에 공개된 메뉴가 없을 때 THEN 시스템 SHALL 빈 메뉴 목록을 반환한다

### Requirement 5: Order Context - 장바구니 관리

**User Story:** As a 고객, I want 장바구니에 메뉴를 추가하고 관리할 수 있기를, so that 원하는 메뉴들을 모아서 주문할 수 있다

#### Acceptance Criteria

1. WHEN Cart.addItem() 메서드가 호출될 때 AND 장바구니가 비어있거나 같은 가게의 메뉴일 때 THEN 시스템 SHALL 메뉴를 장바구니에 추가한다
2. WHEN 다른 가게의 메뉴를 추가할 때 THEN 시스템 SHALL Cart.start() 메서드를 호출하여 기존 장바구니를 초기화한다
3. WHEN 메뉴를 장바구니에 추가할 때 THEN 시스템 SHALL Shop.isOpen()을 확인하여 가게가 영업 중인지 검증한다
4. WHEN 동일한 메뉴와 옵션 조합이 추가될 때 THEN 시스템 SHALL CartLineItem.combine() 메서드를 사용하여 수량으로 병합한다
5. WHEN Cart.getTotalPrice() 메서드가 호출될 때 THEN 시스템 SHALL (메뉴 + 옵션) × 수량의 합계를 Money 타입으로 반환한다
6. IF 사용자가 식별되지 않거나 장바구니가 존재하지 않을 때 THEN 시스템 SHALL 적절한 오류 메시지를 반환한다

### Requirement 6: Order Context - 주문 처리

**User Story:** As a 고객, I want 장바구니의 내용으로 주문을 생성할 수 있기를, so that 선택한 메뉴들을 실제로 주문할 수 있다

#### Acceptance Criteria

1. WHEN Cart.placeOrder() 메서드가 호출될 때 AND 장바구니에 아이템이 존재할 때 AND 최소 주문금액을 충족할 때 THEN 시스템 SHALL 장바구니의 모든 정보를 주문으로 복사한다
2. WHEN 주문이 생성될 때 THEN 시스템 SHALL LocalDateTime.now()를 사용하여 주문 생성 시점을 자동으로 기록한다
3. WHEN Order.getPrice() 메서드가 호출될 때 THEN 시스템 SHALL 주문 라인 아이템별 금액의 합계를 Money 타입으로 반환한다
4. IF 장바구니가 비어있거나 최소 주문금액을 충족하지 않을 때 THEN 시스템 SHALL 주문 생성을 거부하고 오류 메시지를 반환한다

### Requirement 7: User Context - 사용자 식별

**User Story:** As a 시스템, I want 사용자를 식별하고 관리할 수 있기를, so that 사용자별로 장바구니와 주문을 구분할 수 있다

#### Acceptance Criteria

1. WHEN 사용자 식별 요청이 있을 때 THEN 시스템 SHALL Long 타입 ID를 통한 유효성 확인을 수행한다
2. WHEN 유효한 사용자 ID가 제공될 때 THEN 시스템 SHALL 유효한 UserId를 반환한다
3. WHEN 사용자별 장바구니 조회 요청이 있을 때 AND 사용자가 식별될 때 THEN 시스템 SHALL 해당 사용자의 장바구니를 반환한다
4. IF 장바구니가 존재하지 않을 때 THEN 시스템 SHALL 적절한 오류 메시지를 반환한다

### Requirement 8: 컨텍스트 간 상호작용

**User Story:** As a 시스템, I want 바운디드 컨텍스트 간 안전한 상호작용을 보장할 수 있기를, so that 데이터 일관성과 시스템 무결성을 유지할 수 있다

#### Acceptance Criteria

1. WHEN Order Context에서 장바구니 아이템을 생성할 때 THEN 시스템 SHALL Shop Context의 API를 통해 메뉴 정보를 조회한다
2. WHEN 장바구니에 메뉴를 추가할 때 THEN 시스템 SHALL Shop Context의 API를 통해 가게 영업 상태를 확인한다
3. WHEN 옵션이 선택될 때 THEN 시스템 SHALL Shop Context의 API를 통해 옵션 정보의 유효성을 검증한다
4. WHEN User Context와 Order Context가 상호작용할 때 THEN 시스템 SHALL 사용자 ID를 통해 장바구니와 주문을 구분한다
5. WHEN CartLineItemMapper가 사용될 때 THEN 시스템 SHALL 장바구니 아이템을 메뉴 정보와 정확히 매핑한다
6. WHEN 가격 정보가 필요할 때 THEN 시스템 SHALL Shop Context의 옵션 가격 정보와 동기화를 검증한다

### Requirement 9: CQRS 패턴 적용

**User Story:** As a 개발자, I want CQRS 패턴을 통해 읽기와 쓰기를 분리할 수 있기를, so that 성능 최적화와 확장성을 확보할 수 있다

#### Acceptance Criteria

1. WHEN Command 작업이 수행될 때 THEN 시스템 SHALL Repository 패턴을 사용하여 쓰기 최적화를 수행한다
2. WHEN Query 작업이 수행될 때 THEN 시스템 SHALL DAO 패턴을 사용하여 읽기 최적화를 수행한다
3. WHEN Command Handler가 실행될 때 THEN 시스템 SHALL @Transactional 어노테이션을 적용한다
4. WHEN Query Handler가 실행될 때 THEN 시스템 SHALL @Transactional(readOnly = true) 어노테이션을 적용한다
5. WHEN Read Model이 생성될 때 THEN 시스템 SHALL UI 최적화된 불변 데이터 객체로 구성한다

### Requirement 10: 멀티 프로젝트 구조

**User Story:** As a 개발팀, I want 독립적인 바운디드 컨텍스트로 시스템을 구성할 수 있기를, so that 각 도메인을 독립적으로 개발하고 배포할 수 있다

#### Acceptance Criteria

1. WHEN 각 컨텍스트가 빌드될 때 THEN 시스템 SHALL Common 모듈에만 의존하고 다른 컨텍스트에는 의존하지 않는다
2. WHEN Shop Context가 실행될 때 THEN 시스템 SHALL 8081 포트에서 독립적으로 실행된다
3. WHEN Order Context가 실행될 때 THEN 시스템 SHALL 8082 포트에서 독립적으로 실행된다
4. WHEN User Context가 실행될 때 THEN 시스템 SHALL 8083 포트에서 독립적으로 실행된다
5. WHEN 컨텍스트 간 통신이 필요할 때 THEN 시스템 SHALL HTTP REST API를 통해서만 통신한다