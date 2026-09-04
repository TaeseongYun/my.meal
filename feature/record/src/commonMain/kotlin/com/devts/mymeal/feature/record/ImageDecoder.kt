package com.devts.mymeal.feature.record

import androidx.compose.ui.graphics.ImageBitmap

// 선택한 사진 바이트 → 미리보기 비트맵.
// ponytail: home의 path 버전에 이은 2번째 중복 — 3번째 필요 시 공용 소스셋으로 승격
expect fun decodeImageBitmap(bytes: ByteArray): ImageBitmap?
