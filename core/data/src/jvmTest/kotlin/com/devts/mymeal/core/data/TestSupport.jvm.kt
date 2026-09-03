package com.devts.mymeal.core.data

import androidx.room.RoomDatabase
import com.devts.mymeal.core.data.db.MymealDatabase
import com.devts.mymeal.core.data.db.mymealDatabaseBuilder
import kotlin.io.path.createTempDirectory

actual fun testDatabaseBuilder(): RoomDatabase.Builder<MymealDatabase> =
    mymealDatabaseBuilder(createTempDirectory("mymeal-db").toFile())

actual fun testPhotoDir(): String = createTempDirectory("mymeal-photos").toString()
