# Implementation Plan

- [ ] 1. 이벤트 인프라스트럭처 기반 구축
  - [ ] 1.1 Common 모듈 이벤트 인터페이스 구현
    - EventPublisher 인터페이스 작성
    - OutboxEvent 엔티티 및 Repository 구현
    - DomainEventListener 구현 (Spring ApplicationEvent 수신)
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5_
  
  - [ ] 1.2 트랜잭셔널 아웃박스 패턴 구현
    - OutboxEventPublisher 스케줄러 구현 (@Scheduled 사용)
    - 재시도 로직 및 DLQ 처리 구현
    - 이벤트 직렬화/역직렬화 유틸리티 구현
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6_
  
  - [ ] 1.3 AggregateRoot 도메인 이벤트 지원 강화
    - @DomainEvents, @AfterDomainEventPublication 어노테이션 활용
    - 기존 AggregateRoot 클래스에 도메인 이벤트 관리 기능 추가
    - _Requirements: 1.1, 1.4_

- [ ] 2. Kafka 통합 및 설정
  - [ ] 2.1 Kafka 설정 및 인프라스트럭처
    - Docker Compose에 Kafka 클러스터 추가
    - KafkaConfig 클래스 구현 (Producer/Consumer 설정)
    - 토픽 생성 스크립트 작성 (shop-events, order-events, user-events, dead-letter-queue)
    - _Requirements: 3.1, 3.2, 8.1_
  
  - [ ] 2.2 KafkaEventPublisher 구현
    - KafkaTemplate 기반 이벤트 발행 구현
    - 이벤트 직렬화 및 토픽 라우팅 로직 구현
    - 발행 실패 시 에러 핸들링 구현
    - _Requirements: 3.1, 3.3, 3.4, 3.5, 3.6_
  
  - [ ] 2.3 이벤트 에러 처리 및 DLQ 구현
    - EventProcessingException 및 EventErrorHandler 구현
    - Kafka 에러 핸들러 설정 (SeekToCurrentErrorHandler)
    - DLQ 토픽으로 실패한 메시지 전송 로직 구현
    - _Requirements: 3.6, 7.1, 7.2_

- [ ] 3. Shop Context 이벤트 발행 구현
  - [ ] 3.1 Shop 애그리게이트 이벤트 발행
    - Shop.close() 메서드에서 ShopClosedEvent 발행 구현
    - Shop 통계 업데이트 기능 및 관련 이벤트 추가
    - _Requirements: 4.2_
  
  - [ ] 3.2 Menu 애그리게이트 이벤트 발행
    - Menu.open() 메서드에서 MenuOpenedEvent 발행 구현
    - 기존 MenuOpenedEvent 클래스 검토 및 필요시 수정
    - _Requirements: 4.1_
  
  - [ ] 3.3 Shop Context Outbox 테이블 생성
    - Shop Context용 outbox_event 테이블 생성
    - OutboxEventRepository 의존성 주입 및 설정
    - _Requirements: 2.1, 2.2_

- [ ] 4. Order Context 이벤트 발행 및 소비 구현
  - [ ] 4.1 Order 애그리게이트 이벤트 발행
    - Order.from() 메서드에서 OrderPlacedEvent 발행 구현
    - 기존 OrderPlacedEvent 클래스 검토 및 필요시 수정
    - _Requirements: 5.1_
  
  - [ ] 4.2 Cart 애그리게이트 강화
    - Cart.deactivate() 메서드 구현 (가게 종료 시 장바구니 비활성화)
    - Cart.createEmpty() 정적 메서드 구현 (신규 사용자용)
    - 관련 도메인 이벤트 발행 로직 추가
    - _Requirements: 4.3, 6.2_
  
  - [ ] 4.3 Order Context 이벤트 소비자 구현
    - ShopEventHandler 구현 (@KafkaListener 사용)
    - ShopClosedEvent 처리 로직 구현 (활성 장바구니 비활성화)
    - UserEventHandler 구현 (UserRegisteredEvent 처리)
    - _Requirements: 4.3, 6.2_
  
  - [ ] 4.4 Order Context Outbox 테이블 생성
    - Order Context용 outbox_event 테이블 생성
    - OutboxEventRepository 의존성 주입 및 설정
    - _Requirements: 2.1, 2.2_

