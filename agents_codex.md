# Repository Guidelines

## Project Structure & Module Organization
This workspace is a multi-module Gradle build. Bounded contexts live under `domains/` as independent Spring Boot services: `common` provides shared abstractions, while `shop`, `order`, and `user` each own their CQRS stacks. Within every context keep the `command/` and `query/` split strict—command-side domain logic belongs in `command/domain`, read models and DAOs in `query`. Shared integration adapters stay in root `src/main/java`, with supporting tests in `src/test/java`. Infrastructure assets sit under `docker/` alongside `docker-compose.yml` for local services.

## Steering & Specs Documents (필독)
- `.kiro/steering/product.md`: 바운디드 컨텍스트 책임, 핵심 애그리게이트 요약.
- `.kiro/steering/tech.md`: CQRS, 도메인 이벤트, 예외 규칙 및 필수 스택.
- `.kiro/steering/structure.md`: 멀티 모듈 디렉터리, 패키지 네이밍, DDD 상속 계층.
- `.kiro/steering/test-commands.md`: 테스트 실행 규칙—모든 명령은 `--info` 필수.
- `.kiro/specs/**/requirements.md|design.md|tasks.md`: EARS 요구사항, 설계 다이어그램, 진행 체크리스트. 구현 전후 반드시 참조하세요.

## Build, Test, and Development Commands
```bash
./gradlew compileJava                     # 전체 컴파일 검증
./gradlew test --info                     # 전체 테스트 (로그 필수)
./gradlew :domains:shop:test --info       # 컨텍스트별 테스트
./gradlew :domains:shop:bootRun --args='--spring.profiles.active=shop'
docker compose up -d db                   # MySQL 컨테이너 기동
```
All Gradle test runs **must** include `--info`; append `--stacktrace` or `--continue` as needed. Run bounded-context apps with `:domains:<context>:bootRun` and matching profiles.

## Prohibited Actions (🚨 금지)
```gradle
// 다른 컨텍스트 의존 금지
dependencies {
    implementation project(':domains:shop')   // ❌
    implementation project(':domains:common') // ✅
}
```
```bash
# --info 없는 테스트 실행 금지
./gradlew test              # ❌
./gradlew test --info       # ✅
```
```java
// ErrorCode 없는 예외 금지
throw new RuntimeException("에러");               // ❌
throw new MenuDomainException(MenuErrorCode.MENU_NOT_FOUND); // ✅
```

## Required Architecture Practices (✅ 필수)
- 유지: CQRS 패키지 구분 (`command/`, `query/`).
- 상속: AggregateRoot, DomainEntity, ValueObject 계층 엄수.
- 트랜잭션: Command `@Transactional`, Query `@Transactional(readOnly = true)`.
- 이벤트: 공통 `DomainEvent` 인터페이스, 구체 구현은 각 컨텍스트 `command/domain/event/`.

## Coding Style & Naming Conventions
Use Java 21, 4-space indentation, trailing newline. Packages follow `harry.boilerplate.<context>`. Command handlers adopt imperative names (`CreateMenuCommandHandler`), query handlers end with `QueryHandler`, and read models end with `ReadModel`. Error codes follow `{DOMAIN}-{LAYER}-{CODE}`.

## Testing Guidelines
Convert each EARS acceptance criterion from specs into a dedicated test. Place command-side tests under command packages and ensure query tests avoid domain mutations. Always assert on `ErrorCode` values. Use targeted runs via `./gradlew :domains:<context>:test --tests <Pattern> --info`. Keep Docker services running when persistence or integration tests execute.

## Development Workflow Checklist
- [ ] `.kiro/steering/` 문서 및 해당 스펙 확인.
- [ ] 유사 구현 패턴 검토 후 기존 파일 우선 수정.
- [ ] 변경 후 `./gradlew compileJava` → `./gradlew test --info` 순서로 검증.
- [ ] 바운디드 컨텍스트 격리와 CQRS 계층 규칙 준수 확인.

## Commit & Pull Request Guidelines
Follow existing history: start commit subjects with a type (`feat:`, `docs:`) or milestone marker (`CHECKPOINT:`, `COMPLETE:`) plus a succinct summary. Reference spec tasks or issue IDs when relevant. Pull requests must describe affected contexts, list executed Gradle commands (with `--info`), and outline any CQRS boundary decisions. Attach screenshots only for API/UX changes.

## Architecture & Environment Notes
Respect context isolation—only `domains:common` may be shared. Cross-context communication happens via REST or domain events; never link to another context’s code. Document infrastructure updates under `docker/` and sync steering specs when architectural patterns change. 파일 이동 시에는 `grep -r "클래스명" domains/`로 사용처 확인 후 import 수정, `./gradlew compileJava`, `./gradlew test --info` 순으로 재검증하세요.
