package com.devts.mymeal.core.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun mymealDatabaseBuilder(context: Context): RoomDatabase.Builder<MymealDatabase> =
    Room.databaseBuilder(
        context.applicationContext,
        MymealDatabase::class.java,
        context.getDatabasePath(MymealDatabase.FILE_NAME).absolutePath,
    )
