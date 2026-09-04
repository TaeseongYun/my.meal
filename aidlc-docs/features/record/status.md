# Feature Status — record

- Feature Slug: record (F-3) — **UI 슬라이스만 구현** (디자인 우선, 사용자 지시 2026-09-03)
- Request Type: ui-first-slice (F-3 전체 범위의 화면 부분)
- Current Phase: UI 구현 완료 — score loop COMPLETE
- Post-Implementation Score: **98/100** (round 1, COMPLETE — dependency-check.md 참조)
- 구현 범위: `:feature:record` (RecordScreen/RecordUiState/RecordNavigation), Figma 832:98315.
  홈 FAB → 기록 화면 내비게이션 연결(F-1 NavHost 규약 준수)
- 잔여 (후속 피처/사용자 대기):
  - 저장·카메라·시간 변경 no-op → F-2 데이터 연결 (upsert, 사진 expect/actual, 시간 피커)
  - 날짜+끼니 슬롯 조회: `MealRepository`에 슬롯 조회 API 없음 — 날짜+슬롯 단건 조회 추가 필요
  - **슬롯 정의 충돌 (2026-09-04 확인)**: 미머지 `diary` 브랜치 커밋 `8adaa83`이 `:core:model`에
    `MealType { BREAKFAST, LUNCH, DINNER }` + `meal_entries.meal_type`(스키마 v2, AutoMigration 1→2)을
    이미 추가함 — **간식(SNACK) 없음**. 기록 화면은 디자인 기준 4칸(간식 포함)이라 데이터 연결 시
    ① 간식을 MealType에 추가(스키마 v3)하거나 ② 기록 화면에서 간식을 제거해야 함. 디자이너·F-5와 확정 필요
  - 생성/수정 분기: 현재 `isEdit` 파생값 — 실기록 id 존재 여부로 교체(F-2)
  - 디자인 원본 JSON(manifest/tokens) 추출 — REST nodes API 429 해소 시
  - 디자이너 확인: 간식 슬롯 포함 여부, 이모지 9종 의미·선택 표시, 대표 게시물 동작, 날짜 요일 불일치 (design-manifest.md)
  - Canvas 근사 아이콘(←/›/카메라/체크) 렌더 크롭 교체 후보
- Last Updated: 2026-09-03
