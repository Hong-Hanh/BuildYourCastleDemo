package com.honghanh.buildyourcastledemo.core.model


data class PerformanceState(
    val totalFocusMinutes: Int = 0,
    val totalTasksCompleted: Int = 0,
    val totalAetherCrystalsEarned: Int = 0,
    // Mảng chứa 7 phần tử tương ứng từ Thứ 2 đến Chủ Nhật để vẽ biểu đồ
    val weeklyFocusMinutes: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0)
)