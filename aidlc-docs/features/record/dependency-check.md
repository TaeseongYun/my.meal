<!-- workflow-step: post-implementation | producer: ctx-score-loop | location: aidlc-docs/features/record | append-only-history -->
# Dependency Check — record (UI 슬라이스)

F-3 record의 **디자인 우선(UI) 슬라이스** — 사진 촬영/선택, 항목·섭취량 입력, 로컬 저장은 이 슬라이스 범위 밖(F-2 데이터 연결 시). AC 출처: 사용자 승인 구현 계획(2026-09-03, plan file `goofy-foraging-sonnet.md`) — 이 슬라이스에는 unit-of-work.md/requirements.md가 없어 계획서가 FR-5의 대체 원천임을 명시한다 (home 선례).

## Loop Config (optional — override defaults)

기본값 사용 (complete_threshold 85 초과, stall_rounds 2, max_rounds 10, max_minutes 30).

| Parameter | Default | This Feature's Setting |
|----------|--------|-------------|
| complete_threshold (exceed criterion) | 85 | (기본) |

---

## 1. Inter-Feature Ordering Dependencies (Functional Order)

| # | Prerequisite Item | Expected State | Resolved | BLOCK | Rationale | Source |
|---|-----------|-----------|------|-------|------|------|
| F-1 | design-system (F-0) | completed | ☑ | ☐ | :core:designsystem main 병합 완료, SikdorokTheme colors/typography/spacing 소비 | <!-- src: auto --> |
| F-2 | app-foundation (F-1) 내비게이션 | completed | ☑ | ☐ | main 병합(PR #6) — RecordRoute/recordDestination을 정식 NavHost에 등록, 임시 remember 전환 불필요 | <!-- src: auto --> |
| F-3 | data-foundation (F-2) 데이터 계층 | **비선행** | ☑ | ☐ | UI 슬라이스 — stubRecordUiState로 대체. 저장/사진/시간 선택 no-op, 슬롯 스키마 결정은 F-2 소유 | <!-- src: auto --> |

## 2. Build / Library Dependencies (Build / Library)

| # | Dependency | Expected Version/Config | Resolved | BLOCK | Rationale | Source |
|---|--------|----------------|------|-------|------|------|
| B-1 | kotlinx-datetime | 0.8.0 (libs.versions.toml 선언) | ☑ | ☐ | LocalDate/LocalTime/LocalDateTime 사용, :feature:record:assemble 통과 | <!-- src: auto --> |
| B-2 | androidx-navigation-compose + kotlinx-serialization-core | F-1 카탈로그 선언(2.9.2 / 1.11.0) | ☑ | ☐ | @Serializable RecordRoute + composable<RecordRoute>, :androidApp:assembleDebug 통과 | <!-- src: auto --> |
| B-3 | compose components.resources (webp 드로어블) | CMP 1.11.1 (기존 선언) | ☑ | ☐ | lunchbox_placeholder.webp 생성 Res 접근, 에뮬레이터 렌더 확인 | <!-- src: auto --> |
| B-4 | :feature:record 모듈 등록 | settings.gradle.kts include + AGP KMP 설정 | ☑ | ☐ | :feature:home과 동일 템플릿, assemble/test 태스크 동작 | <!-- src: auto --> |

## 3. Inter-Module Dependencies (Module)

| # | From → To | Expected Contract | Resolved | BLOCK | Rationale | Source |
|---|-----------|-----------|------|-------|------|------|
| M-1 | :feature:record → :core:designsystem | SikdorokTheme 접근자만 사용 | ☑ | ☐ | RecordScreen.kt 전 스타일 토큰 참조, 토큰 외 값 5건은 매니페스트 표 등재 | <!-- src: auto --> |
| M-2 | :shared → :feature:record | recordDestination(onBack) 등록 (F-1 피처 등록 규약) | ☑ | ☐ | App.kt NavHost 등록, 홈 FAB → RecordRoute → popBackStack 에뮬레이터 검증 | <!-- src: auto --> |
| M-3 | 피처 간 직접 의존 금지 | :feature:record ↛ :feature:home | ☑ | ☐ | build.gradle.kts 의존은 :core:designsystem뿐. home MealType 미참조 — RecordSlot 별도 정의 (ADR-0001 준수) | <!-- src: auto --> |
| M-4 | :feature:home 시그니처 변경 | homeDestination(onNavigateToRecord) | ☑ | ☐ | FAB 콜백 연결 — home 모듈 내 1줄 변경, 기존 HomeScreen API 불변 | <!-- src: auto --> |

### AC 체크리스트 (사용자 승인 계획 — FR-5 대체 원천) <!-- src: auto -->
1. 날짜/시간 헤더: 날짜 라벨 + 시간 칩(탭 가능, 변경은 F-2) — 에뮬레이터 03 + 테스트 T1~T4
2. 끼니 칩 4종(아침/점심/저녁/간식) 단일 선택, 기본 아침 — 에뮬레이터 03(아침 선택) / 04(점심 전환)
3. 도시락 프레임 + 카메라 버튼(no-op), 사진 없으면 일러스트 자리표시자 — 에뮬레이터 03
4. 음식 이모지 단일 선택 + "?" 해제, 대표 게시물 체크박스 토글 — 에뮬레이터 04(🍚 선택 + 체크 ✓)
5. 메모 입력(플레이스홀더 + 괘선), 화면 스크롤 — 에뮬레이터 03
6. 홈 FAB → 기록 화면 진입, 뒤로가기 → 홈 복귀 — 에뮬레이터 02→03→05
7. 생성/수정 동일 화면: 데이터 유무로 분기 (isEdit 파생) — 테스트 T5/T6, 실데이터 분기는 F-2

---

## 4. Current Score (latest round) <!-- src: auto -->

| Axis | Points | Score | Rationale (required) |
|----|------|------|-------------|
| 1. Dependency resolution | 25 | 25 | §1~3 체크리스트 11건 전부 ☑, BLOCK 0건 (기능 8/8 · 빌드 8/8 · 모듈 9/9) |
| 2. Build/compile | 25 | 25 | 4개 명령 전부 exit 0 (`:feature:record:assemble`, `:feature:record:compileKotlinIosSimulatorArm64`, `:feature:record:testAndroidHostTest`, `:androidApp:assembleDebug`) · 경고 0건 (로그 score-round1-tests.log grep `^w: |warning:` = 0) (5/5) · 린트 미구성 → 해당 없음 제외, 적용 만점 20/20을 25로 정규화 |
| 3. Test/coverage | 25 | 25 | `--rerun-tasks` 재실행: RecordUiStateTest 7/7 통과 × Android host·iOS sim (TEST-*.xml tests=7 failures=0 errors=0) · 엣지 테스트 T2(요일 파생 불일치 고정)/T4(0시·정오·자정 경계·분 패딩)/T6(콘텐츠별 isEdit) (5/5) · 커버리지 목표 미정의 → 해당 없음 제외, 적용 만점 20/20을 25로 정규화 |
| 4. Requirements/AC satisfaction | 25 | 23 | AC 7/7 검증 (에뮬레이터 스크린샷 01~05 + T1~T7). 감점 −2: 디자인 원본 JSON 미확보(REST nodes API 429 지속 — 레이아웃·색을 2x 렌더 픽셀 실측으로 판독, manifest.json/tokens.json 커밋 불가) 및 간식 슬롯·이모지 선택 표시 임의 결정(디자이너 확인 대기, 매니페스트 등재) |
| **Total** | **100** | **98** | |

**Verdict**: COMPLETE (98 > 85 & build ≠ 0, GR-1/GR-2 통과)

---

## 5. Score History (append-only)

| round | total | per_axis (dep/build/test/AC) | verdict | timestamp (UTC) |
|-------|-------|----------------------|---------|-----------------|
| 1 | 98 | 25/25/25/23 | COMPLETE | 2026-09-03T10:50Z |
