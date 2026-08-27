# Requirement Verification Questions — design-system

> **Request Anchor**: 피그마 스타일 가이드(node 836:33127) 매니페스트를 기반으로 KMP(Compose Multiplatform) 디자인 시스템 파운데이션을 shared 모듈에 구축한다.

## Summary

| # | Priority | Type | 질문 | If unanswered | 상태 |
|---|----------|------|------|---------------|------|
| Q1 | P1-IMPORTANT | domain | 이서윤체 폰트 파일 소싱 | AI-RECOMMEND-B | 답변됨: A |
| Q2 | P1-IMPORTANT | domain | 아이콘 구현 범위 | AI-RECOMMEND-B | 답변됨: B |
| Q3 | P1-IMPORTANT | domain | Material3 연동 방식 | AI-RECOMMEND-C | 답변됨: C |
| Q4 | P1-IMPORTANT | domain | 다크 모드 지원 여부 | AI-RECOMMEND-A | 답변됨: A |
| Q5 | P1-IMPORTANT | domain | 디자인 시스템 모듈 배치 | AI-RECOMMEND-A | 답변됨: :core:designsystem 신설 |

BLOCK 질문: 0개. 질문 예산: standard 7 중 5 사용.

---

### Q1. 이서윤체(LeeSeoyun) 폰트 파일을 어떻게 확보할까요?
- Priority: P1-IMPORTANT
- Scope: [Original Request] 디자인 명기 "이서윤체 국, 영문 통일" — 전체 타이포의 기반 서체
- Type: domain
- Category: integration
- Impact: high
- Reason: 폰트 파일(TTF/OTF)이 commonMain 리소스로 번들되어야 디자인과 동일 렌더링. 배포용 라이선스 확인도 필요 (AI가 라이선스를 단정하지 않음)
- Choices:
  - A) 사용자가 폰트 파일 제공 → 받는 즉시 리소스 번들 + FontFamily 토큰 연결
  - B) 폰트 파일 없이 우선 구현 → FontFamily 토큰만 시스템 기본 폰트로 두고, 파일 확보 시 토큰 한 곳 교체
  - C) 기타 (직접 입력)
- AI Recommendation: B) 구현이 폰트 파일에 차단되지 않도록 FontFamily 주입 구조(FR-6)로 먼저 구축 — Rationale: 토큰 구조는 폰트와 무관하게 동일, 파일 도착 시 1개 파일 수정으로 완결
- If unanswered: AI-RECOMMEND-B (구현 비차단 구조 우선)
- [Answer]: A
- [Confidence]: 확실 확실 (단, 폰트 파일 수령 대기 — UOW 의존성으로 추적)

### Q2. 아이콘 24종은 어떻게 처리할까요? (디자인 원본 주석: "확정 x, 예시로 넣었습니다")
- Priority: P1-IMPORTANT
- Scope: [Original Request] 스타일 가이드 @ Icon 그룹
- Type: domain
- Category: scope
- Impact: medium
- Reason: 디자이너가 미확정이라 명기함. 선구현 시 재작업 리스크
- Choices:
  - A) 24종 전부 지금 벡터 리소스로 구현 → 재작업 리스크 수용
  - B) 파운데이션에서 제외, 아이콘 확정 후 별도 작업 → 이번 범위 축소
  - C) `ic/` 네임드 9종(share, list, arrow 좌/우, settings, loading, check, more, download)만 구현 → 앱 고유 아이콘만 선반영
- AI Recommendation: B) 제외 — Rationale: 원본에 "확정 x" 명기. 미확정 자산 선구현은 낭비이며, 아이콘은 토큰 구조에 의존하지 않아 나중에 독립 추가 가능
- If unanswered: AI-RECOMMEND-B
- [Answer]: B
- [Confidence]: 확실

