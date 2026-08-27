package com.devts.mymeal.core.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme

/**
 * M3 브리지 (technical-design.md ADR-3): 디자인 근거가 있는 슬롯만 매핑,
 * 나머지는 lightColorScheme()/Typography() 기본값. 앱 코드는 SikdorokTheme 접근자를 사용한다.
 */
fun sikdorokColorScheme(colors: SikdorokColors): ColorScheme = lightColorScheme(
    primary = colors.accent,
    background = colors.bg1,
    surface = colors.bg2,
    onBackground = colors.text4,
    onSurface = colors.text4,
    error = colors.alertRed,
    outline = colors.line,
)

fun sikdorokM3Typography(t: SikdorokTypography): Typography = Typography(
    displayLarge = t.h1,
    headlineLarge = t.h2,
    headlineMedium = t.h3,
    titleMedium = t.h4,
    bodyLarge = t.body1,
    bodyMedium = t.body2,
    labelSmall = t.detail,
)
