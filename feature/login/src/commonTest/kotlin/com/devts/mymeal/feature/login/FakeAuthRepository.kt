package com.devts.mymeal.feature.login

import com.devts.mymeal.core.auth.AuthRepository
import com.devts.mymeal.core.auth.AuthTokens

class FakeAuthRepository(
    var failure: Throwable? = null,
    var emailMissingAfterKakao: Boolean = false,
    var sessionAfterSignUp: Boolean = true,
) : AuthRepository {
    val calls = mutableListOf<String>()
    private var signedUp = false

    private fun record(call: String) {
        calls += call
        failure?.let { throw it }
    }

    override suspend fun signUpWithEmail(email: String, password: String) {
        record("signUp:$email")
        signedUp = true
    }

    override suspend fun signInWithEmail(email: String, password: String) = record("signIn:$email")

    override suspend fun signInWithKakao(idToken: String) = record("kakao:$idToken")

    override suspend fun linkEmailPassword(email: String, password: String) = record("link:$email")

    override fun needsEmailLink(): Boolean = emailMissingAfterKakao

    override suspend fun hasValidSession(): Boolean = currentTokens() != null

    override fun currentTokens(): AuthTokens? =
        if (signedUp && !sessionAfterSignUp) null else AuthTokens("access", "refresh")

    override suspend fun signOut() = record("signOut")
}
