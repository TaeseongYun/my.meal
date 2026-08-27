# CTX Index

## Project
- name: mymeal
- type: Kotlin Multiplatform (Compose Multiplatform, Android + iOS)
- language: Kotlin (SwiftUI entry point for iOS)

## Key Modules
- `:shared` — 공유 로직 + UI (iOS framework "Shared")
- `:androidApp` — Android 엔트리
- `:core:designsystem` — 디자인 토큰/테마 (SikdorokTheme)

## Constraints
- UI 코드에서 색·치수·TextStyle 리터럴을 하드코딩하지 말고 SikdorokTheme 접근자를 사용하라.
- 디자인 토큰 값을 바꿀 때는 aidlc-docs/features/design-system/design-manifest.md를 함께 갱신하라.
