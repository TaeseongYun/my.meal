package com.devts.mymeal.di

import com.devts.mymeal.core.data.db.MymealDatabase
import com.devts.mymeal.core.data.photo.PhotoStore
import com.devts.mymeal.core.data.repository.MealRepository
import com.devts.mymeal.core.data.repository.RoomMealRepository
import org.koin.core.module.Module
import org.koin.dsl.module

// :core:data는 Koin 무지(순수) — 데이터 계층 조립은 shared 소유 (diary ADR-D2).
// 플랫폼 모듈이 MymealDatabase·PhotoStore를 제공한다.
expect fun platformDataModule(): Module

internal fun dataModules(): List<Module> = listOf(
    platformDataModule(),
    module {
        single<MealRepository> { RoomMealRepository(get<MymealDatabase>().mealDao(), get<PhotoStore>()) }
    },
)
