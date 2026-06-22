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
        // 1. Tự sinh ID ngẫu nhiên trước
        val generatedId = db.collection("house").document().id

        // 2. Tạo Map dữ liệu cho bảng house
        val houseMap = hashMapOf(
            "idHouse" to generatedId,
            "nameHouse" to house.nameHouse,
            "imageUrl" to house.imageUrl,
            "assetName" to house.assetName,
            "priceGold" to house.priceGold,
            "priceEC" to house.priceEC,
            "rarity" to house.rarity.name
        )

        // 3. Tạo Map dữ liệu cho bảng gachapool
        val gachaItem = hashMapOf(
            "idHouse" to generatedId,
            "nameHouse" to house.nameHouse,
            "imageUrl" to house.imageUrl,
            "description" to "Vật phẩm độ hiếm ${house.rarity.name}",
            "dropRate" to dropRate
        )

        // 🔥 TIẾN HÀNH GHI LẺ - BƯỚC 1: Ghi vào bảng 'house' trước
        db.collection("house").document(generatedId)
            .set(houseMap)
            .addOnSuccessListener {
                // Nếu bảng 'house' lưu thành công -> Tiến hành bước 2: Ghi vào 'gachapool'
                db.collection("gachapool").document(generatedId)
                    .set(gachaItem)
                    .addOnSuccessListener {
                        // Cả hai bảng đều thành công hoàn toàn
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        // Bảng house ăn nhưng bảng gacha xịt
                        android.util.Log.e("FA_ERROR", "Lỗi ghi bảng gachapool: ", e)
                        onError("Kho tổng OK nhưng Vòng quay lỗi: ${e.localizedMessage}")
                    }
            }
            .addOnFailureListener { e ->
                // Ngay từ bảng 'house' đã bị Firebase từ chối
                android.util.Log.e("FA_ERROR", "Lỗi ghi bảng house: ", e)
                onError("Lỗi ghi vào Kho tổng (house): ${e.localizedMessage}")
            }
    }
}