package com.devts.mymeal.core.data.db

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
fun mymealDatabaseBuilder(): RoomDatabase.Builder<MymealDatabase> {
    val docDir = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )?.path ?: error("iOS 문서 디렉터리를 찾을 수 없음")
    return Room.databaseBuilder<MymealDatabase>(name = "$docDir/${MymealDatabase.FILE_NAME}")
}
