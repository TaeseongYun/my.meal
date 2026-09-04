<!-- workflow-step: STEP-6.5 | gate: GATE-3.5 | producer: ctx-aidlc-run -->
# Technical Design — record (F-3)

> **Request Anchor**: 기록 생성 플로우 전체 (Q1=mealType, Q2=expect/actual 직접, Q3=설계 단계 확정).

## Implementation Scope
- production / 제외: AI 인식·칼로리(F-4), 동기화(F-6), 수정·삭제(F-5 잔여)

## 1. Design Overview
- brownfield touchpoints: :feature:record(신설), :feature:home(FAB 콜백 노출 — homeDestination(onEditClick)), :shared(NavHost dest 추가·appModules에 recordModule), androidApp Manifest(CAMERA), iosApp Info.plist(NSCameraUsageDescription·NSPhotoLibraryAddUsageDescription 불요 — PHPicker는 권한 불요)

## 2. Architecture Decisions
### ADR-R1. 사진 입력 expect/actual 직접 (Q2=A, 사용자 결정)
- common: `PhotoPickResult(bytes: ByteArray)`, `@Composable expect fun rememberPhotoPicker(onResult): PhotoPickerLauncher { launchCamera(); launchGallery() }`
- Android: rememberLauncherForActivityResult(TakePicture→FileProvider 임시파일→bytes, PickVisualMedia→uri→bytes). CAMERA 권한 요청 포함
- iOS: UIImagePickerController(sourceType=.camera)/PHPickerViewController — UIKit interop, delegate→bytes
- 서드파티 0. 카메라 없는 에뮬레이터/시뮬레이터는 앨범 경로로 검증
### ADR-R2. 끼니 선택은 mealType 저장 (Q1=A — diary ADR-D1 스키마 v2 소비)
- 기본 선택: 현재 시각 구간 근사(아침 ~10:59/점심 11:00~15:59/저녁 16:00~) — 저장은 항상 사용자 선택값
### ADR-R3. 저장 원자성
- entryId = uuid 선생성 → PhotoStore.save(entryId, bytes) → upsert(photoPath 포함). upsert 실패 시 PhotoStore.delete로 사진 롤백 (고아 파일 방지 — F-2 delete 계약 재사용)

## 3. API Specification (Kotlin)
```kotlin
// :feature:record
@Serializable data object RecordRoute
fun NavGraphBuilder.recordDestination(onDone: () -> Unit)
class RecordViewModel(repository: MealRepository, photoStore: PhotoStore) : ViewModel {
    val uiState: StateFlow<RecordUiState>   // mealType, photoBytes?, items: List<ItemInput>, note, mealAt
    fun onAction(a: RecordAction)            // SelectMealType/SetPhoto/AddItem/RemoveItem/EditItem/EditNote/EditTime/Save
    val effects: Flow<RecordEffect>          // Saved
}
val recordModule: Module
// :feature:home 변경 — homeDestination(onEditClick: () -> Unit) 파라미터 추가 (FAB 콜백 hoisting 유지)
```

## 4. Data Model
- 변경 없음 — diary의 v2(mealType) 소비만 (FR-6)

## 5. Module/Component Structure
| Module/File | 책임 | UOW |
|---|---|---|
| feature/record build.gradle.kts + settings — :feature:login 템플릿 | 모듈 신설 | R1 |
| feature/record RecordNavigation.kt / shared App.kt·AppModule.kt | 진입·DI 등록 | R1 |
| feature/record photo/PhotoPicker.kt(+android/ios actual), androidApp Manifest·iosApp Info.plist | 사진 입력 | R2 |
| feature/record RecordScreen.kt·RecordUiState.kt·RecordViewModel.kt | UI·UDF | R3 |
| feature/record 저장 로직(ADR-R3) + 테스트 | 파이프라인 | R4 |

## 6. Interaction Flow
홈 FAB → RecordRoute → 사진(촬영/앨범)·끼니·항목·메모·시각 입력 → Save → PhotoStore+upsert → Saved effect → popBackStack → 홈 Flow 자동 갱신

## 7. Non-functional Design
- 사진 원본 저장(P2 — 리사이즈는 F-6), 로그에 사진 경로·기록 내용 금지 (CTX)

## 8. Testing Approach
| UOW | 유형 | 내용 |
|---|---|---|
| R1 | 빌드 | 전 모듈 컴파일 + FAB 전환(에뮬레이터) |
| R2 | 실행 | 에뮬레이터 앨범 선택→미리보기, 권한 거부 경로 |
| R3 | 단위 | RecordViewModel 전이(항목 추가/삭제, 저장 활성, 끼니 기본값) |
| R4 | 단위+실행 | 저장 성공(사진 유/무)·upsert 실패 롤백(fake), E2E 저장→홈 반영·재실행 복원 |

## 9. Open Items
- ⚠️ 생성하기 프레임 최종 판독(UOW-R3 착수 시) — 필드 구성이 로드맵 정의와 다르면 게이트에 보고
- ⚠️ UNCERTAIN: iOS 카메라 UIKit interop 실동작 — 시뮬레이터는 카메라 없음 → 앨범 검증 + 실기기 항목으로 이연
