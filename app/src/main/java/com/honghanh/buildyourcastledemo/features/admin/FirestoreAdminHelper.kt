package com.honghanh.buildyourcastledemo.features.admin

import com.google.firebase.firestore.FirebaseFirestore
import com.honghanh.buildyourcastledemo.core.model.HouseData
// =======================================================
// 👑 CẬP NHẬT FIRESTORE ADMIN HELPER (TÁCH BIỆT 2 TÍNH NĂNG)
// =======================================================
class FirestoreAdminHelper {
    private val db = FirebaseFirestore.getInstance()

    // HÀM 1: CHỈ ĐẨY LÊN BẢNG SHOP (KHO TỔNG)
    fun addOnlyToShop(
        house: HouseData,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val docRef = db.collection("house").document()
        val finalizedHouse = house.copy(idHouse = docRef.id)

        docRef.set(finalizedHouse)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError("Lỗi đăng Shop: ${e.message}") }
    }

    // 🔥 HÀM 2 ĐÃ SỬA: LƯU VÀO GACHAPOOL ĐỒNG THỜI LƯU THẲNG VÀO KHO TỔNG 'HOUSE'
    fun addGachaAndBaseHouse(
        house: HouseData,
        dropRate: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Tạo một ID ngẫu nhiên dùng chung duy nhất cho cả 2 bảng dữ liệu
        val houseRef = db.collection("house").document()
        val generatedId = houseRef.id

        val finalizedHouse = house.copy(idHouse = generatedId)

        // Tạo Map chứa dữ liệu đồng nhất cho GachaPool
        val gachaItem = hashMapOf(
            "idHouse" to generatedId,
            "nameHouse" to finalizedHouse.nameHouse,
            "imageUrl" to finalizedHouse.imageUrl,
            "description" to "Vật phẩm độ hiếm ${finalizedHouse.rarity.name}",
            "dropRate" to dropRate
        )

        // Sử dụng Write Batch để đảm bảo tính toàn vẹn dữ liệu (Ghi cả hai hoặc hủy bỏ)
        val batch = db.batch()

        // 1. Đưa lệnh lưu vào bảng kho tổng 'house' vào batch
        batch.set(houseRef, finalizedHouse)

        // 2. Đưa lệnh lưu vào bảng 'GachaPool' vào batch với ID tài liệu trùng khớp
        val gachaRef = db.collection("gachapool").document(generatedId)
        batch.set(gachaRef, gachaItem)

        batch.commit()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError("Lỗi đồng bộ Gacha & Kho tổng: ${e.message}") }
    }
}