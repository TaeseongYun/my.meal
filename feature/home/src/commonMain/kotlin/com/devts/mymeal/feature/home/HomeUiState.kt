package com.devts.mymeal.feature.home

import androidx.compose.runtime.Immutable
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.DrawableResource

import com.devts.mymeal.feature.home.generated.resources.Res
import com.devts.mymeal.feature.home.generated.resources.sample_meal_photo

/**
 * 홈 화면 상태 (Figma 832:92613). 화면 구성 슬라이스 — 기록 데이터는 F-2/F-5에서
 * 연결하며, 그 전까지 [stubHomeUiState]가 실제 오늘 기준 주 + 스텁 기록을 공급한다.
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
    val markEmoji: String?, // 기록됨 = 대표 음식 이모지, null = 미기록 "?" 박스
)

// 아침/점심 이모지는 생성하기 프레임 렌더 판독(디자이너 확인 항목) — 홈 디자인엔 저녁만 존재
enum class MealType(val label: String) {
    BREAKFAST("☀️ 아침"),
    LUNCH("⛅ 점심"),
    DINNER("🌑 저녁"),
}

@Immutable
data class MealSlotState(
    val type: MealType,
    val photo: DrawableResource?, // null = 미등록(자리표시자 — 빈 화면 PNG 제공 시 교체)
    val note: String?,
    val menuEmoji: String?,       // "오늘의 메뉴" 옆 대표 이모지
    val time: String?,            // 화면 구성 범위: 포맷된 문자열 ("오후 12:53")
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

private val STUB_MARKS = listOf("🥗", "🍚", "🍞") // 디자인 스텁: 오늘 이전 3일 기록

/** 실제 오늘 기준 주 + 디자인과 동일한 스텁 기록(저녁만 등록) 상태. */
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
            MealSlotState(MealType.BREAKFAST, photo = null, note = null, menuEmoji = null, time = null),
            MealSlotState(MealType.LUNCH, photo = null, note = null, menuEmoji = null, time = null),
            MealSlotState(
                type = MealType.DINNER,
                photo = Res.drawable.sample_meal_photo,
                // 디자인의 한글 입숨 글귀 (FAB에 가린 꼬리는 근사 복원)
                note = "기관과 품었기 끓는 위하여 그들은 낙원을 알는 싶이 때문이다. " +
                    "이상은 설산에서 영락과 시들고 그림자는 스며들어 이상이 그들은 칼이 되었다.",
                menuEmoji = "🍚",
                time = "오후 12:53",
            ),
        ),
    )
}
