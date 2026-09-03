package com.devts.mymeal.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

// UDF 골격 — 로그인 실동작(카카오/이메일 인증)은 F-6 account-sync 소관.
data class LoginUiState(val isLoading: Boolean = false)

sealed interface LoginAction {
    data object KakaoClick : LoginAction
    data object EmailClick : LoginAction
}

sealed interface LoginEffect {
    data object NavigateToHome : LoginEffect
}

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _effects = Channel<LoginEffect>(Channel.BUFFERED) // State에 이벤트 금지 (ADR)
    val effects: Flow<LoginEffect> = _effects.receiveAsFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.KakaoClick,
            LoginAction.EmailClick,
            -> viewModelScope.launch { _effects.send(LoginEffect.NavigateToHome) }
        }
    }
}
