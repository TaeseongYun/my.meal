package com.devts.mymeal.feature.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devts.mymeal.core.data.photo.PhotoStore
import com.devts.mymeal.core.data.repository.MealRepository
import com.devts.mymeal.core.model.MealEntry
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

sealed interface RecordAction {
    data class SelectSlot(val slot: RecordSlot) : RecordAction
    data class SelectFood(val emoji: String?) : RecordAction
    data object ToggleRepresentative : RecordAction
    data class EditMemo(val memo: String) : RecordAction
    data class SetTime(val time: LocalTime) : RecordAction
    data class SetPhoto(val bytes: ByteArray) : RecordAction
    data object Save : RecordAction
}

sealed interface RecordEffect {
    data object Saved : RecordEffect
}

class RecordViewModel(
    private val repository: MealRepository,
    private val photoStore: PhotoStore,
    private val timeZone: TimeZone = TimeZone.currentSystemDefault(),
    private val clock: Clock = Clock.System,
    private val decodePhoto: (ByteArray) -> androidx.compose.ui.graphics.ImageBitmap? = ::decodeImageBitmap, // 호스트 테스트 주입점
) : ViewModel() {

    private var photoBytes: ByteArray? = null
    private var saving = false

    private val _uiState = MutableStateFlow(stubRecordUiState(clock.now().toLocalDateTime(timeZone)))
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    private val _effects = Channel<RecordEffect>(Channel.BUFFERED)
    val effects: Flow<RecordEffect> = _effects.receiveAsFlow()

    fun onAction(action: RecordAction) {
        when (action) {
            is RecordAction.SelectSlot -> _uiState.update { it.copy(slot = action.slot) }
            is RecordAction.SelectFood -> _uiState.update { it.copy(foodEmoji = action.emoji) }
            RecordAction.ToggleRepresentative -> _uiState.update { it.copy(isRepresentative = !it.isRepresentative) }
            is RecordAction.EditMemo -> _uiState.update { it.copy(memo = action.memo) }
            is RecordAction.SetTime -> _uiState.update { it.copy(time = action.time) }
            is RecordAction.SetPhoto -> {
                photoBytes = action.bytes
                _uiState.update { it.copy(photo = decodePhoto(action.bytes)) }
            }
            RecordAction.Save -> save()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun save() {
        if (saving) return // 저장 버튼 연타 방지
        saving = true
        viewModelScope.launch {
            val s = _uiState.value
            val id = Uuid.random().toString()
            val path = photoBytes?.let { bytes ->
                runCatching { photoStore.save(id, bytes) }.getOrNull() // 사진 실패는 기록 저장을 막지 않음
            }
            val now = clock.now().toEpochMilliseconds()
            val entry = MealEntry(
                id = id,
                mealAt = LocalDateTime(s.date, s.time).toInstant(timeZone).toEpochMilliseconds(),
                mealType = s.slot.toMealType(),
                note = s.memo.ifBlank { null },
                photoPath = path,
                createdAt = now,
                updatedAt = now,
                items = emptyList(), // 음식 항목 상세 입력은 디자인에 없음 — F-4(분석) 시 재검토
                foodEmoji = s.foodEmoji,
                isRepresentative = s.isRepresentative,
            )
            runCatching { repository.upsert(entry) }
                .onSuccess { _effects.send(RecordEffect.Saved) }
                .onFailure {
                    path?.let { runCatching { photoStore.delete(id) } } // ADR-R3: 고아 사진 롤백
                    saving = false
                }
        }
    }
}
