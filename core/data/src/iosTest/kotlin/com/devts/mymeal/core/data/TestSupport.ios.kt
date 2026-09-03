package com.devts.mymeal.core.data

import androidx.room.Room
import androidx.room.RoomDatabase
import com.devts.mymeal.core.data.db.MymealDatabase
import platform.Foundation.NSTemporaryDirectory
import kotlin.random.Random

actual fun testDatabaseBuilder(): RoomDatabase.Builder<MymealDatabase> =
    Room.databaseBuilder<MymealDatabase>(name = NSTemporaryDirectory() + "mymeal-test-${Random.nextInt()}.db")

actual fun testPhotoDir(): String = NSTemporaryDirectory() + "mymeal-photos-${Random.nextInt()}"
