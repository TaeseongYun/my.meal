package com.devts.mymeal.feature.settings

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.devts.mymeal.core.designsystem.SikdorokTheme

// 레이아웃 근거: Figma 333:3518 (설정). 원본 프레임은 흑백 아이데이션 목업이라 색·서체는
// SikdorokTheme 토큰으로 옮겼고(톤앤매너 유지), 레이아웃 수치만 실측을 따랐다.
// 항목 구성은 2026-09-09 재정의 — 목업의 "원본 사진 자동 저장" 4행은 플레이스홀더였다.
// 아이콘은 home/record와 같은 Canvas 근사 — 아이콘 세트 확정 시 교체 대상.
private val SettingsDark = Color(0xFF413A31) // 섹션 아이콘·연필·셰브런·로그아웃 (home HomeDark와 동일)
private val AccentBrown = Color(0xFF53422C)  // 뒤로가기 (record와 동일)
private val RowHeight = 60.dp                // 실측 60, 행 간격 4 (합 64 피치)
private val CardShape = RoundedCornerShape(8.dp)

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onBackClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onUpdateClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onWithdrawClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = SikdorokTheme.colors
    val s = SikdorokTheme.spacing

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(c.bg3)
            .safeContentPadding(),
    ) {
        SettingsTopBar(onBackClick, Modifier.padding(horizontal = s.s16))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = s.s16),
        ) {
            Spacer(Modifier.height(s.s16))
            ProfileCard(state.profile, onEditProfileClick)

            Spacer(Modifier.height(s.s20))
            SectionHeader("정보") { InfoIcon(SettingsDark, it) }
            Spacer(Modifier.height(s.s16))
            RowCard("이용약관", onTermsClick) { ChevronRightIcon(SettingsDark, Modifier.size(16.dp)) }
            Spacer(Modifier.height(s.s4))
            RowCard("개인정보 처리방침", onPrivacyPolicyClick) {
                ChevronRightIcon(SettingsDark, Modifier.size(16.dp))
            }
            Spacer(Modifier.height(s.s4))
            VersionRow(state, onUpdateClick)

            Spacer(Modifier.height(s.s20))
            SectionHeader("계정") { AccountIcon(SettingsDark, it) }
            Spacer(Modifier.height(s.s16))
            RowCard("로그아웃", onLogoutClick) { LogoutIcon(SettingsDark, Modifier.size(20.dp)) }
            Spacer(Modifier.height(s.s4))
            // 스토어 정책상 계정 삭제는 인앱 경로가 필수 (Apple 5.1.1(v) / Play data deletion).
            // 실제 삭제 파이프라인(계정 + 기록 + 사진 cascade)은 F-7.
            RowCard("회원 탈퇴", onWithdrawClick, labelColor = c.alertRed) {
                ChevronRightIcon(c.alertRed, Modifier.size(16.dp))
            }
            Spacer(Modifier.height(s.s24))
        }
    }
}

@Composable
private fun SettingsTopBar(onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    val c = SikdorokTheme.colors
    Box(modifier.fillMaxWidth().height(44.dp), contentAlignment = Alignment.CenterStart) {
        BackArrowIcon(
            AccentBrown,
            Modifier
                .size(24.dp)
                .clickable(onClick = onBackClick)
                .semantics { contentDescription = "뒤로가기" },
        )
        Text(
            "설정",
            style = SikdorokTheme.typography.h3,
            color = c.text4,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ProfileCard(profile: SettingsProfile, onEditClick: () -> Unit) {
    val c = SikdorokTheme.colors
    val t = SikdorokTheme.typography
    val s = SikdorokTheme.spacing
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(CardShape)
            .background(c.bg1)
            .padding(horizontal = s.s16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 프로필 사진 자리표시자 — 실제 이미지 연결은 F-7
        Box(Modifier.size(48.dp).clip(CircleShape).background(c.text3))
        Spacer(Modifier.width(s.s12))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(profile.nickname, style = t.h3, color = c.text4)
                Spacer(Modifier.width(s.s4))
                EditIcon(
                    SettingsDark,
                    Modifier
                        .size(16.dp)
                        .clickable(onClick = onEditClick)
                        .semantics { contentDescription = "닉네임 수정" },
                )
            }
            Spacer(Modifier.height(s.s4))
            Text(profile.email, style = t.detail, color = c.text3)
        }
    }
}

/** 최신 버전이 있으면 스토어 이동 행, 없으면 버전만 보여주는 비활성 행. */
@Composable
private fun VersionRow(state: SettingsUiState, onUpdateClick: () -> Unit) {
    val c = SikdorokTheme.colors
    val t = SikdorokTheme.typography
    if (state.updateAvailable) {
        RowCard("버전", onUpdateClick) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("업데이트", style = t.body1, color = c.accent)
                Spacer(Modifier.width(SikdorokTheme.spacing.s4))
                ChevronRightIcon(c.accent, Modifier.size(16.dp))
            }
        }
    } else {
        RowCard("버전", onClick = null) {
            Text(state.version, style = t.body1, color = c.text3)
        }
    }
}

