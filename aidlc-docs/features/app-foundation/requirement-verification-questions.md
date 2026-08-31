# Requirement Verification Questions — app-foundation

> **Request Anchor**: 로드맵 F-1 — 앱 골격(내비·DI·UDF·로깅)과 조립 루트 구축.

## Summary
| # | Priority | Type | 질문 | If unanswered | 상태 |
|---|----------|------|------|---------------|------|
| Q1 | P1-IMPORTANT | domain | 내비게이션 라이브러리 선택 (열어둔 결정 #7) | AI-RECOMMEND-A | 미답변 |
| Q2 | P1-IMPORTANT | policy | 최소 지원 iOS 버전 (열어둔 결정 #9, 현재 18.2) | BLOCK | 미답변 |

BLOCK 1건. 질문 예산: comprehensive 12 중 2 사용.

### Q1. 내비게이션 라이브러리는 무엇으로 할까요?
- Priority: P1-IMPORTANT
- Scope: [Original Request] 열어둔 결정 #7 — F-1 담당
- Type: domain
- Category: flow
- Impact: high
- Reason: 전 화면의 라우팅 방식·백스택 규약을 결정. 혼용 금지 규칙상 최초 선택이 사실상 고정
- Choices:
  - A) JetBrains Navigation Compose (androidx Navigation의 KMP 포팅) → Android Compose 경험 그대로, 공식 KMP 지원, 기존 androidx-lifecycle 사용과 정합
  - B) Voyager → ScreenModel 통합이 간결하나 profile의 "ViewModel KMP 한 종류만" 규칙과 상충 여지
  - C) Decompose → 구성 컴포넌트 강력하나 학습 비용 높음 (1인 개발 기준 불리)
- AI Recommendation: A — Rationale: 개발자의 기존 Compose/androidx 경험 재사용(profile 학습비용 기준), lifecycle-viewmodel과 동일 계열, CMP 공식 문서 커버. 버전은 구현 시점 검증
- If unanswered: AI-RECOMMEND-A
- [Answer]:
- [Confidence]:

### Q2. 최소 지원 iOS 버전을 확정해 주세요 (현재 템플릿 기본 18.2)
- Priority: P1-IMPORTANT
- Scope: [Original Request] 열어둔 결정 #9 — 시장 도달 범위 결정
- Type: policy
- Category: scope
- Impact: high
- Reason: 18.2는 최신 기기만 지원(도달 범위 제한). 낮출수록 도달 넓어지나 테스트 부담 증가. 비즈니스 결정이라 AI가 확정하지 않음
- Choices:
  - A) 18.x 유지 → 최신 기기만, 테스트 부담 최소
  - B) 17.0 → 도달 범위 확대, 대부분 라이브러리 요구 충족
  - C) 16.0 → 더 넓은 도달, 구형 기기 검증 부담
  - D) 직접 입력
- If unanswered: BLOCK (Android는 기존 minSdk 24 유지 — 기설정 사실)
- [Answer]:
- [Confidence]:

## AI 자동 결정 (P2)
| # | 항목 | 기본값 | 근거 | 변경 영향 |
|---|------|--------|------|-----------|
| 1 | 로깅 | Kermit 도입, 루트 로거 shared 초기화 | profile 승인 스택("Kermit 계열") | 라이브러리 교체 시 1곳 |
| 2 | Koin 루트 모듈 위치 | shared(조립 루트) | ADR-0001 shared 책임 | 이동 용이 |
| 3 | :core:ui 등 추가 core 모듈 | 생성 안 함 | 빈 모듈 선행 스캐폴딩 금지 (ADR-0001) | 내용 발생 시 생성 |
| 4 | 라우트 표현 | type-safe(직렬화 가능 라우트) 규약 | 선택 라이브러리 표준 방식 따름 | 구현 시 확정 |
