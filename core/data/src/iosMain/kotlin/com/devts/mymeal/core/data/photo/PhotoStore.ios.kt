package com.devts.mymeal.core.data.photo

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.dataWithBytes
import platform.Foundation.writeToFile

@OptIn(ExperimentalForeignApi::class)
private class IosPhotoStore(private val baseDir: String) : PhotoStore {
    private fun pathFor(entryId: String) = "$baseDir/photos/$entryId.jpg"

    override suspend fun save(entryId: String, bytes: ByteArray): String {
        val path = pathFor(entryId)
        NSFileManager.defaultManager.createDirectoryAtPath("$baseDir/photos", true, null, null)
        val data: NSData = if (bytes.isEmpty()) NSData() else bytes.usePinned { pinned ->
            NSData.dataWithBytes(pinned.addressOf(0), bytes.size.toULong())
        }
        data.writeToFile(path, true)
        return path
    }

    override suspend fun delete(entryId: String) {
        NSFileManager.defaultManager.removeItemAtPath(pathFor(entryId), null)
    }

    override fun pathOf(entryId: String): String? =
        pathFor(entryId).takeIf { NSFileManager.defaultManager.fileExistsAtPath(it) }
}

actual fun createPhotoStore(baseDir: String): PhotoStore = IosPhotoStore(baseDir)
