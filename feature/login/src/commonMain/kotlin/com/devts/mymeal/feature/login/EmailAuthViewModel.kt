package com.devts.mymeal.feature.login

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devts.mymeal.core.auth.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class EmailAuthMode {
    LOGIN,
    SIGN_UP,

    /** 카카오로 로그인했지만 이메일이 없어서 이메일 로그인 수단을 추가로 받는 화면. */
    LINK_KAKAO,
}

@Immutable
data class EmailAuthUiState(
    val mode: EmailAuthMode = EmailAuthMode.LOGIN,
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    /** 오류가 아닌 안내 (가입 후 이메일 인증 대기 등). */
    val noticeMessage: String? = null,
) {
    // Supabase 기본 비밀번호 최소 길이 6
    val canSubmit: Boolean get() = !isLoading && "@" in email && password.length >= 6
}

sealed interface EmailAuthAction {
    data class EditEmail(val value: String) : EmailAuthAction
    data class EditPassword(val value: String) : EmailAuthAction
    data object Submit : EmailAuthAction
}

sealed interface EmailAuthEffect {
    data object NavigateToHome : EmailAuthEffect
}

class EmailAuthViewModel(
    mode: EmailAuthMode,
    private val repository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailAuthUiState(mode = mode))
    val uiState: StateFlow<EmailAuthUiState> = _uiState.asStateFlow()

    private val _effects = Channel<EmailAuthEffect>(Channel.BUFFERED) // State에 이벤트 금지 (ADR)
    val effects: Flow<EmailAuthEffect> = _effects.receiveAsFlow()

    fun onAction(action: EmailAuthAction) {
        when (action) {
            is EmailAuthAction.EditEmail ->
                _uiState.update { it.copy(email = action.value, errorMessage = null, noticeMessage = null) }
            is EmailAuthAction.EditPassword ->
                _uiState.update { it.copy(password = action.value, errorMessage = null, noticeMessage = null) }
            EmailAuthAction.Submit -> submit()
        }
    }

    private fun submit() {
        val state = _uiState.value
        if (!state.canSubmit) return
        _uiState.update { it.copy(isLoading = true, errorMessage = null, noticeMessage = null) }
        viewModelScope.launch {
            // true = 세션 확보(홈으로), false = 가입은 됐지만 이메일 인증 대기
            val result = runCatching {
                when (state.mode) {
                    EmailAuthMode.LOGIN -> repository.signInWithEmail(state.email, state.password)
                    // 이메일 확인이 켜져 있으면 가입 직후 세션이 없다. 이때 바로 로그인하면
                    // "Email not confirmed"가 떠서 가입이 실패한 것처럼 보이므로 안내로 끝낸다.
                    EmailAuthMode.SIGN_UP -> repository.signUpWithEmail(state.email, state.password)
                    EmailAuthMode.LINK_KAKAO -> repository.linkEmailPassword(state.email, state.password)
                }
                repository.currentTokens() != null
            }
            result.fold(
                onSuccess = { hasSession ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            noticeMessage = if (hasSession) null else CONFIRM_EMAIL_NOTICE,
                        )
                    }
                    if (hasSession) _effects.send(EmailAuthEffect.NavigateToHome)
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = authErrorMessage(error)) }
                },
            )
        }
    }
}

internal const val CONFIRM_EMAIL_NOTICE = "인증 메일을 보냈어요. 메일함에서 인증한 뒤 로그인해주세요"

internal fun authErrorMessage(error: Throwable): String {
    val raw = error.message.orEmpty()
    return when {
        raw.contains("Invalid login credentials", ignoreCase = true) -> "이메일 또는 비밀번호가 올바르지 않습니다"
        raw.contains("already registered", ignoreCase = true) -> "이미 가입된 이메일입니다"
        raw.contains("Email not confirmed", ignoreCase = true) -> "이메일 인증을 완료한 뒤 로그인해주세요"
        raw.isBlank() -> "잠시 후 다시 시도해주세요"
        else -> raw
    }
}
