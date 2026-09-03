package com.devts.mymeal.feature.login

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
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

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun kakaoClick_emitsNavigateToHome() = runTest(dispatcher.scheduler) {
        val viewModel = LoginViewModel()
        viewModel.onAction(LoginAction.KakaoClick)
        assertEquals(LoginEffect.NavigateToHome, viewModel.effects.first())
    }

    @Test
    fun emailClick_emitsNavigateToHome() = runTest(dispatcher.scheduler) {
        val viewModel = LoginViewModel()
        viewModel.onAction(LoginAction.EmailClick)
        assertEquals(LoginEffect.NavigateToHome, viewModel.effects.first())
    }

    @Test
    fun initialState_isNotLoading() {
        assertFalse(LoginViewModel().uiState.value.isLoading)
    }
}
