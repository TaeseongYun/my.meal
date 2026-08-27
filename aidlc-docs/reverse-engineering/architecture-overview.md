# Architecture Overview — mymeal

코드베이스는 JetBrains KMP 템플릿 초기 상태 (실제 도메인 코드 없음).

- 패턴: Kotlin Multiplatform + Compose Multiplatform (공유 UI)
- 모듈: `:androidApp` (Android 엔트리), `:shared` (공유 로직+UI, iOS framework "Shared"), `iosApp/` (SwiftUI 엔트리)
- 타깃: Android (minSdk 24, compileSdk 36), iosArm64, iosSimulatorArm64
- 스택: Kotlin 2.4.10, Compose Multiplatform 1.11.1, Material3 1.11.0-alpha07, AGP 9.0.1
- 리소스: `compose-components-resources` 라이브러리 등록됨(버전 카탈로그) — 폰트/이미지 commonMain 리소스 사용 가능
- 데이터 흐름/외부 연동/DB: 없음
- 진입점: `androidApp MainActivity` → `shared App.kt` / `iosApp` → `MainViewController.kt` → `App.kt`
