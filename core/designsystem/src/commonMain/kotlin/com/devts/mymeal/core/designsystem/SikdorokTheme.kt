package com.devts.mymeal.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.font.FontFamily
import com.devts.mymeal.core.designsystem.generated.resources.Res
import com.devts.mymeal.core.designsystem.generated.resources.lee_seoyun_regular
import org.jetbrains.compose.resources.Font

internal val LocalSikdorokColors = staticCompositionLocalOf { SikdorokColors() }
internal val LocalSikdorokTypography =
    staticCompositionLocalOf { SikdorokTypography(FontFamily.Default) }
internal val LocalSikdorokSpacing = staticCompositionLocalOf { SikdorokSpacing() }

object SikdorokTheme {
    val colors: SikdorokColors
        @Composable @ReadOnlyComposable get() = LocalSikdorokColors.current
    val typography: SikdorokTypography
        @Composable @ReadOnlyComposable get() = LocalSikdorokTypography.current
    val spacing: SikdorokSpacing
        @Composable @ReadOnlyComposable get() = LocalSikdorokSpacing.current
}

@Composable
fun SikdorokTheme(content: @Composable () -> Unit) {
    // ADR-2: FontFamily 단일 주입 지점 — 폰트 교체 시 이 한 줄만 변경
    val fontFamily = FontFamily(Font(Res.font.lee_seoyun_regular))
    val colors = SikdorokColors()
    val typography = SikdorokTypography(fontFamily)
    val spacing = SikdorokSpacing()
    CompositionLocalProvider(
        LocalSikdorokColors provides colors,
        LocalSikdorokTypography provides typography,
        LocalSikdorokSpacing provides spacing,
    ) {
        MaterialTheme(
            colorScheme = sikdorokColorScheme(colors),
            typography = sikdorokM3Typography(typography),
            content = content,
        )
    }
}
