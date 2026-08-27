package com.devts.mymeal.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
class SikdorokTypography(
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val h4: TextStyle,
    val body1: TextStyle,
    val body2: TextStyle,
    val detail: TextStyle,
)

/**
 * 값 출처: design-manifest.md 실측(소수 1자리 반올림). letterSpacing은 디자인 주석
 * "*All letter spacing 0px"에 따라 전부 0 (P2 자동결정). 이서윤체 단일 웨이트(400).
 */
fun SikdorokTypography(fontFamily: FontFamily): SikdorokTypography {
    fun style(size: Float, lineHeight: Float) = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = size.sp,
        lineHeight = lineHeight.sp,
        letterSpacing = 0.sp,
    )
    return SikdorokTypography(
        h1 = style(24f, 24f),
        h2 = style(20f, 22.4f),
        h3 = style(18f, 20.1f),
        h4 = style(16f, 16f),
        body1 = style(14f, 14f),
        body2 = style(13f, 20f),
        detail = style(12f, 12f),
    )
}