- [ ] 5. User Context 이벤트 발행 및 소비 구현
  - [ ] 5.1 User 애그리게이트 이벤트 발행
    - User 생성 시 UserRegisteredEvent 발행 구현
    - 기존 UserRegisteredEvent 클래스 검토 및 필요시 수정
    - _Requirements: 6.1_
  
  - [ ] 5.2 User Context 이벤트 소비자 구현
    - OrderEventHandler 구현 (@KafkaListener 사용)
    - OrderPlacedEvent 처리 로직 구현 (사용자 주문 이력 업데이트)
    - UserOrderHistoryService 구현
    - _Requirements: 5.2, 5.3_
  
  - [ ] 5.3 User Context Outbox 테이블 생성
    - User Context용 outbox_event 테이블 생성
    - OutboxEventRepository 의존성 주입 및 설정
    - _Requirements: 2.1, 2.2_

- [ ] 6. Shop Context 이벤트 소비자 구현
  - [ ] 6.1 Shop Context 이벤트 소비자 구현
    - OrderEventHandler 구현 (@KafkaListener 사용)
    - OrderPlacedEvent 처리 로직 구현 (가게 주문 통계 업데이트)
    - ShopStatisticsService 구현
    - _Requirements: 5.2_
  
  - [ ] 6.2 Shop 애그리게이트 통계 기능 강화
    - Shop 엔티티에 totalOrders, totalRevenue 필드 추가
    - updateOrderStatistics() 메서드 구현
    - 관련 데이터베이스 스키마 업데이트
    - _Requirements: 5.2_

- [ ] 7. 이벤트 처리 안정성 및 모니터링 구현
  - [ ] 7.1 멱등성 보장 구현
    - 이벤트 ID 기반 중복 처리 방지 로직 구현
    - 각 이벤트 핸들러에 멱등성 체크 로직 추가
    - _Requirements: 7.3_
  
  - [ ] 7.2 이벤트 순서 보장 구현
    - Kafka 파티션 키 설정 (애그리게이트 ID 기반)
    - 순서가 중요한 이벤트에 대한 파티셔닝 전략 구현
    - _Requirements: 7.4_
  
  - [ ] 7.3 이벤트 모니터링 로깅 구현
    - 이벤트 발행/처리 시 상세 로그 기록
    - 처리 시간, 성공/실패 상태 로깅
    - 성능 메트릭 수집을 위한 로그 구조 설계
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5, 8.6_

- [ ] 8. 테스트 인프라스트럭처 구현
  - [ ] 8.1 이벤트 테스트 유틸리티 구현
    - EventTestUtils 클래스 구현
    - @DomainEvents 테스트 헬퍼 메서드 구현
    - Outbox 이벤트 검증 유틸리티 구현
    - _Requirements: 8.3, 8.4_
  
  - [ ] 8.2 Kafka 통합 테스트 환경 구축
    - TestContainers 기반 Kafka 테스트 환경 구성
    - @KafkaListener 테스트 유틸리티 구현
    - 이벤트 발행부터 처리까지 전체 플로우 테스트 구현
    - _Requirements: 8.2, 8.4_
  
  - [ ] 8.3 단위 테스트 작성
    - 각 이벤트 핸들러 단위 테스트 작성
    - OutboxEventPublisher 단위 테스트 작성
    - 도메인 이벤트 발행 단위 테스트 작성
    - _Requirements: 8.3, 8.4_

- [ ] 9. 통합 테스트 및 End-to-End 시나리오 검증
  - [ ] 9.1 Shop Context 이벤트 시나리오 테스트
    - 메뉴 공개 → 이벤트 발행 → Outbox 저장 → Kafka 발행 플로우 테스트
    - 가게 종료 → 이벤트 발행 → Order Context에서 장바구니 비활성화 플로우 테스트
    - _Requirements: 4.1, 4.2, 4.3_
  
  - [ ] 9.2 Order Context 이벤트 시나리오 테스트
    - 주문 생성 → 이벤트 발행 → Shop/User Context에서 통계 업데이트 플로우 테스트
    - 신규 사용자 등록 → 빈 장바구니 생성 플로우 테스트
    - _Requirements: 5.1, 5.2, 5.3, 6.2_
  
  - [ ] 9.3 User Context 이벤트 시나리오 테스트
    - 사용자 등록 → 이벤트 발행 → Order Context에서 장바구니 생성 플로우 테스트
    - 주문 이력 업데이트 플로우 테스트
    - _Requirements: 6.1, 6.2_
  
  - [ ] 9.4 에러 처리 및 복구 시나리오 테스트
    - 이벤트 처리 실패 → 재시도 → DLQ 이동 플로우 테스트
    - Kafka 브로커 다운 → 복구 → 미처리 이벤트 처리 플로우 테스트
    - _Requirements: 7.1, 7.2_

