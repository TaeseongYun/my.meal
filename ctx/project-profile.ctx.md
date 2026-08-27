# Project Profile

- name: mymeal (제품 가칭: 도시락 일기 / 디자인 브랜드: Sikdorok 식도록)
- type: Kotlin Multiplatform + Compose Multiplatform — Android와 iOS만 출시. Desktop/Web은 MVP 범위 밖
- language: Kotlin (iOS 진입점만 SwiftUI)
- test-strategy: test-after
- project-state: greenfield 1인 개발 (design-system 파운데이션은 구현·머지 완료 — 예외적 기구현 자산)
- platform-guidance: /Users/yoontaeseong/work/aidlc-workflow/platforms/kmp/guidance.md (활성 코어 실경로. env `TEAM_AI_WORKFLOW_DIR`는 구사본을 가리키므로 신뢰하지 않는다)

## 개발자 컨텍스트

- Android/Kotlin/Jetpack Compose 경험 충분. KMP, iOS, Swift/Xcode, 백엔드는 처음.
- 1인 개발·운영 — 기술 선택 시 학습 비용과 운영 비용을 평가 기준에 포함하라.

## 승인된 기본 방향 (변경은 ADR + 사용자 승인 없이는 금지)

- commonMain 중심의 공유 UI(Compose Multiplatform)와 공유 비즈니스 로직
- Kotlin Coroutines + Flow
- 화면 상태는 androidx.lifecycle ViewModel KMP + StateFlow 기반 UDF 한 종류만 사용하라
- DI는 Koin 한 종류만 사용하라
- 네트워크는 Ktor + kotlinx.serialization
- 로컬 저장은 Room KMP 우선
- Android/iOS Host 앱은 얇게 유지하라
- 플랫폼 카메라, Photo Picker, 파일 시스템, MediaPipe 추론은 expect/actual 또는 명시적 플랫폼 Adapter 뒤에 두라
- 백엔드는 Supabase(Postgres/Auth/Storage/RLS)가 MVP 기본 후보 — **잠정 선택**이며 호환성·보안·운영비 검증 후 기술 설계 ADR에서 확정하라
- 별도 Spring/Ktor 서버는 MVP에서 필요성이 입증되기 전에는 만들지 마라
- 오프라인 우선: 로컬 저장 성공과 원격 동기화를 분리하라
- 로깅은 Kermit 계열 구조화 로깅
- 테스트 도구: kotlin.test, kotlinx-coroutines-test, Turbine, runComposeUiTest
- 라이브러리와 버전은 구현 시점에 공식 문서·실제 저장소로 검증하라 (추측 금지)

## 아키텍처 원칙

- Composable은 UiState 렌더링과 Action 전달만 담당하라
- Composable에서 Repository/API/플랫폼 API를 직접 호출하지 마라
- ViewModel → 선택적 UseCase → Repository → DataSource/Platform Adapter 방향만 허용하라
- Domain Model은 Compose/Android/iOS 타입을 참조하지 마라
- 일회성 이벤트는 State에 nullable로 넣지 말고 Channel 또는 SharedFlow로 분리하라
- Feature가 다른 Feature 내부 구현을 직접 참조하지 마라
- 공통화는 실제로 2개 이상의 Feature에서 필요할 때만 core로 승격하라
- UseCase/Repository/domain/data 폴더는 실제 책임이 생겼을 때만 만들라 — 위임만 하는 계층과 빈 폴더 금지

## 초기 Gradle 구조 원칙

- 첫 구현은 shared KMP 모듈 + androidApp + iosApp을 기본 후보로 하라
- shared/commonMain 내부는 feature-first 패키지: core, data, features/record, features/diary, features/settings
- 실제 분리 신호(AI 모델 바이너리, 독립 빌드·테스트 경계, 두 번째 앱/소비자)가 생기기 전에는 core/feature별 Gradle 멀티모듈을 만들지 마라
- **기확정 예외**: `:core:designsystem` Gradle 모듈은 사용자 승인(2026-08-27 GATE-2.7)으로 이미 존재한다. 로드맵에서 shared 내부 패키지로의 통합 여부를 재확인하라
- 기술 설계에 향후 멀티모듈 전환 조건과 목표 의존성 그래프를 별도로 기록하라

## 참고 프로젝트

- DroidKaigi/conference-app-2026 (main 112f35c): source set 분리, 얇은 플랫폼 진입점, feature 간 직접 의존 금지, app layer 조립 원칙**만** 참고하라
- Metro, Soil, 자체 FIR Compiler Plugin, 자체 KSP Processor, 3단 Preview 모듈, Desktop/Web 타깃은 복사하지 마라

## Forbidden Decisions

- 검증되지 않은 의존성 또는 존재하지 않는 버전을 추측해서 추가하지 마라
- 서로 다른 Navigation, DI, 상태관리 프레임워크를 혼용하지 마라
- API Key, service-role key, 토큰, 비밀번호를 앱 또는 저장소에 포함하지 마라
- 사진 한 장으로 정확한 칼로리를 보장한다고 표현하지 마라
- 의료 진단 또는 치료 효과를 제공한다고 표현하지 마라
- GATE 승인 전 프로덕션 코드와 프로젝트 스캐폴딩을 생성하지 마라
