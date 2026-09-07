package com.devts.mymeal.feature.login

import androidx.compose.runtime.Composable

/**
 * 카카오 네이티브 로그인 진입점. 성공 시 Supabase가 교환할 OIDC id_token을 돌려준다
 * (카카오 개발자 콘솔에서 OpenID Connect 활성화 + scope에 openid 필요).
 */
@Composable
expect fun rememberKakaoLogin(onResult: (Result<String>) -> Unit): () -> Unit
