package com.honghanh.buildyourcastledemo.core.model


enum class CurencyType(
    val displayName: String,
    val symbol: String,
    val description: String
){
    GOLD("Vàng","⌚", "Thưởng khi hoàn thành tập trung"),
    EC("Era Chronos", "🌙", "Huy hiệu kỷ nguyên, dùng mua đồ hiếm"),
    GEMS("Gems", "💎", "Kim cương, dùng để roll, quy đổi từ tiền thật")
}

data class UserWallet(
    val gold: Long = 0,
    val ec: Long = 0,
    val gems: Long=0
)