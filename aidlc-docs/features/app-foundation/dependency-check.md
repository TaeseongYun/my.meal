<!-- workflow-step: post-implementation | producer: ctx-score-loop | location: aidlc-docs/features/app-foundation | append-only-history -->
# Dependency Check — app-foundation

AC 출처(FR-5): unit-of-work.md UOW-1~4 AC + requirements.md FR-1~6.

## Loop Config (optional — override defaults)

기본값 사용 (complete_threshold 85 초과, stall_rounds 2, max_rounds 10, max_minutes 30).

---

## 1. Inter-Feature Ordering Dependencies (Functional Order)

| # | Prerequisite Item | Expected State | Resolved | BLOCK | Rationale | Source |
|---|-----------|-----------|------|-------|------|------|
| F-1 | design-system (F-0) | completed | ☑ | ☐ | :core:designsystem main 병합(c450ced), SikdorokTheme 소비 | <!-- src: auto --> |
| F-2 | 홈 UI-slice | main 머지 (homeDestination 전제) | ☑ | ☐ | PR #5 squash 머지(1358753) — :feature:home 존재, 워크트리 베이스 | <!-- src: auto --> |
| F-3 | data-foundation | **비선행** (F-1과 상호 독립) | ☑ | ☐ | 로드맵 §5-1 Phase 1 parallel — F-2 완료 상태(PR #4)라 접점 자체 해소 | <!-- src: auto --> |

## 2. Build / Library Dependencies (Build / Library)

| # | Dependency | Expected Version/Config | Resolved | BLOCK | Rationale | Source |
|---|--------|----------------|------|-------|------|------|
| B-1 | navigation-compose (KMP) | 2.9.2 (안정 최신) | ☑ | ☐ | repo1 maven-metadata 검증(2026-09-03) — 2.9.2 안정/2.10.0-alpha02 최신, 안정 채택. 빌드 통과 | <!-- src: auto --> |
| B-2 | koin-core/android/compose-viewmodel | 4.2.2 | ☑ | ☐ | repo1 metadata latest 4.2.2. 구성검증 테스트 green | <!-- src: auto --> |
| B-3 | kermit | 2.1.0 | ☑ | ☐ | repo1 metadata latest 2.1.0. Logger.setTag 초기화 컴파일 통과 | <!-- src: auto --> |
| B-4 | kotlinx-serialization | core 1.11.0 + 플러그인(kotlin 2.4.10) | ☑ | ☐ | repo1 metadata 확인. type-safe 라우트 @Serializable 컴파일 통과 | <!-- src: auto --> |
| B-5 | iOS 배포 타깃 | pbxproj 18.2→17.0 (ADR-3) | ☑ | ☐ | 2개 구성 모두 17.0, xcodebuild BUILD SUCCEEDED — 라이브러리 최소 요구 충돌 없음 | <!-- src: auto --> |

## 3. Inter-Module Dependencies (Module)

| # | From → To | Expected Contract | Resolved | BLOCK | Rationale | Source |
|---|-----------|-----------|------|-------|------|------|
| M-1 | :shared → :feature:login | loginDestination(onNavigateToHome)+loginModule만 소비 | ☑ | ☐ | App.kt/AppModule.kt — 등록 함수·module 경유, LoginScreen 직접 호출 제거 | <!-- src: auto --> |
| M-2 | :shared → :feature:home | homeDestination()만 소비 | ☑ | ☐ | App.kt NavHost 등록, HomeScreen 직접 호출 제거 | <!-- src: auto --> |
| M-3 | 피처 간 직접 의존 금지 | :feature:login ↛ :feature:home | ☑ | ☐ | 양쪽 build.gradle.kts 피처 상호 의존 0 — 전환은 shared 콜백 경유 (FR-2) | <!-- src: auto --> |
| M-4 | 호스트 얇음 (FR-6) | androidApp/iosApp = 조립 호출만 | ☑ | ☐ | MymealApplication=initKoin, MainActivity=setContent{App()}, MainViewController=initKoin+App | <!-- src: auto --> |

---

## 4. Current Score (latest round)

| Axis | Points | Score | Rationale (required) |
|----|------|------|-------------|
| 1. Dependency resolution | 25 | 25 | 체크리스트 12/12 해소, BLOCK 0 (GR-2 미적용) |
| 2. Build/compile | 25 | 25 | assemble+iosSimulatorArm64 컴파일+assembleDebug+xcodebuild 전부 exit 0 (f1-score-round1.log, FAILED 0). 경고는 기존 klib 중복·expect/actual beta뿐 — 신규 경고 0 |
| 3. Test/coverage | 25 | 25 | KoinConfigurationTest 1 + LoginViewModelTest 3 ×2플랫폼(androidHost+iosSimulatorArm64) green, 기존 회귀 포함 전체 green. 커버리지 목표 미정의 — 해당 없음(전례 준용). 엣지: Koin 전 정의 resolve·초기상태·양 액션 |
| 4. Requirements/AC satisfaction | 25 | 23 | UOW-1~4 AC 전부 충족(스크린샷: Android login→home 전환, iOS login 렌더·기동). FR-1~6 반영(FR-5 CTX Constraints 기록). 감점 −2: iOS '전환 탭' 육안 미검증 — simctl 터치 주입 미지원·osascript 접근성 권한 없음. 전환은 공통 코드로 Android에서 검증됨 |
| **Total** | **100** | **98** | |

**Verdict**: COMPLETE (>85 & build≠0, GR-1/GR-2 통과)

---

## 5. Score History (append-only)

| round | total | per_axis (dep/build/test/AC) | verdict | timestamp (UTC) |
|-------|-------|----------------------|---------|-----------------|
| 1 | 98 | 25/25/25/23 | COMPLETE | 2026-09-03T09:35Z |

## 잔여 (후속)
- iOS 시뮬레이터에서 로그인 버튼 탭→홈 전환 육안 확인 (사용자 1클릭 가능 — 시뮬레이터 부팅·앱 설치 상태)
- LoginUiState.isLoading은 골격 — F-6 실인증에서 소비
