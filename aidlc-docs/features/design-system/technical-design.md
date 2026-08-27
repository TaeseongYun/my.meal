<!-- workflow-step: STEP-6.5 | gate: GATE-3.5 | producer: ctx-aidlc-run -->
# Technical Design — design-system

> **Request Anchor**: 피그마 스타일 가이드(node 836:33127) 매니페스트를 기반으로 KMP 디자인 시스템 파운데이션을 `:core:designsystem` 모듈로 구축한다.

## Implementation Scope

- Implementation level: `production`
- Mock allowance scope: `none`
- Items excluded from this implementation: 아이콘(Q2=B), 다크 모드(Q4=A), 컴포넌트 라이브러리
- Additional work when transitioning to Production: none

## 1. Design Overview

- Target system/module: 신규 `:core:designsystem` (KMP, android + iosArm64 + iosSimulatorArm64)
- Design direction: 피그마 매니페스트 값을 불변 토큰 클래스로 옮기고, CompositionLocal로 노출. MaterialTheme 브리지는 순수 함수로 분리해 컴포지션 없이 테스트 가능하게 한다. 구조는 DroidKaigi conference-app-2026의 core/designsystem 패턴을 따른다 (GATE-2.7 확정).
- brownfield touchpoints: `settings.gradle.kts`(include 추가), `shared/build.gradle.kts`(의존 추가), `shared App.kt`(테마 래핑) — 3곳뿐

## 2. Architecture Decisions

### ADR-1. 모듈 빌드 구성은 `:shared` 설정을 미러링

- Status: accepted
- Context: 프로젝트가 AGP 9의 신규 KMP 라이브러리 플러그인(`com.android.kotlin.multiplatform.library`)을 사용 중. 검증된 조합을 벗어나면 리스크
- Options:
  - A) shared의 플러그인/타깃 구성 복제 (kotlinMultiplatform + androidMultiplatformLibrary + composeMultiplatform + composeCompiler) — 검증된 조합, 일관성
  - B) 전통 `com.android.library` 플러그인 — 프로젝트 내 이례적 구성이 됨
- Decision: A. namespace `com.devts.mymeal.core.designsystem`, `androidResources.enable = true` (compose 리소스 전제), iOS framework 설정은 불필요(shared가 소비만 함, export 없음 — component-dependency.md 근거)
- Impact: gradle 설정 diff 최소화. 버전 카탈로그 기존 항목만 사용, 신규 의존성 0개

### ADR-2. 폰트 로딩은 compose-components-resources + 팩토리 함수

- Status: accepted
- Context: `Font(Res.font.*)`는 @Composable API라 정적 객체에서 FontFamily를 만들 수 없음 (UOW-3의 예상 질문 해소)
- Options:
  - A) `SikdorokTypography(fontFamily)` 팩토리 + 테마 컴포저블에서 `FontFamily(Font(Res.font.lee_seoyun_regular))` 주입 — 타이포 값 로직이 순수 함수로 테스트 가능
  - B) expect/actual로 플랫폼별 폰트 로딩 — FR-7(플랫폼 분기 없음) 위반
- Decision: A. FR-6(단일 주입 지점)과 정합 — 폰트 교체 시 테마 컴포저블 1곳만 수정
- Impact: commonTest에서 FontFamily.Default 주입으로 값 검증 가능

### ADR-3. M3 브리지는 순수 함수 + 부분 매핑

- Status: accepted (GATE-3.5 승인, 2026-08-27)
- Context: Q3=C(하이브리드). Sikdorok 13색을 M3 ColorScheme 슬롯 전체(29개)에 대응시킬 근거가 디자인에 없음
- Options:
  - A) 주요 슬롯만 명시 매핑, 나머지는 `lightColorScheme()` 기본값 — 근거 있는 것만 결정
  - B) 전 슬롯 매핑 — 디자인 근거 없는 색 결정 다수 발생 (no-implicit-decisions 위반)
- Decision: A. `fun sikdorokColorScheme(colors): ColorScheme`, `fun sikdorokM3Typography(typography): Typography` 순수 함수로 구현
- Impact: M3 컴포넌트가 위화감 없는 기본색을 갖되, 앱 코드는 계속 `SikdorokTheme.*` 접근자만 사용

## 3. API Specification

HTTP API 없음. 공개 Kotlin API (패키지 `com.devts.mymeal.core.designsystem`):

```kotlin
@Composable fun SikdorokTheme(content: @Composable () -> Unit)

object SikdorokTheme {  // 접근자 (companion 아닌 동명 object — DroidKaigi/M3 관례)
    val colors: SikdorokColors        @Composable get
    val typography: SikdorokTypography @Composable get
    val spacing: SikdorokSpacing       @Composable get
}

@Immutable class SikdorokColors(
    bg1, bg2, bg3, bg4, line,          // #FFFFFF #FCFAF7 #F8F5ED #E9E6DE / #9D9792@10%
    text1, text2, text3, text4, textDim, // #EBEAE9 #CECBC8 #9D9792 #3C3025 / #3C3025@80%
    alertRed, alertGreen, accent)       // #FF6363 #02B57F #00CC8F  — 각 필드 Color 타입

@Immutable class SikdorokTypography(h1, h2, h3, h4, body1, body2, detail)  // TextStyle
fun SikdorokTypography(fontFamily: FontFamily): SikdorokTypography  // ADR-2 팩토리

@Immutable class SikdorokSpacing(s4, s8, s12, s16, s20, s24, s32, s40)  // Dp

fun sikdorokColorScheme(colors: SikdorokColors): ColorScheme   // ADR-3
fun sikdorokM3Typography(t: SikdorokTypography): Typography
```

