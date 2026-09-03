# Status — app-foundation (F-1)

- Feature: app-foundation
- Status: **implementation-ready** (GATE-3.5 승인, GATE-5 대기)
- Request Type: prepared-requirement (_source-plan.md §기술 방향·구조 설계 원칙 발췌)
- Depth Level: comprehensive
- Project State: brownfield (F-0 design-system, :feature:login 기구현)
- Last Updated: 2026-09-03

## Roadmap Context
- Roadmap Source: F-1 (`_roadmap.md`)
- Depends On: F-0 design-system (완료 — 대기 없음)
- Shared Resources 소유: Navigation 골격, Koin 루트, App 조립 지점 (로드맵 ⚠ 해소 표의 single-owner)
- 병렬: F-2와 parallel-capable (Gradle 파일 접점만 — 커밋 분리 규약)

## Readiness Score: 90 / 100 — READY
| 영역 | 점수 | 만점 | 근거 |
|------|------|------|------|
| 1. 기능 범위 | 15 | 15 | Goal/In/Out 명확 |
| 2. 정책/예외 | 20 | 20 | Q1·Q2 답변 [확실] (Nav Compose, iOS 17.0) |
| 3. 사용자 시나리오 | 9 | 15 | 소비자=개발자(골격) — 화면 시나리오는 각 피처 소관 |
| 4. NFR | 13 | 15 | 성능 n/a, 로깅 개인정보 금지 규약 포함 |
| 5. 승인 항목 | 20 | 20 | BLOCK 0, GATE-2 통과 (2026-08-31) |
| 6. 리스크 | 13 | 15 | 4건 식별+대응. UNCERTAIN 1건(iOS 동작 차) |

## Uncertain Areas
- ⚠️ KMP Navigation/ViewModel의 iOS 실동작 — 구현 시 시뮬레이터 검증

## Progress
- [x] STEP 1/1-B/1-C/2/3/4/5 (RE 기존 산출물 참조 — STEP 1.5 스킵)
- [x] GATE-2 통과 (2026-08-31, Q1=A, Q2=17.0)
- [x] STEP 6 (UOW 4개: M/M/M/M)
- [x] GATE-3 승인 (2026-08-31, "둘 다 승인")
- [x] STEP 6.5 (technical-design.md — ADR 3건)
- [x] 설계 개정 (2026-09-03: 홈 UI 머지 반영 — HomeRoute/homeDestination, 임시 remember 전환 제거를 UOW-3에 흡수. 사용자 승인 계획 기반)
- [x] GATE-3.5 승인 (2026-09-03)
- [x] STEP 7~9 (build-instructions.md, test-instructions.md, Readiness 90 READY 유지)
- [ ] GATE-5 (구현 개시) ← **대기 중**
