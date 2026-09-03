package com.devts.mymeal

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.devts.mymeal.core.designsystem.SikdorokTheme
import com.devts.mymeal.feature.home.HomeRoute
import com.devts.mymeal.feature.home.homeDestination
import com.devts.mymeal.feature.login.LoginRoute
import com.devts.mymeal.feature.login.loginDestination

// 첫 화면 = 로그인 (Figma 832:48657) → 로그인 액션 시 홈 (Figma 832:92613).
// 홈 데이터는 화면 구성 스텁 — F-5에서 실데이터 연결.
@Composable
@Preview
fun App() {
    SikdorokTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = LoginRoute) {
            loginDestination(
                onNavigateToHome = {
                    navController.navigate(
                        HomeRoute,
                        navOptions { popUpTo<LoginRoute> { inclusive = true } }, // 로그인은 백스택에서 제거
                    )
                },
            )
            homeDestination()
        }
    }
}
