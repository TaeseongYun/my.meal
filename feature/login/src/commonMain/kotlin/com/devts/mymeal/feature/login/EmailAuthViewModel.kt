package com.devts.mymeal.feature.login

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devts.mymeal.core.auth.AuthRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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
    /** 오류가 아닌 안내 (코드 재전송 완료 등). */
    val noticeMessage: String? = null,
    /** 코드를 보낸 주소. null이 아니면 화면은 코드 입력 단계다. */
    val codeSentTo: String? = null,
    val code: String = "",
    /** 재전송까지 남은 초. 0이면 재전송 가능. */
    val resendCooldown: Int = 0,
) {
    // Supabase 기본 비밀번호 최소 길이 6
    val canSubmit: Boolean get() = !isLoading && "@" in email && password.length >= 6
}

sealed interface EmailAuthAction {
    data class EditEmail(val value: String) : EmailAuthAction
    data class EditPassword(val value: String) : EmailAuthAction
    data object Submit : EmailAuthAction

    /** 코드 입력 단계 — 키패드는 숫자 한 자씩만 넣는다. */
    data class AppendDigit(val digit: Char) : EmailAuthAction
    data object DeleteDigit : EmailAuthAction
    data object ResendCode : EmailAuthAction
    data object BackToForm : EmailAuthAction
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

    private var cooldownJob: Job? = null

    fun onAction(action: EmailAuthAction) {
        when (action) {
            is EmailAuthAction.EditEmail ->
                _uiState.update { it.copy(email = action.value, errorMessage = null, noticeMessage = null) }
            is EmailAuthAction.EditPassword ->
                _uiState.update { it.copy(password = action.value, errorMessage = null, noticeMessage = null) }
            EmailAuthAction.Submit -> submit()
            is EmailAuthAction.AppendDigit -> appendDigit(action.digit)
            EmailAuthAction.DeleteDigit ->
                _uiState.update { it.copy(code = it.code.dropLast(1), errorMessage = null) }
            EmailAuthAction.ResendCode -> resendCode()
            EmailAuthAction.BackToForm -> {
                cooldownJob?.cancel()
                _uiState.update {
                    it.copy(codeSentTo = null, code = "", resendCooldown = 0, errorMessage = null, noticeMessage = null)
                }
            }
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
                    // "Email not confirmed"가 떠서 가입이 실패한 것처럼 보이므로 코드 입력으로 넘긴다.
                    EmailAuthMode.SIGN_UP -> repository.signUpWithEmail(state.email, state.password)
                    EmailAuthMode.LINK_KAKAO -> repository.linkEmailPassword(state.email, state.password)
                }
                repository.currentTokens() != null
            }
            result.fold(
                onSuccess = { hasSession ->
                    if (hasSession) {
                        _uiState.update { it.copy(isLoading = false) }
                        _effects.send(EmailAuthEffect.NavigateToHome)
                    } else {
                        enterCodeStep(state.email)
                    }
                },
                onFailure = { error ->
                    // 이미 가입했지만 확인을 안 한 계정은 로그인이 영영 막힌다 — 코드를 새로 보내 같은 단계로 보낸다.
                    if (error.message.orEmpty().contains("Email not confirmed", ignoreCase = true)) {
                        runCatching { repository.resendSignUpCode(state.email) }
                        enterCodeStep(state.email)
                    } else {
                        _uiState.update { it.copy(isLoading = false, errorMessage = authErrorMessage(error)) }
                    }
                },
            )
        }
    }

    private fun enterCodeStep(email: String) {
        _uiState.update { it.copy(isLoading = false, codeSentTo = email, code = "", errorMessage = null) }
        startCooldown()
    }

    private fun appendDigit(digit: Char) {
        val state = _uiState.value
        if (state.isLoading || state.code.length >= CODE_LENGTH) return
        val code = state.code + digit
        _uiState.update { it.copy(code = code, errorMessage = null, noticeMessage = null) }
        if (code.length == CODE_LENGTH) verify(code)
    }

    private fun verify(code: String) {
        val email = _uiState.value.codeSentTo ?: return
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            runCatching {
                repository.verifyEmailCode(email, code)
                repository.currentTokens() != null
            }.fold(
                onSuccess = { hasSession ->
                    if (hasSession) {
                        _uiState.update { it.copy(isLoading = false) }
                        _effects.send(EmailAuthEffect.NavigateToHome)
                    } else {
                        // 코드는 맞았는데 세션이 없다 = 확인만 된 경우. 비밀번호로 로그인하면 된다.
                        _uiState.update { it.copy(isLoading = false, code = "", errorMessage = VERIFIED_NO_SESSION_NOTICE) }
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, code = "", errorMessage = authErrorMessage(error)) }
                },
            )
        }
    }

    private fun resendCode() {
        val state = _uiState.value
        val email = state.codeSentTo ?: return
        if (state.isLoading || state.resendCooldown > 0) return
        _uiState.update { it.copy(isLoading = true, errorMessage = null, noticeMessage = null) }
        viewModelScope.launch {
            runCatching { repository.resendSignUpCode(email) }.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, code = "", noticeMessage = RESENT_NOTICE) }
                    startCooldown()
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = authErrorMessage(error)) }
                },
            )
        }
    }

    private fun startCooldown() {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            _uiState.update { it.copy(resendCooldown = RESEND_COOLDOWN_SECONDS) }
            while (_uiState.value.resendCooldown > 0) {
                delay(1000)
                _uiState.update { it.copy(resendCooldown = it.resendCooldown - 1) }
            }
        }
    }
}

internal const val CODE_LENGTH = 6
internal const val RESEND_COOLDOWN_SECONDS = 60
internal const val RESENT_NOTICE = "인증 코드를 다시 보냈어요"
internal const val VERIFIED_NO_SESSION_NOTICE = "인증이 끝났어요. 비밀번호로 로그인해주세요"

internal fun authErrorMessage(error: Throwable): String {
    val raw = error.message.orEmpty()
    return when {
        raw.contains("Invalid login credentials", ignoreCase = true) -> "이메일 또는 비밀번호가 올바르지 않습니다"
        raw.contains("already registered", ignoreCase = true) -> "이미 가입된 이메일입니다"
        // Supabase는 오타 코드와 만료 코드에 같은 문구를 쓴다 — 구분하지 않고 한 줄로 안내한다.
        raw.contains("Token has expired or is invalid", ignoreCase = true) ||
            raw.contains("otp", ignoreCase = true) -> "인증 코드가 올바르지 않거나 만료됐어요"
        raw.isBlank() -> "잠시 후 다시 시도해주세요"
        else -> raw
    }
}
