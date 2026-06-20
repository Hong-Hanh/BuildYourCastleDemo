package com.honghanh.buildyourcastledemo.core.model

data class UserProfile(
    val userId: String = "",
    val currentGold: Int = 0,
    val currentEC: Int = 0,
    val idHouse: String = "",
    val currentHouseImageUrl: String = "",  // ← thêm
    val ownedHouses: List<String> = emptyList(),  // ← thêm luôn cho đồng bộ
    val currentAvatarUrl: String = ""
)