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
