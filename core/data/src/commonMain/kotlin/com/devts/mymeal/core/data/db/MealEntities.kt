package com.devts.mymeal.core.data.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

// 스키마 v2 — technical-design.md §4 + diary ADR-D1(meal_type). sync/soft-delete 필드 금지(F-6 소관)
@Entity(tableName = "meal_entries", indices = [Index("meal_at")])
data class MealEntryEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "meal_at") val mealAt: Long,
    @ColumnInfo(name = "meal_type", defaultValue = "DINNER") val mealType: String,
    val note: String?,
    @ColumnInfo(name = "photo_path") val photoPath: String?,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
)

@Entity(
    tableName = "meal_items",
    indices = [Index("entry_id")],
    foreignKeys = [ForeignKey(
        entity = MealEntryEntity::class,
        parentColumns = ["id"],
        childColumns = ["entry_id"],
        onDelete = ForeignKey.CASCADE,
    )],
)
data class MealItemEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "entry_id") val entryId: String,
    val name: String,
    @ColumnInfo(name = "amount_label") val amountLabel: String?,
    @ColumnInfo(name = "estimated_kcal") val estimatedKcal: Int?,
    @ColumnInfo(name = "order_index") val orderIndex: Int,
)

data class MealEntryWithItems(
    @Embedded val entry: MealEntryEntity,
    @Relation(parentColumn = "id", entityColumn = "entry_id") val items: List<MealItemEntity>,
)
