package com.devts.mymeal.feature.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute

fun NavGraphBuilder.settingsDestination(onBack: () -> Unit) {
    composable<SettingsRoute> {
        // 화면 구성 슬라이스 — 계정 정보·약관 링크·업데이트 확인·로그아웃/탈퇴 연결은 F-7에서 ViewModel로 교체.
        SettingsScreen(state = stubSettingsUiState(), onBackClick = onBack)
    }
}
