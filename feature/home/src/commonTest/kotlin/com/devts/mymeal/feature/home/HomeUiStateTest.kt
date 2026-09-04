package com.devts.mymeal.feature.home

import com.devts.mymeal.core.model.MealType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

/** 기대값 출처: Figma 832:92613 (주별 캘린더 일~토, 캐러셀 아침/점심/저녁, 기본 저녁) */
class HomeUiStateTest {

    // T1: 주는 항상 일요일 시작 7일이고 오늘을 포함한다
    @Test
    fun weekOf_returnsSevenDaysStartingSunday() {
        val today = LocalDate(2026, 8, 31) // 월요일
        val week = weekOf(today)
        assertEquals(7, week.size)
        assertEquals(DayOfWeek.SUNDAY, week.first().dayOfWeek)
        assertEquals(DayOfWeek.SATURDAY, week.last().dayOfWeek)
        assertTrue(today in week)
    }

    // T2: 월 경계 주 — 8/30(일)~9/5(토)
    @Test
    fun weekOf_crossesMonthBoundary() {
        val week = weekOf(LocalDate(2026, 9, 4))
        assertEquals(LocalDate(2026, 8, 30), week.first())
        assertEquals(LocalDate(2026, 9, 5), week.last())
    }

    // T3: 연 경계 주 — 2026-12-27(일)~2027-01-02(토)
    @Test
    fun weekOf_crossesYearBoundary() {
        val week = weekOf(LocalDate(2027, 1, 1))
        assertEquals(LocalDate(2026, 12, 27), week.first())
        assertEquals(LocalDate(2027, 1, 2), week.last())
    }

    // T4: 오늘이 일요일이면 주의 첫날이 오늘
    @Test
    fun weekOf_todayOnSunday() {
        val sunday = LocalDate(2026, 8, 30)
        assertEquals(sunday, weekOf(sunday).first())
    }

    // T5: 주 라벨 — 목요일 기준 달·몇째 주
    @Test
    fun weekLabel_usesThursdayMonthAndOrdinal() {
        assertEquals("9월 첫째주", weekLabelOf(weekOf(LocalDate(2026, 9, 4))))   // 목=9/3
        assertEquals("8월 셋째주", weekLabelOf(weekOf(LocalDate(2026, 8, 20)))) // 목=8/20
    }

    // T6: 스텁 상태 — 7일, 오늘 1개, 끼니 3개 아침/점심/저녁 순, 기본 페이지=저녁
    @Test
    fun stubState_matchesDesignShape() {
        val state = stubHomeUiState(LocalDate(2026, 9, 4))
        assertEquals(7, state.weekDays.size)
        assertEquals(1, state.weekDays.count { it.isToday })
        assertEquals(
            listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER),
            state.meals.map { it.type },
        )
        assertEquals(MealType.DINNER, state.meals[state.initialMealIndex].type)
        assertEquals("9월의 도시락", state.title)
    }

    // T7: 스텁 기록 — 오늘 이전 3일에만 이모지 마크, 오늘은 미기록
    @Test
    fun stubState_marksThreeDaysBeforeToday() {
        val state = stubHomeUiState(LocalDate(2026, 9, 4)) // 주: 8/30~9/5, 마크: 9/1~9/3
        val marks = state.weekDays.map { it.markEmoji }
        assertEquals(listOf(null, null, "🥗", "🍚", "🍞", null, null), marks)
        assertNull(state.weekDays.first { it.isToday }.markEmoji)
    }
}
