package com.devts.mymeal.core.designsystem

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 디자인 시스템 육안 확인용 쇼케이스. 구성 근거:
 * aidlc-docs/features/design-system/design-manifest.md "Style Guide 화면 구성" (Figma 836:33127).
 * 아이콘 섹션은 디자인 미확정(Q2=B)으로 제외.
 */
@Composable
fun StyleGuideScreen(modifier: Modifier = Modifier) {
    val c = SikdorokTheme.colors
    val t = SikdorokTheme.typography
    val s = SikdorokTheme.spacing

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(c.bg1)
            .safeContentPadding()
            .verticalScroll(rememberScrollState())
            .padding(s.s20),
    ) {
        Text("Style Guide", style = t.h1, color = c.text4)
        Text("Sikdorok", style = t.detail, color = c.text3)

        SectionTitle("Font")
        Column(Modifier.fillMaxWidth().background(c.bg3).padding(s.s16)) {
            Text("매일 먹는 도시락, 식도록과 함께 간편하게 기록하세요!", style = t.h3, color = c.text4)
            Spacer(Modifier.height(s.s8))
            Text("이서윤체 국, 영문 통일", style = t.detail, color = c.text3)
        }

        SectionTitle("Text Style")
        TypeRow("H1 / 24", "식도록과 함께하는", t.h1)
        TypeRow("H2 / 20", "6월의 도시락", t.h2)
        TypeRow("H3 / 18", "오늘의 도시락", t.h3)
        TypeRow("H4 / 16", "저녁", t.h4)
        TypeRow("Body 1 / 14", "오늘의 메뉴", t.body1)
        TypeRow("Body 2 / 13", "메모를 남겨보세요", t.body2)
        TypeRow("Detail / 12", "오후 12:53", t.detail)

        SectionTitle("Color — Bg")
        ColorChip("BG 1", "Gray 0", "#FFFFFF", c.bg1)
        ColorChip("BG 2", "Beige 1", "#FCFAF7", c.bg2)
        ColorChip("BG 3", "Beige 2", "#F8F5ED", c.bg3)
        ColorChip("BG 4", "Beige 3", "#E9E6DE", c.bg4)
        ColorChip("Line", "10%", "#9D9792", c.line)

        SectionTitle("Color — Txt / Btn")
        ColorChip("Text 1", "Gray 1", "#EBEAE9", c.text1)
        ColorChip("Text 2", "Gray 2", "#CECBC8", c.text2)
        ColorChip("Text 3", "Gray 3", "#9D9792", c.text3)
        ColorChip("Text 4", "Gray 5", "#3C3025", c.text4)
        ColorChip("Opacity 80%", "Gray 4", "#3C3025", c.textDim)

        SectionTitle("Color — Alert / Accent")
        ColorChip("Alert", "Red", "#FF6363", c.alertRed)
        ColorChip("Alert", "Green", "#02B57F", c.alertGreen)
        ColorChip("Accent", "", "#00CC8F", c.accent)

        SectionTitle("Spacing")
        SpacingBar("4", s.s4)
        SpacingBar("8", s.s8)
        SpacingBar("12", s.s12)
        SpacingBar("16", s.s16)
        SpacingBar("20", s.s20)
        SpacingBar("24", s.s24)
        SpacingBar("32", s.s32)
        SpacingBar("40", s.s40)
        Spacer(Modifier.height(s.s40))
    }
}

@Composable
private fun SectionTitle(title: String) {
    val s = SikdorokTheme.spacing
    Spacer(Modifier.height(s.s32))
    Text(title, style = SikdorokTheme.typography.h2, color = SikdorokTheme.colors.text4)
    Spacer(Modifier.height(s.s12))
}

@Composable
private fun TypeRow(label: String, sample: String, style: androidx.compose.ui.text.TextStyle) {
    val c = SikdorokTheme.colors
    Row(
        Modifier.fillMaxWidth().padding(vertical = SikdorokTheme.spacing.s8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = SikdorokTheme.typography.detail, color = c.text3, modifier = Modifier.weight(0.35f))
        Text(sample, style = style, color = c.text4, modifier = Modifier.weight(0.65f))
    }
}

@Composable
private fun ColorChip(role: String, palette: String, hex: String, color: Color) {
    val c = SikdorokTheme.colors
    val s = SikdorokTheme.spacing
    Row(
        Modifier.fillMaxWidth().padding(vertical = s.s4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(50.dp).background(c.bg1)) {
            Box(Modifier.size(50.dp).background(color))
        }
        Spacer(Modifier.size(s.s12))
        Column {
            Text(role, style = SikdorokTheme.typography.body2, color = c.text4)
            Text(listOf(palette, hex).filter { it.isNotEmpty() }.joinToString(" · "), style = SikdorokTheme.typography.detail, color = c.text3)
        }
    }
}

@Composable
private fun SpacingBar(label: String, height: androidx.compose.ui.unit.Dp) {
    val c = SikdorokTheme.colors
    Column(Modifier.fillMaxWidth().padding(vertical = SikdorokTheme.spacing.s4)) {
        Text(label, style = SikdorokTheme.typography.detail, color = c.text3)
        Box(Modifier.fillMaxWidth().height(height).background(c.text4))
    }
}
