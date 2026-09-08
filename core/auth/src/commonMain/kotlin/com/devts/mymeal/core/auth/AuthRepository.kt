package com.devts.mymeal.core.auth

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Kakao
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.createSupabaseClient
import kotlin.time.Duration.Companion.seconds

/** 로그인으로 발급된 토큰. 저장·자동 갱신은 supabase-kt가 담당한다. */
data class AuthTokens(val accessToken: String, val refreshToken: String)

interface AuthRepository {
    /** 이메일 회원가입 — auth.users insert가 트리거로 public.profiles를 채운다. */
    suspend fun signUpWithEmail(email: String, password: String)

    suspend fun signInWithEmail(email: String, password: String)

    /** 카카오 OIDC id_token 교환. 같은 이메일의 기존 계정이 있으면 Supabase가 자동 연동한다. */
    suspend fun signInWithKakao(idToken: String)

    /** 카카오 계정에 이메일 로그인 수단을 붙인다 (카카오가 이메일을 안 준 경우). */
    suspend fun linkEmailPassword(email: String, password: String)

    /** 로그인 후에도 이메일이 없는 상태 = 이메일/비밀번호를 추가로 받아야 한다. */
    fun needsEmailLink(): Boolean

    /**
     * 저장소(안드로이드는 SharedPreferences)에서 세션 복원이 끝날 때까지 기다린 뒤 로그인 여부를 준다.
     * 자동 로그인 판정용 — 앱 시작 시 시작 화면을 고르는 데 쓴다.
     */
    suspend fun hasValidSession(): Boolean

    fun currentTokens(): AuthTokens?

    suspend fun signOut()
}

fun createAuthRepository(): AuthRepository = SupabaseAuthRepository()

internal class SupabaseAuthRepository : AuthRepository {

    // Auth 플러그인의 기본 SessionManager가 플랫폼 저장소(안드로이드는 Context)를 요구해서
    // 클라이언트 생성을 첫 사용 시점으로 미룬다 — DI 그래프 검증이 Android 런타임 없이 돌아야 한다.
    private val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = AuthConfig.SUPABASE_URL,
            supabaseKey = AuthConfig.SUPABASE_ANON_KEY,
        ) {
            // 기본 10초는 가입에 빠듯하다 — 이메일 확인이 켜져 있으면 서버가 확인 메일을 보내는
            // 동안 응답을 붙들어 실측 3.0~3.1s가 나온다(2026-09-08 auth 로그). 느린 회선에서 초과.
            requestTimeout = 30.seconds
            // 세션(액세스/리프레시 토큰) 저장과 만료 전 자동 갱신은 Auth 플러그인이 처리한다.
            // autoLoadFromStorage 기본값 true → 앱 재시작 시 저장된 세션을 자동 복원한다.
            install(Auth)
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String) {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signInWithEmail(email: String, password: String) {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signInWithKakao(idToken: String) {
        client.auth.signInWith(IDToken) {
            this.idToken = idToken
            this.provider = Kakao
        }
    }

    override suspend fun linkEmailPassword(email: String, password: String) {
        client.auth.updateUser {
            this.email = email
            this.password = password
        }
    }

    override fun needsEmailLink(): Boolean = client.auth.currentUserOrNull()?.email.isNullOrBlank()

    override suspend fun hasValidSession(): Boolean {
        client.auth.awaitInitialization() // 저장소 복원·최초 갱신이 끝날 때까지 대기
        return client.auth.currentSessionOrNull() != null
    }

    override fun currentTokens(): AuthTokens? = client.auth.currentSessionOrNull()?.let {
        AuthTokens(accessToken = it.accessToken, refreshToken = it.refreshToken)
    }

    override suspend fun signOut() {
        client.auth.signOut()
    }
}
