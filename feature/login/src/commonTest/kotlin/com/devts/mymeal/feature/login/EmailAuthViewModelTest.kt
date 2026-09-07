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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EmailAuthViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeAuthRepository()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(mode: EmailAuthMode = EmailAuthMode.LOGIN) =
        EmailAuthViewModel(mode, repository)

    private fun EmailAuthViewModel.fillValidForm() {
        onAction(EmailAuthAction.EditEmail("sikdorok@naver.com"))
        onAction(EmailAuthAction.EditPassword("123456"))
    }

    @Test
    fun canSubmit_requiresEmailShapeAndSixCharPassword() {
        val vm = viewModel()
        assertFalse(vm.uiState.value.canSubmit)
        vm.onAction(EmailAuthAction.EditEmail("sikdorok@naver.com"))
        vm.onAction(EmailAuthAction.EditPassword("12345"))
        assertFalse(vm.uiState.value.canSubmit)
        vm.onAction(EmailAuthAction.EditPassword("123456"))
        assertTrue(vm.uiState.value.canSubmit)
    }

    @Test
    fun submit_withInvalidInput_callsNothing() = runTest(dispatcher.scheduler) {
        val vm = viewModel()
        vm.onAction(EmailAuthAction.EditEmail("nope"))
        vm.onAction(EmailAuthAction.Submit)
        advanceUntilIdle()
        assertEquals(emptyList(), repository.calls)
    }

    @Test
    fun login_signsInAndNavigatesHome() = runTest(dispatcher.scheduler) {
        val vm = viewModel(EmailAuthMode.LOGIN)
        vm.fillValidForm()
        vm.onAction(EmailAuthAction.Submit)
        assertEquals(EmailAuthEffect.NavigateToHome, vm.effects.first())
        assertEquals(listOf("signIn:sikdorok@naver.com"), repository.calls)
    }

    @Test
    fun signUp_withoutAutoSession_signsInAfterSignUp() = runTest(dispatcher.scheduler) {
        repository.sessionAfterSignUp = false
        val vm = viewModel(EmailAuthMode.SIGN_UP)
        vm.fillValidForm()
        vm.onAction(EmailAuthAction.Submit)
        assertEquals(EmailAuthEffect.NavigateToHome, vm.effects.first())
        assertEquals(listOf("signUp:sikdorok@naver.com", "signIn:sikdorok@naver.com"), repository.calls)
    }

    @Test
    fun signUp_withAutoSession_skipsExtraSignIn() = runTest(dispatcher.scheduler) {
        val vm = viewModel(EmailAuthMode.SIGN_UP)
        vm.fillValidForm()
        vm.onAction(EmailAuthAction.Submit)
        assertEquals(EmailAuthEffect.NavigateToHome, vm.effects.first())
        assertEquals(listOf("signUp:sikdorok@naver.com"), repository.calls)
    }

    @Test
    fun linkKakao_updatesUserInsteadOfSigningIn() = runTest(dispatcher.scheduler) {
        val vm = viewModel(EmailAuthMode.LINK_KAKAO)
        vm.fillValidForm()
        vm.onAction(EmailAuthAction.Submit)
        assertEquals(EmailAuthEffect.NavigateToHome, vm.effects.first())
        assertEquals(listOf("link:sikdorok@naver.com"), repository.calls)
    }

    @Test
    fun failure_showsMappedMessageAndStopsLoading() = runTest(dispatcher.scheduler) {
        repository.failure = IllegalStateException("Invalid login credentials")
        val vm = viewModel()
        vm.fillValidForm()
        vm.onAction(EmailAuthAction.Submit)
        advanceUntilIdle()
        assertEquals("이메일 또는 비밀번호가 올바르지 않습니다", vm.uiState.value.errorMessage)
        assertFalse(vm.uiState.value.isLoading)
    }
}
