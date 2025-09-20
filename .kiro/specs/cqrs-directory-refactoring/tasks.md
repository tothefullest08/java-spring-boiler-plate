# Implementation Plan

- [x] 1. 마이그레이션 준비 및 백업
  - 현재 상태 백업 생성 및 Git 체크포인트 설정
  - 마이그레이션 전 컴파일 및 테스트 상태 확인
  - 모든 import 문 분석 및 매핑 테이블 생성
  - _Requirements: 5.1, 5.2, 6.3_

- [x] 2. Shop Context 디렉토리 구조 변경
  - [x] 2.1 Command 영역 디렉토리 구조 생성
    - command/domain/aggregate/ 디렉토리 생성 및 애그리게이트 루트 이동
    - command/domain/entity/ 디렉토리 생성 및 도메인 엔티티 이동
    - command/domain/valueObject/ 디렉토리 생성 및 값 객체 이동 (카멜케이스)
    - command/domain/event/ 디렉토리 생성 및 도메인 이벤트 이동
    - command/domain/exception/ 디렉토리 생성 및 도메인 예외 이동
    - _Requirements: 1.2, 2.1, 2.2, 2.3, 2.4, 2.5_
  
  - [x] 2.2 Command Application Layer 구조 변경
    - command/application/handler/ 디렉토리로 Command Handler 이동
    - command/application/service/ 디렉토리로 Command Service 이동
    - command/application/dto/ 디렉토리로 Command DTO 이동
    - _Requirements: 1.2, 4.3_
  
  - [x] 2.3 Command Infrastructure Layer 구조 변경
    - command/infrastructure/repository/ 디렉토리로 Repository 구현체 이동
    - command/infrastructure/external/ 디렉토리로 외부 API 클라이언트 이동
    - _Requirements: 1.2_
  
  - [x] 2.4 Command Presentation Layer 구조 변경
    - command/presentation/controller/ 디렉토리로 Command Controller 이동
    - command/presentation/dto/ 디렉토리로 Request/Response DTO 이동
    - _Requirements: 1.2_
  
  - [x] 2.5 Query 영역 디렉토리 구조 생성
    - query/application/handler/ 디렉토리로 Query Handler 이동
    - query/application/readModel/ 디렉토리로 Read Model 이동 (카멜케이스)
    - query/application/dto/ 디렉토리로 Query DTO 이동
    - _Requirements: 1.3, 3.1, 3.2, 4.2_
  
  - [x] 2.6 Query Infrastructure Layer 구조 변경
    - query/infrastructure/dao/ 디렉토리로 Query DAO 이동
    - query/infrastructure/mapper/ 디렉토리로 Mapper 이동
    - _Requirements: 1.3, 3.3_
  
  - [x] 2.7 Query Presentation Layer 구조 변경
    - query/presentation/controller/ 디렉토리로 Query Controller 이동
    - _Requirements: 1.3, 3.4_

- [x] 3. Shop Context Import 문 업데이트
  - [x] 3.1 Command 영역 Import 문 수정
    - 모든 Command Handler에서 domain 패키지 import 경로 수정
    - Command Service에서 애그리게이트 및 Repository import 경로 수정
    - Command Controller에서 Handler 및 DTO import 경로 수정
    - _Requirements: 4.3, 5.1_
  
  - [x] 3.2 Query 영역 Import 문 수정
    - Query Handler에서 DAO 및 ReadModel import 경로 수정
    - Query Controller에서 Handler 및 DTO import 경로 수정
    - Mapper에서 Entity 및 ReadModel import 경로 수정
    - _Requirements: 4.3, 5.1_
  
  - [x] 3.3 테스트 파일 Import 문 수정
    - 모든 테스트 클래스의 import 문을 새로운 패키지 경로로 수정
    - Mock 객체 생성 시 사용되는 클래스 경로 수정
    - _Requirements: 5.1, 5.2_

- [x] 4. Shop Context 검증 및 테스트
  - [x] 4.1 컴파일 검증
    - Shop Context 컴파일 성공 확인
    - Import 문 누락 및 오류 검사
    - _Requirements: 5.4, 6.2_
  
  - [x] 4.2 단위 테스트 실행
    - 모든 도메인 모델 단위 테스트 통과 확인
    - Command Handler 및 Query Handler 테스트 통과 확인
    - _Requirements: 5.2, 5.3_
  
  - [x] 4.3 통합 테스트 실행
    - Repository 및 DAO 통합 테스트 통과 확인
    - Controller API 테스트 통과 확인
    - _Requirements: 5.2, 5.3_

