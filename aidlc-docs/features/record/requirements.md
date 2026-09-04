# Requirements — record (F-3)

> **Request Anchor**: 로드맵 F-3 — 기록 생성 플로우: 촬영/사진 선택(expect/actual), 음식 항목·섭취량·메모·시각 입력, 로컬 저장. 사용자 확정(2026-09-03): 전체 범위.

## Goal
홈 FAB에서 진입해 도시락 기록(사진+항목+메모+시각)을 생성하고 로컬에 저장한다.

## In-Scope
- :feature:record 모듈 — RecordRoute/recordDestination 등록 (F-1 규약)
- 홈 FAB → 기록 생성 화면 진입, 저장 후 홈 복귀
- 사진 입력: 촬영/앨범 선택 (expect/actual 플랫폼 어댑터)
- 입력 필드: 음식 항목(이름·섭취량 자유 라벨), 메모, 시각 — 디자인 생성하기 프레임 기준
- 저장: PhotoStore.save + MealRepository.upsert (오프라인 우선)
- RecordViewModel UDF (F-1 골격)

## Out-of-Scope
- AI 음식 인식·칼로리 자동 계산 (F-4), 원격 동기화 (F-6)
- 기록 수정/삭제 (F-5 잔여 — 생성만)

## Functional Requirements
- FR-1: 홈 FAB 탭 → 기록 생성 화면으로 정식 내비게이션 전환
- FR-2: 촬영 또는 앨범에서 사진 1장을 선택해 미리보기로 표시한다
- FR-3: 음식 항목(이름 필수, 섭취량 자유 라벨 선택), 메모, 시각을 입력할 수 있다
- FR-4: 저장 시 사진은 PhotoStore, 기록은 MealRepository.upsert로 저장되고 홈으로 복귀한다
- FR-5: 저장된 기록은 홈 캘린더·캐러셀에 반영된다 (F-5 연동)
- FR-6: :feature:record는 :core:data를 소비만 한다 (스키마·DAO 수정 금지)
- FR-7: 권한 거부(카메라/앨범) 시 대체 경로 또는 안내를 제공한다

## Requirement Gaps → requirement-verification-questions.md
- 끼니 구분 방식 (F-5와 공유 쟁점 — 생성 시 끼니 선택 여부)
- 사진 입력 플랫폼 구현 방식 (직접 expect/actual vs 라이브러리)
- 생성 화면 필드 구성 확정 (Figma 생성하기 프레임 — API 429 시 뷰어 판독 우회)

## Initial Risk Assessment
- ⚠️ 카메라/포토피커는 플랫폼 API 연동 (P0급) — 권한·OS 버전 차 검증 필수
- Figma REST 429 지속 — 디자인 판독은 브라우저 뷰어 렌더 우회 (전례 있음)
