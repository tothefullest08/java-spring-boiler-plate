# 테스트 명령어 가이드

## 🚨 필수 규칙: 모든 테스트 명령어에 --info 옵션 포함

**모든 테스트 실행 시 반드시 `--info` 옵션을 포함해야 합니다.**

## 📋 표준 테스트 명령어

### 컨텍스트별 테스트 실행
```bash
# Common 모듈 테스트
./gradlew :domains:common:test --info

# Shop Context 테스트  
./gradlew :domains:shop:test --info

# Order Context 테스트
./gradlew :domains:order:test --info

# User Context 테스트
./gradlew :domains:user:test --info

# 전체 프로젝트 테스트
./gradlew test --info
```

### 특정 테스트 클래스 실행
```bash
# Shop Context의 MenuTest 클래스만 실행
./gradlew :domains:shop:test --tests MenuTest --info

# Order Context의 CartTest 클래스만 실행
./gradlew :domains:order:test --tests CartTest --info

# 여러 테스트 클래스 동시 실행
./gradlew :domains:shop:test --tests "MenuTest,OptionTest" --info
```

### 특정 테스트 메서드 실행
```bash
# 특정 메서드만 실행
./gradlew :domains:shop:test --tests MenuTest.메뉴_생성_성공 --info

# 패턴 매칭으로 여러 메서드 실행
./gradlew :domains:shop:test --tests "MenuTest.*생성*" --info
```

### 고급 테스트 옵션
```bash
# 테스트 실패 시 스택트레이스 포함
./gradlew :domains:shop:test --info --stacktrace

# 테스트 실패해도 계속 진행
./gradlew :domains:shop:test --info --continue

# 디버그 레벨 출력 (매우 상세)
./gradlew :domains:shop:test --debug

# 병렬 테스트 실행
./gradlew test --info --parallel
```

## ✅ --info 옵션의 장점

### 1. 실시간 에러 확인
- 테스트 실패 시 에러 메시지가 터미널에 즉시 출력
- HTML 리포트를 별도로 열 필요 없음

### 2. 상세한 실패 정보
```bash
# --info 없이 실행 시
> Task :domains:shop:test FAILED
60 tests completed, 19 failed

# --info 포함 실행 시  
> Task :domains:shop:test FAILED
MenuTest > 메뉴_생성_실패_이름_null() FAILED
    java.lang.AssertionError: 
    Expecting actual throwable to be an instance of:
      harry.boilerplate.shop.domain.exception.MenuDomainException
    but was:
      java.lang.IllegalArgumentException: 메뉴 이름은 필수입니다
        at harry.boilerplate.shop.domain.aggregate.Menu.<init>(Menu.java:50)
```

### 3. 빠른 문제 해결
- 실패 원인을 즉시 파악 가능
- 어떤 라인에서 에러가 발생했는지 정확한 위치 제공
- 기대값과 실제값의 차이를 명확하게 표시

## ❌ 금지된 명령어

**다음과 같은 `--info` 옵션이 없는 명령어는 절대 사용 금지:**

```bash
# ❌ 금지
./gradlew :domains:shop:test
./gradlew test
./gradlew :domains:order:test --tests CartTest

# ✅ 올바른 사용
./gradlew :domains:shop:test --info
./gradlew test --info  
./gradlew :domains:order:test --tests CartTest --info
```

## 🔧 IDE 통합

### IntelliJ IDEA 설정
1. **Run/Debug Configurations** 열기
2. **Gradle** 템플릿 선택
3. **Arguments** 필드에 `--info` 추가
4. **Apply** 클릭

### VS Code 설정
```json
// .vscode/tasks.json
{
    "version": "2.0.0",
    "tasks": [
        {
            "label": "Test with info",
            "type": "shell",
            "command": "./gradlew",
            "args": ["test", "--info"],
            "group": "test"
        }
    ]
}
```

## 📊 테스트 결과 해석

### 성공 시 출력 예시
```bash
BUILD SUCCESSFUL in 3s
60 tests completed, 60 passed
```

### 실패 시 출력 예시
```bash
BUILD FAILED in 2s
60 tests completed, 19 failed

MenuTest > 메뉴_생성_실패_이름_null() FAILED
    java.lang.AssertionError at MenuTest.java:82
```

## 🎯 베스트 프랙티스

1. **항상 --info 옵션 사용**
2. **실패한 테스트는 즉시 수정**
3. **테스트 실행 전 코드 컴파일 확인**
4. **특정 테스트만 실행하여 빠른 피드백**
5. **CI/CD 파이프라인에도 --info 옵션 적용**