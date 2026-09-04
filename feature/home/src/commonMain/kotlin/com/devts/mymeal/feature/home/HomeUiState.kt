package com.devts.mymeal.feature.home

import androidx.compose.runtime.Immutable
import com.devts.mymeal.core.model.MealType
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

/**
 * 홈 화면 상태 (Figma 832:92613). 실데이터 매핑은 HomeViewModel(F-5),
 * [stubHomeUiState]는 프리뷰·테스트 전용.
 */
@Immutable
data class HomeUiState(
    val title: String,              // "6월의 도시락"
    val weekLabel: String,          // "6월 둘째주"
    val weekDays: List<WeekDay>,    // 항상 7개, 일~토
    val meals: List<MealSlotState>, // 항상 3개, 아침/점심/저녁 순
    val initialMealIndex: Int = 2,  // 디자인 기본 노출 = 저녁
)

@Immutable
data class WeekDay(
    val dayNumber: Int,
    val isToday: Boolean,
    val markEmoji: String?, // 기록됨 = 스텁 세트 결정적 랜덤, null = 미기록 "?" 박스
)

// 아침/점심 이모지는 생성하기 프레임 렌더 판독(디자이너 확인 항목) — 홈 디자인엔 저녁만 존재
val MealType.label: String
    get() = when (this) {
        MealType.BREAKFAST -> "☀️ 아침"
        MealType.LUNCH -> "⛅ 점심"
        MealType.DINNER -> "🌑 저녁"
        MealType.SNACK -> "🍰 간식"
    }

@Immutable
data class MealSlotState(
    val type: MealType,
    val photoPath: String?,       // null = 미등록(자리표시자 — 빈 화면 PNG 제공 시 교체)
    val note: String?,
    val menuEmoji: String?,       // "오늘의 메뉴" 옆 대표 이모지
    val time: String?,            // 포맷된 문자열 ("오후 12:53")
)

/** 일요일 시작, [today]가 속한 주의 7일. */
fun weekOf(today: LocalDate): List<LocalDate> {
    // DayOfWeek.ordinal: MONDAY=0..SUNDAY=6 → 일요일로부터의 경과일
    val daysFromSunday = (today.dayOfWeek.ordinal + 1) % 7
    val sunday = today.minus(daysFromSunday, DateTimeUnit.DAY)
    return (0..6).map { sunday.plus(it, DateTimeUnit.DAY) }
}

/**
 * "6월 둘째주" 라벨. 주의 목요일(과반 요일)이 속한 달과 그 달의 몇째 주인지로 결정.
 * ponytail: 디자인 샘플 데이터로는 규칙 역산 불가 — 단순 규칙 채택, 디자이너 확인 항목.
 */
fun weekLabelOf(week: List<LocalDate>): String {
    val thursday = week[4]
    val ordinal = (thursday.day - 1) / 7
    return "${thursday.month.ordinal + 1}월 ${WEEK_ORDINALS[ordinal]}주"
}

private val WEEK_ORDINALS = listOf("첫째", "둘째", "셋째", "넷째", "다섯째")

// 스텁 이모지 세트 — 사용자 결정(2026-09-03): 실데이터도 이 세트에서 결정적 랜덤 표시
internal val STUB_MARKS = listOf("🥗", "🍚", "🍞")

/** 실제 오늘 기준 주 + 디자인과 동일한 스텁 기록 상태 — 프리뷰·테스트 전용. */
fun stubHomeUiState(
    today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
): HomeUiState {
    val week = weekOf(today)
    val recorded = STUB_MARKS.indices.associate { today.minus(3 - it, DateTimeUnit.DAY) to STUB_MARKS[it] }
    return HomeUiState(
        title = "${today.month.ordinal + 1}월의 도시락",
        weekLabel = weekLabelOf(week),
        weekDays = week.map { WeekDay(it.day, isToday = it == today, markEmoji = recorded[it]) },
        meals = listOf(
            MealSlotState(MealType.BREAKFAST, photoPath = null, note = null, menuEmoji = null, time = null),
            MealSlotState(MealType.LUNCH, photoPath = null, note = null, menuEmoji = null, time = null),
            MealSlotState(
                type = MealType.DINNER,
                photoPath = null,
                // 디자인의 한글 입숨 글귀 (FAB에 가린 꼬리는 근사 복원)
                note = "기관과 품었기 끓는 위하여 그들은 낙원을 알는 싶이 때문이다. " +
                    "이상은 설산에서 영락과 시들고 그림자는 스며들어 이상이 그들은 칼이 되었다.",
                menuEmoji = "🍚",
                time = "오후 12:53",
            ),
        ),
    )
}
