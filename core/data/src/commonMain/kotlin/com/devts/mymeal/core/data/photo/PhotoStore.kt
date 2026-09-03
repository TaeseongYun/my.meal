package com.devts.mymeal.core.data.photo

/** ADR-3: 앱 내부 photos/{entryId}.jpg. DB에는 경로만 (원본 Bitmap 저장 금지) */
interface PhotoStore {
    suspend fun save(entryId: String, bytes: ByteArray): String
    suspend fun delete(entryId: String)
    fun pathOf(entryId: String): String?
}

/** @param baseDir 플랫폼 앱 내부 저장 경로 (android: filesDir, iOS: Documents, jvm: 지정 경로) */
expect fun createPhotoStore(baseDir: String): PhotoStore
