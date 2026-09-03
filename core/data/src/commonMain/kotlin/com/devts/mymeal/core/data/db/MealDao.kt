package com.devts.mymeal.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Transaction
    @Query("SELECT * FROM meal_entries WHERE meal_at >= :startInclusive AND meal_at < :endExclusive ORDER BY meal_at DESC")
    fun observeBetween(startInclusive: Long, endExclusive: Long): Flow<List<MealEntryWithItems>>

    @Transaction
    @Query("SELECT * FROM meal_entries WHERE id = :id")
    suspend fun get(id: String): MealEntryWithItems?

    @Upsert
    suspend fun upsertEntry(entry: MealEntryEntity)

    @Query("DELETE FROM meal_items WHERE entry_id = :entryId")
    suspend fun deleteItemsOf(entryId: String)

    @Insert
    suspend fun insertItems(items: List<MealItemEntity>)

    @Query("DELETE FROM meal_entries WHERE id = :id")
    suspend fun deleteEntry(id: String)

    // entry+items 단일 트랜잭션 교체 (technical-design §7 Consistency)
    @Transaction
    suspend fun upsertWithItems(entry: MealEntryEntity, items: List<MealItemEntity>) {
        upsertEntry(entry)
        deleteItemsOf(entry.id)
        insertItems(items)
    }
}
