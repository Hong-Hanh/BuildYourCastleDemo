package com.honghanh.buildyourcastledemo.features.gacha

/**
 * Thực thể ánh xạ chính xác cấu trúc dữ liệu của một tài liệu (Document)
 * nằm trong bộ sưu tập "GachaPool" trên Firestore.
 * * Lưu ý: Các giá trị mặc định được gán sẵn (như "", 0, false) giúp Firestore
 * có thể tự động ép kiểu (Deserialization) thông qua hàm .toObject() mà không bị lỗi.
 */


/**
 * Thực thể cấu hình Gacha với dropRate hỗ trợ số thực (Double)
 */
data class GachaPoolItem(
    val idGachaItem: String = "",
    val idHouse: String = "",
    val dropRate: Double = 0.0,
    val isRarityGuaranteed: Boolean = false
)