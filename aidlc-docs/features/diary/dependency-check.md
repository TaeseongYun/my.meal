<!-- workflow-step: post-implementation | producer: ctx-score-loop | location: aidlc-docs/features/diary | append-only-history -->
# Dependency Check — diary (F-5 홈 슬라이스)

AC 출처(FR-5): unit-of-work.md UOW-D1~D3 AC + requirements.md FR-1~6. Q4(SNACK) 반영 2026-09-04.

## Loop Config (optional — override defaults)
기본값 사용.

---

## 1. Inter-Feature Ordering Dependencies (Functional Order)

| # | Prerequisite Item | Expected State | Resolved | BLOCK | Rationale | Source |
|---|-----------|-----------|------|-------|------|------|
| F-1 | app-foundation | main 머지 (Nav·Koin·UDF) | ☑ | ☐ | cc6ef60(#6) — koinViewModel·homeModule 등록 규약 사용 | <!-- src: auto --> |
| F-2 | data-foundation | main 머지 (MealRepository) | ☑ | ☐ | e7d2f37(#4) — observeByDate 소비. 스키마 v2 확장은 F-2 절차 준수 | <!-- src: auto --> |
| F-3 | record UI (#7) | main 머지 — homeDestination(onNavigateToRecord) 시그니처 | ☑ | ☐ | 18e9edb 머지 반영, FAB 콜백 유지 | <!-- src: auto --> |

## 2. Build / Library Dependencies (Build / Library)

| # | Dependency | Expected Version/Config | Resolved | BLOCK | Rationale | Source |
|---|--------|----------------|------|-------|------|------|
| B-1 | Room v2 스키마 export | schemas/2.json + AutoMigration(1→2) | ☑ | ☐ | KSP 컴파일 검증 통과, meal_type defaultValue DINNER 확인 | <!-- src: auto --> |
| B-2 | room-runtime 노출 | :core:data `api` (공개 API가 RoomDatabase 노출) | ☑ | ☐ | shared 메타데이터 컴파일 오류 해소 | <!-- src: auto --> |
| B-3 | kotlin.daemon 힙 | 6144M (iOS 릴리스 링크 OOM 해소) | ☑ | ☐ | 3072M에서 linkRelease OOM 재현 2회 → 6144M로 BUILD SUCCESSFUL (f5-score-round1.log) | <!-- src: auto --> |

## 3. Inter-Module Dependencies (Module)

| # | From → To | Expected Contract | Resolved | BLOCK | Rationale | Source |
|---|-----------|-----------|------|-------|------|------|
| M-1 | :feature:home → :core:data | MealRepository 소비만 (DAO/스키마 수정 금지 — v2 확장은 F-2 절차 커밋으로 분리) | ☑ | ☐ | HomeViewModel은 observeByDate만 호출 | <!-- src: auto --> |
| M-2 | :shared → 데이터 조립 | platformDataModule(android Context/iOS documents) + MealRepository single | ☑ | ☐ | DataModule expect/actual, 실기기 Koin resolve 확인(앱 기동) | <!-- src: auto --> |
| M-3 | MealType 공유 | :core:model 4종(SNACK 포함) — home label 확장·record RecordSlot 매핑 가능 | ☑ | ☐ | Q4=A(2026-09-04). 홈 캐러셀은 CAROUSEL_TYPES 3끼 고정 | <!-- src: auto --> |

---

## 4. Current Score (latest round)

| Axis | Points | Score | Rationale (required) |
|----|------|------|-------------|
| 1. Dependency resolution | 25 | 25 | 체크리스트 9/9 해소, BLOCK 0 |
| 2. Build/compile | 25 | 25 | linkReleaseFrameworkIosSimulatorArm64+assembleDebug+전 테스트 태스크 BUILD SUCCESSFUL, FAILED 0 (f5-score-round1.log). 신규 경고 0 |
| 3. Test/coverage | 25 | 25 | HomeMappingTest 6(SNACK 캐러셀 제외 포함)+HomeUiStateTest 8 ×2플랫폼, :core:data jvm+iOS(v2 왕복), Koin 구성검증 — 전부 green. 커버리지 목표 미정의(해당 없음, 전례 준용) |
| 4. Requirements/AC satisfaction | 25 | 23 | D1~D3 AC 충족, FR-1~6 반영 — 스크린샷 2종(기록 0건 전부 미등록 / DB 주입 후 마크 🥗🍚+저녁 메모·시각, 재실행 복원). 감점 −2: 화면 표시 중 라이브 Flow 갱신 육안 미검증(재실행 반영만 확인 — F-3 저장 연결 시 자연 검증), iOS 실행 육안 미검증(컴파일·테스트만 — 전례 준용) |
| **Total** | **100** | **98** | |

**Verdict**: COMPLETE (>85 & build≠0, GR-1/GR-2 통과)

---

## 5. Score History (append-only)

| round | total | per_axis (dep/build/test/AC) | verdict | timestamp (UTC) |
|-------|-------|----------------------|---------|-----------------|
| 1 | 98 | 25/25/25/23 | COMPLETE | 2026-09-04T12:45Z |

## 잔여 (후속)
- record 데이터 연결(F-3 잔여) 후 통합 E2E: FAB 저장 → 홈 라이브 갱신 확인
- 스텁 이모지 세트·주차 라벨 규칙 — 디자이너 확정 시 교체
- 기록 상세·수정·삭제 (F-5 다음 사이클)
