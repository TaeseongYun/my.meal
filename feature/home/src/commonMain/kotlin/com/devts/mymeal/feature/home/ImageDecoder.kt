package com.devts.mymeal.feature.home

import androidx.compose.ui.graphics.ImageBitmap

// 로컬 사진 파일 → ImageBitmap. 서드파티(coil 등) 없이 플랫폼 디코더 사용 (ADR-D3).
// ponytail: 캐시 없음 — remember(photoPath)로 충분, 목록 스크롤 생기면 재검토
expect fun decodeImageBitmap(path: String): ImageBitmap?
