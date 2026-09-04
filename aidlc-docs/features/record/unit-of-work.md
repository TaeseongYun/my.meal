# Unit of Work — record (F-3)

> **Request Anchor**: 로드맵 F-3 전체 (Q1=mealType 공유, Q2=expect/actual 직접, Q3=설계 단계 필드 확정).

## UOW 목록

### UOW-R1: :feature:record 모듈 + FAB 진입
- Size: **M**
- 내용: :feature:record 신설(RecordRoute/recordDestination — F-1 규약), homeDestination FAB onEditClick → RecordRoute 내비게이션(shared NavHost 등록), recordModule(Koin)
- AC1: 홈 FAB 탭 → 기록 화면 전환(양 플랫폼 빌드) AC2: 피처 간 직접 참조 0

### UOW-R2: 사진 입력 expect/actual
- Size: **L**
- 내용: `PhotoPicker` expect/actual — Android: ActivityResultContracts(TakePicture+PickVisualMedia), iOS: UIImagePickerController(촬영)+PHPickerViewController(앨범). Compose launcher 패턴, 결과=ByteArray. 카메라 권한 처리(Android CAMERA/iOS NSCameraUsageDescription)
- AC1: 에뮬레이터/시뮬레이터에서 앨범 선택 → 미리보기 표시 AC2: 권한 거부 시 크래시 없이 안내(FR-7)

### UOW-R3: 생성 화면 UI + RecordViewModel
- Size: **M**
- 내용: 생성하기 Figma 프레임 판독(429 시 뷰어 우회) 기준 UI — 끼니 선택(아침/점심/저녁), 사진 영역, 음식 항목 리스트(이름·섭취량 자유 라벨), 메모, 시각(기본=현재). RecordViewModel UDF(UiState/Action/Effect), 이름 필수 저장 활성 규칙
- AC1: ViewModel 상태 전이 테스트 green(항목 추가/삭제·저장 활성) AC2: UI가 판독된 디자인과 대조 기록됨

### UOW-R4: 저장 파이프라인
- Size: **M**
- 내용: 저장 → PhotoStore.save(entryId, bytes) + MealRepository.upsert(mealType 포함) → popBackStack 홈 복귀. 사진 없는 저장 허용 여부는 디자인 판독 따름(기본: 허용 — photoPath null)
- AC1: 저장 후 홈 캘린더·캐러셀 반영(E2E, F-5 병합 후) AC2: 재실행 후 복원(FR-5·오프라인 우선)

## 의존 그래프
UOW-R1 → UOW-R2 → UOW-R3 → UOW-R4 (직렬). **선행: diary UOW-D1(스키마 v2) main 머지 후 리베이스**

## 응집도: 단일 도메인(기록 생성). 사이즈: M×3+L×1 → 기술 설계 필수 (GATE-3.5)
