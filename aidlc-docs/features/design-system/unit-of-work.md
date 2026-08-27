# Unit of Work — design-system

> **Request Anchor**: 피그마 스타일 가이드(node 836:33127) 매니페스트를 기반으로 KMP 디자인 시스템 파운데이션을 `:core:designsystem` 모듈로 구축한다.

구조 참고: DroidKaigi conference-app-2026의 `core/designsystem` 패턴 (GATE-2.7에서 사용자 확정, 2026-08-27).

## UOW 목록

### UOW-1: `:core:designsystem` 모듈 스캐폴드
- Size: **M**
- 내용: `core/designsystem/build.gradle.kts` 신규 (KMP + compose 플러그인, android/iosArm64/iosSimulatorArm64 타깃 — `:shared`와 동일 구성, `compose-components-resources` 포함), `settings.gradle.kts`에 include, `:shared`에 의존성 추가
- 전제: 없음
- Acceptance Criteria:
  - AC1: `./gradlew :core:designsystem:assembleDebug` 및 iOS 타깃 컴파일(`compileKotlinIosSimulatorArm64`) 성공
  - AC2: `:shared` → `:core:designsystem` 의존 상태로 전체 빌드 통과
- 예상 질문 수: 0

### UOW-2: 컬러 / 스페이싱 토큰
- Size: **S**
- 내용: `SikdorokColors`(13종, semantic + raw 팔레트), `SikdorokSpacing`(8종 Dp). 값 출처는 design-manifest.md
- 전제: UOW-1
- Acceptance Criteria:
  - AC1: 13개 컬러 hex/alpha가 매니페스트와 1:1 일치 — 단위 테스트로 검증
  - AC2: 스페이싱 4/8/12/16/20/24/32/40 Dp 노출
- 예상 질문 수: 0

### UOW-3: 타이포그래피 + 이서윤체 폰트 번들
- Size: **M**
- 내용: `lee_seoyun_regular.ttf` compose 리소스 로딩(파일은 이미 `core/designsystem/src/commonMain/composeResources/font/`에 배치됨), FontFamily 구성, `SikdorokTypography` 7종 TextStyle (letterSpacing 0 — P2 자동결정)
- 전제: UOW-1
- Acceptance Criteria:
  - AC1: 폰트 리소스가 android/iOS 공통 번들에 포함되고 `Res.font.lee_seoyun_regular` 접근 가능
  - AC2: 7개 TextStyle의 fontSize/lineHeight/letterSpacing이 매니페스트와 일치 — 단위 테스트로 검증
- 예상 질문 수: 1 (compose resources의 비동기 폰트 로딩 API 선택 — 기술설계에서 해소)

### UOW-4: SikdorokTheme + M3 브리지 + 앱 적용
- Size: **M**
- 내용: CompositionLocal 3종 + `SikdorokTheme` 컴포저블(내부에서 MaterialTheme 브리지 — Q3=C), `shared App.kt` 루트 래핑
- 전제: UOW-2, UOW-3
- Acceptance Criteria:
  - AC1: 하위 컴포저블에서 `SikdorokTheme.colors/typography/spacing` 접근 동작
  - AC2: `MaterialTheme.colorScheme` 주요 값(primary/background/onBackground/error)이 Sikdorok 토큰과 일치 — 테스트 검증
  - AC3: androidApp 빌드 + iOS 시뮬레이터 타깃 컴파일 통과 상태로 App.kt 래핑 완료
- 예상 질문 수: 0

## 의존 그래프

```
UOW-1 --> UOW-2 --> UOW-4
UOW-1 --> UOW-3 --> UOW-4
```

텍스트 설명: UOW-1이 선행. UOW-2와 UOW-3은 병렬 가능. UOW-4가 최종 통합. 순환 없음.

## 응집도 검증

- 단일 도메인(디자인 파운데이션) — 이종 도메인 혼입 없음
- 유닛별 예상 질문 수 최대 1 → 적정 크기
- 외부 연동 없음 (폰트 파일은 이미 로컬 확보)

## 사이즈 요약

| UOW | Size |
|-----|------|
| 1 | M |
| 2 | S |
| 3 | M |
| 4 | M |

M 3건 → STEP 6.5 기술 설계 필수 (GATE-3.5), STEP 9 빌드/테스트 가이드 대상 (GATE-5).
