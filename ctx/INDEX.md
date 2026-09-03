# CTX Index

## Project
- name: mymeal (제품 가칭: 도시락 일기 / Sikdorok)
- type: Kotlin Multiplatform (Compose Multiplatform, Android + iOS만 — Desktop/Web 제외)
- language: Kotlin (SwiftUI entry point for iOS)
- 기술 스택/아키텍처 규칙의 source of truth: ctx/project-profile.ctx.md

## Key Modules
- `:shared` — 공유 로직 + UI (iOS framework "Shared")
- `:androidApp` — Android 엔트리
- `:core:designsystem` — 디자인 토큰/테마 (SikdorokTheme)
- `:core:model` — 도메인 모델(순수 Kotlin)
- `:core:data` — Room KMP 로컬 저장+사진 파일(스키마 v1)

## Constraints
- UI 코드에서 색·치수·TextStyle 리터럴을 하드코딩하지 말고 SikdorokTheme 접근자를 사용하라.
- 디자인 토큰 값을 바꿀 때는 aidlc-docs/features/design-system/design-manifest.md를 함께 갱신하라.
- 기술 스택 변경(상태관리/DI/네트워크/DB/백엔드)은 ADR과 사용자 승인 없이 처리하지 마라.
- GATE 승인 전 프로덕션 코드와 스캐폴딩을 생성하지 마라.
- KMP 모듈에서 소스셋 dependsOn을 수동 추가할 때 applyDefaultHierarchyTemplate()를 명시하라.
- Room 스키마를 바꿀 때 version 증가와 core/data/schemas/ export를 함께 커밋하라.
