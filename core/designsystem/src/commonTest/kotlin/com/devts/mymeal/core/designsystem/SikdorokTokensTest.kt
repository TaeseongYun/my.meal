package com.devts.mymeal.core.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

/** 기대값 출처: aidlc-docs/features/design-system/design-manifest.md */
class SikdorokTokensTest {

    private val colors = SikdorokColors()

    // T1: 컬러 13종 ARGB 값 대조
    @Test
    fun colors_match_design_manifest() {
        assertEquals(Color(0xFFFFFFFF), colors.bg1)
        assertEquals(Color(0xFFFCFAF7), colors.bg2)
        assertEquals(Color(0xFFF8F5ED), colors.bg3)
        assertEquals(Color(0xFFE9E6DE), colors.bg4)
        assertEquals(Color(0x1A9D9792), colors.line)
        assertEquals(Color(0xFFEBEAE9), colors.text1)
        assertEquals(Color(0xFFCECBC8), colors.text2)
        assertEquals(Color(0xFF9D9792), colors.text3)
        assertEquals(Color(0xFF3C3025), colors.text4)
        assertEquals(Color(0xCC3C3025), colors.textDim)
        assertEquals(Color(0xFFFF6363), colors.alertRed)
        assertEquals(Color(0xFF02B57F), colors.alertGreen)
        assertEquals(Color(0xFF00CC8F), colors.accent)
    }

    // T2: 알파 내장 토큰 (Line 10%, TextDim 80%)
    @Test
    fun alpha_channels_match_figma_opacity() {
        val tolerance = 1f / 255f
        assertEquals(0.10f, colors.line.alpha, tolerance)
        assertEquals(0.80f, colors.textDim.alpha, tolerance)
    }

    // T3: 스페이싱 8종
    @Test
    fun spacing_matches_design_manifest() {
        val s = SikdorokSpacing()
        assertEquals(4.dp, s.s4)
        assertEquals(8.dp, s.s8)
        assertEquals(12.dp, s.s12)
        assertEquals(16.dp, s.s16)
        assertEquals(20.dp, s.s20)
        assertEquals(24.dp, s.s24)
        assertEquals(32.dp, s.s32)
        assertEquals(40.dp, s.s40)
    }

    // T4: 타이포 7종 size/lineHeight/letterSpacing/weight
    @Test
    fun typography_matches_design_manifest() {
        val t = SikdorokTypography(FontFamily.Default)
        val expected = mapOf(
            t.h1 to (24f to 24f),
            t.h2 to (20f to 22.4f),
            t.h3 to (18f to 20.1f),
            t.h4 to (16f to 16f),
            t.body1 to (14f to 14f),
            t.body2 to (13f to 20f),
            t.detail to (12f to 12f),
        )
        expected.forEach { (style, sizes) ->
            assertEquals(sizes.first, style.fontSize.value)
            assertEquals(sizes.second, style.lineHeight.value)
            assertEquals(0f, style.letterSpacing.value)
            assertEquals(FontWeight.Normal, style.fontWeight)
        }
    }

    // T5: M3 ColorScheme 매핑 표 (technical-design.md §3)
    @Test
    fun m3_color_scheme_mapping() {
        val scheme = sikdorokColorScheme(colors)
        assertEquals(colors.accent, scheme.primary)
        assertEquals(colors.bg1, scheme.background)
        assertEquals(colors.bg2, scheme.surface)
        assertEquals(colors.text4, scheme.onBackground)
        assertEquals(colors.text4, scheme.onSurface)
        assertEquals(colors.alertRed, scheme.error)
        assertEquals(colors.line, scheme.outline)
    }

    // T6: M3 Typography 매핑
    @Test
    fun m3_typography_mapping() {
        val t = SikdorokTypography(FontFamily.Default)
        val m3 = sikdorokM3Typography(t)
        assertEquals(t.h1, m3.displayLarge)
        assertEquals(t.h2, m3.headlineLarge)
        assertEquals(t.h3, m3.headlineMedium)
        assertEquals(t.h4, m3.titleMedium)
        assertEquals(t.body1, m3.bodyLarge)
        assertEquals(t.body2, m3.bodyMedium)
        assertEquals(t.detail, m3.labelSmall)
    }
}
