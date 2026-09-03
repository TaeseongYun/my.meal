package com.devts.mymeal.feature.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object LoginRoute

fun NavGraphBuilder.loginDestination(onNavigateToHome: () -> Unit) {
    composable<LoginRoute> {
        LoginDestination(onNavigateToHome)
    }
}

@Composable
private fun LoginDestination(onNavigateToHome: () -> Unit) {
    val viewModel = koinViewModel<LoginViewModel>()
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                LoginEffect.NavigateToHome -> onNavigateToHome()
            }
        }
    }
    LoginScreen(
        onKakaoClick = { viewModel.onAction(LoginAction.KakaoClick) },
        onEmailClick = { viewModel.onAction(LoginAction.EmailClick) },
    )
}
