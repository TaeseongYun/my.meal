package com.devts.mymeal.feature.record

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devts.mymeal.core.designsystem.SikdorokTheme
import com.devts.mymeal.feature.record.generated.resources.Res
import com.devts.mymeal.feature.record.generated.resources.lunchbox_placeholder
import org.jetbrains.compose.resources.painterResource

// 디자인 근거: Figma 832:98315 (기록 생성/수정). 토큰 외 색·수치는 디자이너 확인 항목으로
// design-manifest에 기록됨. 아이콘은 Canvas 근사 — 렌더 크롭 확보 시 교체 대상.
private val RecordDark = Color(0xFF413A31)     // 선택 칩·도시락 프레임 (home HomeDark와 동일 실측)
private val AccentBrown = Color(0xFF53422C)    // 뒤로가기·저장·시간 셰브런 (home DayBrown과 동일 실측)
private val CameraGray = Color(0xFFB6B2AC)     // 카메라 버튼 면 (렌더 실측)
private val UnselectedGray = Color(0x85DBD6CA) // "?" 타일 (α0.52, home 미기록 박스와 동일)
private val MemoLineBlack = Color(0x0F000000)  // 메모 괘선 (α0.06, home 글귀 괘선과 동일)
private val FOOD_EMOJIS = listOf("🍚", "🍜", "🥗", "🍖", "🍞", "🍔", "🍣", "🍰")

@Composable
fun RecordScreen(
    state: RecordUiState,
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onTimeClick: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onSlotSelect: (RecordSlot) -> Unit = {},
    onFoodSelect: (String?) -> Unit = {},
    onRepresentativeToggle: () -> Unit = {},
    onMemoChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = SikdorokTheme.colors
    val s = SikdorokTheme.spacing

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(c.bg1)
            .safeContentPadding(),
    ) {
        RecordTopBar(onBackClick, onSaveClick, Modifier.padding(horizontal = s.s16))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = s.s16),
        ) {
            Spacer(Modifier.height(s.s24))
            DateTimeRow(dateLabelOf(state.date), timeLabelOf(state.time), onTimeClick)
            Spacer(Modifier.height(s.s20))
            MealSlotChips(state.slot, onSlotSelect)
            Spacer(Modifier.height(s.s40))
            LunchboxPhotoFrame(
                photo = state.photo,
                onCameraClick = onCameraClick,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(s.s40))
            FoodEmojiRow(state.foodEmoji, onFoodSelect)
            Spacer(Modifier.height(s.s16))
            RepresentativeCheckRow(state.isRepresentative, onRepresentativeToggle)
            Spacer(Modifier.height(s.s16))
            MemoField(state.memo, onMemoChange)
            Spacer(Modifier.height(s.s24))
        }
    }
}

@Composable
private fun RecordTopBar(onBackClick: () -> Unit, onSaveClick: () -> Unit, modifier: Modifier = Modifier) {
    val t = SikdorokTheme.typography
    Row(
        modifier = modifier.fillMaxWidth().height(44.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BackArrowIcon(
            AccentBrown,
            Modifier
                .size(24.dp)
                .clickable(onClick = onBackClick)
                .semantics { contentDescription = "뒤로가기" },
        )
        Spacer(Modifier.weight(1f))
        Text(
            "저장",
            style = t.h3,
            color = AccentBrown,
            modifier = Modifier.clickable(onClick = onSaveClick),
        )
    }
}

@Composable
private fun DateTimeRow(dateLabel: String, timeLabel: String, onTimeClick: () -> Unit) {
    val c = SikdorokTheme.colors
    val t = SikdorokTheme.typography
    val s = SikdorokTheme.spacing
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(dateLabel, style = t.h3, color = c.text3)
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(c.bg3)
                .clickable(onClick = onTimeClick)
                .padding(horizontal = s.s12, vertical = s.s8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(timeLabel, style = t.body1, color = c.text4)
            Spacer(Modifier.width(s.s8))
            ChevronRightIcon(AccentBrown, Modifier.size(width = 5.dp, height = 9.dp))
        }
    }
}

@Composable
private fun MealSlotChips(selected: RecordSlot, onSelect: (RecordSlot) -> Unit) {
    val s = SikdorokTheme.spacing
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(s.s8)) {
        RecordSlot.entries.forEach { slot ->
            SlotChip(
                label = slot.label,
                isSelected = slot == selected,
                onClick = { onSelect(slot) },
            )
        }
    }
}

@Composable
private fun RowScope.SlotChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val c = SikdorokTheme.colors
    val t = SikdorokTheme.typography
    Box(
        modifier = Modifier
            .weight(1f)
            .height(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) RecordDark else c.bg3)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = t.body1, color = if (isSelected) c.bg1 else c.text3)
    }
}

@Composable
private fun LunchboxPhotoFrame(
    photo: org.jetbrains.compose.resources.DrawableResource?,
    onCameraClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = SikdorokTheme.spacing
    // 도시락 통: 좌우·상단 8dp, 하단 24dp 받침 (렌더 실측)
    Box(
        modifier = modifier
            .size(width = 245.dp, height = 262.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(RecordDark)
            .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 24.dp),
    ) {
        Box(Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))) {
            Image(
                painterResource(photo ?: Res.drawable.lunchbox_placeholder),
                contentDescription = if (photo != null) "도시락 사진" else null, // 자리표시자는 장식
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(s.s16)
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CameraGray)
                    .clickable(onClick = onCameraClick)
                    .semantics { contentDescription = "사진 추가" },
                contentAlignment = Alignment.Center,
            ) {
                CameraIcon(SikdorokTheme.colors.bg1, Modifier.size(24.dp))
            }
        }
    }
}

