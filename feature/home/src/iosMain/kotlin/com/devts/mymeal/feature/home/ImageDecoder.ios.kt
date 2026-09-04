package com.devts.mymeal.feature.home

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
actual fun decodeImageBitmap(path: String): ImageBitmap? {
    val data = NSData.dataWithContentsOfFile(path) ?: return null
    val size = data.length.toInt()
    if (size == 0) return null
    val bytes = ByteArray(size)
    bytes.usePinned { memcpy(it.addressOf(0), data.bytes, data.length) }
    return runCatching { Image.makeFromEncoded(bytes).toComposeImageBitmap() }.getOrNull()
}
