package com.devts.mymeal.core.data.db

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [MealEntryEntity::class, MealItemEntity::class],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2), // v2: meal_entries.meal_type (기본 DINNER)
        AutoMigration(from = 2, to = 3), // v3: food_emoji(NULL)·is_representative(기본 0)
    ],
)
@ConstructedBy(MymealDatabaseConstructor::class)
abstract class MymealDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao

    companion object {
        const val FILE_NAME = "mymeal.db"
    }
}

// Room KMP 규약: actual은 컴파일러가 생성
@Suppress("KotlinNoActualForExpect", "NO_ACTUAL_FOR_EXPECT")
expect object MymealDatabaseConstructor : RoomDatabaseConstructor<MymealDatabase> {
    override fun initialize(): MymealDatabase
}

/** ADR-2: 번들 SQLite 드라이버로 양 플랫폼 동일 동작 */
fun mymealDatabase(builder: RoomDatabase.Builder<MymealDatabase>): MymealDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
