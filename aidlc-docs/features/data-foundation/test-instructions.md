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
