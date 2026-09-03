package com.devts.mymeal.core.data

import androidx.room.RoomDatabase
import com.devts.mymeal.core.data.db.MymealDatabase

/** 임시 디렉터리 기반 테스트 자원 — 플랫폼별 actual (jvm: temp dir, iOS: NSTemporaryDirectory) */
expect fun testDatabaseBuilder(): RoomDatabase.Builder<MymealDatabase>
expect fun testPhotoDir(): String
