package com.devts.mymeal.feature.login

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeAuthRepository()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun kakaoClick_launchesPlatformLogin() = runTest(dispatcher.scheduler) {
        val viewModel = LoginViewModel(repository)
        viewModel.onAction(LoginAction.KakaoClick)
        assertEquals(LoginEffect.LaunchKakao, viewModel.effects.first())
    }

    @Test
    fun emailClick_emitsNavigateToEmailAuth() = runTest(dispatcher.scheduler) {
        val viewModel = LoginViewModel(repository)
        viewModel.onAction(LoginAction.EmailClick)
        assertEquals(LoginEffect.NavigateToEmailAuth, viewModel.effects.first())
    }

    @Test
    fun kakaoResult_withEmail_navigatesHome() = runTest(dispatcher.scheduler) {
        val viewModel = LoginViewModel(repository)
        viewModel.onAction(LoginAction.KakaoResult(Result.success("id-token")))
        assertEquals(LoginEffect.NavigateToHome, viewModel.effects.first())
        assertEquals(listOf("kakao:id-token"), repository.calls)
    }

    @Test
    fun kakaoResult_withoutEmail_asksForEmailLink() = runTest(dispatcher.scheduler) {
        repository.emailMissingAfterKakao = true
        val viewModel = LoginViewModel(repository)
        viewModel.onAction(LoginAction.KakaoResult(Result.success("id-token")))
        assertEquals(LoginEffect.NavigateToKakaoEmailLink, viewModel.effects.first())
    }

    @Test
    fun kakaoResult_failure_showsErrorWithoutCallingSupabase() = runTest(dispatcher.scheduler) {
        val viewModel = LoginViewModel(repository)
        viewModel.onAction(LoginAction.KakaoResult(Result.failure(IllegalStateException("카카오 로그인 취소"))))
        advanceUntilIdle()
        assertEquals("카카오 로그인 취소", viewModel.uiState.value.errorMessage)
        assertEquals(emptyList(), repository.calls)
    }

    @Test
    fun initialState_isNotLoading() {
        assertFalse(LoginViewModel(repository).uiState.value.isLoading)
    }
}
