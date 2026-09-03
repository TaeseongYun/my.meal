package com.devts.mymeal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.devts.mymeal.core.designsystem.SikdorokTheme
import com.devts.mymeal.feature.home.HomeScreen
import com.devts.mymeal.feature.home.stubHomeUiState
import com.devts.mymeal.feature.login.LoginScreen

// 첫 화면 = 로그인 (Figma 832:48657) → 버튼 클릭 시 홈 (Figma 832:92613).
// 임시 remember 전환 — 내비게이션 골격은 F-1 app-foundation에서 도입 예정.
// 홈 데이터는 화면 구성 스텁 — F-2/F-5에서 실데이터 연결.
@Composable
@Preview
fun App() {
    SikdorokTheme {
        var loggedIn by remember { mutableStateOf(false) }
        if (loggedIn) {
            HomeScreen(state = remember { stubHomeUiState() })
        } else {
            LoginScreen(
                onKakaoClick = { loggedIn = true },
                onEmailClick = { loggedIn = true },
            )
        }
    }
}
