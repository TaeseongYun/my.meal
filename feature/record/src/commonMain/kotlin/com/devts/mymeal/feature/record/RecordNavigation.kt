package com.devts.mymeal.feature.record

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object RecordRoute

fun NavGraphBuilder.recordDestination(onBack: () -> Unit) {
    composable<RecordRoute> {
        RecordDestination(onBack)
    }
}

@Composable
private fun RecordDestination(onBack: () -> Unit) {
    // 스텁 상태 — RecordViewModel·저장/사진/시간 선택·기존 기록 로드는 F-2 데이터 연결 소관
    var state by remember { mutableStateOf(stubRecordUiState()) }
    RecordScreen(
        state = state,
        onBackClick = onBack,
        onSlotSelect = { state = state.copy(slot = it) },
        onFoodSelect = { state = state.copy(foodEmoji = it) },
        onRepresentativeToggle = { state = state.copy(isRepresentative = !state.isRepresentative) },
        onMemoChange = { state = state.copy(memo = it) },
    )
}
