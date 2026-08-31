package com.devts.mymeal

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.devts.mymeal.core.designsystem.SikdorokTheme
import com.devts.mymeal.feature.login.LoginScreen

// 첫 화면 = 로그인 (Figma 832:48657). 내비게이션 골격은 F-1 app-foundation에서 도입 예정 —
// 클릭 핸들러는 그때 연결한다.
@Composable
@Preview
fun App() {
    SikdorokTheme {
        LoginScreen()
    }
}
