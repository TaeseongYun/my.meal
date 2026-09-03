# Requirements — diary (F-5) · 홈 연결 슬라이스

> **Request Anchor**: 로드맵 F-5 중 사용자 확정 범위(2026-09-03): 홈 캘린더·끼니 캐러셀의 실데이터 연결만.
> 기록 상세·수정·삭제 화면은 다음 사이클 (Out-of-Scope).

## Goal
홈 화면(:feature:home)이 스텁(stubHomeUiState) 대신 MealRepository의 실데이터를 표시한다.

## In-Scope
- HomeViewModel (F-1 UDF 골격: StateFlow UiState + Action, Koin module 기여)
- 이번 주 7일 기록 관찰 → WeekCalendarCard 마크 (기록 있음/없음)
- 오늘의 아침/점심/저녁 캐러셀 실데이터 (사진 photoPath, 메모 note, 시각 mealAt)
- shared homeDestination이 HomeViewModel 상태를 공급 (스텁 제거)

## Out-of-Scope
- 기록 상세/수정/삭제 화면 (F-5 잔여, 다음 사이클)
- 주차 이동(이전/다음 주 스와이프 — 디자인 미확정), 월 드롭다운 동작
- 기록 생성 (F-3), 빈 화면 PNG 교체(사용자 제공 대기)

## Functional Requirements
- FR-1: 홈 진입 시 이번 주(일~토) 각 날짜의 기록 유무가 캘린더 마크에 반영된다
- FR-2: 오늘의 끼니 캐러셀은 끼니별 기록(사진·메모·시각)을 표시하고, 없으면 미등록 상태를 보여준다
- FR-3: 기록 변경(저장/삭제)이 Flow로 홈에 자동 반영된다 (재조회 조작 불요)
- FR-4: :feature:home은 :core:data를 소비만 한다 (스키마·DAO 수정 금지 — F-2 소유)
- FR-5: HomeScreen 컴포저블은 stateless 유지 — 상태는 HomeViewModel이 공급
- FR-6: 앱 재실행 후에도 저장된 기록이 홈에 복원 표시된다 (오프라인 우선)

## Requirement Gaps → requirement-verification-questions.md
- 끼니 구분 방식 (MealEntry에 mealType 없음 — F-3와 공유 쟁점)
- 같은 끼니 다중 기록 표시 규칙
- 캘린더 마크·메뉴 라인 이모지 정책 (실데이터 기준)

## Initial Risk Assessment
- 스키마 변경 필요 시 F-2 소유권 절차(version 증가 + schemas export) — CTX Constraints 준수
- 7일 × observeByDate Flow 결합 성능은 로컬 DB 규모상 무시 가능
