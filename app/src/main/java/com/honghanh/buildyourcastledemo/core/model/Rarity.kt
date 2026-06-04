package com.honghanh.buildyourcastledemo.core.model

import android.graphics.Color

//ENUM CHỈ GIỮ VAI trò định danh id và hiển thị
enum class Rarity {
    COMMON, RARE, EPIC, LEGENDARY
}
data class RarityConfig (
    val rarity: Rarity,
    val displayname: String,
    val dropRate: Double, //có thể thay đổi tỷ lệ
    val color: Color
)