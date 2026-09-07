package com.devts.mymeal.feature.login

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

data class LoginUiState(val isLoading: Boolean = false, val errorMessage: String? = null)

sealed interface LoginAction {
    data object KakaoClick : LoginAction
    data class KakaoResult(val idToken: Result<String>) : LoginAction
    data object EmailClick : LoginAction
}

sealed interface LoginEffect {
    /** 화면이 플랫폼 카카오 SDK를 띄운다 (ViewModel은 플랫폼 API를 모른다). */
    data object LaunchKakao : LoginEffect
    data object NavigateToHome : LoginEffect
    data object NavigateToEmailAuth : LoginEffect

    /** 카카오 계정에 이메일이 없어 이메일/비밀번호를 추가로 받아야 하는 경우. */
    data object NavigateToKakaoEmailLink : LoginEffect
}

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _effects = Channel<LoginEffect>(Channel.BUFFERED) // State에 이벤트 금지 (ADR)
    val effects: Flow<LoginEffect> = _effects.receiveAsFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.KakaoClick -> emit(LoginEffect.LaunchKakao)
            LoginAction.EmailClick -> emit(LoginEffect.NavigateToEmailAuth)
            is LoginAction.KakaoResult -> signInWithKakao(action.idToken)
        }
    }

    private fun signInWithKakao(idToken: Result<String>) {
        val token = idToken.getOrElse { error ->
            _uiState.update { it.copy(isLoading = false, errorMessage = authErrorMessage(error)) }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { repository.signInWithKakao(token) }.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                    _effects.send(
                        // 카카오가 이메일을 주지 않은 계정 → 이메일 로그인 수단을 붙이러 보낸다
                        if (repository.needsEmailLink()) {
                            LoginEffect.NavigateToKakaoEmailLink
                        } else {
                            LoginEffect.NavigateToHome
                        },
                    )
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = authErrorMessage(error)) }
                },
            )
        }
    }

    private fun emit(effect: LoginEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
