package com.devts.mymeal

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.devts.mymeal.core.designsystem.SikdorokTheme
import com.devts.mymeal.core.designsystem.StyleGuideScreen

// 디자인 시스템 육안 확인용 구성. F-1 app-foundation에서 실제 앱 골격으로 교체된다.
@Composable
@Preview
fun App() {
    SikdorokTheme {
        StyleGuideScreen()
    }
}
