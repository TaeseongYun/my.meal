# Application Design — Components (design-system)

> **Request Anchor**: 피그마 스타일 가이드(node 836:33127) 매니페스트를 기반으로 KMP 디자인 시스템 파운데이션을 `:core:designsystem` 모듈로 구축한다.

## 신규 모듈: `:core:designsystem`

KMP 라이브러리 모듈 (targets: android, iosArm64, iosSimulatorArm64 — `:shared`와 동일 구성).
패키지: `com.devts.mymeal.core.designsystem`

| 컴포넌트 | 책임 | 공개 여부 |
|----------|------|-----------|
| `SikdorokColors` | 컬러 토큰 13종 보유 (immutable data holder). semantic 이름 = 공개 API, raw 팔레트(Gray/Beige) = 내부 값 | public |
| `SikdorokTypography` | TextStyle 7종 (H1~H4, Body1, Body2, Detail). FontFamily는 생성 시 주입 | public |
| `SikdorokSpacing` | Dp 스페이싱 8종 | public |
| `SikdorokTheme` (컴포저블) | CompositionLocal 제공 + MaterialTheme 브리지(Q3=C). 하위에서 `SikdorokTheme.colors/typography/spacing` 접근자 제공 | public — **유일한 진입점** |
| 폰트 리소스 (LeeSeoyun) | composeResources font. 파일 수령 전까지 미포함, FontFamily 기본값 = 시스템 폰트 | internal |

## 변경되는 기존 컴포넌트

| 컴포넌트 | 변경 |
|----------|------|
| `settings.gradle.kts` | `include(":core:designsystem")` 추가 |
| `:shared` build.gradle.kts | `implementation(project(":core:designsystem"))` 추가 |
| `shared App.kt` | 루트를 `SikdorokTheme { }`로 래핑 (데모 UI 유지) |

## 설계 원칙

- 토큰 값의 유일한 출처는 `design-manifest.md` — 코드에 값 하드코딩은 이 모듈 내부에만 존재
- M3 브리지는 단방향 (Sikdorok 토큰 → ColorScheme/Typography 매핑). 앱 코드는 M3 시맨틱이 아닌 Sikdorok 접근자를 사용
- 플랫폼 분기 없음 (commonMain 단일 소스셋)
