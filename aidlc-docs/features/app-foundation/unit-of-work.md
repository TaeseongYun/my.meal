# Unit of Work — app-foundation (F-1)

> **Request Anchor**: 로드맵 F-1 — 앱 골격(내비·DI·UDF·로깅)과 조립 루트 (Nav Compose, iOS 17.0 확정).

## UOW 목록

### UOW-1: 의존성·플랫폼 기준선
- Size: **M**
- 내용: 카탈로그에 navigation-compose(KMP)/koin/kermit 추가(각 버전 공식 문서·Maven 실존 검증 기록), iOS 배포 타깃 18.2→17.0(pbxproj), shared 의존 연결
- AC1: 전 모듈 컴파일(android+iOS) AC2: 버전 검증 근거가 audit에 기록됨
### UOW-2: Koin DI 부트스트랩
- Size: **M**
- 내용: shared 루트 Koin 모듈 + 피처 module 기여 규약, Android(Application)/iOS(MainViewController) 초기화 지점
- AC1: 양 플랫폼 앱 기동 시 Koin start 1회 AC2: :feature:login이 자기 Koin module 파일만 노출
### UOW-3: 내비게이션 골격 + login 편입
- Size: **M**
- 내용: NavHost/라우트 규약(type-safe), :feature:login에 destination 등록 함수, App.kt 직접 호출 제거
- AC1: 앱 시작 시 login 표시(FR-1) AC2: feature 간 직접 참조 0 (등록 함수 경유)
### UOW-4: UDF 골격 + 로깅
- Size: **M**
- 내용: LoginViewModel 골격(UiState/Action, 이벤트 Channel), Kermit 초기화, 개인정보 로깅 금지 규약 CTX 반영
- AC1: LoginScreen이 ViewModel 상태 렌더+Action 전달만(FR-3/4) AC2: commonTest에서 ViewModel 상태 전이 테스트 green

## 의존 그래프
UOW-1 → UOW-2 → UOW-3 → UOW-4 (직렬. UOW-3·4는 부분 병행 가능하나 1인 개발 기준 직렬 권장)

## 응집도: 단일 도메인(앱 골격), 유닛별 질문 0~1. 사이즈: M×4 → 기술 설계 필수 (GATE-3.5)
