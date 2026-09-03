# Unit of Work — diary (F-5 홈 슬라이스)

> **Request Anchor**: 홈 캘린더·끼니 캐러셀 실데이터 연결 (Q1~Q3 답변 반영, 2026-09-03).

## UOW 목록

### UOW-D1: 스키마 v2 — mealType (F-2 확장 절차)
- Size: **M**
- 내용: :core:model에 MealType(BREAKFAST/LUNCH/DINNER) + MealEntry.mealType 추가, :core:data entity·mapper 반영, Room v1→2 auto-migration(ADD COLUMN 기본값), schemas export, 저장 테스트 갱신. data-foundation 문서에 v2 기록
- AC1: 스키마 v2 export 커밋 포함, 저장 테스트(양 타깃) green AC2: 기존 v1 DB 열림(auto-migration)

### UOW-D2: HomeViewModel + 데이터 DI
- Size: **M**
- 내용: :feature:home에 HomeViewModel(UDF) — observeByDate×7 combine → HomeUiState 매핑(마크=기록 있는 날 스텁 세트 {🥗🍚🍞} 결정적 랜덤, 끼니 슬롯=mealType별 최신 1건, 시각 "오후 h:mm"), homeModule(Koin) 노출. shared에 데이터 계층 platform Koin module(DB·PhotoStore·Repository — Android Context/iOS documents 경로)
- AC1: HomeViewModel 매핑 단위 테스트 green (기록→마크/슬롯, 다중 기록 최신 1건, 미등록 null) AC2: Koin 구성검증 테스트에 신규 정의 포함 green

### UOW-D3: homeDestination 실데이터 연결
- Size: **M**
- 내용: homeDestination이 koinViewModel<HomeViewModel> 상태 공급(스텁 제거 — stubHomeUiState는 테스트/프리뷰 전용으로 격하), photoPath→ImageBitmap 디코더 expect/actual(Android BitmapFactory / iOS skia), MealSlotState 사진 타입 교체
- AC1: 앱 실행 시 홈이 DB 상태를 표시(기록 0건이면 전부 미등록) AC2: FR-3 — DB 변경이 자동 반영(Flow)

## 의존 그래프
UOW-D1 → UOW-D2 → UOW-D3 (직렬)

## 응집도: 단일 도메인(홈 조회). 사이즈: M×3 → 기술 설계 필수 (GATE-3.5)
