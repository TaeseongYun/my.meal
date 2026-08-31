# Requirement Verification Questions — data-foundation

> **Request Anchor**: 로드맵 F-2 — Room KMP 로컬 DB + 이미지 파일 저장, 오프라인 우선.

## Summary
| # | Priority | Type | 질문 | If unanswered | 상태 |
|---|----------|------|------|---------------|------|
| Q1 | P1-IMPORTANT | domain | 데이터 모듈 배치 | AI-RECOMMEND-A | 답변됨: B |
| Q2 | P1-IMPORTANT | domain | 음식 양(amount) 표현 방식 | AI-RECOMMEND-C | 답변됨: B |
| Q3 | P1-IMPORTANT | domain | 총 예상 칼로리 저장 방식 | AI-RECOMMEND-A | 답변됨: A |

BLOCK 0건. 질문 예산: comprehensive 12 중 3 사용.

### Q1. 데이터 계층은 어떤 모듈로 만들까요?
- Priority: P1-IMPORTANT
- Scope: [Original Request] ADR-0001 "data 계층 모듈 분리는 F-2 기술 설계에서 판단"
- Type: domain
- Category: flow
- Impact: high
- Reason: F-3/F-5/F-6이 소비할 경계. DroidKaigi는 core/data+core/model 분리형
- Choices:
  - A) `:core:data` 단일 모듈 (Room+이미지 저장+repository+도메인 모델 포함) → 모듈 1개, 소비자는 인터페이스만 봄
  - B) `:core:model` + `:core:data` 분리 (DroidKaigi 동형) → 도메인 모델 독립성 명확, 모듈 2개
- AI Recommendation: A — Rationale: 현재 도메인 모델 소비자가 data 소비자와 동일. 모델 독립 소비자(예: F-4 추론 결과 타입 공유)가 생기면 :core:model 분리는 기계적 이동. 빈 모듈 최소화 원칙 정합
- If unanswered: AI-RECOMMEND-A
- [Answer]: B
- [Confidence]: 확실

### Q2. 음식 항목의 양(amount)은 어떻게 저장할까요? (소스 문서: "음식명, 양 또는 g 값을 확인·수정")
- Priority: P1-IMPORTANT
- Scope: [Original Request] 핵심 흐름 5 / MVP "음식별 섭취량"
- Type: domain
- Category: policy
- Impact: high
- Reason: 스키마 초판 필드 — 이후 변경은 migration 필요. F-4 칼로리 계산의 입력
- Choices:
  - A) g 수치만 (amountGram: Int) → 계산 단순, 사용자가 무게를 모르면 입력 부담
  - B) 자유 라벨만 ("1인분", "반 개") → 입력 쉬움, 칼로리 계산은 라벨 해석 필요
  - C) 라벨 + 선택적 g (amountLabel: String?, amountGram: Int?) → 소스 문서의 "양 또는 g" 문구 그대로, 둘 다 수용
- AI Recommendation: C — Rationale: 소스 문서가 두 표현을 병기. F-4에서 g 있으면 정밀 계산, 없으면 라벨 기반 추정으로 흐름 유지
- If unanswered: AI-RECOMMEND-C
- [Answer]: B
- [Confidence]: 확실

### Q3. 기록의 총 예상 칼로리는 저장할까요, 계산할까요?
- Priority: P1-IMPORTANT
- Scope: [Original Request] MVP "총 예상 칼로리"
- Type: domain
- Category: policy
- Impact: medium
- Reason: 정합성 vs 조회 성능의 표준 트레이드오프
- Choices:
  - A) 항목 합산으로 계산 (비저장) → 정합성 보장, 항목 수 적어 성능 무의미
  - B) entry에 저장 → 항목 수정 시 갱신 누락 위험
- AI Recommendation: A — Rationale: 기록당 항목 수가 소수(한 끼 식사). 저장 시 불일치 버그 위험만 추가. 성능 최적화는 측정 근거 있을 때만(품질 기준)
- If unanswered: AI-RECOMMEND-A
- [Answer]: A
- [Confidence]: 확실

## AI 자동 결정 (P2)
| # | 항목 | 기본값 | 근거 | 변경 영향 |
|---|------|--------|------|-----------|
| 1 | PK | UUID 문자열 | 오프라인 생성+향후 원격 동기화(F-6) 충돌 회피 표준 | migration |
| 2 | 이미지 경로 규약 | 앱 내부 files/photos/{entryId}.jpg | 외부 저장소 권한 불필요, entry와 1:1 | 파일 이동 유틸 |
| 3 | 시각 표현 | epoch millis(UTC) 저장 + 표시 시 로컬 변환 | KMP 공통 표준(kotlinx-datetime 계열, 구현 시 검증) | 변환 계층만 |
| 4 | Room/SQLite 버전 | 구현 시점 공식 문서·Maven 실존 검증 후 확정 | Forbidden Decisions(버전 추측 금지) | 카탈로그 1곳 |

soft-delete/sync 필드: 질문 아님 — 로드맵 ⚠ 해소 표에 따라 F-6 소유로 확정됨.