@Composable
private fun FoodEmojiRow(selected: String?, onSelect: (String?) -> Unit) {
    val c = SikdorokTheme.colors
    val t = SikdorokTheme.typography
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FOOD_EMOJIS.forEach { emoji ->
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    // 선택 표시는 디자인에 없음 — bg4 배경 채택, 디자이너 확인 항목
                    .background(if (emoji == selected) c.bg4 else Color.Transparent)
                    .clickable(onClick = { onSelect(emoji) }),
                contentAlignment = Alignment.Center,
            ) {
                Text(emoji, fontSize = 20.sp)
            }
        }
        // "?" = 대표 음식 미선택 (home 미기록 박스와 동일 시각)
        Box(
            modifier = Modifier
                .size(32.dp)
                .clickable(onClick = { onSelect(null) })
                .semantics { contentDescription = "대표 음식 선택 안 함" },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier.size(22.dp).clip(RoundedCornerShape(3.dp)).background(UnselectedGray),
                contentAlignment = Alignment.Center,
            ) {
                Text("?", style = t.body2, color = c.bg1)
            }
        }
    }
}

@Composable
private fun RepresentativeCheckRow(checked: Boolean, onToggle: () -> Unit) {
    val c = SikdorokTheme.colors
    val t = SikdorokTheme.typography
    val s = SikdorokTheme.spacing
    Row(
        modifier = Modifier.clickable(onClick = onToggle),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .let {
                    if (checked) it.background(RecordDark)
                    else it.border(1.5.dp, c.text2, RoundedCornerShape(6.dp))
                },
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                CheckIcon(c.bg1, Modifier.size(12.dp))
            }
        }
        Spacer(Modifier.width(s.s8))
        Text("대표 게시물 설정하기", style = t.body1, color = c.text3)
    }
}

@Composable
private fun MemoField(memo: String, onMemoChange: (String) -> Unit) {
    val c = SikdorokTheme.colors
    val t = SikdorokTheme.typography
    val s = SikdorokTheme.spacing
    val memoStyle = t.body1.copy(lineHeight = 28.sp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(c.bg3)
            .padding(horizontal = s.s16),
    ) {
        Column(Modifier.fillMaxSize()) {
            // 괘선: 첫 줄 41dp, 이후 28dp 간격 (렌더 실측 — lineHeight 28과 정렬)
            Spacer(Modifier.height(41.dp))
            repeat(7) {
                HorizontalDivider(color = MemoLineBlack)
                Spacer(Modifier.height(27.dp))
            }
        }
        BasicTextField(
            value = memo,
            onValueChange = onMemoChange,
            textStyle = memoStyle.copy(color = c.text4),
            modifier = Modifier.fillMaxSize().padding(top = 13.dp),
            decorationBox = { innerTextField ->
                Box {
                    if (memo.isEmpty()) {
                        Text("메모를 남겨보세요", style = memoStyle, color = c.text3)
                    }
                    innerTextField()
                }
            },
        )
    }
}

// ---- Canvas 아이콘 (렌더 크롭 확보 시 교체 후보) ----

@Composable
private fun BackArrowIcon(color: Color, modifier: Modifier) {
    Canvas(modifier) {
        val stroke = 2.5.dp.toPx()
        val midY = size.height / 2
        val left = 3.dp.toPx()
        drawLine(color, Offset(left, midY), Offset(21.dp.toPx(), midY), stroke, StrokeCap.Round)
        drawLine(color, Offset(left, midY), Offset(10.dp.toPx(), midY - 7.dp.toPx()), stroke, StrokeCap.Round)
        drawLine(color, Offset(left, midY), Offset(10.dp.toPx(), midY + 7.dp.toPx()), stroke, StrokeCap.Round)
    }
}

@Composable
private fun ChevronRightIcon(color: Color, modifier: Modifier) {
    Canvas(modifier) {
        // home TriangleIcon(RIGHT)과 동일한 5x9 삼각형
        val tri = Path().apply {
            moveTo(0f, 0f)
            lineTo(0f, size.height)
            lineTo(size.width, size.height / 2)
            close()
        }
        drawPath(tri, color)
    }
}

@Composable
private fun CameraIcon(color: Color, modifier: Modifier) {
    Canvas(modifier) {
        // 근사 카메라: 몸통 + 뷰파인더 돌출부, 렌즈는 버튼 면색으로 뚫음
        val bodyTop = 7.dp.toPx()
        drawRoundRect(
            color,
            topLeft = Offset(2.dp.toPx(), bodyTop),
            size = Size(20.dp.toPx(), 13.dp.toPx()),
            cornerRadius = CornerRadius(2.5.dp.toPx()),
        )
        drawRoundRect(
            color,
            topLeft = Offset(8.5.dp.toPx(), 4.dp.toPx()),
            size = Size(7.dp.toPx(), 4.dp.toPx()),
            cornerRadius = CornerRadius(1.5.dp.toPx()),
        )
        drawCircle(CameraGray, radius = 3.5.dp.toPx(), center = Offset(center.x, bodyTop + 6.5.dp.toPx()))
    }
}

@Composable
private fun CheckIcon(color: Color, modifier: Modifier) {
    Canvas(modifier) {
        val path = Path().apply {
            moveTo(2.dp.toPx(), 6.5.dp.toPx())
            lineTo(4.8.dp.toPx(), 9.dp.toPx())
            lineTo(10.dp.toPx(), 3.dp.toPx())
        }
        drawPath(path, color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
    }
}
