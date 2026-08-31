# Build Instructions — data-foundation (F-2)

## UOW 순서 (직렬)
UOW-1(:core:model) → UOW-2(:core:data+Room) → UOW-3(스키마·DAO·repo) → UOW-4(PhotoStore)

## 검증 명령 (신규 AGP KMP 플러그인 태스크 기준)
```bash
./gradlew :core:model:assemble :core:model:compileKotlinIosSimulatorArm64      # UOW-1
./gradlew :core:data:assemble  :core:data:compileKotlinIosSimulatorArm64       # UOW-2~4
./gradlew :core:data:jvmTest :core:data:iosSimulatorArm64Test                  # 테스트 (DB·파일: jvm+iOS. android 런타임은 앱 빌드로 검증)
./gradlew :androidApp:assembleDebug                                            # 전체 회귀
```

## 주의
- room/sqlite/ksp 버전은 추가 시점에 공식 문서+Maven 실존 검증 후 카탈로그 기록 (Forbidden: 버전 추측)
- F-1과 병행 시 libs.versions.toml/settings.gradle.kts 커밋 분리 (로드맵 병렬 안전 규약)
- sync/soft-delete 필드 추가 금지 — F-6 migration 소관
