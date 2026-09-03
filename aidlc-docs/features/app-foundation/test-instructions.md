# Test Instructions — app-foundation (F-1)

## 실행
`./gradlew :shared:testAndroidHostTest :feature:login:testAndroidHostTest` (+ iosSimulatorArm64Test 동일 대상)

## 시나리오 (AC 매핑)
| # | 대상 | 내용 | UOW |
|---|------|------|-----|
| T1 | Koin | appModule 전 정의 resolve 구성 검증 (ADR-2 회신 사항) | 2 |
| T2 | LoginViewModel | KakaoClick/EmailClick → NavigateToHome effect 방출, State 전이 | 4 |
| T3 | 실행 | 앱 시작 시 login 표시 → 카카오/이메일 클릭 → 홈 전환 (Android 에뮬레이터 + iOS 시뮬레이터 육안) | 3 |

## Quality Gate
전 테스트 green(양 타깃) + `:androidApp:assembleDebug` 성공 + T3 실행 확인 + 버전 검증 근거 audit 기록
⚠️ UNCERTAIN 해소 대상: Nav Compose type-safe 라우트의 iOS 실동작 (T3 iOS에서 검증)
