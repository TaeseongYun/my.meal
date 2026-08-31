<!-- workflow-step: STEP-6.5 | gate: GATE-3.5 | producer: ctx-aidlc-run -->
# Technical Design — data-foundation (F-2)

> **Request Anchor**: 로드맵 F-2 — Room KMP 로컬 DB + 이미지 파일 저장, 오프라인 우선 (:core:model + :core:data).

## Implementation Scope
- Implementation level: production / Mock: none
- 제외: sync·soft-delete 필드(F-6 migration), 이미지 리사이즈(F-6), 화면(F-3/F-5)

## 1. Design Overview
- Target: 신규 `:core:model`(순수 Kotlin KMP), `:core:data`(Room+파일 저장) — ADR-0001 구조
- 방향: 스키마 v1 최소(답변 반영: 라벨 양, 칼로리 비저장), repository는 Flow 반환, 소비자는 인터페이스만
- brownfield touchpoints: settings.gradle.kts(2 include), libs.versions.toml(room/sqlite/ksp), shared는 아직 미연결(F-3에서 소비 시작)

## 2. Architecture Decisions
### ADR-1. 모듈 분리 :core:model + :core:data
- Status: accepted (GATE-2 Q1=B, 사용자 결정)
- Decision: model=도메인 모델·합산 함수(의존성 0), data=Room entity/DAO/repository 구현+PhotoStore. 소비 방향: feature → core:data(인터페이스) → core:model
### ADR-2. Room KMP + 번들 SQLite 드라이버
- Status: accepted (조건부 — 버전은 구현 시점 검증)
- Context: Room KMP(2.7+ 계열)가 androidx 공식 KMP 경로. 드라이버는 번들 SQLite로 양 플랫폼 동일 동작
- Options: A) Room KMP+BundledSQLiteDriver — 공식·profile 승인 스택 / B) SQLDelight — 검증됐으나 profile 우선순위(Room) 위배
- Decision: A. ⚠️ UNCERTAIN: KSP/Room gradle plugin의 KMP 타깃별 구성 세부 — 구현 시 공식 문서로 확정 (UOW-2 AC2에 검증 기록 포함)
- Impact: DB 파일 경로만 expect/actual (android: context DB path, iOS: NSFileManager 문서 경로)
### ADR-3. 이미지 저장·삭제 규약
- Status: accepted
- Decision: 앱 내부 files/photos/{entryId}.jpg (P2 규약). 삭제는 DB 우선 커밋 후 파일 삭제, 파일 삭제 실패는 무롤백 + 고아 파일 정리 유틸(기동 시 선택 실행). 사진 원본 보존 정책(백업용 압축 등)은 F-6 소관

## 3. API Specification (Kotlin 공개 API)
```kotlin
// :core:model (순수 Kotlin)
data class MealEntry(id: String/*UUID*/, mealAt: Long/*epoch ms UTC*/, note: String?, photoPath: String?, createdAt: Long, updatedAt: Long, items: List<MealItem>)
data class MealItem(id: String, name: String, amountLabel: String?, estimatedKcal: Int?, orderIndex: Int)
fun MealEntry.totalEstimatedKcal(): Int?  // 항목 합산(Q3=A), 전 항목 null이면 null

// :core:data
interface MealRepository {
    fun observeByDate(localDate: LocalDateStamp): Flow<List<MealEntry>>  // 일 단위(로컬 TZ)
    suspend fun get(id: String): MealEntry?
    suspend fun upsert(entry: MealEntry)
    suspend fun delete(id: String)   // ADR-3 규약으로 사진 파일도 정리
}
interface PhotoStore {
    suspend fun save(entryId: String, bytes: ByteArray): String  // 반환: 저장 경로
    suspend fun delete(entryId: String)
    fun pathOf(entryId: String): String?
}
```
HTTP API 없음.

## 4. Data Model (schema v1)
| Table | Field | Type | 제약 |
|-------|------|------|------|
| meal_entries | id | TEXT PK | UUID |
| | meal_at | INTEGER | epoch ms UTC, index |
| | note | TEXT NULL | |
| | photo_path | TEXT NULL | |
| | created_at / updated_at | INTEGER | |
| meal_items | id | TEXT PK | UUID |
| | entry_id | TEXT | FK→meal_entries CASCADE, index |
| | name | TEXT | |
| | amount_label | TEXT NULL | Q2=B: g 필드 없음 |
| | estimated_kcal | INTEGER NULL | |
| | order_index | INTEGER | |
Migration: v1 시작. F-6이 sync/delete 상태를 v2 migration으로 추가 (본 설계는 예약 필드 없음 — no-implicit-decisions).

## 5. Module/Component Structure
| Module/File | 책임 | UOW |
|---|---|---|
| core/model/build.gradle.kts + 모델 | 도메인 모델·합산 | UOW-1 |
| core/data/build.gradle.kts | Room/KSP/드라이버 구성 | UOW-2 |
| core/data …/db/ (entity, dao, database, 빌더 expect/actual) | 스키마·접근 | UOW-2·3 |
| core/data …/repository/ | MealRepository 구현 | UOW-3 |
| core/data …/photo/ (expect/actual 경로) | PhotoStore | UOW-4 |
| settings.gradle.kts / libs.versions.toml | 등록 | UOW-1·2 |

## 6. Interaction Flow
upsert: ViewModel(F-3) → MealRepository → DAO(트랜잭션: entry+items 교체) / delete: DB 삭제 커밋 → PhotoStore.delete (실패 시 로그+고아 정리 대상)

## 7. Non-functional Design
- Consistency: entry+items 단일 트랜잭션, FK CASCADE. 삭제 규약 ADR-3
- Performance: 측정 근거 전 최적화 금지 — index 2개(meal_at, entry_id)만
- Security: 내부 저장소만 사용(권한 불요), 로그에 기록 내용 금지

## 8. Testing Approach
| UOW | 유형 | 내용 |
|---|---|---|
| 1 | 컴파일+단위 | 합산 함수(전부/일부/무 kcal) |
| 2 | 단위 | DB 오픈/버전 (android host + iOS sim) |
| 3 | 단위 | CRUD·날짜별 조회(TZ 경계 포함)·재실행 복원(동일 DB 재오픈) |
| 4 | 단위 | 저장→경로 기록→삭제 연계, 고아 정리 |

## 9. Open Items
- ⚠️ UNCERTAIN: Room KMP gradle/KSP 타깃 구성 세부 — UOW-2에서 공식 문서 검증 후 확정 (그 외 없음)
