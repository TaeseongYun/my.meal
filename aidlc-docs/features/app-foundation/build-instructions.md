# Build Instructions — app-foundation (F-1)

## UOW 순서 (직렬)
UOW-1(의존성·pbxproj 17.0) → UOW-2(Koin 부트스트랩) → UOW-3(NavHost 조립+login/home destination) → UOW-4(LoginViewModel UDF+Kermit)

## 검증 명령 (신규 AGP KMP 플러그인 태스크 기준)
```bash
./gradlew assemble                                                              # UOW-1 전 모듈 컴파일
./gradlew :shared:compileKotlinIosSimulatorArm64                                # iOS 측 컴파일
./gradlew :shared:testAndroidHostTest :feature:login:testAndroidHostTest        # Koin 구성 검증 + ViewModel 테스트
./gradlew :shared:iosSimulatorArm64Test :feature:login:iosSimulatorArm64Test    # iOS 테스트
./gradlew :androidApp:assembleDebug                                             # 전체 회귀 (앱 모듈은 assembleDebug 존재)
```

## 주의
- nav·koin·kermit·serialization 버전은 추가 시점에 공식 문서+Maven 실존 검증 후 카탈로그 기록 (Forbidden: 버전 추측)
- iOS 배포 타깃 17.0은 pbxproj 직접 수정 (ADR-3) — 라이브러리 최소 요구 충돌 시 즉시 보고
- 이 피처가 Nav 골격·Koin 루트 소유 (로드맵 single-owner) — F-3/F-5 착수는 이 피처 main 머지 후
- `shared/App.kt` 임시 remember 전환 제거는 UOW-3에서 수행 (홈 UI-slice 이연 항목 흡수, 2026-09-03 개정)
