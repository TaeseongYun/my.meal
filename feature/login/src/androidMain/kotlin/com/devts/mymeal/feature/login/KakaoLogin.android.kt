package com.devts.mymeal.feature.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.devts.mymeal.core.auth.kakaoNativeAppKey
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

@Composable
actual fun rememberKakaoLogin(onResult: (Result<String>) -> Unit): () -> Unit {
    val context = LocalContext.current
    val callback by rememberUpdatedState(onResult)

    return remember(context) {
        {
            KakaoSdk.init(context, kakaoNativeAppKey) // 재호출 안전
            val finish: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                callback(
                    when {
                        error != null -> Result.failure(error)
                        token?.idToken == null -> Result.failure(
                            IllegalStateException("카카오 id_token 없음 — OpenID Connect 활성화와 openid scope를 확인하세요"),
                        )
                        else -> Result.success(token.idToken!!)
                    },
                )
            }
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    // 사용자가 카카오톡 로그인을 취소한 경우만 그대로 종료, 그 외 실패는 카카오계정으로 폴백
                    if (error != null && !(error is ClientError && error.reason == ClientErrorCause.Cancelled)) {
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = finish)
                    } else {
                        finish(token, error)
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = finish)
            }
        }
    }
}
