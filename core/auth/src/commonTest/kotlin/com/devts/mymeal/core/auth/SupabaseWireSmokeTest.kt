package com.devts.mymeal.core.auth

import io.github.jan.supabase.auth.user.UserSession
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * 실경로 스모크 — 페이크가 아니라 supabase-kt의 **실제** 세션 직렬화기를 현재 클래스패스에서 돌린다.
 * 네트워크 없음, 서버 응답 모양의 픽스처만.
 *
 * 이 테스트가 존재하는 이유: supabase-kt 3.1.4는 kotlinx-datetime 0.6.x의
 * `InstantIso8601Serializer`를 참조하는데 이 프로젝트는 0.8.0(해당 클래스 삭제)을 쓴다.
 * 컴파일·기존 단위 테스트(전부 FakeAuthRepository)는 전부 통과했고, 로그인 응답을 실제로
 * 역직렬화하는 순간에만 NoClassDefFoundError로 터졌다(PR #11 → #13).
 * 라이브러리 버전을 올릴 때 이 테스트가 먼저 깨진다.
 */
class SupabaseWireSmokeTest {

    @Test
    fun userSession_deserializes_with_current_classpath() {
        val json = """
            {"access_token":"a","refresh_token":"r","expires_in":3600,"token_type":"bearer"}
        """.trimIndent()

        val session = Json { ignoreUnknownKeys = true }.decodeFromString<UserSession>(json)

        assertEquals("a", session.accessToken)
        assertEquals("r", session.refreshToken)
        assertEquals(3600L, session.expiresIn)
    }
}
