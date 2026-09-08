# ADR-0002: 이메일 확인을 링크 클릭에서 6자리 코드(OTP)로 전환

- Status: accepted (2026-09-08, 사용자 결정: "피그마 톤앤매너로 키패드 입력 형태의 이메일 인증")
- Context: 가입 확인이 Supabase 기본 템플릿의 `{{ .ConfirmationURL }}` 링크였다. 모바일에서
  메일 앱 → 브라우저로 앱 밖으로 튕겨 나가고, 돌아올 딥링크가 없어 사용자가 로그인 화면에
  방치된다. Supabase는 링크와 무관하게 `{{ .Token }}`(6자리 OTP)을 **항상 함께 발급**하므로
  템플릿만 바꾸면 앱 안에서 인증을 끝낼 수 있다.
- Options:
  - A) 링크 유지 + 딥링크 처리 추가 — AndroidManifest·Associated Domains·iOS 양쪽 배선 필요,
    메일 클라이언트의 링크 프리페치(Safe Links)가 토큰을 미리 소진하는 알려진 함정도 남는다
  - B) 완전 패스워드리스 OTP — 비밀번호 필드·비밀번호 찾기가 통째로 사라진다. 코드는 가장 적지만
    이미 구현된 비밀번호 로그인과 Figma 로그인 화면을 버려야 한다
  - C) 비밀번호 로그인 유지 + **가입 확인만** 6자리 코드 — 변경 범위가 좁고 링크가 사라진다
- Decision: **C**. 로그인은 기존 이메일/비밀번호 그대로, 가입 직후 세션이 없을 때만 코드 입력
  단계로 전환한다. 새 라우트·ViewModel·Koin 등록을 만들지 않고 `EmailAuthUiState`에
  `codeSentTo`/`code`/`resendCooldown` 3필드를 더해 **같은 화면이 단계 전환**한다.
  입력은 커스텀 숫자 키패드(0~9, 지움)이고 6번째 자리에서 자동 검증한다.
- Impact:
  - `core/auth`에 `verifyEmailCode`(= `verifyEmailOtp(OtpType.Email.EMAIL, …)`),
    `resendSignUpCode`(= `resendEmail(SIGNUP, …)`) 추가. supabase-kt 3.8.0 기본 API로 충당했고
    새 의존성은 없다.
  - 코드 도입 전에 가입만 해둔 계정은 로그인이 영영 막히므로, `Email not confirmed`를 만나면
    코드를 새로 보내고 같은 단계로 보낸다. 이 경로가 없으면 기존 계정이 전부 잠긴다.
  - 비밀번호 찾기(RECOVERY)는 이 결정의 범위 밖이다 — 여전히 링크 방식이다.

## 이 결정이 성립하려면 저장소 밖에서 참이어야 하는 것

코드만으로는 동작하지 않는다. Supabase 프로젝트 설정 3건에 의존한다 (2026-09-08 적용 완료).

| 설정 | 값 | 어긋나면 |
|------|-----|---------|
| `mailer_autoconfirm` | `false` (Confirm email ON) | 가입 즉시 세션이 나와 코드 화면에 도달하지 않는다 |
| Confirm signup 템플릿 | `{{ .Token }}` 포함 | 메일에 숫자가 안 실리고 링크만 온다 |
| `mailer_otp_length` | `6` | 앱의 `CODE_LENGTH = 6`과 어긋나 자동 검증이 영영 안 걸린다 (도입 시점 값은 **8**이었다) |

확인: `curl "$SUPABASE_URL/auth/v1/settings" -H "apikey: $ANON"` 로 `mailer_autoconfirm`을 볼 수 있다.
템플릿과 OTP 길이는 Management API(`GET /v1/projects/{ref}/config/auth`, PAT 필요)에서 읽는다.

**무료 티어 + 기본 이메일 제공자 조합에서는 템플릿 수정이 거부된다.** Management API가
`400 "Email template modification is not available for free tier projects using the default
email provider"`를 돌려준다. 커스텀 SMTP 연결이 전제 조건이고, 이 문서 기준 Resend를 붙였다.

Resend는 도메인 인증 전까지 발신 주소가 `onboarding@resend.dev`이고, 이때 **계정 소유자 주소로만**
발송된다(그 외에는 `550 You can only send testing emails to your own email address`). 실사용 전에
도메인 인증 + `smtp_admin_email` 교체가 필요하다.
