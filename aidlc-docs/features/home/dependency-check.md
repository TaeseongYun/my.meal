<!-- workflow-step: post-implementation | producer: ctx-score-loop | location: aidlc-docs/features/home | append-only-history -->
# Dependency Check — home

로드맵 외 UI-first 슬라이스 (로그인 선례). AC 출처: 사용자 승인 구현 계획(2026-08-31)의 4개 수용 기준 — 이 슬라이스에는 unit-of-work.md/requirements.md가 없어 계획서가 FR-5의 대체 원천임을 명시한다.

## Loop Config (optional — override defaults)

기본값 사용 (complete_threshold 85 초과, stall_rounds 2, max_rounds 10, max_minutes 30).

| Parameter | Default | This Feature's Setting |
|----------|--------|-------------|
| complete_threshold (exceed criterion) | 85 | (기본) |

---

## 1. Inter-Feature Ordering Dependencies (Functional Order)

| # | Prerequisite Item | Expected State | Resolved | BLOCK | Rationale | Source |
|---|-----------|-----------|------|-------|------|------|
| F-1 | design-system (F-0) | completed | ☑ | ☐ | :core:designsystem main 병합 완료(c450ced), SikdorokTheme 소비 | <!-- src: auto --> |
| F-2 | app-foundation 내비게이션 | **비선행** | ☑ | ☐ | 임시 App.kt remember 전환으로 대체(사용자 승인) — BLOCK 아님, F-1에서 교체 | <!-- src: auto --> |
| F-3 | data-foundation 데이터 계층 | **비선행** | ☑ | ☐ | 스텁 상태(stubHomeUiState)로 대체(사용자 승인) — F-2/F-5에서 연결 | <!-- src: auto --> |

## 2. Build / Library Dependencies (Build / Library)

| # | Dependency | Expected Version/Config | Resolved | BLOCK | Rationale | Source |
|---|--------|----------------|------|-------|------|------|
| B-1 | kotlinx-datetime | 0.7.1 (libs.versions.toml 선언) + 빌드 통과 | ☑ | ☐ | 로컬 gradle 캐시·프로젝트 klib에서 0.7.1 검증 후 선언, :feature:home:assemble 통과 | <!-- src: auto --> |
| B-2 | compose.foundation pager | CMP 1.11.1 (기존 선언) | ☑ | ☐ | foundation-iosSimulatorArm64Main-1.11.1.klib linkdata에 pager 패키지 확인, 컴파일 통과 | <!-- src: auto --> |
| B-3 | :feature:home 모듈 등록 | settings.gradle.kts include + AGP KMP 설정 | ☑ | ☐ | :feature:login과 동일 템플릿, assemble/테스트 태스크 동작 | <!-- src: auto --> |

## 3. Inter-Module Dependencies (Module)

| # | From → To | Expected Contract | Resolved | BLOCK | Rationale | Source |
|---|-----------|-----------|------|-------|------|------|
| M-1 | :feature:home → :core:designsystem | SikdorokTheme colors/typography/spacing 접근자만 사용 | ☑ | ☐ | HomeScreen.kt 전 스타일 토큰 참조 (토큰 외 값은 매니페스트 표 등재) | <!-- src: auto --> |
| M-2 | :shared → :feature:home | HomeScreen(state) + stubHomeUiState() 소비 | ☑ | ☐ | App.kt 로그인 버튼 → 홈 전환, :androidApp:assembleDebug 통과 | <!-- src: auto --> |
| M-3 | 피처 간 직접 의존 금지 | :feature:home ↛ :feature:login | ☑ | ☐ | build.gradle.kts 의존은 :core:designsystem뿐 (ADR-0001 준수) | <!-- src: auto --> |

### AC 체크리스트 (사용자 승인 계획 — FR-5 대체 원천) <!-- src: auto -->
1. 주별 캘린더: 실제 오늘 기준 주(일~토), 기록일=이모지·미기록="?"·오늘 강조 — 에뮬레이터 스크린샷 + 테스트 T1~T4/T7
2. 아침-점심-저녁 캐러셀: 등록=사진+글귀+메뉴/시각, 미등록=자리표시자+빈 괘선, 화살표·스와이프 이동 — 스크린샷 3종 + T6
3. 작성 FAB 노출 (클릭 no-op, F-3에서 연결) — 스크린샷
4. 로그인 완료 시 홈 노출 (임시 전환) — 에뮬레이터 로그인 버튼 탭 → 홈 확인

---

## 4. Current Score (latest round) <!-- src: auto -->

| Axis | Points | Score | Rationale (required) |
|----|------|------|-------------|
| 1. Dependency resolution | 25 | 25 | §1~3 체크리스트 9건 전부 ☑, BLOCK 0건 (기능 8/8 · 빌드 8/8 · 모듈 9/9) |
| 2. Build/compile | 25 | 25 | 5개 명령 전부 exit 0 (assemble·testAndroidHostTest·iosSimulatorArm64Test·:shared:compileKotlinIosSimulatorArm64·:androidApp:assembleDebug, 로그 score-round1.log) · 경고 0건 (5/5) · 린트 미구성 → 해당 없음 제외, 적용 만점 20/20을 25로 정규화 |
| 3. Test/coverage | 25 | 25 | --rerun-tasks 재실행: HomeUiStateTest 7/7 통과 × Android host·iOS sim (TEST-*.xml failures=0) · 엣지 테스트 T2 월경계/T3 연경계/T4 일요일 (5/5) · 커버리지 목표 미정의 → 해당 없음 제외, 적용 만점 20/20을 25로 정규화 |
| 4. Requirements/AC satisfaction | 25 | 23 | AC 4/4 검증 (에뮬레이터 스크린샷 emu-home/emu-lunch/emu-swipe + T1~T7). 감점 −2: 아침/점심 이모지 추정·주차 라벨 규칙 임의 결정 (디자이너 확인 대기, 매니페스트 등재) |
| **Total** | **100** | **98** | |

**Verdict**: COMPLETE (98 > 85 & build ≠ 0, GR-1/GR-2 통과)

---

## 5. Score History (append-only)

| round | total | per_axis (dep/build/test/AC) | verdict | timestamp (UTC) |
|-------|-------|----------------------|---------|-----------------|
| 1 | 98 | 25/25/25/23 | COMPLETE | 2026-08-31T13:20Z |
