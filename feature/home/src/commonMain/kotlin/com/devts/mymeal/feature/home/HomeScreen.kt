package com.devts.mymeal.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devts.mymeal.core.designsystem.SikdorokTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

// 디자인 근거: Figma 832:92613 (홈 화면). 토큰 외 색·수치는 디자이너 확인 항목으로
// design-manifest에 기록됨. 아이콘은 Canvas 근사 — 렌더 크롭 확보 시 교체 대상.
private val HomeDark = Color(0xFF413A31)       // FAB·폴라로이드 테두리·오늘 박스
private val DayBrown = Color(0xFF53422C)       // 날짜 숫자·캐러셀 화살표
private val UnrecordedGray = Color(0x85DBD6CA) // 미기록 "?" 박스 (α0.52)
private val PhotoFrameGray = Color(0xFFF2F2F2) // 사진 자리표시자 면
private val NoteLineBlack = Color(0x0F000000)  // 글귀 괘선 (α0.06)
private const val DimAlpha = 0.40f
private const val ArrowDimAlpha = 0.29f
private val DAY_HEADERS = listOf("일", "월", "화", "수", "목", "금", "토")

@Composable
fun HomeScreen(
    state: HomeUiState,
    onEditClick: () -> Unit = {},
    onTitleClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = SikdorokTheme.colors
    val s = SikdorokTheme.spacing

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(c.bg3)
            .safeContentPadding(),
    ) {
        Column(Modifier.fillMaxSize().padding(horizontal = s.s16)) {
            HomeTopBar(state.title, onTitleClick, onMenuClick, onSettingsClick)
            Spacer(Modifier.height(s.s12))
            WeekCalendarCard(state.weekLabel, state.weekDays)
            Spacer(Modifier.height(s.s16))
            MealPagerCard(state.meals, state.initialMealIndex, Modifier.weight(1f))
        }
        EditFab(
            onClick = onEditClick,
            modifier = Modifier.align(Alignment.BottomEnd).padding(s.s32),
        )
    }
}

@Composable
private fun HomeTopBar(
    title: String,
    onTitleClick: () -> Unit,
    onMenuClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val c = SikdorokTheme.colors
    val t = SikdorokTheme.typography
    val s = SikdorokTheme.spacing
    Row(
        modifier = Modifier.fillMaxWidth().height(44.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.clickable(onClick = onTitleClick),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title, style = t.h3, color = c.text4)
            Spacer(Modifier.width(s.s4))
            TriangleIcon(TriangleDirection.DOWN, DayBrown, Modifier.size(9.dp, 5.dp))
        }
        Spacer(Modifier.weight(1f))
        MenuIcon(HomeDark, Modifier.size(20.dp).clickable(onClick = onMenuClick))
        Spacer(Modifier.width(s.s12))
        SettingsIcon(HomeDark, Modifier.size(20.dp).clickable(onClick = onSettingsClick))
    }
}

@Composable
private fun WeekCalendarCard(weekLabel: String, days: List<WeekDay>) {
    val c = SikdorokTheme.colors
    val t = SikdorokTheme.typography
    val s = SikdorokTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(c.bg1)
            .padding(s.s16),
    ) {
        Text(weekLabel, style = t.h4, color = c.text4)
        Spacer(Modifier.height(s.s12))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            DAY_HEADERS.forEach { day ->
                Box(Modifier.width(26.dp), contentAlignment = Alignment.Center) {
                    Text(day, style = t.body1, color = c.text2)
                }
            }
        }
        Spacer(Modifier.height(s.s12))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            days.forEach { DayColumn(it) }
        }
    }
}

