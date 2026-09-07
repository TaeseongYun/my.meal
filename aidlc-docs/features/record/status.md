# Feature Status — record

- Feature Slug: record (F-3) — **UI 슬라이스만 구현** (디자인 우선, 사용자 지시 2026-09-03)
- Request Type: ui-first-slice (F-3 전체 범위의 화면 부분)
- Current Phase: 데이터 연결 완료 — score loop COMPLETE (UI 98/100 → 데이터 연결 96 → 재검증 93/100)
- Post-Implementation Score: **93/100** (round 2, COMPLETE — dependency-check.md 참조).
  round 1(96) 대비 −3은 코드 회귀가 아니라 경고 계측을 처음 반영한 결과
- 구현 범위: `:feature:record` (RecordScreen/RecordUiState/RecordNavigation), Figma 832:98315.
  홈 FAB → 기록 화면 내비게이션 연결(F-1 NavHost 규약 준수)
- 잔여 (후속 피처/사용자 대기):
  - 저장·카메라·시간 변경 no-op → F-2 데이터 연결 (upsert, 사진 expect/actual, 시간 피커)
  - 날짜+끼니 슬롯 조회: `MealRepository`에 슬롯 조회 API 없음 — 날짜+슬롯 단건 조회 추가 필요
  - ~~**슬롯 정의 충돌 (2026-09-04 확인)**~~ → **해소(2026-09-07 재검증)**: `MealType`이 SNACK 포함
    4종으로 확정(#8), `RecordSlot.toMealType()`가 이름 1:1 매핑. 전수 매핑 테스트
    `toMealType_mapsEverySlot`로 고정 — 슬롯이 어긋나면 테스트가 먼저 깨진다
  - 생성/수정 분기: 현재 `isEdit` 파생값 — 실기록 id 존재 여부로 교체(F-2)
  - 디자인 원본 JSON(manifest/tokens) 추출 — REST nodes API 429 해소 시
  - 디자이너 확인: 간식 슬롯 포함 여부, 이모지 9종 의미·선택 표시, 대표 게시물 동작, 날짜 요일 불일치 (design-manifest.md)
  - Canvas 근사 아이콘(←/›/카메라/체크) 렌더 크롭 교체 후보
- 데이터 연결 (2026-09-04): RecordViewModel UDF+저장 파이프라인(uuid→PhotoStore→upsert, 실패 롤백),
  사진 expect/actual(Android TakePicture/PickVisualMedia+FileProvider, iOS UIImagePicker),
  Material3 TimePicker, 스키마 v3(food_emoji·is_representative)+홈 이모지 연동, recordModule Koin 등록
- Last Updated: 2026-09-04
