package com.devts.mymeal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.devts.mymeal.core.auth.AuthRepository
import com.devts.mymeal.core.designsystem.SikdorokTheme
import com.devts.mymeal.feature.home.HomeRoute
import com.devts.mymeal.feature.home.homeDestination
import com.devts.mymeal.feature.login.EmailAuthMode
import com.devts.mymeal.feature.login.EmailAuthRoute
import com.devts.mymeal.feature.login.LoginRoute
import com.devts.mymeal.feature.login.emailAuthDestination
import com.devts.mymeal.feature.login.loginDestination
import com.devts.mymeal.feature.record.RecordRoute
import com.devts.mymeal.feature.record.recordDestination
import org.koin.core.context.GlobalContext

// 첫 화면 = 로그인 (Figma 832:48657) → 이메일 인증 (Figma 832:106628) 또는 홈 (Figma 832:92613).
// 홈 FAB → 기록 생성/수정 (Figma 832:98315). 홈·기록 데이터는 화면 구성 스텁 — F-2/F-5에서 연결.
@Composable
@Preview
fun App() {
    SikdorokTheme {
        // 자동 로그인: supabase-kt가 세션을 플랫폼 저장소(안드로이드=SharedPreferences)에 보관하고
        // 시작 시 복원한다. 복원이 끝나기 전에는 NavHost를 만들지 않는다 — 로그인 화면이 깜빡인다.
        val authRepository = remember { GlobalContext.get().get<AuthRepository>() }
        var startDestination by remember { mutableStateOf<Any?>(null) }
        LaunchedEffect(Unit) {
            startDestination = if (authRepository.hasValidSession()) HomeRoute else LoginRoute
        }
        val start = startDestination ?: return@SikdorokTheme // 스플래시 유지

        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = start) {
            val toHome = {
                navController.navigate(
                    HomeRoute,
                    navOptions { popUpTo<LoginRoute> { inclusive = true } }, // 인증 화면은 백스택에서 제거
                )
            }
            loginDestination(
                onNavigateToHome = toHome,
                onNavigateToEmailAuth = { mode -> navController.navigate(EmailAuthRoute(mode.name)) },
            )
            emailAuthDestination(
                onNavigateToHome = toHome,
                onNavigateToSignUp = { navController.navigate(EmailAuthRoute(EmailAuthMode.SIGN_UP.name)) },
                onBack = { navController.popBackStack() },
            )
            homeDestination(onNavigateToRecord = { navController.navigate(RecordRoute) })
            recordDestination(onBack = { navController.popBackStack() })
        }
    }
}
