# ADR-0002: 이메일 확인(Confirm email) 비활성 — 가입 즉시 세션 발급

- Status: accepted (2026-09-08, 사용자 결정: A안 채택, 후속으로 C안(OTP 코드) 진행)
- Context:
  - Supabase 호스팅 프로젝트는 Confirm email이 기본 ON이다(실측 `/auth/v1/settings` → `mailer_autoconfirm: false`).
    이 상태에서 `/signup`은 세션 없이 200을 돌려주고, 사용자는 메일의 확인 링크를 눌러야 로그인할 수 있다.
  - 확인 링크의 목적지는 Supabase의 Site URL 기본값 `http://localhost:3000`이다. 모바일 앱에는 존재하지
    않는 주소라, 인증이 성공해도 사용자는 열리지 않는 페이지를 본다.
  - 링크는 원타임이다. 2026-09-08 07:13:45(UTC) `/verify`가 303으로 성공해 `email_confirmed_at`이
    채워졌는데, 21초 뒤 같은 링크의 재요청이 `403 One-time token not found`를 돌려줬다(auth 로그).
    메일 클라이언트의 링크 프리페치나 새로고침만으로도 사용자에게는 "만료됨"으로 보인다.
  - `/signup`은 확인 메일을 인라인 발송하느라 서버에서만 3.0~3.1s가 걸린다(auth 로그 `duration`).
    supabase-kt 기본 `requestTimeout` 10초에 여유가 없어 ADR-외 조치로 30초로 올려둔 상태다(PR #13).
- Options:
  - A) Confirm email OFF — 가입 응답에 세션이 실려 앱이 바로 홈으로 간다. 메일 왕복·딥링크·Site URL이
       전부 경로에서 빠진다. 대신 이메일 소유 증명이 사라진다.
  - B) 딥링크 유지 — Site URL/Redirect URLs에 앱 스킴을 등록하고 `install(Auth) { scheme; host }`로
       앱에 복귀시킨다. 브라우저를 거치는 구조와 원타임 링크 재방문 문제는 그대로 남는다.
  - C) 링크 대신 6자리 코드 — 메일 템플릿을 `{{ .Token }}`으로 바꾸고 앱에서 코드를 입력받아
       `auth.verifyEmailOtp(OtpType.Email.SIGNUP, email, token)`으로 검증한다. 이메일 소유 증명을
       유지하면서 브라우저·딥링크·Site URL이 모두 필요 없다. 코드 입력 화면이 하나 늘어난다.
- Decision: **지금은 A, 다음 사이클에 C로 이행.**
  - A를 먼저 두는 이유는 이메일 소유 증명이 필요 없어서가 아니라, 그 증명을 링크 방식으로 받는 동안
    사용자가 보는 것이 죽은 localhost 페이지이기 때문이다. B는 그 페이지를 앱으로 바꿀 뿐 원타임 링크
    재방문 문제를 남기고, 카카오 딥링크 배선과 함께 손대는 편이 싸다.
  - C가 최종 목표다. 코드 입력 화면은 기존 캐릭터 자산 기반의 키패드 UI로 구성한다(사용자 지정).
    C 완료 시 이 ADR을 superseded로 전환하고 Confirm email을 다시 켠다.
- Impact:
  - 대시보드 `Authentication > Providers > Email > Confirm email` OFF. 저장소 밖 자원이라 코드 리뷰로는
    확인할 수 없다 — 변경 여부는 `/auth/v1/settings`의 `mailer_autoconfirm`으로 확인한다.
  - 앱 코드는 바꾸지 않는다. `EmailAuthViewModel`은 가입 후 `currentTokens()` 유무로 이미 두 경로를
    모두 처리하고, 세션이 있으면 홈으로 간다. `CONFIRM_EMAIL_NOTICE` 분기는 **남긴다** — 토글이 다시
    켜지거나 C로 이행하는 동안 필요한 안전망이고, 지우면 앱 코드가 대시보드 상태에 의존하게 된다.
  - 이 기간 동안 `auth.users`에 이메일 소유가 증명되지 않은 계정이 생길 수 있다. C 이행 시점에
    기존 계정의 처리 방침(일괄 확인 처리 / 재인증 요구)을 별도로 정해야 한다.
  - 리뷰 티어: 대시보드 설정은 코드 밖이므로 PR 본문 "남은 항목"에 적어 사람이 확인한다(CLAUDE.md 규칙).
