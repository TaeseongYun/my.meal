package com.devts.mymeal.core.data.repository

import com.devts.mymeal.core.data.db.MealDao
import com.devts.mymeal.core.data.db.MealEntryEntity
import com.devts.mymeal.core.data.db.MealEntryWithItems
import com.devts.mymeal.core.data.db.MealItemEntity
import com.devts.mymeal.core.data.photo.PhotoStore
import com.devts.mymeal.core.model.MealEntry
import com.devts.mymeal.core.model.MealItem
import com.devts.mymeal.core.model.MealType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus

class RoomMealRepository(
    private val dao: MealDao,
    private val photoStore: PhotoStore,
    private val timeZone: () -> TimeZone = { TimeZone.currentSystemDefault() },
) : MealRepository {

    override fun observeByDate(date: LocalDate): Flow<List<MealEntry>> {
        val tz = timeZone()
        val start = date.atStartOfDayIn(tz).toEpochMilliseconds()
        val end = date.plus(1, DateTimeUnit.DAY).atStartOfDayIn(tz).toEpochMilliseconds()
        return dao.observeBetween(start, end).map { rows -> rows.map { it.toModel() } }
    }

    override suspend fun get(id: String): MealEntry? = dao.get(id)?.toModel()

    override suspend fun upsert(entry: MealEntry) =
        dao.upsertWithItems(entry.toEntity(), entry.items.map { it.toEntity(entry.id) })

    override suspend fun delete(id: String) {
        dao.deleteEntry(id) // FK CASCADE로 items 제거
        photoStore.delete(id) // 실패해도 무롤백 — 고아 정리 대상 (ADR-3)
    }
}

private fun MealEntryWithItems.toModel() = MealEntry(
    id = entry.id,
    mealAt = entry.mealAt,
    mealType = MealType.entries.firstOrNull { it.name == entry.mealType } ?: MealType.DINNER,
    note = entry.note,
    photoPath = entry.photoPath,
    createdAt = entry.createdAt,
    updatedAt = entry.updatedAt,
    items = items.sortedBy { it.orderIndex }.map { MealItem(it.id, it.name, it.amountLabel, it.estimatedKcal, it.orderIndex) },
)

private fun MealEntry.toEntity() = MealEntryEntity(id, mealAt, mealType.name, note, photoPath, createdAt, updatedAt)
private fun MealItem.toEntity(entryId: String) = MealItemEntity(id, entryId, name, amountLabel, estimatedKcal, orderIndex)
