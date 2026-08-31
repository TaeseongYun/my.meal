package com.devts.mymeal.features.login.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devts.mymeal.core.designsystem.SikdorokTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

import mymeal.shared.generated.resources.Res
import mymeal.shared.generated.resources.ic_kakao
import mymeal.shared.generated.resources.ic_mail_white
import mymeal.shared.generated.resources.login_hero

// 디자인 근거: Figma 832:48657 (로그인 화면). 소셜 브랜드/전용 서피스 색은 토큰 외 —
// 디자이너 확인 항목으로 audit에 기록됨.
private val KakaoYellow = Color(0xFFFFE617)
private val EmailButtonDark = Color(0xFF222222)

@Composable
fun LoginScreen(
    onKakaoClick: () -> Unit = {},
    onEmailClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = SikdorokTheme.colors
    val t = SikdorokTheme.typography
    val s = SikdorokTheme.spacing

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(c.bg3)
            .safeContentPadding()
            .padding(horizontal = s.s20),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(80.dp))
        Text(
            "매일 먹는 도시락,\n간편하게 기록하세요!",
            // 디자인 실측 32/38 — 토큰 스케일(H1/24) 밖이라 h1에서 파생
            style = t.h1.copy(fontSize = 32.sp, lineHeight = 38.sp),
            color = c.text4,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(s.s8))
        Text(
            "식도록과 함께하는 도시락 일기",
            style = t.h4.copy(lineHeight = 26.sp),
            color = c.text4,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.weight(1f))
        Image(
            painterResource(Res.drawable.login_hero),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(0.82f),
        )
        Spacer(Modifier.weight(1f))
        LoginButton(
            text = "카카오로 로그인",
            textColor = c.text4,
            background = KakaoYellow,
            icon = Res.drawable.ic_kakao,
            onClick = onKakaoClick,
        )
        Spacer(Modifier.height(s.s16))
        LoginButton(
            text = "이메일로 로그인",
            textColor = c.bg1,
            background = EmailButtonDark,
            icon = Res.drawable.ic_mail_white,
            onClick = onEmailClick,
        )
        Spacer(Modifier.height(s.s40))
    }
}

@Composable
private fun LoginButton(
    text: String,
    textColor: Color,
    background: Color,
    icon: DrawableResource,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(icon), contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.size(SikdorokTheme.spacing.s8))
            Text(text, style = SikdorokTheme.typography.body1, color = textColor)
        }
    }
}
