<!-- workflow-step: STEP-6.5 | gate: GATE-3.5 | producer: ctx-aidlc-run -->
# Technical Design — diary (F-5 홈 슬라이스)

> **Request Anchor**: 홈 실데이터 연결 (Q1=mealType v2, Q2=최신 1건, Q3=스텁 세트 랜덤).

## Implementation Scope
- production / 제외: 기록 상세·수정·삭제(다음 사이클), 주차 이동, 빈 화면 PNG

## 1. Design Overview
- brownfield touchpoints: :core:model(MealType 신설), :core:data(entity·mapper·migration·schemas), :feature:home(HomeViewModel·HomeUiState 매핑·HomeNavigation), :shared(dataModule·appModules)

## 2. Architecture Decisions
### ADR-D1. mealType 스키마 v2 (Q1=A, 사용자 결정)
- `:core:model` `enum class MealType { BREAKFAST, LUNCH, DINNER }`, `MealEntry.mealType: MealType`
- `meal_entries.mealType TEXT NOT NULL DEFAULT 'DINNER'` — Room AutoMigration(1→2) + schemas export. 기존 데이터 없음(프리 프로덕션)이라 기본값 영향 없음
- :feature:home의 `MealType(label)` enum은 core 타입 소비로 교체 — UI 라벨은 feature 확장 프로퍼티
### ADR-D2. 데이터 계층 Koin 조립은 shared 소유
- :core:data는 Koin 무지(순수 유지). shared에 `dataModule` — expect/actual: Android(androidContext→DatabaseBuilder/filesDir), iOS(documents 경로). single<MealRepository>
### ADR-D3. 이미지 로딩은 expect/actual 디코더 (서드파티 0)
- `expect fun decodeImageBitmap(path: String): ImageBitmap?` — Android BitmapFactory, iOS skia Image.makeFromEncoded. coil 등 도입 안 함 (ponytail — 필요해지면 교체)

## 3. API Specification (Kotlin)
```kotlin
// :feature:home
class HomeViewModel(repository: MealRepository, clock: Clock = Clock.System) : ViewModel {
    val uiState: StateFlow<HomeUiState>   // 주간 7일 observeByDate combine → 매핑
}
val homeModule: Module                     // viewModelOf(::HomeViewModel)
fun NavGraphBuilder.homeDestination()      // 내부 koinViewModel, 스텁 제거
// 매핑 규칙 (순수 함수, 테스트 대상)
fun mapToUiState(today: LocalDate, week: List<LocalDate>, byDay: List<List<MealEntry>>): HomeUiState
// 마크: 기록 있는 날 = STUB_MARKS[date.dayOfMonth % 3] (결정적 랜덤), 없는 날 = null("?")
// 슬롯: mealType별 최신(mealAt max) 1건 — photoPath·note·"오후 h:mm"·메뉴 이모지 STUB_MARKS[id.hashCode % 3]
// MealSlotState.photo: DrawableResource? → photoPath: String? 로 교체 (sample_meal_photo는 프리뷰 전용)
```

## 4. Data Model
- v2 migration: ADD COLUMN mealType (위 ADR-D1). DAO 쿼리 변경 없음(observeBetween 그대로)

## 5. Module/Component Structure
| Module/File | 책임 | UOW |
|---|---|---|
| core/model MealEntry.kt(+MealType) | 도메인 타입 | D1 |
| core/data MealEntities/MymealDatabase(v2·AutoMigration)/mapper/schemas | 스키마 확장 | D1 |
| feature/home HomeViewModel.kt(+매핑)/HomeKoinModule.kt | UDF·DI | D2 |
| shared di/DataModule.kt(expect/actual) + AppModule 포함 | 데이터 조립 | D2 |
| feature/home HomeNavigation.kt/HomeUiState.kt/HomeScreen.kt(사진 타입) + ImageDecoder expect/actual | 실데이터 렌더 | D3 |

## 6. Interaction Flow
홈 진입 → HomeViewModel(observeByDate×7 combine) → HomeUiState → HomeScreen 렌더. 저장/삭제 발생 시 Flow 자동 갱신 (FR-3)

## 7. Non-functional Design
- 로그: 기록 내용·사진 경로 금지 (CTX). 성능: 로컬 DB 7쿼리 — 무시 가능

## 8. Testing Approach
| UOW | 유형 | 내용 |
|---|---|---|
| D1 | 저장 테스트(jvm+iOS) | v2 왕복(mealType 보존), auto-migration 스모크 |
| D2 | 단위 | 매핑: 기록→마크/슬롯·최신 1건·미등록 null·시각 포맷. Koin 구성검증 갱신 |
| D3 | 실행 | 에뮬레이터 — 기록 0건 홈(전부 미등록) 표시. F-3 병합 후 E2E에서 실기록 반영 확인 |

## 9. Open Items
- 스텁 이모지 세트·랜덤 규칙은 디자이너 확정 시 교체 (기존 확정 필요 항목과 함께)
- E2E(기록 생성→홈 반영)는 F-3 머지 후 통합 검증에서 수행