### 타이포 값 (매니페스트 실측, 소수 1자리 반올림, letterSpacing 전부 0 — P2 결정)

| Style | size.sp | lineHeight.sp |
|-------|---------|---------------|
| h1 | 24 | 24 |
| h2 | 20 | 22.4 |
| h3 | 18 | 20.1 |
| h4 | 16 | 16 |
| body1 | 14 | 14 |
| body2 | 13 | 20 |
| detail | 12 | 12 |

weight는 전부 400 (이서윤체 단일 웨이트).

### M3 브리지 매핑 표 (ADR-3, GATE-3.5 검토 항목)

| M3 슬롯 | Sikdorok 토큰 | 근거 |
|---------|---------------|------|
| primary | accent (#00CC8F) | 피그마 "Accent" |
| background | bg1 (#FFFFFF) | "BG 1" |
| surface | bg2 (#FCFAF7) | 카드/표면 성격의 Beige 1 |
| onBackground / onSurface | text4 (#3C3025) | 본문 텍스트 최고 대비 "Text 4" |
| error | alertRed (#FF6363) | "Alert Red" |
| outline | line (#9D9792@10%) | "Line" |
| 나머지 슬롯 | lightColorScheme() 기본값 | 디자인 근거 없음 — 임의 결정 회피 |

Typography 브리지: displayLarge←h1, headlineLarge←h2, headlineMedium←h3, titleMedium←h4, bodyLarge←body1, bodyMedium←body2, labelSmall←detail. 나머지 슬롯은 M3 기본값.

## 4. Data Model

Not applicable — DB/스키마 없음.

## 5. Module/Component Structure

| Module/File | Responsibility | New/Changed | Target UOW |
|-------------|------|----------|---------|
| `core/designsystem/build.gradle.kts` | 모듈 빌드 구성 (ADR-1) | New | UOW-1 |
| `settings.gradle.kts` | `include(":core:designsystem")` | Changed | UOW-1 |
| `shared/build.gradle.kts` | designsystem 의존 추가 | Changed | UOW-1 |
| `.../designsystem/SikdorokColors.kt` | 컬러 토큰 | New | UOW-2 |
| `.../designsystem/SikdorokSpacing.kt` | 스페이싱 토큰 | New | UOW-2 |
| `.../designsystem/SikdorokTypography.kt` | 타이포 토큰 + 팩토리 (ADR-2) | New | UOW-3 |
| `composeResources/font/lee_seoyun_regular.ttf` | 폰트 (배치 완료) | New | UOW-3 |
| `.../designsystem/SikdorokTheme.kt` | 테마 컴포저블 + 접근자 + CompositionLocal | New | UOW-4 |
| `.../designsystem/M3Bridge.kt` | ColorScheme/Typography 순수 함수 (ADR-3) | New | UOW-4 |
| `shared/.../App.kt` | 루트를 SikdorokTheme로 래핑 | Changed | UOW-4 |

## 6. Interaction Flow

```
App() --> SikdorokTheme()
            |-- FontFamily(Font(Res.font.lee_seoyun_regular))   (ADR-2)
            |-- CompositionLocalProvider(colors, typography, spacing)
            +-- MaterialTheme(sikdorokColorScheme(..), sikdorokM3Typography(..))  (ADR-3)
                  +-- content()  --> SikdorokTheme.colors.* 로 소비
```

## 7. Non-functional Design

- Performance: 토큰은 @Immutable — 리컴포지션 스킵 가능. 폰트 1.3MB는 앱 번들 포함(네트워크 로딩 없음)
- Consistency: 토큰 값의 유일 출처는 design-manifest.md. 값 변경은 토큰 파일 1곳 수정
- Security / Operations: 해당 없음

## 8. Testing Approach

| Target UOW | Test Type | Verification Content |
|---------|-----------|----------|
| UOW-1 | 빌드 검증 | `:core:designsystem:assembleDebug` + `compileKotlinIosSimulatorArm64` |
| UOW-2 | commonTest 단위 | 13색 hex/alpha == 매니페스트 값 |
| UOW-3 | commonTest 단위 | `SikdorokTypography(FontFamily.Default)` 7종 size/lineHeight/letterSpacing 검증 |
| UOW-4 | commonTest 단위 | `sikdorokColorScheme()` 매핑 표 일치 (순수 함수 — 컴포지션 불필요) |
| UOW-4 | 빌드 검증 | App.kt 래핑 후 androidApp assembleDebug + iOS 타깃 컴파일 |

## 9. Open Items

- ⚠️ UNCERTAIN: 이서윤체 lineHeight 1.0 배율의 실기기 렌더링 — 구현 후 androidApp 실행으로 육안 확인 (값 조정은 토큰 1곳)
- ~~M3 브리지 매핑 표(§3)~~ GATE-3.5 승인으로 확정 (2026-08-27)
