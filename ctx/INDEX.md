# CTX Index

## Project
- name: mymeal (제품 가칭: 도시락 일기 / Sikdorok)
- type: Kotlin Multiplatform (Compose Multiplatform, Android + iOS만 — Desktop/Web 제외)
- language: Kotlin (SwiftUI entry point for iOS)
- 기술 스택/아키텍처 규칙의 source of truth: ctx/project-profile.ctx.md

## Key Modules
- `:shared` — 공유 로직 + UI 조립 루트: NavHost·Koin initKoin/appModules (iOS framework "Shared")
- `:androidApp` — Android 엔트리
- `:core:designsystem` — 디자인 토큰/테마 (SikdorokTheme)
- `:core:model` — 도메인 모델(순수 Kotlin)
- `:core:data` — Room KMP 로컬 저장+사진 파일(스키마 v1)
- `:feature:login` / `:feature:home` — 화면 피처 (destination 등록 함수 + Koin module만 노출)

## Constraints
- UI 코드에서 색·치수·TextStyle 리터럴을 하드코딩하지 말고 SikdorokTheme 접근자를 사용하라.
- 디자인 토큰 값을 바꿀 때는 aidlc-docs/features/design-system/design-manifest.md를 함께 갱신하라.
- 기술 스택 변경(상태관리/DI/네트워크/DB/백엔드)은 ADR과 사용자 승인 없이 처리하지 마라.
- GATE 승인 전 프로덕션 코드와 스캐폴딩을 생성하지 마라.
- KMP 모듈에서 소스셋 dependsOn을 수동 추가할 때 applyDefaultHierarchyTemplate()를 명시하라.
- Room 스키마를 바꿀 때 version 증가와 core/data/schemas/ export를 함께 커밋하라.
- 로그(Kermit, 태그 "Sikdorok")에 사진 경로·기록 내용·계정 ID 등 개인정보를 남기지 마라.
- 피처 모듈은 자기 destination 등록 함수(`NavGraphBuilder.<slug>Destination`)와 Koin module만 노출하고, 피처 간 직접 의존을 만들지 마라. 신규 피처는 shared의 appModules()·NavHost에만 등록을 추가하라.
