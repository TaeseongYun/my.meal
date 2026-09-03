package com.devts.mymeal.core.data.repository

import com.devts.mymeal.core.model.MealEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/** technical-design §3 — 소비자(F-3/F-5)는 이 인터페이스만 본다 */
interface MealRepository {
    /** 로컬 타임존 기준 해당 일자의 기록 (FR-3) */
    fun observeByDate(date: LocalDate): Flow<List<MealEntry>>
    suspend fun get(id: String): MealEntry?
    suspend fun upsert(entry: MealEntry)
    /** ADR-3: DB 삭제 커밋 후 사진 파일 정리 */
    suspend fun delete(id: String)
}
