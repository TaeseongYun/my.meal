# Requirement Verification Questions — record (F-3)

> **Request Anchor**: 로드맵 F-3 기록 생성 플로우 전체 (사용자 확정 2026-09-03).

## 요약

| # | 질문 | 유형 | 우선순위 | if-unanswered | 답변 |
|---|------|------|---------|---------------|------|
| Q1 | 끼니 구분 방식 | domain | P1 | BLOCK | A — diary Q1과 공유 [확실] |
| Q2 | 사진 입력 플랫폼 구현 | domain(외부 플랫폼 API) | P0 | BLOCK | A — expect/actual 직접 [확실] |
| Q3 | 생성 화면 필드 구성 | scope | P1 | AI-RECOMMEND-A | A — 디자인 판독 기준, 설계 단계 확정 [확실] |

## Q1. 끼니 구분 방식 (Scope: F-3 + F-5 공유)
diary Q1과 동일 결정 — 생성 화면에 끼니 선택(아침/점심/저녁)을 두고 mealType으로 저장.
- **답변 (2026-09-03)**: **A** — mealType 스키마 v2, 생성 시 끼니 선택 [확실]

## Q2. 사진 입력(촬영/앨범) 플랫폼 구현 (Scope: F-3, P0 — 플랫폼 API 연동)
- **A (AI 추천)**: expect/actual 직접 구현 — Android: ActivityResultContracts(TakePicture + PickVisualMedia), iOS: UIImagePickerController(카메라) + PHPickerViewController(앨범). 신규 서드파티 0 (승인 스택 유지), OS 표준 피커라 권한 모델 단순(Android PickVisualMedia·iOS PHPicker는 런타임 권한 불요, 카메라만 권한)
- B: CMP 라이브러리(peekaboo/calf 등) — 의존 추가·버전 검증 필요, 유지보수 리스크
- **답변 (2026-09-03)**: **A** — expect/actual 직접 [확실]

## Q3. 생성 화면 필드 구성 (Scope: F-3)
로드맵 정의: 음식 항목(이름·섭취량 자유 라벨)·메모·시각. Figma 생성하기 프레임과 대조 확정 필요 (REST 429 시 브라우저 뷰어 판독 우회 — 전례).
- **A (AI 추천)**: 설계 단계(STEP 6.5)에서 생성하기 프레임 판독 후 필드·레이아웃 확정, 로드맵 정의를 상한으로
- **답변 (2026-09-03)**: **A** — 설계 단계 확정 [확실]

## AI 자동 결정 (P2)
- 사진 저장: 원본 바이트 그대로 PhotoStore.save (리사이즈·압축은 F-6 업로드 시 — 로드맵 정의)
- 시각 기본값: 현재 시각, 편집 가능
- 저장 후 내비게이션: popBackStack으로 홈 복귀 (Flow가 자동 갱신 — F-5 FR-3)
- 항목 이름 미입력 시 저장 버튼 비활성 (FR-3 이름 필수)
