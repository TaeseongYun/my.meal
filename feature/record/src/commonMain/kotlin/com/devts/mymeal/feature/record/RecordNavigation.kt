package com.devts.mymeal.feature.record

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object RecordRoute

fun NavGraphBuilder.recordDestination(onBack: () -> Unit) {
    composable<RecordRoute> {
        RecordDestination(onBack)
    }
}

@Composable
private fun RecordDestination(onBack: () -> Unit) {
    val viewModel = koinViewModel<RecordViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val photoPicker = rememberPhotoPicker { viewModel.onAction(RecordAction.SetPhoto(it)) }
    var showPhotoSource by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                RecordEffect.Saved -> onBack() // 저장 후 홈 복귀 — 홈은 Flow로 자동 갱신
            }
        }
    }

    RecordScreen(
        state = state,
        onBackClick = onBack,
        onSaveClick = { viewModel.onAction(RecordAction.Save) },
        onTimeClick = { showTimePicker = true },
        onCameraClick = { showPhotoSource = true },
        onSlotSelect = { viewModel.onAction(RecordAction.SelectSlot(it)) },
        onFoodSelect = { viewModel.onAction(RecordAction.SelectFood(it)) },
        onRepresentativeToggle = { viewModel.onAction(RecordAction.ToggleRepresentative) },
        onMemoChange = { viewModel.onAction(RecordAction.EditMemo(it)) },
    )

    if (showPhotoSource) {
        PhotoSourceDialog(
            onCamera = { showPhotoSource = false; photoPicker.launchCamera() },
            onGallery = { showPhotoSource = false; photoPicker.launchGallery() },
            onDismiss = { showPhotoSource = false },
        )
    }
    if (showTimePicker) {
        RecordTimePickerDialog(
            initial = state.time,
            onConfirm = { viewModel.onAction(RecordAction.SetTime(it)); showTimePicker = false },
            onDismiss = { showTimePicker = false },
        )
    }
}

@Composable
private fun PhotoSourceDialog(onCamera: () -> Unit, onGallery: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("사진 추가") },
        confirmButton = { TextButton(onClick = onCamera) { Text("촬영") } },
        dismissButton = { TextButton(onClick = onGallery) { Text("앨범에서 선택") } },
    )
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun RecordTimePickerDialog(initial: LocalTime, onConfirm: (LocalTime) -> Unit, onDismiss: () -> Unit) {
    val pickerState = rememberTimePickerState(initialHour = initial.hour, initialMinute = initial.minute, is24Hour = false)
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { TimePicker(state = pickerState) },
        confirmButton = {
            TextButton(onClick = { onConfirm(LocalTime(pickerState.hour, pickerState.minute)) }) { Text("확인") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소") } },
    )
}