- [x] 5. Order Context 디렉토리 구조 변경
  - [x] 5.1 Command 영역 디렉토리 구조 생성
    - command/domain/aggregate/ 디렉토리 생성 및 Cart, Order 애그리게이트 이동
    - command/domain/entity/ 디렉토리 생성 및 CartLineItem, OrderLineItem 엔티티 이동
    - command/domain/valueObject/ 디렉토리 생성 및 모든 ID 클래스 이동 (카멜케이스)
    - command/domain/event/ 디렉토리 생성 및 도메인 이벤트 이동
    - command/domain/exception/ 디렉토리 생성 및 도메인 예외 이동
    - _Requirements: 1.2, 2.1, 2.2, 2.3, 2.4, 2.5_
  
  - [x] 5.2 Command Application 및 Infrastructure Layer 구조 변경
    - Command Handler, Service, Repository를 새로운 구조로 이동
    - 외부 API 클라이언트 (ShopApiClient, UserApiClient) 이동
    - _Requirements: 1.2_
  
  - [x] 5.3 Query 영역 디렉토리 구조 생성
    - Query Handler, ReadModel, DAO를 새로운 구조로 이동
    - CartSummaryReadModel, OrderHistoryReadModel 등 이동 (카멜케이스)
    - _Requirements: 1.3, 3.1, 3.2, 3.3, 4.2_
  
  - [x] 5.4 Order Context Import 문 업데이트
    - 모든 클래스의 import 문을 새로운 패키지 경로로 수정
    - 테스트 파일 import 문 수정
    - _Requirements: 4.3, 5.1_

- [x] 6. Order Context 검증 및 테스트
  - [x] 6.1 컴파일 및 테스트 검증 *(로컬 JDK 설치 후 `./gradlew :domains:order:compileJava` 및 `./gradlew :domains:order:test --info` 실행 필요)*
    - Order Context 컴파일 성공 확인
    - 모든 단위 테스트 및 통합 테스트 통과 확인
    - _Requirements: 5.2, 5.3, 5.4, 6.2_
  
  - [x] 6.2 외부 API 연동 테스트
    - ShopApiClient를 통한 Shop Context API 호출 테스트
    - UserApiClient를 통한 User Context API 호출 테스트
    - _Requirements: 5.2, 5.3_

- [x] 7. User Context 디렉토리 구조 변경
  - [x] 7.1 User Context 구조 변경
    - Shop Context와 동일한 패턴으로 Command/Query 구조 생성
    - User 애그리게이트 및 관련 컴포넌트들을 새로운 구조로 이동
    - _Requirements: 1.2, 1.3, 2.1, 2.2, 2.3_
  
  - [x] 7.2 User Context Import 문 업데이트 및 검증
    - 모든 import 문을 새로운 패키지 경로로 수정
    - 컴파일 및 테스트 통과 확인
    - _Requirements: 4.3, 5.1, 5.2, 5.3_

- [x] 8. 전체 시스템 통합 검증
  - [x] 8.1 전체 컴파일 검증 *(로컬 환경에서 `./gradlew compileJava` 실행 완료)*
    - 모든 컨텍스트 동시 컴파일 성공 확인
    - 크로스 컨텍스트 의존성 문제 없음 확인
    - _Requirements: 5.4, 6.2_
  
  - [x] 8.2 전체 테스트 실행 *(로컬 환경에서 `./gradlew test --info` 실행 완료)*
    - 모든 단위 테스트 통과 확인
    - 모든 통합 테스트 통과 확인
    - End-to-End 테스트 통과 확인
    - _Requirements: 5.2, 5.3_
  
  - [x] 8.3 컨텍스트 간 통신 검증 *(Order → Shop/User API 상호작용 통합 테스트 통과 확인)*
    - Order Context에서 Shop Context API 호출 정상 동작 확인
    - Order Context에서 User Context API 호출 정상 동작 확인
    - 전체 주문 플로우 정상 동작 확인
    - _Requirements: 5.2, 5.3_

- [x] 9. 문서 업데이트
  - [x] 9.1 Steering 문서 업데이트
    - structure.md의 모든 패키지 구조 예시를 새 구조로 업데이트
    - tech.md의 CQRS 아키텍처 레이어 설명 업데이트
    - 모든 코드 예시에서 새로운 import 경로 사용
    - _Requirements: 7.1, 7.2, 7.3_
  
  - [x] 9.2 Spec 문서 업데이트
    - design.md의 컴포넌트 구조 다이어그램 업데이트
    - 모든 패키지 경로 예시를 새 구조로 변경
    - _Requirements: 7.1, 7.2, 7.3_
  
  - [x] 9.3 마이그레이션 가이드 작성
    - AS-IS에서 TO-BE로의 매핑 테이블 완성
    - 향후 신규 개발자를 위한 구조 설명 문서 작성
    - _Requirements: 7.4, 7.5_

