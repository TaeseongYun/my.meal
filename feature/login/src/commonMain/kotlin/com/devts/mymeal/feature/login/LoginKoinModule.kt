package com.devts.mymeal.feature.login

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

// 피처 Koin module — shared의 appModules()가 포함한다 (파일 단위 분리 규약).
val loginModule = module {
    viewModelOf(::LoginViewModel)
}
