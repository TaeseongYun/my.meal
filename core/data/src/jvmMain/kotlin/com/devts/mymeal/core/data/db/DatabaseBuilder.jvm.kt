package com.devts.mymeal.core.data.db

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

fun mymealDatabaseBuilder(directory: File): RoomDatabase.Builder<MymealDatabase> =
    Room.databaseBuilder<MymealDatabase>(name = File(directory, MymealDatabase.FILE_NAME).absolutePath)
