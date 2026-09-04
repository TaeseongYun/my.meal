package com.devts.mymeal.feature.record

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

class RecordUiStateTest {

    @Test
    fun dateLabel_designSampleShape() {
        assertEquals("2023년 6월 2일 금요일", dateLabelOf(LocalDate(2023, 6, 2)))
    }

    @Test
    fun dateLabel_derivesWeekdayFromDate() {
        // 디자인 샘플(2023-06-04 "금요일")은 불일치 — 실제 요일(일요일) 파생을 고정
        assertEquals("2023년 6월 4일 일요일", dateLabelOf(LocalDate(2023, 6, 4)))
    }

    @Test
    fun timeLabel_designSample() {
        assertEquals("오후 12:53", timeLabelOf(LocalTime(12, 53)))
    }

    @Test
    fun timeLabel_boundaries() {
        assertEquals("오전 12:05", timeLabelOf(LocalTime(0, 5)))
        assertEquals("오전 9:07", timeLabelOf(LocalTime(9, 7)))
        assertEquals("오후 11:59", timeLabelOf(LocalTime(23, 59)))
    }

    @Test
    fun isEdit_falseForEmptyCreateState() {
        assertFalse(stubRecordUiState(LocalDateTime(2023, 6, 4, 12, 53)).isEdit)
    }

    @Test
    fun isEdit_trueWhenAnyContentExists() {
        val empty = stubRecordUiState(LocalDateTime(2023, 6, 4, 12, 53))
        assertTrue(empty.copy(memo = "김밥").isEdit)
        assertTrue(empty.copy(foodEmoji = "🍚").isEdit)
        assertTrue(empty.copy(isRepresentative = true).isEdit)
    }

    @Test
    fun stubState_defaults() {
        val state = stubRecordUiState(LocalDateTime(2023, 6, 4, 12, 53))
        assertEquals(RecordSlot.BREAKFAST, state.slot)
        assertNull(state.photo)
        assertNull(state.foodEmoji)
        assertEquals("", state.memo)
        assertEquals(LocalDate(2023, 6, 4), state.date)
        assertEquals(LocalTime(12, 53), state.time)
    }
}
