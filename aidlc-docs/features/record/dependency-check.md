<!-- workflow-step: post-implementation | producer: ctx-score-loop | location: aidlc-docs/features/record | append-only-history -->
# Dependency Check — record (F-3 데이터 연결)

UI 슬라이스(#7, 98/100)와 별도 회차 — 이 파일은 데이터 연결(잔여 범위) 채점. AC 출처: unit-of-work.md R2~R4 + requirements.md FR + 2026-09-04 답변(Q4 v3, Q5 TimePicker).

## 1~3. Dependencies

| # | 항목 | Resolved | BLOCK | Rationale | Source |
|---|------|------|-------|------|------|
| F-1 | diary 스키마 v2 선행 | ☑ | ☐ | #8 머지(32ff62c) 기반, v3는 그 위 확장 | <!-- src: auto --> |
| B-1 | 스키마 v3 export | ☑ | ☐ | AutoMigration 2→3, schemas/3.json, jvmTest 왕복 green | <!-- src: auto --> |
| B-2 | androidx-core-ktx 1.18.0 | ☑ | ☐ | 1.19.0은 compileSdk 37 요구 → 36 호환 1.18.0으로 조정(구글 메이븐 검증) | <!-- src: auto --> |
| M-1 | :feature:record → :core:data 소비만 | ☑ | ☐ | Repository/PhotoStore 주입, DAO 무접촉 | <!-- src: auto --> |
| M-2 | 홈 이모지 연동 | ☑ | ☐ | dayMarkEmoji: 대표→최신→스텁, menuEmoji: foodEmoji 우선 (매핑 테스트) | <!-- src: auto --> |

## 4. Current Score (latest round)

| Axis | Points | Score | Rationale (required) |
|----|------|------|-------------|
| 1. Dependency resolution | 25 | 25 | 체크리스트 5/5, BLOCK 0 |
| 2. Build/compile | 25 | 25 | 전 태스크(호스트·iOS 테스트, iOS 컴파일, assembleDebug) BUILD SUCCESSFUL, FAILED 0 (f3-score-round1.log) |
| 3. Test/coverage | 25 | 25 | RecordViewModelTest 4(저장·사진·빈 메모·롤백)+RecordUiStateTest 7+home 매핑 10+storage v3 왕복 — 양 플랫폼 green. 커버리지 목표 미정의(해당 없음) |
| 4. Requirements/AC satisfaction | 25 | 21 | FAB→기록 화면 실기 진입·실시간 날짜/시각·사진 다이얼로그/시스템 피커 오픈 확인(스크린샷 3종). 저장 파이프라인·롤백은 단위테스트로 충족. 감점 −4: 사진 실선택→저장→홈 반영 육안 E2E 미수행(사용자 지시로 세부 검증 중단 — 수동 확인 가능), iOS 실행 육안 미검증(전례 준용) |
| **Total** | **100** | **96** | |

**Verdict**: COMPLETE (>85 & build≠0, GR-1/GR-2 통과)

## 5. Score History (append-only)

| round | total | per_axis (dep/build/test/AC) | verdict | timestamp (UTC) |
|-------|-------|----------------------|---------|-----------------|
| 1 | 96 | 25/25/25/21 | COMPLETE | 2026-09-04T13:20Z |

## 잔여
- 사진 선택→저장→홈 반영 육안 E2E (수동 1회면 충분), iOS 카메라 실기기 검증
- 이모지 9종 의미·대표 게시물 동작 디자이너 확인 (기존 항목 유지)
