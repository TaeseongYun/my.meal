package com.devts.mymeal.di

import com.devts.mymeal.core.data.db.MymealDatabase
import com.devts.mymeal.core.data.db.mymealDatabase
import com.devts.mymeal.core.data.db.mymealDatabaseBuilder
import com.devts.mymeal.core.data.photo.PhotoStore
import com.devts.mymeal.core.data.photo.createPhotoStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformDataModule(): Module = module {
    single<MymealDatabase> { mymealDatabase(mymealDatabaseBuilder(androidContext())) }
    single<PhotoStore> { createPhotoStore(androidContext().filesDir.absolutePath) }
}
