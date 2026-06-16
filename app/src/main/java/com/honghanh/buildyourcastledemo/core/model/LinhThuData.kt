package com.honghanh.buildyourcastledemo.core.model

data class LinhThuData(
    // nhóm định danh linh thú
    val idLinhThu:  String = "",
    val nameLinhThu: String = "",
    val rarity: Rarity = Rarity.COMMON,
    val priceGold: Int=0,
    val priceGems: Int=0,
    //Era Chronos, viết tắt EC
    val priceEC: Int=0,
    //nhóm đồ họa
    val assetName: String="",//tên file chứa animation nhân vật
    //nhóm tính cách
    val personalityType: String="", //tính cách
    val aiSystemPrompt: String="",//câu lệnh định hình giọng văn
   //nhóm chỉ số
    val affinityLevel: Int =0,//chỉ số thân thiết
    val totalFocusTime: Int=0, // tổng số phút người dùng sử dụng linh thú
    val unlockdDialogues: List<String> = emptyList()
) {
    constructor() : this("")
}