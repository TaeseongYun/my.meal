package com.devts.mymeal.logging

import co.touchlab.kermit.Logger

// 태그 규약 "Sikdorok". 사진 경로·기록 내용·계정 ID는 로그 금지 (CTX Constraints).
fun initLogging() {
    Logger.setTag("Sikdorok")
}