@Composable
private fun DayColumn(day: WeekDay) {
    val c = SikdorokTheme.colors
    val t = SikdorokTheme.typography
    Column(
        modifier = Modifier.width(26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        if (day.isToday) {
            Box(
                modifier = Modifier.size(22.dp).clip(RoundedCornerShape(3.dp)).background(HomeDark),
                contentAlignment = Alignment.Center,
            ) {
                Text("${day.dayNumber}", style = t.body2, color = c.bg1)
            }
        } else {
            Box(Modifier.size(22.dp), contentAlignment = Alignment.Center) {
                Text(
                    "${day.dayNumber}",
                    style = t.body2,
                    color = DayBrown,
                    modifier = Modifier.alpha(DimAlpha),
                )
            }
        }
        Box(Modifier.size(26.dp), contentAlignment = Alignment.Center) {
            if (day.markEmoji != null) {
                Text(day.markEmoji, fontSize = 20.sp)
            } else {
                Box(
                    modifier = Modifier.size(22.dp).clip(RoundedCornerShape(3.dp)).background(UnrecordedGray),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("?", style = t.body2, color = c.bg1)
                }
            }
        }
    }
}

@Composable
private fun MealPagerCard(meals: List<MealSlotState>, initialIndex: Int, modifier: Modifier = Modifier) {
    val c = SikdorokTheme.colors
    val t = SikdorokTheme.typography
    val s = SikdorokTheme.spacing
    val pagerState = rememberPagerState(initialPage = initialIndex) { meals.size }
    val scope = rememberCoroutineScope()

    Column(
        // 디자인상 카드 하단은 화면 밖으로 이어짐 — 위 모서리만 둥글게, 남은 높이 채움
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(c.bg1)
            .padding(horizontal = s.s20, vertical = s.s24),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("오늘의 도시락 🍱", style = t.h4, color = c.text4)
            Spacer(Modifier.weight(1f))
            val canPrev = pagerState.currentPage > 0
            val canNext = pagerState.currentPage < meals.lastIndex
            TriangleIcon(
                TriangleDirection.LEFT,
                DayBrown,
                Modifier
                    .size(24.dp)
                    .alpha(if (canPrev) 1f else ArrowDimAlpha)
                    .clickable(enabled = canPrev) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                    },
            )
            Text(
                meals[pagerState.currentPage].type.label,
                style = t.h4,
                color = c.text4,
                textAlign = TextAlign.Center,
            )
            TriangleIcon(
                TriangleDirection.RIGHT,
                DayBrown,
                Modifier
                    .size(24.dp)
                    .alpha(if (canNext) 1f else ArrowDimAlpha)
                    .clickable(enabled = canNext) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    },
            )
        }
        Spacer(Modifier.height(s.s24))
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
            MealPage(meals[page])
        }
    }
}

@Composable
private fun MealPage(slot: MealSlotState) {
    val c = SikdorokTheme.colors
    val t = SikdorokTheme.typography
    val s = SikdorokTheme.spacing
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        PhotoFrame(slot)
        Spacer(Modifier.height(s.s20))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("오늘의 메뉴", style = t.body2, color = c.text3)
            if (slot.menuEmoji != null) {
                Spacer(Modifier.width(s.s4))
                Text(slot.menuEmoji, fontSize = 14.sp)
            }
            Spacer(Modifier.weight(1f))
            if (slot.time != null) {
                Text(slot.time, style = t.body2, color = c.text3)
            }
        }
        Spacer(Modifier.height(s.s8))
        NoteArea(slot.note)
    }
}

@Composable
private fun PhotoFrame(slot: MealSlotState) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(199.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(PhotoFrameGray)
                .border(7.dp, HomeDark, RoundedCornerShape(9.dp)),
        ) {
            if (slot.photo != null) {
                Image(
                    painterResource(slot.photo),
                    contentDescription = "${slot.type.label} 도시락 사진",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
            // TODO(빈 화면 PNG): 미등록 자리표시자 — 사용자 제공 PNG 확보 시 교체
        }
        // 폴라로이드 하단 받침 (Figma Vector 61, 207x21 근사)
        Box(
            modifier = Modifier
                .size(width = 207.dp, height = 10.dp)
                .clip(RoundedCornerShape(bottomStart = 9.dp, bottomEnd = 9.dp))
                .background(HomeDark),
        )
    }
}