### Q3. Material3와의 연동 방식은?
- Priority: P1-IMPORTANT
- Scope: [Original Request] 테마 진입점/토큰 접근 API 설계
- Type: domain
- Category: flow
- Impact: high
- Reason: shared 모듈에 material3 의존성이 이미 있음. 이후 컴포넌트 구현 방식을 결정하는 구조 선택
- Choices:
  - A) MaterialTheme(ColorScheme/Typography)에만 매핑 → M3 시맨틱에 피그마 토큰을 끼워맞춤, 이름 왜곡 발생
  - B) 순수 커스텀 테마(CompositionLocal)만 → 피그마 이름 그대로, 단 M3 컴포넌트 사용 시 기본색 불일치
  - C) 하이브리드: 커스텀 토큰(주 API) + MaterialTheme 브리지(주요 값만 매핑) → 피그마 충실 + M3 컴포넌트 호환
- AI Recommendation: C) 하이브리드 — Rationale: 접근 API는 피그마 명명(Bg/Text/Accent) 그대로 유지하고, 이미 의존 중인 M3 컴포넌트(버튼 등)도 위화감 없는 기본값을 갖게 됨. 업계 표준 패턴
- If unanswered: AI-RECOMMEND-C
- [Answer]: C
- [Confidence]: 확실

### Q4. 다크 모드를 지원할까요?
- Priority: P1-IMPORTANT
- Scope: [Original Request] 컬러 토큰 구조
- Type: domain
- Category: scope
- Impact: medium
- Reason: 디자인에는 라이트 팔레트만 존재. 다크 팔레트를 AI가 임의 생성하는 것은 디자인 결정 침범
- Choices:
  - A) 라이트 전용 → 디자인에 있는 것만 구현. 테마 구조 자체가 추후 다크 추가 지점이 됨
  - B) AI가 다크 팔레트 생성해 즉시 지원 → 디자이너 미승인 색상 사용
- AI Recommendation: A) 라이트 전용 — Rationale: 디자인 근거 없는 색 생성 금지. CompositionLocal 구조상 다크 추가는 추후 팔레트 1개 정의로 가능
- If unanswered: AI-RECOMMEND-A
- [Answer]: A
- [Confidence]:

### Q5. 디자인 시스템 코드는 어디에 둘까요?
- Priority: P1-IMPORTANT
- Scope: [Original Request] "KMP 형태로" — 모듈 구조
- Type: domain
- Category: flow
- Impact: medium
- Reason: 배치에 따라 gradle 구성 변경 여부가 달라짐
- Choices:
  - A) `shared` 모듈 내 패키지 `com.devts.mymeal.designsystem` → gradle 변경 최소, 즉시 사용
  - B) 신규 `:designsystem` gradle 모듈 분리 → 모듈 경계 명확, 빌드 스크립트/iOS 프레임워크 구성 추가 작업
- AI Recommendation: A) shared 내 패키지 — Rationale: 현재 2모듈 템플릿에서 별도 모듈은 투기적 분리. 패키지→모듈 승격은 필요해질 때 기계적으로 가능
- If unanswered: AI-RECOMMEND-A
- [Answer]: :core:designsystem 모듈을 하나 추가
- [Confidence]: 확실

---

## AI 자동 결정 (P2)

| # | 항목 | 기본값 | 근거 | 변경 영향 |
|---|------|--------|------|-----------|
| 1 | Letter spacing | 전 스타일 0px | 디자인 주석 "*All letter spacing 0px"가 명시적. 샘플 실측 -0.3(Body/Detail)과 상충하나 주석을 우선 | 스타일당 1줄 수정 |
| 2 | 컬러 명명 | semantic(Bg/Text/Alert/Accent) 주 API + raw(Gray/Beige) 내부 팔레트 | 피그마 이중 표기 그대로 반영 | 이름 변경만 |
| 3 | "BG 3" 중복 라벨 | #F8F5ED=Bg3, #E9E6DE=Bg4로 순번 부여 | 원본 라벨 중복은 오기로 판단, 팔레트명(Beige2/3) 순서 유지 | 이름 변경만 |
| 4 | 타이포 명명 | H1~H4, Body1, Body2, Detail | 피그마 그룹명 그대로 | 이름 변경만 |
| 5 | 스페이싱 명명 | 수치 그대로 (spacing4 … spacing40) | 피그마 라벨이 수치 자체 | 이름 변경만 |

## Additional Questions (Next Round)

없음.
