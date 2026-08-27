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

## Constraints
- UI 코드에서 색·치수·TextStyle 리터럴을 하드코딩하지 말고 SikdorokTheme 접근자를 사용하라.
- 디자인 토큰 값을 바꿀 때는 aidlc-docs/features/design-system/design-manifest.md를 함께 갱신하라.
- 기술 스택 변경(상태관리/DI/네트워크/DB/백엔드)은 ADR과 사용자 승인 없이 처리하지 마라.
- GATE 승인 전 프로덕션 코드와 스캐폴딩을 생성하지 마라.
