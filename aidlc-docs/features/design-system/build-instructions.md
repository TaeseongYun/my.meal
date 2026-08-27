# Build Instructions — design-system

> **Request Anchor**: 피그마 스타일 가이드 매니페스트 기반 KMP 디자인 시스템 파운데이션을 `:core:designsystem` 모듈로 구축한다.

## UOW 구현 순서 (의존 그래프 반영)

```
UOW-1 (스캐폴드) --> UOW-2 (컬러/스페이싱) --+--> UOW-4 (테마/적용)
                --> UOW-3 (타이포/폰트)   --+
```

UOW-2와 UOW-3은 병렬 가능. UOW-4는 둘 다 완료 후.

## UOW별 빌드 검증 명령

### UOW-1: 모듈 스캐폴드
```bash
./gradlew :core:designsystem:assembleDebug
./gradlew :core:designsystem:compileKotlinIosSimulatorArm64
./gradlew :shared:assembleDebug          # 의존 연결 확인
```

### UOW-2 / UOW-3: 토큰 구현
```bash
./gradlew :core:designsystem:assembleDebug
# UOW-3 폰트 리소스 생성 확인: Res.font.lee_seoyun_regular 참조가 컴파일되면 통과
```

### UOW-4: 테마 + 앱 적용 (최종)
```bash
./gradlew :androidApp:assembleDebug
./gradlew :shared:compileKotlinIosSimulatorArm64
```

iOS 앱 실행 검증은 Xcode에서 `iosApp` 열어 시뮬레이터 실행 (README 기존 절차).

## 주의사항

- 신규 외부 의존성 금지 — 버전 카탈로그(`gradle/libs.versions.toml`) 기존 항목만 사용 (ADR-1)
- `core/designsystem/build.gradle.kts`는 `shared/build.gradle.kts`를 기준으로 작성 (iOS framework 블록은 제외 — 소비 전용 모듈)
- 폰트 파일은 이미 배치됨: `core/designsystem/src/commonMain/composeResources/font/lee_seoyun_regular.ttf`
