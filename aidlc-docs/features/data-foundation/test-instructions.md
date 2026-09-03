# Test Instructions — data-foundation (F-2)

## 실행
`./gradlew :core:data:testAndroidHostTest :core:data:iosSimulatorArm64Test` (+ :core:model 동일)

## 시나리오 (AC 매핑)
| # | 대상 | 내용 | AC |
|---|------|------|-----|
| T1 | totalEstimatedKcal | 전 항목 kcal / 일부 null / 전부 null(→null) | UOW-1 |
| T2 | DB | 오픈·schema v1 생성 (양 플랫폼) | UOW-2 |
| T3 | Repository | upsert→get 왕복, items 교체 트랜잭션 | UOW-3 |
| T4 | Repository | observeByDate — 로컬 TZ 자정 경계 엣지 | UOW-3 |
| T5 | Repository | 재오픈 후 복원(FR-2 시뮬레이션) | UOW-3 |
| T6 | delete+PhotoStore | DB 삭제→파일 정리, 파일 실패 시 무롤백, 고아 정리 | UOW-4 |

## Quality Gate
전 테스트 green(양 타깃) + :androidApp:assembleDebug 성공 + Room 구성 검증 근거(공식 문서 링크·버전) audit 기록

## Runtime Verification (ROLE 2 기록, 2026-08-31)

- 실행: `:core:data:jvmTest`(5/5) + `:core:model:jvmTest`(4/4) green, `:core:data:iosSimulatorArm64Test` BUILD SUCCESSFUL, `:core:model:testAndroidHostTest` green
- 전체 회귀: `:androidApp:assembleDebug` + `:shared:compileKotlinIosSimulatorArm64` 성공
- Environment Constraints / 계획 조정: **DB·파일 테스트는 jvm+iOS에서 실행** (Room android 아티팩트는 instrumented Context 필요 — Robolectric 도입 없이 android 호스트 테스트 불가). android 런타임 동작은 앱 빌드+후속 피처(F-3) 실기 검증으로 커버. 이에 따라 build-instructions의 `:core:data:testAndroidHostTest`는 `:core:data:jvmTest`로 대체됨
- Room 스키마 JSON: core/data/schemas/ 에 export (schema v1)
