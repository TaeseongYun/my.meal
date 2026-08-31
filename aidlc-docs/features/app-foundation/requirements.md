# Requirements — app-foundation (F-1)

> **Request Anchor**: 로드맵 F-1 — 앱 골격(내비게이션, Koin DI, ViewModel+StateFlow UDF, Kermit 로깅)과 조립 루트를 구축해 이후 모든 피처가 같은 뼈대 위에서 개발되게 한다.

## Goal
shared(조립 루트)에 내비게이션·DI·상태관리·로깅의 단일 골격을 세우고, 기존 `:feature:login`을 첫 destination으로 편입한다. ADR-0001 구조(:core:* / :feature:* / shared 조립)를 코드로 확정한다.
근거: `_source-plan.md` §기술 방향·§구조 설계 원칙, `_roadmap.md` F-1, ADR-0001.

## In-Scope
- **내비게이션 골격**: JetBrains Navigation Compose (Q1=A 확정) — 그래프/라우트 규약 + 첫 destination = login
- **Koin DI 부트스트랩**: shared 루트 모듈 + 피처별 module 기여 규약 (Android/iOS 초기화 지점 포함)
- **ViewModel KMP + StateFlow UDF 골격**: 규약 확립 (UiState/Action, 일회성 이벤트는 Channel/SharedFlow) — 첫 적용은 LoginViewModel 골격
- **Kermit 구조화 로깅** 초기화 (개인정보 로깅 금지 규칙 준수)
- **버전 카탈로그**: navigation/koin/kermit 신규 항목 (버전은 구현 시점 공식 문서 검증)
- **iOS 최소 버전 17.0 반영** (Q2 확정 — 현재 18.2에서 하향, pbxproj 수정)

## Out-of-Scope
- 로그인 실제 동작(F-6), 화면 추가(F-3/F-5/F-7), :core:ui 등 빈 모듈 생성(내용 발생 시), 데이터 계층(F-2), 딥링크(후속 — kmp-navigation-platform 규칙으로 그때 검증)

## Functional Requirements
- FR-1: 앱 시작 시 내비게이션 그래프가 login destination을 표시한다
- FR-2: 피처 모듈은 자기 destination 등록 함수와 Koin module만 노출한다 (feature 간 직접 참조 금지 — 로드맵 ⚠ 해소 규약)
- FR-3: ViewModel은 단일 UiState(StateFlow)를 노출하고 Composable은 렌더+Action 전달만 한다
- FR-4: 일회성 이벤트는 State에 넣지 않는다
- FR-5: Kermit 로거가 commonMain에서 사용 가능하고 사진/기록/계정 ID를 로그에 남기지 않는 규약을 CTX에 기록한다
- FR-6: androidApp/iosApp/catalogApp 호스트는 얇게 유지된다 (조립 호출만)

## Derived Requirements
- DR-1: libs.versions.toml에 navigation/koin/kermit 추가 — 존재하는 버전만 (Forbidden Decisions)
- DR-2: `:feature:login`이 내비 골격에 destination 등록 방식으로 재연결 (App.kt 직접 호출 제거)

## Requirement Gaps
~~Q1·Q2~~ 답변 완료 (2026-08-31): 내비=Navigation Compose, iOS 최소=17.0. P2 자동결정 4건은 questions 파일.

## Initial Risk Assessment
| 리스크 | 수준 | 대응 |
|--------|------|------|
| 내비 라이브러리 선택이 이후 전 화면에 파급 | 중 | Q1 사용자 확정 + ADR 기록 |
| iOS 18.2 최소 버전이 도달 범위를 크게 제한 | 중 | Q2 BLOCK — 사용자 결정 |
| KMP ViewModel/Navigation의 iOS 동작 차이 | 중 | ⚠️ UNCERTAIN: 구현 시 iOS 시뮬레이터 실행으로 검증 |
| 신규 의존성 3종 버전 오지정 | 저 | 구현 시점 공식 문서·Maven 실존 검증 (Forbidden 규칙) |
