package com.devts.mymeal.feature.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data object LoginRoute

/** mode는 [EmailAuthMode] 이름 — 타입 안전 라우트의 enum 지원에 기대지 않는다. */
@Serializable
data class EmailAuthRoute(val mode: String)

fun NavGraphBuilder.loginDestination(
    onNavigateToHome: () -> Unit,
    onNavigateToEmailAuth: (EmailAuthMode) -> Unit,
) {
    composable<LoginRoute> {
        val viewModel = koinViewModel<LoginViewModel>()
        val launchKakao = rememberKakaoLogin { result ->
            viewModel.onAction(LoginAction.KakaoResult(result))
        }
        LaunchedEffect(viewModel) {
            viewModel.effects.collect { effect ->
                when (effect) {
                    LoginEffect.LaunchKakao -> launchKakao()
                    LoginEffect.NavigateToHome -> onNavigateToHome()
                    LoginEffect.NavigateToEmailAuth -> onNavigateToEmailAuth(EmailAuthMode.LOGIN)
                    LoginEffect.NavigateToKakaoEmailLink -> onNavigateToEmailAuth(EmailAuthMode.LINK_KAKAO)
                }
            }
        }
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        LoginScreen(
            errorMessage = state.errorMessage,
            onKakaoClick = { viewModel.onAction(LoginAction.KakaoClick) },
            onEmailClick = { viewModel.onAction(LoginAction.EmailClick) },
        )
    }
}

fun NavGraphBuilder.emailAuthDestination(
    onNavigateToHome: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onBack: () -> Unit,
) {
    composable<EmailAuthRoute> { entry ->
        val mode = EmailAuthMode.valueOf(entry.toRoute<EmailAuthRoute>().mode)
        val viewModel = koinViewModel<EmailAuthViewModel>(key = "email-auth-$mode") { parametersOf(mode) }
        LaunchedEffect(viewModel) {
            viewModel.effects.collect { effect ->
                when (effect) {
                    EmailAuthEffect.NavigateToHome -> onNavigateToHome()
                }
            }
        }
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        EmailAuthScreen(
            state = state,
            onEmailChange = { viewModel.onAction(EmailAuthAction.EditEmail(it)) },
            onPasswordChange = { viewModel.onAction(EmailAuthAction.EditPassword(it)) },
            onSubmit = { viewModel.onAction(EmailAuthAction.Submit) },
            onBack = onBack,
            onSwitchToSignUp = onNavigateToSignUp,
        )
    }
}
