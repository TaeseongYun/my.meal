# ADR-0001: DroidKaigi식 Gradle 모듈 구조 채택

- Status: accepted (2026-08-31, 사용자 결정: "디자인 시스템은 충족하지만 이제 드로이드 카이기 식으로 모듈 분리를 원합니다.")
- Context: 기존 원칙은 "단일 shared 모듈 + feature-first 패키지, 멀티모듈은 독립 소비자 발생 시" (kmp-module-structure 규칙 + project-profile). :core:designsystem은 catalogApp 소비자로 이미 모듈. 로그인 화면 배치 논의에서 사용자가 DroidKaigi conference-app-2026식 모듈 구조로 전환을 확정.
- Options:
  - A) 단일 shared 유지 (기존 규칙) — 모듈 오버헤드 없음, 그러나 사용자가 원하는 경계 명시성 부족
  - B) DroidKaigi식 core/feature 모듈 분리 — 경계가 Gradle로 강제됨, 빌드 구성 비용 증가
- Decision: B. 구조 규칙:
  - `:core:<name>` — 공유 기반 (현재 designsystem. model/ui 등은 실제 내용 생길 때)
  - `:feature:<slug>` — 화면 피처 (로드맵 F-3~F-7은 각 피처 착수 시 모듈 생성)
  - `shared` — 조립 루트(App, 내비게이션, DI 루트) + iOS framework "Shared"
  - `androidApp`/`iosApp`/`catalogApp` — 얇은 호스트
  - 빈 모듈 선행 스캐폴딩 금지는 유지 — 착수 시점에 생성
  - feature 간 직접 의존 금지 유지 — 교차는 core로 승격
- Impact: kmp-module-structure의 "single module until consumer" 규칙은 이 프로젝트에서 ctx 우선 원칙으로 대체됨 (스킬 문서 스스로 "Project ctx/ overrides this document" 명시). 첫 적용: :feature:login 신설.
