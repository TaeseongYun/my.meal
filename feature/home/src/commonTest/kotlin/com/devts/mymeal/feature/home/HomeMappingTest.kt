package com.devts.mymeal.feature.home

import com.devts.mymeal.core.model.MealEntry
import com.devts.mymeal.core.model.MealType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class HomeMappingTest {

    private val tz = TimeZone.of("Asia/Seoul")
    private val today = LocalDate(2026, 9, 3) // 목요일
    private val week = weekOf(today)

    private fun at(date: LocalDate, hour: Int, minute: Int): Long =
        LocalDateTime(date.year, date.month, date.day, hour, minute).toInstant(tz).toEpochMilliseconds()

    private fun entry(id: String, mealAt: Long, type: MealType, note: String? = null, photoPath: String? = null) =
        MealEntry(id, mealAt, type, note, photoPath, 0L, 0L, emptyList())

    @Test
    fun recordedDay_getsDeterministicMark_unrecordedDay_getsNull() {
        val monday = week[1]
        val byDay = week.map { date ->
            if (date == monday) listOf(entry("e1", at(date, 12, 0), MealType.LUNCH)) else emptyList()
        }
        val state = mapToHomeUiState(today, week, byDay, tz)
        assertEquals(STUB_MARKS[monday.day % STUB_MARKS.size], state.weekDays[1].markEmoji)
        assertNull(state.weekDays[2].markEmoji)
    }

    @Test
    fun mealSlot_showsLatestEntryPerType() {
        val early = entry("early", at(today, 11, 30), MealType.LUNCH, note = "이전")
        val late = entry("late", at(today, 12, 53), MealType.LUNCH, note = "최신", photoPath = "/p/late.jpg")
        val byDay = week.map { if (it == today) listOf(early, late) else emptyList() }
        val slot = mapToHomeUiState(today, week, byDay, tz).meals[MealType.LUNCH.ordinal]
        assertEquals("최신", slot.note)
        assertEquals("/p/late.jpg", slot.photoPath)
        assertEquals("오후 12:53", slot.time)
    }

    @Test
    fun unrecordedSlot_isEmpty() {
        val byDay = week.map { emptyList<MealEntry>() }
        val slot = mapToHomeUiState(today, week, byDay, tz).meals[MealType.BREAKFAST.ordinal]
        assertNull(slot.photoPath)
        assertNull(slot.note)
        assertNull(slot.time)
        assertNull(slot.menuEmoji)
    }

    @Test
    fun formatMealTime_handlesMeridiemAndMidnightNoon() {
        assertEquals("오후 12:53", formatMealTime(at(today, 12, 53), tz))
        assertEquals("오전 12:05", formatMealTime(at(today, 0, 5), tz))
        assertEquals("오전 9:07", formatMealTime(at(today, 9, 7), tz))
        assertEquals("오후 11:59", formatMealTime(at(today, 23, 59), tz))
    }

    @Test
    fun snackEntry_marksCalendar_butKeepsCarouselAtThreeMeals() {
        val byDay = week.map { if (it == today) listOf(entry("s1", at(today, 15, 0), MealType.SNACK)) else emptyList() }
        val state = mapToHomeUiState(today, week, byDay, tz)
        assertEquals(3, state.meals.size)
        assertEquals(listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER), state.meals.map { it.type })
        assertEquals(STUB_MARKS[today.day % STUB_MARKS.size], state.weekDays[week.indexOf(today)].markEmoji)
        state.meals.forEach { assertNull(it.note) }
    }

    @Test
    fun title_and_weekLabel_followToday() {
        val state = mapToHomeUiState(today, week, week.map { emptyList() }, tz)
        assertEquals("9월의 도시락", state.title)
        assertEquals(weekLabelOf(week), state.weekLabel)
    }
}
