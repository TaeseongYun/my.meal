package com.devts.mymeal.feature.login

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
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

    /** 가입 → 코드 단계까지 진입시킨 뒤, 쿨다운 타이머는 아직 돌고 있는 상태로 둔다. */
    private fun TestScope.signUpIntoCodeStep(): EmailAuthViewModel {
        // 이메일 확인이 켜진 프로젝트: 가입 직후 세션이 없다 (실측 2026-09-08)
        repository.sessionAfterSignUp = false
        val vm = viewModel(EmailAuthMode.SIGN_UP)
        vm.fillValidForm()
        vm.onAction(EmailAuthAction.Submit)
        runCurrent()
        return vm
    }

    @Test
    fun signUp_withoutAutoSession_entersCodeStepWithResendCooldown() = runTest(dispatcher.scheduler) {
        val vm = signUpIntoCodeStep()
        assertEquals(listOf("signUp:sikdorok@naver.com"), repository.calls)
        assertEquals("sikdorok@naver.com", vm.uiState.value.codeSentTo)
        assertEquals(RESEND_COOLDOWN_SECONDS, vm.uiState.value.resendCooldown)
        assertEquals(null, vm.uiState.value.errorMessage)
        assertFalse(vm.uiState.value.isLoading)
        advanceUntilIdle()
    }

    @Test
    fun sixthDigit_verifiesCodeAndNavigatesHome() = runTest(dispatcher.scheduler) {
        val vm = signUpIntoCodeStep()
        "12345".forEach { vm.onAction(EmailAuthAction.AppendDigit(it)) }
        assertEquals(listOf("signUp:sikdorok@naver.com"), repository.calls) // 6자리 전에는 호출 없음
        vm.onAction(EmailAuthAction.AppendDigit('6'))
        assertEquals(EmailAuthEffect.NavigateToHome, vm.effects.first())
        assertEquals("verify:sikdorok@naver.com:123456", repository.calls.last())
        advanceUntilIdle()
    }

    @Test
    fun deleteDigit_dropsLastAndBlocksVerify() = runTest(dispatcher.scheduler) {
        val vm = signUpIntoCodeStep()
        "123456".dropLast(1).forEach { vm.onAction(EmailAuthAction.AppendDigit(it)) }
        vm.onAction(EmailAuthAction.DeleteDigit)
        assertEquals("1234", vm.uiState.value.code)
        advanceUntilIdle()
        assertEquals(listOf("signUp:sikdorok@naver.com"), repository.calls)
    }

    @Test
    fun wrongCode_showsMessageAndClearsInput() = runTest(dispatcher.scheduler) {
        val vm = signUpIntoCodeStep()
        repository.failure = IllegalStateException("Token has expired or is invalid")
        "999999".forEach { vm.onAction(EmailAuthAction.AppendDigit(it)) }
        advanceUntilIdle()
        assertEquals("인증 코드가 올바르지 않거나 만료됐어요", vm.uiState.value.errorMessage)
        assertEquals("", vm.uiState.value.code)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun resend_isIgnoredWhileCooldownRunsAndSendsAfterIt() = runTest(dispatcher.scheduler) {
        val vm = signUpIntoCodeStep()
        vm.onAction(EmailAuthAction.ResendCode)
        runCurrent()
        assertEquals(listOf("signUp:sikdorok@naver.com"), repository.calls)

        advanceUntilIdle() // 쿨다운 소진
        assertEquals(0, vm.uiState.value.resendCooldown)
        vm.onAction(EmailAuthAction.ResendCode)
        runCurrent()
        assertEquals("resend:sikdorok@naver.com", repository.calls.last())
        assertEquals(RESENT_NOTICE, vm.uiState.value.noticeMessage)
        advanceUntilIdle()
    }

    @Test
    fun unconfirmedLogin_resendsCodeAndEntersCodeStep() = runTest(dispatcher.scheduler) {
        // 코드 도입 전에 가입만 해둔 계정 — 재전송 없이는 로그인이 영영 막힌다.
        repository.failure = IllegalStateException("Email not confirmed")
        val vm = viewModel(EmailAuthMode.LOGIN)
        vm.fillValidForm()
        vm.onAction(EmailAuthAction.Submit)
        runCurrent()
        assertEquals(listOf("signIn:sikdorok@naver.com", "resend:sikdorok@naver.com"), repository.calls)
        assertEquals("sikdorok@naver.com", vm.uiState.value.codeSentTo)
        advanceUntilIdle()
    }

    @Test
    fun backToForm_leavesCodeStep() = runTest(dispatcher.scheduler) {
        val vm = signUpIntoCodeStep()
        vm.onAction(EmailAuthAction.AppendDigit('1'))
        vm.onAction(EmailAuthAction.BackToForm)
        assertEquals(null, vm.uiState.value.codeSentTo)
        assertEquals("", vm.uiState.value.code)
        assertEquals(0, vm.uiState.value.resendCooldown)
        advanceUntilIdle()
    }

    @Test
    fun countdown_isMinuteSecondPadded() {
        assertEquals("1:00", countdown(60))
        assertEquals("0:09", countdown(9))
        assertEquals("0:00", countdown(0))
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
