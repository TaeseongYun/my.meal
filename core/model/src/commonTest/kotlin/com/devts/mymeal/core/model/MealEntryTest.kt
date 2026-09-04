package com.devts.mymeal.core.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MealEntryTest {
    private fun entry(items: List<MealItem>) =
        MealEntry("e1", 0L, MealType.DINNER, null, null, 0L, 0L, items)
    private fun item(kcal: Int?, idx: Int = 0) =
        MealItem("i$idx", "밥", null, kcal, idx)

    // T1: 전 항목 kcal / 일부 null / 전부 null
    @Test fun total_sums_all() = assertEquals(700, entry(listOf(item(300, 0), item(400, 1))).totalEstimatedKcal())
    @Test fun total_ignores_null_items() = assertEquals(300, entry(listOf(item(300, 0), item(null, 1))).totalEstimatedKcal())
    @Test fun total_null_when_no_kcal() = assertNull(entry(listOf(item(null, 0))).totalEstimatedKcal())
    @Test fun total_null_when_empty() = assertNull(entry(emptyList()).totalEstimatedKcal())
}
