# Requirements — data-foundation (F-2)

> **Request Anchor**: 로드맵 F-2 — Room KMP 로컬 DB(meal_entries/meal_items)와 이미지 파일 저장으로 오프라인 우선 저장 계층을 구축한다.

## Goal
네트워크와 무관하게 기록을 저장·복원할 수 있는 로컬 데이터 기반을 만든다. 스키마·DAO·이미지 파일 저장의 단일 소유자가 되어 F-3(record)/F-5(diary)가 소비만 하게 한다.
근거: `_source-plan.md` §데이터 및 백엔드 방향(로컬)·§MVP In Scope(저장·복원), `_roadmap.md` F-2 + ⚠ 해소 표.

## In-Scope
- **Room KMP**: meal_entries / meal_items 스키마·DAO (Q2·Q3에서 필드 확정)
- **이미지 파일 저장소**: 앱 내부 저장, DB에는 경로/메타만 (원본 Bitmap 저장 금지)
- **repository 인터페이스**: 기록 CRUD + 조회(날짜별) — 도메인 모델은 플랫폼 타입 무참조
- **모듈**: Q1에서 배치 확정 (ADR-0001 구조 내)
- **commonTest**: DAO/repository 단위 테스트 (kotlin.test + kotlinx-coroutines-test)
- **버전 카탈로그**: room/sqlite 신규 항목 (버전 구현 시점 검증)

## Out-of-Scope
- 동기화 상태/삭제 전파 필드 — **F-6 소유** (로드맵 ⚠ 해소: F-6이 migration으로 확장). soft-delete 미도입
- 업로드용 이미지 리사이즈·압축(F-6), 화면(F-3/F-5), 영양 데이터(F-4)

## Functional Requirements
- FR-1: 기록(entry: 사진 경로, 식사 시각, 메모)과 음식 항목(item: 이름, 양, 예상 칼로리)을 저장·수정·삭제할 수 있다
- FR-2: 앱 재실행 후 저장된 기록이 복원된다 (로컬 우선 — 네트워크 무관)
- FR-3: 날짜(일 단위)로 기록 목록을 조회할 수 있다 (F-5 캘린더/타임라인 전제)
- FR-4: 이미지 파일 저장·조회·삭제 API를 제공하고 entry 삭제 시 파일도 정리된다
- FR-5: 도메인 모델은 Compose/Android/iOS 타입을 참조하지 않는다
- FR-6: 총 예상 칼로리는 Q3 결정에 따른다

## Derived Requirements
- DR-1: Room KMP는 각 타깃 SQLite 드라이버 구성 필요 — 공식 문서 기준 구현 시 검증
- DR-2: 스키마 버전 1로 시작, F-6 확장은 migration으로 (버전 관리 규약 명시)

## Requirement Gaps
Q1(모듈 배치), Q2(양 표현), Q3(총 칼로리), P2 자동결정 4건 — questions 파일 참조.

## Initial Risk Assessment
| 리스크 | 수준 | 대응 |
|--------|------|------|
| Room KMP는 개발자 신규 학습 영역 | 중 | 공식 문서 기반 + commonTest로 동작 검증 (kmp-testing 규칙) |
| 스키마 초판이 F-3~F-6 전부의 기반 — 재작업 파급 큼 | 중 | Q2/Q3 확정 + GATE-3.5에서 스키마 검토 |
| iOS에서의 Room/SQLite 동작 차이 | 중 | ⚠️ UNCERTAIN: iosSimulatorArm64Test로 검증 |
| entry 삭제와 파일 정리의 원자성 | 저 | DB 먼저·파일 후 삭제 + 고아 파일 정리 규약 (기술 설계) |
