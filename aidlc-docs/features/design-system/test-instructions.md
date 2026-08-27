# Test Instructions — design-system

## 테스트 실행 명령

```bash
./gradlew :core:designsystem:testAndroidHostTest     # Android 호스트 (shared와 동일 구성)
./gradlew :core:designsystem:iosSimulatorArm64Test   # iOS 시뮬레이터
```

## 시나리오 (AC 매핑)

| # | 대상 | 시나리오 | AC |
|---|------|----------|-----|
| T1 | SikdorokColors | 13개 토큰의 ARGB 값 == design-manifest.md 명시값 | UOW-2 AC1 |
| T2 | SikdorokColors | 알파 내장 토큰 검증: line == #9D9792 alpha 0.10, textDim == #3C3025 alpha 0.80 | UOW-2 AC1 (엣지) |
| T3 | SikdorokSpacing | 8종 == 4/8/12/16/20/24/32/40.dp | UOW-2 AC2 |
| T4 | SikdorokTypography(FontFamily.Default) | 7종 fontSize/lineHeight가 기술설계 §3 표와 일치, letterSpacing 전부 0, weight 400 | UOW-3 AC2 |
| T5 | sikdorokColorScheme() | primary/background/surface/onBackground/onSurface/error/outline이 매핑 표와 일치 | UOW-4 AC2 |
| T6 | sikdorokM3Typography() | displayLarge==h1 … labelSmall==detail 매핑 일치 | UOW-4 AC2 |

빌드 성격 AC(UOW-1 AC1·2, UOW-3 AC1, UOW-4 AC3)는 build-instructions.md의 명령으로 검증.

## 엣지 케이스

- T2의 알파 인코딩: Color(0x1A9D9792) 식 8자리 hex 오기입이 가장 흔한 실수 — alpha 채널 float 비교(오차 1/255)로 검증
- lineHeight 소수값(22.4/20.1sp): TextUnit 비교는 `.value` float 비교로

## Quality Gate

- 위 테스트 전건 green + `:androidApp:assembleDebug` + `:shared:compileKotlinIosSimulatorArm64` 성공
- 실기기/시뮬레이터 육안 확인 1회: App 데모 텍스트가 이서윤체로 렌더링되는지 (⚠️ UNCERTAIN 해소 절차)

## Runtime Verification (ROLE 2 기록, 2026-08-27)

- 실행 명령: `./gradlew :core:designsystem:testAndroidHostTest` / `:core:designsystem:iosSimulatorArm64Test`
- 결과: SikdorokTokensTest 6 tests, 0 failures (Android host) / iOS 시뮬레이터 테스트 BUILD SUCCESSFUL
- 빌드 검증: `:androidApp:assembleDebug` + `:shared:compileKotlinIosSimulatorArm64` 성공 (SikdorokTheme 래핑 상태)
- Environment Constraints: 에뮬레이터/시뮬레이터 GUI 육안 확인(이서윤체 렌더링 — UNCERTAIN 항목)은 이 환경에서 미수행. 사용자가 앱 실행으로 확인 필요
- 태스크명 정정: build-instructions.md의 `:core:designsystem:assembleDebug`는 신규 AGP KMP 플러그인에 없음 → `:core:designsystem:assemble` 사용 (문서 갱신 대상)

## 육안 확인 결과 (2026-08-27, 쇼케이스)

- 구성: `StyleGuideScreen`(:core:designsystem) — 매니페스트 "Style Guide 화면 구성" 그대로 (아이콘 제외). App 루트가 쇼케이스를 표시 (F-1에서 교체 예정)
- 실행: Android 에뮬레이터 Medium_Phone, `adb install` 후 스크린캡처 3장
- 결과: Font/Text Style/Color(Bg·Txt·Alert·Accent)/Spacing 전 섹션 정상. 이서윤체 국·영문 적용, lineHeight 잘림 없음 → Quality Gate의 육안 확인 항목 종결
