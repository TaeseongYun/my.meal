package com.devts.mymeal.di

import com.devts.mymeal.feature.login.loginModule
import org.koin.core.module.Module

// 피처는 자기 Koin module 파일만 노출하고 여기 목록에 추가한다 (로드맵 single-owner 규약).
fun appModules(): List<Module> = listOf(loginModule)
