package com.devts.mymeal.core.model

/**
 * 스키마 근거: aidlc-docs/features/data-foundation/technical-design.md §3·§4.
 * 순수 Kotlin — 플랫폼/Compose 타입 참조 금지 (FR-5).
 */
data class MealEntry(
    val id: String,
    val mealAt: Long,          // epoch ms UTC
    val mealType: MealType,    // 스키마 v2 (F-5 Q1=A, 2026-09-03)
    val note: String?,
    val photoPath: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val items: List<MealItem>,
)

// SNACK 포함 4종 (record 디자인 832:98315, 사용자 확정 2026-09-04). 홈 캐러셀은 3끼만 표시.
enum class MealType { BREAKFAST, LUNCH, DINNER, SNACK }

data class MealItem(
    val id: String,
    val name: String,
    val amountLabel: String?,  // Q2=B: 자유 라벨만, g 필드 없음
    val estimatedKcal: Int?,
    val orderIndex: Int,
)

/** 총 예상 칼로리 — 합산 계산(Q3=A, 비저장). 전 항목 null이면 null. */
fun MealEntry.totalEstimatedKcal(): Int? {
    val kcals = items.mapNotNull { it.estimatedKcal }
    return if (kcals.isEmpty()) null else kcals.sum()
}
