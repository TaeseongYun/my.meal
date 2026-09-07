package com.devts.mymeal.feature.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.getValue

/**
 * iOS 카카오 SDK는 Swift 전용이라 Kotlin에서 직접 호출할 수 없다.
 * iosApp(Swift)이 KakaoLoginBridge.handler에 KakaoSDKUser 로그인 구현을 꽂아준다.
 */
object KakaoLoginBridge {
    /** Swift에서 설정: 로그인 완료 시 id_token 또는 null(실패)을 콜백으로 넘긴다. */
    var handler: (((String?) -> Unit) -> Unit)? = null
}

@Composable
actual fun rememberKakaoLogin(onResult: (Result<String>) -> Unit): () -> Unit {
    val callback by rememberUpdatedState(onResult)
    return remember {
        {
            val handler = KakaoLoginBridge.handler
            if (handler == null) {
                callback(Result.failure(NotImplementedError("iosApp에서 KakaoLoginBridge.handler를 설정해야 합니다")))
            } else {
                handler { idToken ->
                    callback(
                        idToken?.let { Result.success(it) }
                            ?: Result.failure(IllegalStateException("카카오 로그인 실패")),
                    )
                }
            }
        }
    }
}
