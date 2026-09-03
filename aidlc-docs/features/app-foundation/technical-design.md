<!-- workflow-step: STEP-6.5 | gate: GATE-3.5 | producer: ctx-aidlc-run -->
# Technical Design — app-foundation (F-1)

> **Request Anchor**: 로드맵 F-1 — 앱 골격(내비·DI·UDF·로깅) (Nav Compose, Koin, Kermit, iOS 17.0 확정).

## Implementation Scope
- production / Mock: none / 제외: 로그인 실동작(F-6), 딥링크, 추가 화면 (home destination은 예외 — 2026-09-03 개정 참조)

## 1. Design Overview
- Target: shared(조립 루트) 중심 + androidApp/iosApp 초기화 지점, :feature:login 재연결
- brownfield touchpoints: shared(App.kt·신규 골격 파일), feature/login(destination 등록·ViewModel), feature/home(destination 등록 — 개정), androidApp(Application 신설), iosApp(pbxproj 17.0), 카탈로그/settings

### 개정 2026-09-03 (홈 UI 머지 반영 — 사용자 승인 계획)
main에 `:feature:home` 머지됨(PR #5). `shared/App.kt`의 임시 `remember` 전환(홈 UI-slice의
이연 항목 "내비게이션 정식 연결 → F-1")을 이 피처가 흡수한다:
- `:feature:home`에 `HomeRoute` + `homeDestination()` 등록 (규약 동일 — 라우트는 피처 소유)
- 로그인 성공(카카오/이메일 클릭) → `LoginEffect.NavigateToHome` → NavHost가 `HomeRoute`로 전환
- 홈 상태는 기존 `stubHomeUiState()` 공급 유지 — HomeViewModel/실데이터는 F-5 소관 (범위 아님)

## 2. Architecture Decisions
### ADR-1. JetBrains Navigation Compose + type-safe 라우트
- Status: accepted (GATE-2 Q1=A)
- Decision: org.jetbrains.androidx.navigation 계열, @Serializable 라우트 객체(type-safe) — kotlinx-serialization 플러그인을 shared·feature 모듈에 적용(승인 스택). 버전은 구현 시 공식 문서·Maven 실존 검증
- 규약: 피처는 `fun NavGraphBuilder.<slug>Destination(...)` 확장 함수만 노출, 라우트 타입은 해당 피처 모듈이 소유
### ADR-2. Koin 부트스트랩 구조
- Status: accepted
- Decision: shared에 `appModule`(루트) + `initKoin()` 진입 함수. Android는 신설 Application 클래스에서, iOS는 MainViewController 생성 전에 호출. 피처 Koin module은 피처 모듈이 노출하고 appModule이 포함
- 검증: Koin 모듈 구성 검증 테스트를 commonTest에 포함 (런타임 해석 약점 보완 — 사용자 질의 회신 사항)
### ADR-3. iOS 배포 타깃 17.0
- Status: accepted (GATE-2 Q2, 사용자 결정)
- Decision: pbxproj IPHONEOS_DEPLOYMENT_TARGET 18.2→17.0. 라이브러리 최소 요구 충돌 시 즉시 보고(임의 상향 금지)

## 3. API Specification (Kotlin)
```kotlin
// shared
fun initKoin()                      // 플랫폼 진입점이 호출
@Composable fun App()               // NavHost(시작: LoginRoute)
// :feature:login
@Serializable data object LoginRoute
fun NavGraphBuilder.loginDestination(onNavigateToHome: () -> Unit)  // 내부에서 LoginViewModel 주입
class LoginViewModel : ViewModel { val uiState: StateFlow<LoginUiState>; fun onAction(a: LoginAction); val effects: Flow<LoginEffect> }
data class LoginUiState(...초기 골격...)
sealed interface LoginAction { data object KakaoClick; data object EmailClick }
sealed interface LoginEffect { data object NavigateToHome }         // Channel 기반 — State에 이벤트 금지
// :feature:home (개정 2026-09-03)
@Serializable data object HomeRoute
fun NavGraphBuilder.homeDestination()           // 내부는 기존 HomeScreen + stubHomeUiState (F-5 전까지)
```
HTTP API 없음. 로깅: Kermit 루트 로거 태그 규약("Sikdorok"), 개인정보 금지 규약 CTX 반영.

## 4. Data Model
Not applicable.

## 5. Module/Component Structure
| Module/File | 책임 | UOW |
|---|---|---|
| libs.versions.toml / settings | nav·koin·kermit·serialization 추가(버전 검증 기록) | 1 |
| iosApp pbxproj | 17.0 | 1 |
| shared …/di/AppModule.kt, KoinInit.kt | 루트 DI | 2 |
| androidApp MymealApplication.kt(+Manifest) | startKoin | 2 |
| shared …/MainViewController.kt | initKoin 후 App | 2 |
| shared App.kt(NavHost) | 조립 — 임시 remember 전환 제거 | 3 |
| feature/login …/LoginRoute·loginDestination | 등록 규약 | 3 |
| feature/home …/HomeRoute·homeDestination (+build.gradle.kts nav 의존) | 등록 규약 (개정) | 3 |
| feature/login …/LoginViewModel·UiState·Action | UDF 골격 | 4 |
| shared …/logging/ | Kermit 초기화 | 4 |

## 6. Interaction Flow
App 기동 → initKoin → App(NavHost, start=LoginRoute) → loginDestination → LoginViewModel(uiState) → LoginScreen(render+Action)

## 7. Non-functional Design
- Operations: Kermit 로그 — 사진/기록/계정 ID 금지 (CTX Constraints 추가 예정)
- 나머지 해당 없음

## 8. Testing Approach
| UOW | 유형 | 내용 |
|---|---|---|
| 1 | 빌드 | 전 모듈 컴파일(android+iOS) |
| 2 | 단위 | Koin 모듈 구성 검증(전 정의 resolve) |
| 3 | 빌드+실행 | 앱 시작 시 login 표시 + 카카오/이메일 클릭 → 홈 전환(에뮬레이터 확인) |
| 4 | 단위 | LoginViewModel Action→State/Effect 전이 (kotlinx-coroutines-test+Turbine — Turbine 버전 검증 후 추가) |

## 9. Open Items
- ⚠️ UNCERTAIN: Navigation Compose KMP·type-safe 라우트의 iOS 동작 — UOW-3에서 시뮬레이터 검증
