package com.honghanh.buildyourcastledemo.core.model

data class UserProfile(

    val userId: String = "",
    val currentGold: Int = 0,
    val currentEC: Int = 0,
    val idHouse: String = "" // Mẫu lâu đài đang chọn
)
