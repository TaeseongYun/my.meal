package com.devts.mymeal.feature.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devts.mymeal.core.designsystem.SikdorokTheme

// 디자인 근거: Figma 832:106628 (이메일 인증 화면). 로그인/회원가입/카카오 이메일 등록이
// 같은 레이아웃이라 mode로 제목·CTA·하단 링크만 바꾼다. 토큰 외 색(#5DBE73 CTA, #DDDDDD 보더)은
// 스타일가이드에 없는 값 — 디자이너 확인 항목.
// 코드 입력 단계는 같은 톤(48/56dp 높이, r4, 동일 보더·CTA 색)으로 파생 — 전용 디자인 미확정.
private val FieldBorder = Color(0xFFDDDDDD)
private val CtaGreen = Color(0xFF5DBE73)

@Composable
fun EmailAuthScreen(
    state: EmailAuthUiState,
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onSubmit: () -> Unit = {},
    onBack: () -> Unit = {},
    onSwitchToSignUp: () -> Unit = {},
    onFindPassword: () -> Unit = {},
    onDigit: (Char) -> Unit = {},
    onDelete: () -> Unit = {},
    onResend: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = SikdorokTheme.colors

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(c.bg1)
            .safeContentPadding(),
    ) {
        Box(Modifier.fillMaxWidth().height(44.dp).padding(horizontal = SikdorokTheme.spacing.s20)) {
            BackArrowIcon(
                color = c.text4,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(20.dp)
                    .clickable(onClick = onBack)
                    .semantics { contentDescription = "뒤로" },
            )
        }
        if (state.codeSentTo != null) {
            CodeStep(state, onDigit, onDelete, onResend)
        } else {
            CredentialStep(state, onEmailChange, onPasswordChange, onSubmit, onSwitchToSignUp, onFindPassword)
        }
    }
}

