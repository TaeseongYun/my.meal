package com.devts.mymeal.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devts.mymeal.core.data.repository.MealRepository
import com.devts.mymeal.core.model.MealEntry
import com.devts.mymeal.core.model.MealType
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

class HomeViewModel(
    repository: MealRepository,
    private val timeZone: TimeZone = TimeZone.currentSystemDefault(),
    today: LocalDate = Clock.System.todayIn(timeZone),
) : ViewModel() {

    private val week = weekOf(today)

    val uiState: StateFlow<HomeUiState> =
        combine(week.map(repository::observeByDate)) { byDay ->
            mapToHomeUiState(today, week, byDay.toList(), timeZone)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = mapToHomeUiState(today, week, List(7) { emptyList() }, timeZone),
        )
}

/**
 * 순수 매핑 — 테스트 대상. 마크·메뉴 이모지는 기록의 대표 음식 이모지(food_emoji, v3) 우선,
 * 없으면 스텁 세트 결정적 랜덤 (사용자 결정 2026-09-03/04).
 */
internal fun mapToHomeUiState(
    today: LocalDate,
    week: List<LocalDate>,
    byDay: List<List<MealEntry>>,
    timeZone: TimeZone,
): HomeUiState {
    val todayIndex = week.indexOf(today)
    val todayEntries = byDay.getOrNull(todayIndex).orEmpty()
    return HomeUiState(
        title = "${today.month.ordinal + 1}월의 도시락",
        weekLabel = weekLabelOf(week),
        weekDays = week.mapIndexed { i, date ->
            WeekDay(
                dayNumber = date.day,
                isToday = date == today,
                markEmoji = dayMarkEmoji(byDay.getOrNull(i).orEmpty(), date.day),
            )
        },
        meals = CAROUSEL_TYPES.map { type ->
            val entry = todayEntries.filter { it.mealType == type }.maxByOrNull { it.mealAt } // 최신 1건
            MealSlotState(
                type = type,
                photoPath = entry?.photoPath,
                note = entry?.note,
                menuEmoji = entry?.let { it.foodEmoji ?: STUB_MARKS[it.id.hashCode().mod(STUB_MARKS.size)] },
                time = entry?.let { formatMealTime(it.mealAt, timeZone) },
            )
        },
    )
}

// 홈 캐러셀은 디자인(832:92613)대로 3끼만 — SNACK 기록은 캘린더 마크에만 반영
private val CAROUSEL_TYPES = listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER)

/** 마크 규칙: 대표 게시물 이모지 → 최신 기록의 이모지 → 스텁 세트(날짜 기반). 기록 없으면 null("?"). */
internal fun dayMarkEmoji(entries: List<MealEntry>, dayNumber: Int): String? {
    if (entries.isEmpty()) return null
    val representative = entries.filter { it.isRepresentative }.maxByOrNull { it.mealAt }?.foodEmoji
    val latestWithEmoji = entries.sortedByDescending { it.mealAt }.firstNotNullOfOrNull { it.foodEmoji }
    return representative ?: latestWithEmoji ?: STUB_MARKS[dayNumber % STUB_MARKS.size]
}

/** "오후 12:53" — 12시간제, 자정/정오는 12시로. */
internal fun formatMealTime(epochMs: Long, timeZone: TimeZone): String {
    val time = Instant.fromEpochMilliseconds(epochMs).toLocalDateTime(timeZone).time
    val meridiem = if (time.hour < 12) "오전" else "오후"
    val hour12 = (time.hour % 12).let { if (it == 0) 12 else it }
    return "$meridiem $hour12:${time.minute.toString().padStart(2, '0')}"
}
