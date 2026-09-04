package com.devts.mymeal.feature.record

import androidx.compose.runtime.Composable

/** 촬영/앨범 진입점 — 결과는 JPEG/원본 바이트 (ADR-R1: expect/actual 직접, 서드파티 0). */
class PhotoPicker(
    val launchCamera: () -> Unit,
    val launchGallery: () -> Unit,
)

@Composable
expect fun rememberPhotoPicker(onPicked: (ByteArray) -> Unit): PhotoPicker
