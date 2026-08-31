# Unit of Work — data-foundation (F-2)

> **Request Anchor**: 로드맵 F-2 — Room KMP 로컬 DB + 이미지 파일 저장, 오프라인 우선 (모듈: :core:model + :core:data, Q1=B).

## UOW 목록

### UOW-1: `:core:model` 모듈 + 도메인 모델
- Size: **S**
- 내용: MealEntry(id, mealAt, note, photoPath, createdAt/updatedAt), MealItem(id, entryId, name, amountLabel, estimatedKcal, orderIndex) — 순수 Kotlin, 플랫폼 무참조. 총칼로리는 합산 함수(Q3=A)
- 전제: 없음
- AC1: 모듈이 android/iOS 타깃 컴파일. AC2: 모델에 Compose/플랫폼 import 0건 (commonTest 컴파일로 보장)
- 예상 질문: 0

### UOW-2: `:core:data` 모듈 스캐폴드 + Room 구성
- Size: **M**
- 내용: 모듈 생성(:core:model 의존), room/sqlite 카탈로그 추가(버전 공식 문서 검증), android/iOS 드라이버 구성, DB 빌더 expect/actual
- 전제: UOW-1
- AC1: 양 타깃 컴파일 + DB 오픈 테스트 green. AC2: 신규 버전 전부 Maven 실존 확인 기록
- 예상 질문: 1 (드라이버 구성 세부 — 기술 설계에서 해소)

### UOW-3: 스키마·DAO·Repository + 테스트
- Size: **M**
- 내용: entity/DAO(schema v1), MealRepository 인터페이스+구현(CRUD, 날짜별 조회 Flow), commonTest(저장·복원·조회·수정·삭제)
- 전제: UOW-2
- AC1: FR-1~3 시나리오 테스트 green (Android host + iOS sim). AC2: 날짜별 조회가 로컬 타임존 기준 일 단위로 동작
- 예상 질문: 0

### UOW-4: 이미지 파일 저장소 + 삭제 연계
- Size: **M**
- 내용: PhotoStore(expect/actual 파일 경로 — files/photos/{entryId}.jpg), 저장·조회·삭제, entry 삭제 시 파일 정리(DB 먼저·파일 후) + 고아 파일 정리 유틸, 테스트
- 전제: UOW-3
- AC1: 저장→경로 DB 기록→삭제 시 파일 제거 테스트 green. AC2: 파일 삭제 실패가 DB 삭제를 롤백하지 않음(문서화된 규약)
- 예상 질문: 0

## 의존 그래프
UOW-1 → UOW-2 → UOW-3 → UOW-4 (직렬. 순환 없음)

## 응집도 검증
- 단일 도메인(로컬 저장). 유닛별 예상 질문 최대 1 → 적정
- 외부 연동 없음 (Room은 로컬 라이브러리)

## 사이즈 요약
| UOW | 1 | 2 | 3 | 4 |
|-----|---|---|---|---|
| Size | S | M | M | M |

M 3건 → STEP 6.5 기술 설계 필수 (GATE-3.5), GATE-5 대상.
