package com.devts.mymeal.di

import com.devts.mymeal.logging.initLogging
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.mp.KoinPlatformTools

// 플랫폼 진입점(MymealApplication / MainViewController)이 호출한다.
fun initKoin(config: KoinAppDeclaration? = null) {
    if (KoinPlatformTools.defaultContext().getOrNull() != null) return // 재호출 안전
    initLogging()
    startKoin {
        config?.invoke(this)
        modules(appModules())
    }
}
