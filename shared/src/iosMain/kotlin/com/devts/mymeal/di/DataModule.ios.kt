package com.devts.mymeal.di

import com.devts.mymeal.core.data.db.MymealDatabase
import com.devts.mymeal.core.data.db.mymealDatabase
import com.devts.mymeal.core.data.db.mymealDatabaseBuilder
import com.devts.mymeal.core.data.photo.PhotoStore
import com.devts.mymeal.core.data.photo.createPhotoStore
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformDataModule(): Module = module {
    single<MymealDatabase> { mymealDatabase(mymealDatabaseBuilder()) }
    single<PhotoStore> { createPhotoStore(documentsDirPath()) }
}

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
private fun documentsDirPath(): String =
    NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )?.path ?: error("iOS 문서 디렉터리를 찾을 수 없음")
