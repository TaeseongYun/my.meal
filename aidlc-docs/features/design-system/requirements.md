# Requirements — design-system

> **Request Anchor**: 피그마 스타일 가이드(node 836:33127) 매니페스트를 기반으로 KMP(Compose Multiplatform) 디자인 시스템 파운데이션을 `:core:designsystem` 모듈로 구축한다. (Q5 답변으로 배치 확정, 2026-08-27)

## Goal

피그마 "Style Guide"에 정의된 디자인 토큰(컬러, 타이포그래피, 스페이싱, 폰트)을 코드로 옮겨,
이후 모든 화면 구현이 하드코딩 없이 참조하는 단일 디자인 파운데이션을 만든다.
Android/iOS 양 플랫폼에서 동일하게 렌더링되어야 한다.

배경: 코드베이스는 KMP 템플릿 초기 상태로 기존 테마/토큰 자산이 전무하다.
토큰 값의 근거는 `design-manifest.md` (Figma API 추출 스냅샷)이다.

## In-Scope

- **컬러 토큰**: 팔레트 13종 (Bg 4 + Line, Text 5, Alert 2, Accent 1) — semantic 이름 + raw 팔레트 이름 이중 구조
- **타이포그래피 토큰**: H1~H4, Body1, Body2, Detail 7단계 (`TextStyle` 정의)
- **스페이싱 토큰**: 4/8/12/16/20/24/32/40 (Dp)
- **폰트**: 이서윤체(LeeSeoyun) 국·영문 단일 서체 적용 구조 (FontFamily 토큰)
- **테마 진입점**: 앱 루트에서 감싸는 Theme 컴포저블 + 토큰 접근 API
- **모듈 구성**: 신규 `:core:designsystem` KMP 모듈 (Q5 확정) — settings.gradle 등록, `:shared`가 의존
- **적용 지점**: `shared/commonMain`의 `App.kt`가 새 테마로 감싸지는 것까지

## Out-of-Scope

- 컴포넌트 라이브러리(버튼, 카드, 입력 필드 등) — 스타일 가이드 노드에 없음. 후속 기능
- 아이콘 세트 — 디자인 원본에 "확정 x 예시" 명기 (Q2에서 최종 결정)
- 다크 모드 팔레트 — 디자인에 라이트만 존재 (Q4에서 최종 결정)
- 화면(스크린) 구현 일체
- 디자인-코드 자동 동기화 파이프라인 (수동 스냅샷 기반으로 시작)

## Functional Requirements

- FR-1: 매니페스트의 13개 컬러가 semantic 이름(Bg1~4, Line, Text1~4, TextDim, AlertRed, AlertGreen, Accent)으로 노출된다. 값은 `design-manifest.md`와 1:1 일치.
- FR-2: 알파 포함 색(Line 10%, Gray4 80%)은 알파가 토큰에 내장된다.
- FR-3: 타이포 7단계가 `TextStyle`로 정의되고 fontSize/lineHeight가 매니페스트 실측값을 따른다.
- FR-4: 스페이싱 8단계가 `Dp` 상수로 노출된다.
- FR-5: Theme 컴포저블로 감싸면 하위 컴포저블에서 `SikdorokTheme.colors.*` / `.typography.*` / `.spacing.*` 형태로 접근 가능하다.
- FR-6: FontFamily는 단일 지점에서 주입된다. 이서윤체 파일은 사용자가 제공(Q1=A, 수령 대기) — 수령 전까지 시스템 폰트로 동작, 수령 시 토큰 한 곳만 변경.
- FR-7: Android/iOS 공통 코드(commonMain)만으로 구현된다. 플랫폼 분기 없음.

## Derived Requirements

- DR-1: `:core:designsystem` 모듈에 compose 플러그인 + `compose-components-resources` 의존성 구성 필요 (폰트 리소스 사용 전제. 버전 카탈로그에는 이미 존재).
- DR-2: 토큰 값 변경 시 단일 파일 수정으로 전파되는 구조 (하드코딩 금지 규칙의 전제).

## Requirement Gaps

~~Q1~Q5~~ 전부 답변 완료 (2026-08-27): 폰트=사용자 제공(A), 아이콘=제외(B), M3=하이브리드(C), 다크모드=라이트 전용(A), 배치=:core:designsystem 신설. 잔여 의존성: 이서윤체 TTF/OTF 파일 수령.

## Initial Risk Assessment

| 리스크 | 수준 | 대응 |
|--------|------|------|
| 이서윤체 폰트 파일 미확보/라이선스 미확인 | 중 | FR-6 구조로 구현 비차단. 파일·라이선스 확인은 사용자 액션 (Q1) |
| 이서윤체 실측 lineHeight가 특이(대부분 1.0 배율) — 실기기 렌더링이 디자인과 다를 수 있음 | 중 | ⚠️ UNCERTAIN: 매니페스트 실측값으로 시작, 실기기 확인 후 조정 |
| Material3 1.11.0-alpha07 (알파 버전) API 변동 | 저 | 커스텀 토큰 레이어가 M3 의존을 흡수 (Q3 하이브리드 시) |
| 아이콘 미확정 상태에서 선구현 시 재작업 | 저 | 기본 제외 (Q2) |
| 기존 시스템 영향 | 없음 | 템플릿 상태, 데모 코드만 존재 (RE 문서 확인) |