@Composable
private fun CredentialStep(
    state: EmailAuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onSwitchToSignUp: () -> Unit,
    onFindPassword: () -> Unit,
) {
    val c = SikdorokTheme.colors
    val t = SikdorokTheme.typography
    val s = SikdorokTheme.spacing

    Column(Modifier.padding(horizontal = s.s20)) {
        Spacer(Modifier.height(10.dp))
        Text(
            when (state.mode) {
                EmailAuthMode.LOGIN -> "이메일로 로그인"
                EmailAuthMode.SIGN_UP -> "이메일로 회원가입"
                EmailAuthMode.LINK_KAKAO -> "이메일 등록"
            },
            style = t.h1.copy(lineHeight = 37.sp), // 실측 24/37
            color = c.text4,
        )
        Spacer(Modifier.height(36.dp))
        LabeledField(
            label = "이메일",
            value = state.email,
            onValueChange = onEmailChange,
            placeholder = "sikdorok@naver.com",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
        )
        Spacer(Modifier.height(s.s20))
        LabeledField(
            label = "비밀번호",
            value = state.password,
            onValueChange = onPasswordChange,
            placeholder = "비밀번호",
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            mask = true,
        )
        if (state.errorMessage != null) {
            Spacer(Modifier.height(s.s8))
            Text(state.errorMessage, style = t.body2, color = c.alertRed)
        }
        if (state.noticeMessage != null) {
            Spacer(Modifier.height(s.s8))
            Text(state.noticeMessage, style = t.body2, color = c.alertGreen)
        }
        Spacer(Modifier.height(s.s32))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (state.canSubmit) CtaGreen else c.text2)
                .clickable(enabled = state.canSubmit, onClick = onSubmit),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                when (state.mode) {
                    EmailAuthMode.LOGIN -> "로그인"
                    EmailAuthMode.SIGN_UP -> "회원가입"
                    EmailAuthMode.LINK_KAKAO -> "확인"
                },
                style = t.body1.copy(lineHeight = 20.sp),
                color = c.bg1,
            )
        }
        if (state.mode != EmailAuthMode.LINK_KAKAO) {
            Spacer(Modifier.height(s.s24))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (state.mode == EmailAuthMode.LOGIN) {
                    Text(
                        "회원가입",
                        style = t.body1.copy(lineHeight = 20.sp),
                        color = c.text4,
                        modifier = Modifier.clickable(onClick = onSwitchToSignUp),
                    )
                    Spacer(Modifier.width(7.dp))
                    Box(Modifier.width(1.dp).height(12.dp).background(c.text4))
                    Spacer(Modifier.width(7.dp))
                }
                Text(
                    "비밀번호 찾기",
                    style = t.body1.copy(lineHeight = 20.sp),
                    color = c.text4,
                    modifier = Modifier.clickable(onClick = onFindPassword),
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.CodeStep(
    state: EmailAuthUiState,
    onDigit: (Char) -> Unit,
    onDelete: () -> Unit,
    onResend: () -> Unit,
) {
    val c = SikdorokTheme.colors
    val t = SikdorokTheme.typography
    val s = SikdorokTheme.spacing

    Column(Modifier.weight(1f).padding(horizontal = s.s20)) {
        Spacer(Modifier.height(10.dp))
        Text("인증 코드 입력", style = t.h1.copy(lineHeight = 37.sp), color = c.text4)
        Spacer(Modifier.height(s.s12))
        Text(
            "${state.codeSentTo}으로 보낸 6자리 코드를 입력해주세요",
            style = t.body2,
            color = c.text3,
        )
        Spacer(Modifier.height(36.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "인증 코드 ${state.code.length}자리 입력됨" },
            horizontalArrangement = Arrangement.spacedBy(s.s8),
        ) {
            repeat(CODE_LENGTH) { index ->
                val filled = index < state.code.length
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .border(
                            width = if (index == state.code.length) 1.5.dp else 1.dp,
                            color = if (index == state.code.length) CtaGreen else FieldBorder,
                            shape = RoundedCornerShape(4.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (filled) Text(state.code[index].toString(), style = t.h1, color = c.text4)
                }
            }
        }
        if (state.errorMessage != null) {
            Spacer(Modifier.height(s.s8))
            Text(state.errorMessage, style = t.body2, color = c.alertRed)
        }
        if (state.noticeMessage != null) {
            Spacer(Modifier.height(s.s8))
            Text(state.noticeMessage, style = t.body2, color = c.alertGreen)
        }
        Spacer(Modifier.weight(1f))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            val waiting = state.resendCooldown > 0
            Text(
                if (waiting) "재전송 (${countdown(state.resendCooldown)})" else "인증 코드 재전송",
                style = t.body1.copy(lineHeight = 20.sp),
                color = if (waiting) c.text3 else c.text4,
                modifier = Modifier
                    .clickable(enabled = !waiting, onClick = onResend)
                    .padding(s.s8),
            )
        }
        Spacer(Modifier.height(s.s16))
        Keypad(onDigit = onDigit, onDelete = onDelete)
        Spacer(Modifier.height(s.s16))
    }
}

/** mm:ss — 공용 포매터가 없어 직접 만든다(kotlin common에 String.format 없음). */
internal fun countdown(seconds: Int): String =
    "${seconds / 60}:${(seconds % 60).toString().padStart(2, '0')}"

@Composable
private fun Keypad(onDigit: (Char) -> Unit, onDelete: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(SikdorokTheme.spacing.s4)) {
        listOf("123", "456", "789").forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { digit -> KeypadKey(digit.toString()) { onDigit(digit) } }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Spacer(Modifier.weight(1f))
            KeypadKey("0") { onDigit('0') }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clickable(onClick = onDelete)
                    .semantics { contentDescription = "지움" },
                contentAlignment = Alignment.Center,
            ) {
                BackArrowIcon(color = SikdorokTheme.colors.text4, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun RowScope.KeypadKey(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(56.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = SikdorokTheme.typography.h2, color = SikdorokTheme.colors.text4)
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    mask: Boolean = false,
) {
    val c = SikdorokTheme.colors
    val t = SikdorokTheme.typography
    val fieldStyle = t.body1.copy(lineHeight = 20.sp)
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()

    Column {
        Text(label, style = fieldStyle, color = c.text4)
        Spacer(Modifier.height(SikdorokTheme.spacing.s8))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = fieldStyle.copy(color = c.text4),
            cursorBrush = SolidColor(CtaGreen),
            interactionSource = interactionSource,
            visualTransformation = if (mask) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    width = if (focused) 1.5.dp else 1.dp,
                    color = if (focused) CtaGreen else FieldBorder,
                    shape = RoundedCornerShape(4.dp),
                )
                .padding(horizontal = SikdorokTheme.spacing.s16)
                .semantics { contentDescription = label },
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) Text(placeholder, style = fieldStyle, color = c.text3)
                    innerTextField()
                }
            },
        )
    }
}

// 렌더 크롭(ic_arrow_left) 확보 시 교체 후보 — record와 동일한 근사 아이콘.
@Composable
private fun BackArrowIcon(color: Color, modifier: Modifier) {
    Canvas(modifier) {
        val stroke = 2.dp.toPx()
        val midY = size.height / 2
        val left = 2.dp.toPx()
        drawLine(color, Offset(left, midY), Offset(18.dp.toPx(), midY), stroke, StrokeCap.Round)
        drawLine(color, Offset(left, midY), Offset(9.dp.toPx(), midY - 7.dp.toPx()), stroke, StrokeCap.Round)
        drawLine(color, Offset(left, midY), Offset(9.dp.toPx(), midY + 7.dp.toPx()), stroke, StrokeCap.Round)
    }
}
