package com.devts.mymeal.feature.home

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object HomeRoute

fun NavGraphBuilder.homeDestination(onNavigateToRecord: () -> Unit) {
    composable<HomeRoute> {
        val viewModel = koinViewModel<HomeViewModel>()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        HomeScreen(state = state, onEditClick = onNavigateToRecord)
    }
}