@Composable
private fun SectionHeader(label: String, icon: @Composable (Modifier) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon(Modifier.size(16.dp))
        Spacer(Modifier.width(SikdorokTheme.spacing.s4))
        Text(label, style = SikdorokTheme.typography.h4, color = SikdorokTheme.colors.text4)
    }
}

@Composable
private fun RowCard(
    label: String,
    onClick: (() -> Unit)?,
    labelColor: Color = SikdorokTheme.colors.text4,
    trailing: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(RowHeight)
            .clip(CardShape)
            .background(SikdorokTheme.colors.bg1)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = SikdorokTheme.spacing.s16),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = SikdorokTheme.typography.body1, color = labelColor)
        trailing()
    }
}

// ---- Canvas 아이콘 (아이콘 세트 확정 시 교체 후보) ----

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
        // 디자인은 record의 채운 삼각형이 아니라 획 셰브런
        val stroke = 2.dp.toPx()
        val x0 = size.width * 0.3f
        val x1 = size.width * 0.7f
        drawLine(color, Offset(x0, size.height * 0.2f), Offset(x1, size.height / 2), stroke, StrokeCap.Round)
        drawLine(color, Offset(x0, size.height * 0.8f), Offset(x1, size.height / 2), stroke, StrokeCap.Round)
    }
}

@Composable
private fun InfoIcon(color: Color, modifier: Modifier) {
    Canvas(modifier) {
        val r = size.minDimension / 2
        drawCircle(color, radius = r - size.minDimension * 0.06f, style = Stroke(width = size.minDimension * 0.12f))
        drawCircle(color, radius = size.minDimension * 0.06f, center = Offset(center.x, size.height * 0.3f))
        drawLine(
            color,
            Offset(center.x, size.height * 0.45f),
            Offset(center.x, size.height * 0.74f),
            size.minDimension * 0.12f,
            StrokeCap.Round,
        )
    }
}

@Composable
private fun AccountIcon(color: Color, modifier: Modifier) {
    Canvas(modifier) {
        // 근사 사람: 머리 원 + 어깨 반원
        drawCircle(color, radius = size.minDimension * 0.2f, center = Offset(center.x, size.height * 0.28f))
        val shoulders = Path().apply {
            addArc(
                androidx.compose.ui.geometry.Rect(
                    left = size.width * 0.15f,
                    top = size.height * 0.55f,
                    right = size.width * 0.85f,
                    bottom = size.height * 1.15f,
                ),
                180f,
                180f,
            )
            close()
        }
        drawPath(shoulders, color)
    }
}

@Composable
private fun EditIcon(color: Color, modifier: Modifier) {
    Canvas(modifier) {
        // 근사 연필: 45° 회전한 몸통 + 촉 (home EditIcon과 동일 형태, 크기 비례)
        rotate(45f, pivot = center) {
            val bodyW = size.width * 0.3f
            val left = center.x - bodyW / 2
            drawRoundRect(
                color,
                topLeft = Offset(left, size.height * 0.1f),
                size = Size(bodyW, size.height * 0.55f),
                cornerRadius = CornerRadius(bodyW * 0.2f),
            )
            val tip = Path().apply {
                moveTo(left, size.height * 0.68f)
                lineTo(left + bodyW, size.height * 0.68f)
                lineTo(center.x, size.height * 0.9f)
                close()
            }
            drawPath(tip, color)
        }
    }
}

@Composable
private fun LogoutIcon(color: Color, modifier: Modifier) {
    Canvas(modifier) {
        // 근사 로그아웃: 오른쪽이 열린 문 + 밖으로 나가는 화살표
        val stroke = 1.8.dp.toPx()
        val w = size.width
        val h = size.height
        val door = Path().apply {
            moveTo(w * 0.45f, h * 0.1f)
            lineTo(w * 0.1f, h * 0.1f)
            lineTo(w * 0.1f, h * 0.9f)
            lineTo(w * 0.45f, h * 0.9f)
        }
        drawPath(door, color, style = Stroke(width = stroke, cap = StrokeCap.Round))
        drawLine(color, Offset(w * 0.4f, h / 2), Offset(w * 0.92f, h / 2), stroke, StrokeCap.Round)
        drawLine(color, Offset(w * 0.68f, h * 0.26f), Offset(w * 0.92f, h / 2), stroke, StrokeCap.Round)
        drawLine(color, Offset(w * 0.68f, h * 0.74f), Offset(w * 0.92f, h / 2), stroke, StrokeCap.Round)
    }
}
