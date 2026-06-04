package com.honghanh.buildyourcastledemo.core.model

sealed class GachaReward{
    data class Currency(val type: Currency, val amount: Int): GachaReward()
    data class LinhThu(val data: LinhThuData): GachaReward()
    //data class House
}
data class GachaSeason(
    val id: String,
    val name: String,
    val startTime: Long,
    val endTime: Long,
    val poorLinhThu: List<LinhThuData>,
    val costPerRoll: Int=10
)