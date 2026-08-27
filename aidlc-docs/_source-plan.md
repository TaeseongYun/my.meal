# 도시락 일기 — Prepared Requirement (소스 기획 문서)

> 수신: 2026-08-27, 사용자 제공 "도시락 일기 KMP 프로젝트 — Claude/AIDLC 실행 프롬프트" §3 본문.
> 기준 워크플로: aidlc-workflow main 7f5fe6f / 참고 아키텍처: DroidKaigi/conference-app-2026 main 112f35c

## 제품 목표
사용자가 하루에 먹은 도시락이나 식사를 사진으로 기록하고, 날짜별로 다시 볼 수 있는 개인 식사 일기 앱. AI는 사진에서 음식 후보를 제안하고 예상 칼로리 계산을 돕지만 결과를 확정하지 않는다. 사용자가 음식명·섭취량을 확인·수정 후 저장한다.

## 대상 사용자
- 도시락/식사를 꾸준히 기록하고 싶은 개인
- 전문 영양 관리보다 부담 없는 일기 경험 선호
- 초기 출시는 한국어 사용자 우선

## 대상 플랫폼
Android, iOS (Desktop/Web은 MVP 제외)

## 핵심 사용자 흐름
1. 오늘 기록하기 → 2. 촬영 또는 사진 보관함 선택 → 3. 온디바이스 음식 후보 분석 → 4. 미지원/실패 시 수동 입력 즉시 전환 → 5. 음식명·양(g) 확인·수정 → 6. 공식 영양 데이터+섭취량으로 예상 칼로리 계산 → 7. 사진·항목·칼로리·메모·시각 로컬 우선 저장 → 8. 캘린더/타임라인·상세에서 조회 → 9. (선택) 계정 연결 후 Supabase 동기화

## MVP In Scope
- 사진 촬영/선택, 기록당 사진 1장, 식사 시각·짧은 메모
- 음식 항목 추가·수정·삭제, 음식별 섭취량·예상 칼로리, 총 예상 칼로리
- 날짜별 캘린더 또는 타임라인, 기록 상세·수정·삭제
- 로컬 우선 저장·앱 재실행 후 복원
- 음식 AI 후보 제안 + 수동 입력 fallback
- 한국 식품영양성분 데이터 연동 방안
- 계정·원격 백업/동기화의 단계적 도입 설계
- 사진·계정 데이터 삭제 경로
- Android/iOS 기본 접근성·한국어 리소스

## Out of Scope
칼로리/무게의 의학적 정확성 보장, 진단·치료·처방·의료 조언, 실시간 영양 코칭, 소셜 기능, 결제·구독, 관리자 웹, 가족/팀 공유, Desktop/Web, 자체 ML 학습 파이프라인 완성

## AI 및 칼로리 정책
- AI 결과는 `후보`, 칼로리는 `예상값`으로 표시. 사진 한 장에서 무게 확정 금지
- 1차: 음식명 후보 + 대략적 양 제안, 사용자 확인
- 기기 제한적인 플랫폼 Foundation Model을 필수 경로로 쓰지 않음
- 기본 경로: 동일 모델 자산의 MediaPipe/LiteRT/Core ML 호환 방식 검토
- 추론 런타임 차이는 공통 인터페이스 + 플랫폼 Adapter 뒤에
- 모델 부재/실패/타임아웃/낮은 confidence 시 수동 입력 항상 가능
- 영양값은 식품안전나라 K-FIND API 또는 다운로드 DB 우선 검토
- 외부 API Key 필요 호출은 앱에 비밀 키 미포함 구조로

## 데이터 및 백엔드 방향
- 로컬 DB 우선 저장. DB에 원본 Bitmap 금지(파일 경로/메타데이터만)
- 업로드 이미지는 리사이즈·압축, 업로드 정책과 원본 보존 정책 분리
- Supabase Postgres/Auth/Storage/RLS 기본 후보
- meal_entries, meal_items, 동기화 상태·삭제 상태 분리 설계
- service-role key 모바일 포함 금지, RLS로 본인만 접근
- 충돌 정책·삭제 전파·재시도·중복 업로드 방지는 기술 설계에서 명시
- 별도 서버는 비밀 프록시/서버 권한 작업이 실제 필요할 때만 Edge Function/서버리스로

