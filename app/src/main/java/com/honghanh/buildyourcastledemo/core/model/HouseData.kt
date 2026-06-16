package com.honghanh.buildyourcastledemo.core.model

data class HouseData(
    val idHouse: String = "",
    val nameHouse: String = "",
    val rarity: Rarity = Rarity.COMMON,
    val priceGold: Int = 0,
    val priceEC: Int = 0,
    val assetName: String = "",
    val description: String = "",
    val imageUrl: String = ""
)