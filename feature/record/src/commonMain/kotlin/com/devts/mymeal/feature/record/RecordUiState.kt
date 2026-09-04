package com.devts.mymeal.feature.record

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import com.devts.mymeal.core.model.MealType
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * 기록 생성/수정 화면 상태 (Figma 832:98315). 화면 구성 슬라이스 — 저장·사진·시간 선택과
 * 기존 기록 로드는 F-2 데이터 연결 소관이며, 그 전까지 [stubRecordUiState]가 빈 생성
 * 상태를 공급한다. 생성/수정은 같은 화면: 수정 = 채워진 상태 렌더링.
 */
@Immutable
data class RecordUiState(
    val date: LocalDate,
    val time: LocalTime,
    val slot: RecordSlot = RecordSlot.BREAKFAST, // 디자인 기본 선택 = 아침
    val photo: ImageBitmap? = null,              // 선택한 사진 미리보기, null = 도시락 일러스트 자리표시자
    val foodEmoji: String? = null,               // 대표 음식(단일 선택), null = "?" 타일
    val isRepresentative: Boolean = false,
    val memo: String = "",
)

/** 데이터 연결 전 임시 생성/수정 판별 — F-2에서 기록 id 존재 여부로 교체 (이 한 곳만) */
val RecordUiState.isEdit: Boolean
    get() = photo != null || foodEmoji != null || memo.isNotBlank() || isRepresentative

// 아침/점심/저녁 이모지는 home MealType과 동일 파생, 간식은 디자인(832:98315) 판독
enum class RecordSlot(val label: String) {
    BREAKFAST("☀️ 아침"),
    LUNCH("⛅ 점심"),
    DINNER("🌑 저녁"),
    SNACK("🍰 간식"),
}

// 이름 1:1 매핑 — MealType도 4종 (SNACK 확정 2026-09-04)
fun RecordSlot.toMealType(): MealType = MealType.valueOf(name)

/** "2023년 6월 4일 일요일" — 요일은 날짜에서 파생 (디자인 샘플의 "금요일"은 불일치, 매니페스트 참조) */
fun dateLabelOf(date: LocalDate): String =
    "${date.year}년 ${date.month.ordinal + 1}월 ${date.day}일 ${KOREAN_WEEKDAYS[date.dayOfWeek.ordinal]}"

// DayOfWeek.ordinal: MONDAY=0..SUNDAY=6 (home weekOf와 동일 파생)
private val KOREAN_WEEKDAYS = listOf("월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일")

/** "오후 12:53" — 12시간제, 시는 패딩 없음, 분만 2자리 */
fun timeLabelOf(time: LocalTime): String {
    val ampm = if (time.hour < 12) "오전" else "오후"
    val h = time.hour % 12
    val hour12 = if (h == 0) 12 else h
    return "$ampm $hour12:${time.minute.toString().padStart(2, '0')}"
}

/** 현재 시각 기준 빈 생성 상태. */
fun stubRecordUiState(
    now: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
): RecordUiState = RecordUiState(date = now.date, time = now.time)
