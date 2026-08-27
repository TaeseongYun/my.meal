# Component Inventory — mymeal

패키지: `com.devts.mymeal`

| 구분 | 컴포넌트 | 위치 | 비고 |
|------|----------|------|------|
| UI | `App.kt` | shared/commonMain | 템플릿 데모 컴포저블. 유일한 공유 UI 진입점 |
| 공통 | `Greeting.kt`, `GreetingUtil.kt` | shared/commonMain | 템플릿 데모 코드. 재사용 가치 없음 |
| 공통 | `Platform.kt` (+android/ios actual) | shared | expect/actual 플랫폼 정보 패턴 예시 |
| 엔트리 | `MainActivity.kt` | androidApp | setContent { App() } |
| 엔트리 | `MainViewController.kt` | shared/iosMain | ComposeUIViewController { App() } |

- 순환 의존성: 없음 (22파일, codegraph 101노드 확인)
- 재사용 후보: `App.kt` 진입 구조만 유지. 데모 코드(Greeting*)는 향후 제거 대상.
- 디자인 시스템 관련 기존 자산: 없음 (테마/토큰/리소스 미존재)