- [ ] 10. 성능 최적화 및 운영 준비
  - [ ] 10.1 배치 처리 최적화
    - OutboxEventPublisher 배치 크기 최적화
    - Kafka Producer 배치 설정 최적화
    - 대량 이벤트 처리 성능 테스트
    - _Requirements: 10.1, 10.3_
  
  - [ ] 10.2 Kafka 토픽 및 파티션 설정 최적화
    - 각 토픽별 파티션 수 설정
    - Retention 정책 설정
    - 압축 설정 적용
    - _Requirements: 10.4, 10.5, 10.6_
  
  - [ ] 10.3 모니터링 및 알림 설정
    - DLQ 메시지 누적 시 알림 로직 구현
    - 이벤트 처리 지연 모니터링 구현
    - 시스템 상태 체크 엔드포인트 구현
    - _Requirements: 8.4, 8.5_

- [ ] 11. 문서화 및 운영 가이드 작성
  - [ ] 11.1 이벤트 카탈로그 문서 작성
    - 각 바운디드 컨텍스트별 발행/소비 이벤트 목록
    - 이벤트 스키마 및 예시 데이터
    - 이벤트 플로우 다이어그램
    - _Requirements: 4.1, 4.2, 4.3, 5.1, 5.2, 5.3, 6.1, 6.2_
  
  - [ ] 11.2 운영 가이드 문서 작성
    - Kafka 클러스터 운영 가이드
    - 이벤트 처리 장애 대응 가이드
    - 성능 모니터링 및 튜닝 가이드
    - _Requirements: 7.1, 7.2, 7.3, 7.4_
  
  - [ ] 11.3 개발자 가이드 문서 작성
    - 새로운 이벤트 추가 방법
    - 이벤트 핸들러 작성 가이드
    - 테스트 작성 가이드
    - _Requirements: 8.1, 8.2, 8.3, 8.4_

## 이벤트 시나리오 체크리스트

### ✅ Shop Context 이벤트
- [ ] MenuOpenedEvent 발행 (Menu.open() 호출 시)
- [ ] ShopClosedEvent 발행 (Shop.close() 호출 시)
- [ ] OrderPlacedEvent 소비 (가게 통계 업데이트)

### ✅ Order Context 이벤트
- [ ] OrderPlacedEvent 발행 (Order.from() 호출 시)
- [ ] ShopClosedEvent 소비 (활성 장바구니 비활성화)
- [ ] UserRegisteredEvent 소비 (빈 장바구니 생성)

### ✅ User Context 이벤트
- [ ] UserRegisteredEvent 발행 (User 생성 시)
- [ ] OrderPlacedEvent 소비 (사용자 주문 이력 업데이트)

## 기술적 검증 체크리스트

### ✅ 트랜잭셔널 아웃박스 패턴
- [ ] 도메인 이벤트가 Outbox 테이블에 저장됨
- [ ] 비즈니스 트랜잭션과 이벤트 저장이 원자적으로 처리됨
- [ ] OutboxEventPublisher가 주기적으로 미발행 이벤트를 처리함
- [ ] 발행 성공 시 Outbox에서 이벤트가 삭제되거나 발행 완료로 표시됨

### ✅ Kafka 통합
- [ ] 이벤트가 올바른 토픽으로 발행됨
- [ ] 컨슈머가 이벤트를 정상적으로 수신하고 처리함
- [ ] 파티션 키를 통한 순서 보장이 동작함
- [ ] 에러 발생 시 DLQ로 메시지가 이동함

### ✅ 안정성 및 복구
- [ ] 이벤트 처리 실패 시 재시도가 동작함
- [ ] 최대 재시도 후 DLQ로 이동함
- [ ] 멱등성 보장으로 중복 처리가 방지됨
- [ ] Kafka 브로커 장애 후 복구 시 미처리 이벤트가 처리됨

### ✅ 성능 및 모니터링
- [ ] 배치 처리로 성능이 최적화됨
- [ ] 이벤트 발행/처리 로그가 적절히 기록됨
- [ ] 시스템 상태 모니터링이 가능함
- [ ] 대량 이벤트 처리 시 성능이 유지됨

이 구현 계획은 트랜잭셔널 아웃박스 패턴과 Kafka를 활용한 완전한 이벤트 드리븐 아키텍처 구축을 위한 체계적인 가이드를 제공합니다.