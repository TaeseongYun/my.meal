package com.devts.mymeal.feature.home

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavGraphBuilder.homeDestination(onNavigateToRecord: () -> Unit) {
    composable<HomeRoute> {
        // 스텁 상태 공급 — HomeViewModel·실데이터 연결은 F-5 diary 소관.
        HomeScreen(state = remember { stubHomeUiState() }, onEditClick = onNavigateToRecord)
    }
}
