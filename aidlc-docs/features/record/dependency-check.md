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
| 1. Dependency resolution | 25 | 25 | 체크리스트 5/5, BLOCK 0. 간식 슬롯 충돌 해소 확인: `MealType`이 SNACK 포함 4종(core/model), `RecordSlot.toMealType()` 1:1 매핑 — 2026-09-04 미결 항목 종결 |
| 2. Build/compile | 25 | 22 | 7개 태스크 전부 exit 0 BUILD SUCCESSFUL (f3-score-round2.log): record assemble·iOS 컴파일·양 플랫폼 테스트, core:model/core:data 테스트, androidApp assembleDebug (빌드 15/15). 경고 15건으로 감점 −2: KLIB `unique_name` 중복 12건(androidx.* 와 org.jetbrains.* 동일 라이브러리 이중 적재 — lifecycle/savedstate/compose-runtime/collection/annotation, CMP 1.11 전환기 이슈, :feature:home도 동일 선언), expect/actual Beta 3건(Room KMP KT-61573). 피처 코드 기인 경고 0건 (경고 3/5). 린트 미구성 → 해당 없음 제외, 적용 만점 20을 25로 정규화 |
| 3. Test/coverage | 25 | 25 | 재실행(--rerun-tasks) 양 플랫폼 green: RecordUiStateTest 8 · RecordViewModelTest 4 × Android host·iOS sim, MealStorageTest 5, MealEntryTest 4 — failures/errors 0 (f3-score-round3.log). 엣지: 날짜 요일 파생·시각 경계·isEdit 분기·저장 롤백 + **round 2 보완**: `toMealType_mapsEverySlot`(RecordSlot 4종 전수 매핑 — valueOf(name) 런타임 조회가 SNACK만 고정돼 있던 공백 해소). 커버리지 목표 미정의(해당 없음) |
| 4. Requirements/AC satisfaction | 25 | 21 | FAB→기록 화면 실기 진입·실시간 날짜/시각·사진 다이얼로그/시스템 피커 오픈 확인(스크린샷 3종). 저장 파이프라인·롤백은 단위테스트로 충족. 감점 −4: 사진 실선택→저장→홈 반영 육안 E2E 미수행(사용자 지시로 세부 검증 중단 — 수동 확인 가능), iOS 실행 육안 미검증(전례 준용) |
| **Total** | **100** | **93** | |

**Verdict**: COMPLETE (93 > 85 & build≠0, GR-1/GR-2 통과)

## 5. Score History (append-only)

| round | total | per_axis (dep/build/test/AC) | verdict | timestamp (UTC) |
|-------|-------|----------------------|---------|-----------------|
| 1 | 96 | 25/25/25/21 | COMPLETE | 2026-09-04T13:20Z |
| 2 | 93 | 25/22/25/21 | COMPLETE | 2026-09-07T01:25Z |

round 1 → 2의 −3은 **코드 회귀가 아니라 계측 강화**다 (REGRESSED 아님): round 1은 경고 수를 계측하지
않았고 round 2에서 처음 집계해 빌드 축에 −2를 반영했다. 두 라운드 사이 코드 변경은 없었고(작업트리 클린,
HEAD f5b8e7f 동일), round 2에서 추가한 것은 테스트 1건뿐이다.

## 잔여
- KLIB `unique_name` 중복 12건: androidx / org.jetbrains 좌표 이중 적재 — 프로젝트 전역(:feature:home 동일)
  이라 record 단독 수정 대상 아님. CMP·lifecycle 좌표 정렬은 별도 의존성 정비 회차로 분리
- 사진 선택→저장→홈 반영 육안 E2E (수동 1회면 충분), iOS 카메라 실기기 검증
- 이모지 9종 의미·대표 게시물 동작 디자이너 확인 (기존 항목 유지)
