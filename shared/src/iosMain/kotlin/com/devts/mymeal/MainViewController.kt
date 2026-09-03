package com.devts.mymeal

import androidx.compose.ui.window.ComposeUIViewController
import com.devts.mymeal.di.initKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initKoin() // 재호출 안전 가드 내장 (KoinInit.kt)
    return ComposeUIViewController { App() }
}
