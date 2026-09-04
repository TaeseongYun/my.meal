package com.devts.mymeal.feature.home

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun decodeImageBitmap(path: String): ImageBitmap? =
    BitmapFactory.decodeFile(path)?.asImageBitmap()