## 기술 방향
KMP+CMP, Coroutines/Flow, ViewModel KMP+StateFlow UDF, Koin, Ktor+kotlinx.serialization, Room KMP, CMP Resources, Kermit 계열 로깅. 테스트: kotlin.test, kotlinx-coroutines-test, Turbine, runComposeUiTest. 버전은 구현 시점 검증.

## 구조 설계 원칙
- 단일 shared KMP 모듈 + Android/iOS Host 우선, commonMain feature-first 패키지
- 기능 후보: 기록/촬영, 음식 분석·영양 계산, 일기 탐색, 계정·동기화, 설정·개인정보
- roadmap에서 공유 리소스·소유권·선행 관계 분석, 필요시 foundation 추출
- Feature 간 내부 구현 직접 의존 금지, 플랫폼 기능은 expect/actual·Adapter 격리
- 빈 Clean Architecture 계층·위임 UseCase 금지
- Gradle 멀티모듈은 실제 신호(독립 소비자, 빌드/테스트 경계, AI 바이너리 격리) 확인 시에만
- 목표 멀티모듈 구조·전환 조건은 technical-design ADR로

## 디자인 입력
- Figma: https://www.figma.com/design/T3hswISeWAzU2nD6hESZJL/%F0%9F%92%A1-ideation?node-id=721-11215
- 접근 가능 시 kmp-figma-to-code/kmp-design-system 규칙으로 화면·토큰 분석, 불가 시 Export 요청 (추측 구현 금지)
- MaterialTheme·디자인 토큰 사용, Feature Composable에 색상/문자열/간격 하드코딩 금지

## 품질 및 보안 기준
- kmp-architecture/module-structure/state-management/navigation-platform/design-system/security/testing/accessibility/i18n/observability 규칙 적용
- 플랫폼 파일·API 사실은 코드/공식 문서로 검증
- 사진·음식 기록·계정 ID 등 개인정보 로그 금지
- TLS 우회·전체 인증서 허용·평문 토큰 저장 금지
- 외부 입력·Deep Link 파라미터 검증
- 삭제·계정 탈퇴 시 로컬/원격 사진·레코드 처리 범위 명확화
- 성능 최적화는 측정 근거 있을 때만

## 성공 기준
- 오프라인에서 기록 CRUD 가능 / AI 불능 시에도 기록 완료 가능
- Android/iOS 동일 핵심 플로우 / 공통 규칙·상태 로직 commonMain 테스트 가능
- 사진·비밀 키 비노출 / 신규 기능이 기존 Feature 내부 구현 미참조로 추가 가능
- 1인 개발자가 이해·운영 가능한 복잡도

## 명시적으로 열어둔 결정 (AI 자동 확정 금지)
1. 첫 출시 로그인 필수 vs 로컬 익명 후 계정 연결
2. 사진 원본 백업 vs 압축본만
3. 동기화 충돌 정책·삭제 전파 정책
4. 음식 후보 최소 confidence와 UI 표현
5. 초기 AI 범위: 음식명 분류만 vs 영역 분리 포함
6. 영양 데이터: 앱 번들 vs 로컬 캐시 vs 서버 조회
7. Navigation 라이브러리 안정 버전 선택
8. Supabase Kotlin SDK vs Ktor 기반 API 경계
9. 최소 지원 Android/iOS 버전
10. Analytics/Crash Reporting 도입·동의 정책

## 참고 아키텍처 주의
DroidKaigi 2026에서 공통 UI·얇은 진입점·core/feature/app 의존 방향·app 조립만 참고. Metro, Soil, 자체 FIR/KSP, 4플랫폼, 복잡한 Preview 모듈은 미적용.