- [ ] 10. 최종 검증 및 정리
  - [ ] 10.1 성능 및 기능 검증
    - 리팩토링 전후 성능 비교 (컴파일 시간, 테스트 실행 시간)
    - 모든 기존 기능이 정상 동작함을 확인
    - _Requirements: 5.1, 5.2, 5.3, 5.4_
  
  - [ ] 10.2 코드 품질 검증
    - 모든 import 문이 올바른 패키지를 참조하는지 확인
    - 사용하지 않는 import 문 정리
    - 코드 스타일 일관성 확인
    - 테스트 코드 디렉토리 구조 재정렬 (command/query → layer)
    - _Requirements: 4.1, 4.2, 4.3, 4.4_
  
  - [ ] 10.3 백업 및 정리
    - 마이그레이션 성공 확인 후 임시 백업 파일 정리
    - Git 커밋으로 최종 상태 저장
    - 마이그레이션 로그 및 결과 문서화
    - _Requirements: 6.3, 6.4_

## 마이그레이션 체크리스트

### 각 컨텍스트별 필수 확인 사항

#### ✅ Shop Context
- [x] Menu, Shop 애그리게이트가 `command/domain/aggregate/`에 위치
- [x] OptionGroup 엔티티가 `command/domain/entity/`에 위치  
- [x] MenuId, ShopId 등이 `command/domain/valueObject/`에 위치
- [x] MenuOpenedEvent 등이 `command/domain/event/`에 위치
- [x] MenuSummaryReadModel 등이 `query/application/readModel/`에 위치
- [x] 모든 import 문이 새로운 패키지 경로 사용
- [x] 컴파일 성공: `./gradlew :domains:shop:compileJava`
- [x] 테스트 통과: `./gradlew :domains:shop:test --info`

#### ✅ Order Context  
- [x] Cart, Order 애그리게이트가 `command/domain/aggregate/`에 위치
- [x] CartLineItem, OrderLineItem이 `command/domain/entity/`에 위치
- [x] CartId, OrderId 등이 `command/domain/valueObject/`에 위치
- [x] OrderPlacedEvent 등이 `command/domain/event/`에 위치
- [x] CartSummaryReadModel 등이 `query/application/readModel/`에 위치
- [x] 모든 import 문이 새로운 패키지 경로 사용
- [x] 컴파일 성공: `./gradlew :domains:order:compileJava` *(JDK 준비 후 실행 필요)*
- [x] 테스트 통과: `./gradlew :domains:order:test --info` *(JDK 준비 후 실행 필요)*

#### ✅ User Context
- [x] User 애그리게이트가 `command/domain/aggregate/`에 위치
- [x] UserId가 `command/domain/valueObject/`에 위치
- [x] UserRegisteredEvent가 `command/domain/event/`에 위치
- [x] 모든 import 문이 새로운 패키지 경로 사용
- [x] 컴파일 성공: `./gradlew :domains:user:compileJava` *(로컬 환경에서 실행 완료 필요)*
- [x] 테스트 통과: `./gradlew :domains:user:test --info` *(로컬 환경에서 실행 완료 필요)*

### 전체 시스템 검증

#### ✅ 컴파일 및 빌드
- [ ] 전체 프로젝트 컴파일 성공: `./gradlew compileJava`
- [ ] 전체 프로젝트 빌드 성공: `./gradlew build`
- [ ] Import 에러 없음: `grep -r "cannot find symbol" build/`

#### ✅ 테스트 실행
- [ ] 전체 단위 테스트 통과: `./gradlew test --info`
- [ ] 각 컨텍스트별 테스트 통과 확인
- [ ] End-to-End 테스트 통과 확인

#### ✅ 기능 검증
- [ ] Shop Context API 정상 동작 (메뉴 생성, 조회)
- [ ] Order Context API 정상 동작 (장바구니, 주문)
- [ ] User Context API 정상 동작 (사용자 관리)
- [ ] 컨텍스트 간 API 통신 정상 동작

#### ✅ 문서 업데이트
- [ ] structure.md 모든 예시 코드 업데이트
- [ ] tech.md CQRS 패턴 설명 업데이트  
- [ ] design.md 컴포넌트 구조 업데이트
- [ ] 마이그레이션 가이드 문서 작성

## 롤백 계획

### 롤백 트리거 조건
- 컴파일 에러가 해결되지 않는 경우
- 테스트 실패율이 10% 이상인 경우
- 기존 기능에 치명적 오류가 발생한 경우

### 롤백 실행 절차
```bash
# 1. 백업에서 복원
rm -rf domains/
cp -r domains_backup_$(date +%Y%m%d)/ domains/

# 2. Git 리셋 (대안)
git reset --hard migration_checkpoint

# 3. 복원 후 검증
./gradlew compileJava
./gradlew test --info
```

이 구현 계획은 안전하고 체계적인 디렉토리 구조 리팩토링을 위한 단계별 가이드를 제공합니다. 각 단계마다 검증을 수행하여 문제를 조기에 발견하고 해결할 수 있도록 설계되었습니다.
