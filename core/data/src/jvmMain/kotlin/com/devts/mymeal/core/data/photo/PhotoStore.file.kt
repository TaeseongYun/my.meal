package com.devts.mymeal.core.data.photo

import java.io.File

// ponytail: android/jvm 동일 구현 복제 — 3번째 중복 시 공용 소스셋으로 승격
private class FilePhotoStore(private val baseDir: File) : PhotoStore {
    private fun fileOf(entryId: String) = File(baseDir, "photos/$entryId.jpg")

    override suspend fun save(entryId: String, bytes: ByteArray): String {
        val f = fileOf(entryId)
        f.parentFile?.mkdirs()
        f.writeBytes(bytes)
        return f.absolutePath
    }

    override suspend fun delete(entryId: String) {
        fileOf(entryId).delete()
    }

    override fun pathOf(entryId: String): String? =
        fileOf(entryId).takeIf { it.exists() }?.absolutePath
}

actual fun createPhotoStore(baseDir: String): PhotoStore = FilePhotoStore(File(baseDir))
