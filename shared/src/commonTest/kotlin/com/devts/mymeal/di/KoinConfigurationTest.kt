package com.devts.mymeal.di

import com.devts.mymeal.feature.login.LoginViewModel
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertNotNull

// Koin은 런타임 해석이라 구성 오류를 컴파일이 못 잡는다 — 전 정의 resolve 검증 (ADR-2 회신 사항).
class KoinConfigurationTest {

    @Test
    fun appModules_allDefinitionsResolve() {
        val app = koinApplication { modules(appModules()) }
        assertNotNull(app.koin.get<LoginViewModel>())
        app.close()
    }
}