@Composable
private fun NoteArea(note: String?) {
    val c = SikdorokTheme.colors
    val t = SikdorokTheme.typography
    val s = SikdorokTheme.spacing
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(83.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(c.bg3)
            .padding(horizontal = s.s12),
    ) {
        Column(Modifier.fillMaxSize()) {
            Spacer(Modifier.height(18.dp))
            repeat(3) {
                HorizontalDivider(color = NoteLineBlack)
                Spacer(Modifier.height(25.dp))
            }
        }
        if (note != null) {
            Text(
                note,
                style = t.body2.copy(lineHeight = 26.sp),
                color = c.text4,
                maxLines = 3,
                modifier = Modifier.padding(top = 1.dp),
            )
        }
    }
}

@Composable
private fun EditFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val c = SikdorokTheme.colors
    Box(
        modifier = modifier
            .size(52.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(HomeDark)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        EditIcon(c.bg1, Modifier.size(24.dp))
    }
}

// ---- Canvas 아이콘 (렌더 크롭 확보 시 교체 후보) ----

private enum class TriangleDirection { DOWN, LEFT, RIGHT }

@Composable
private fun TriangleIcon(direction: TriangleDirection, color: Color, modifier: Modifier) {
    Canvas(modifier) {
        // 화살표 심볼은 아이콘 박스 중앙의 작은 삼각형 (Figma union 5x9 / 9x5)
        val w = size.width
        val h = size.height
        val tri = Path().apply {
            when (direction) {
                TriangleDirection.DOWN -> {
                    val tw = minOf(w, 9.dp.toPx()); val th = minOf(h, 5.dp.toPx())
                    moveTo((w - tw) / 2, (h - th) / 2)
                    lineTo((w + tw) / 2, (h - th) / 2)
                    lineTo(w / 2, (h + th) / 2)
                }
                TriangleDirection.LEFT -> {
                    val tw = minOf(w, 5.dp.toPx()); val th = minOf(h, 9.dp.toPx())
                    moveTo((w + tw) / 2, (h - th) / 2)
                    lineTo((w + tw) / 2, (h + th) / 2)
                    lineTo((w - tw) / 2, h / 2)
                }
                TriangleDirection.RIGHT -> {
                    val tw = minOf(w, 5.dp.toPx()); val th = minOf(h, 9.dp.toPx())
                    moveTo((w - tw) / 2, (h - th) / 2)
                    lineTo((w - tw) / 2, (h + th) / 2)
                    lineTo((w + tw) / 2, h / 2)
                }
            }
            close()
        }
        drawPath(tri, color)
    }
}

@Composable
private fun MenuIcon(color: Color, modifier: Modifier) {
    Canvas(modifier) {
        val stroke = 2.5.dp.toPx()
        val x = 2.dp.toPx()
        listOf(5f to 15f, 10f to 15f, 15f to 11f).forEach { (y, len) ->
            drawLine(
                color,
                Offset(x, y.dp.toPx()),
                Offset(x + len.dp.toPx(), y.dp.toPx()),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun SettingsIcon(color: Color, modifier: Modifier) {
    Canvas(modifier) {
        // 근사 기어: 링 + 톱니 8개
        val c = center
        val toothW = 3.dp.toPx()
        val toothH = 3.5.dp.toPx()
        repeat(8) { i ->
            rotate(i * 45f, pivot = c) {
                drawRoundRect(
                    color,
                    topLeft = Offset(c.x - toothW / 2, 1.dp.toPx()),
                    size = Size(toothW, toothH),
                    cornerRadius = CornerRadius(1.dp.toPx()),
                )
            }
        }
        drawCircle(color, radius = 6.dp.toPx(), center = c, style = Stroke(width = 3.dp.toPx()))
    }
}

@Composable
private fun EditIcon(color: Color, modifier: Modifier) {
    Canvas(modifier) {
        // 근사 연필: 45° 회전한 몸통 + 촉
        rotate(45f, pivot = center) {
            val bodyW = 7.dp.toPx()
            val left = center.x - bodyW / 2
            drawRoundRect(
                color,
                topLeft = Offset(left, 2.dp.toPx()),
                size = Size(bodyW, 13.dp.toPx()),
                cornerRadius = CornerRadius(1.5.dp.toPx()),
            )
            val tip = Path().apply {
                moveTo(left, 16.dp.toPx())
                lineTo(left + bodyW, 16.dp.toPx())
                lineTo(center.x, 21.dp.toPx())
                close()
            }
            drawPath(tip, color)
        }
    }
}
