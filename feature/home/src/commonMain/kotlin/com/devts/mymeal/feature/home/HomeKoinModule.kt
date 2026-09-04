package com.devts.mymeal.feature.home

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

// 피처 Koin module — shared의 appModules()가 포함한다 (파일 단위 분리 규약).
val homeModule = module {
    viewModel { HomeViewModel(repository = get()) }
}
