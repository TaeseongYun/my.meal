Tier: T0 | T1 | T2  <!-- 아래 분류 결과로 한 줄만 남길 것 -->

## 무엇을 왜

<!-- 결정을 내린 문장은 별도 줄로. 리뷰어가 동의/보류를 표시한다. -->

## 티어 분류

```
git diff --name-only origin/main...HEAD \
 | grep -qE '^(gradle/libs\.versions\.toml|gradle\.properties|core/data/schemas/|supabase/migrations/|core/auth/|.*/(androidMain|iosMain)/|.*AndroidManifest\.xml)' \
 && echo T2 || echo "T1-or-T0"
```

- [ ] **T0** — docs/ctx/테스트만. `/code-review low`
- [ ] **T1** — feature commonMain·designsystem·model·모듈 배선. `/code-review medium --fix` + `/ctx-reviewer`, 화면 스크린샷 1장 vs Figma 노드
- [ ] **T2** — 버전/스키마/마이그레이션/`core/auth`/플랫폼 소스셋/Manifest. `/code-review high --fix` + `/security-review`, **골든패스 필수**

## 크기

- [ ] 추가 ≤600줄 / 파일 ≤20개 (`git diff --shortstat origin/main...HEAD`)
- [ ] `libs.versions.toml` 버전 변경이 있다면 이 PR은 그것만 다룬다

## 골든패스 (T2 필수 / T1은 케이던스 슬롯으로 이연 가능)

```
ADB=~/Library/Android/sdk/platform-tools/adb   # homebrew adb(34.x)는 기기 offline 유발
$ADB kill-server && $ADB start-server
~/Library/Android/sdk/emulator/emulator -avd Medium_Phone -dns-server 8.8.8.8 -no-snapshot-load &
$ADB wait-for-device && ./gradlew :androidApp:installDebug
$ADB shell am start -n com.tsdev.sikdorok/com.devts.mymeal.MainActivity
# logcat에 'isDataStall'이 보이면: $ADB shell "svc wifi disable && svc wifi enable"
```

- [ ] 콜드 스타트 → 크래시 없음
- [ ] 이메일 로그인 → 홈 (**실제 서버 왕복** — Supabase auth 로그에 요청이 남는지)
- [ ] 기록 저장 → 홈 반영
- [ ] 강제 종료 → 재실행 시 세션 복원
- [ ] (해당 시) 되돌리기 어려운 결정 확인 — Room version↔schemas export 동반, migration 적용 시점, ADR 유무, diff에 키·토큰 리터럴 0건

## 남은 항목

<!-- 코드 밖이라 어떤 리뷰도 못 보는 것: Supabase 대시보드 설정, 카카오 OIDC, 디자이너 확인 대기 등 -->
