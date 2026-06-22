package com.honghanh.buildyourcastledemo.features.inventory

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.honghanh.buildyourcastledemo.core.model.HouseData
import kotlin.collections.emptyList

class InventoryViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    var isLoading = mutableStateOf(true)
    var errorMessage = mutableStateOf<String?>(null)

    // ID của ngôi nhà/không gian hiện tại đang được trang bị (UserProfile.idHouse)
    var currentEquippedId = mutableStateOf("")

    // Danh sách các vật phẩm kiến trúc hợp lệ mà người dùng sở hữu
    var ownedHouses = mutableStateListOf<HouseData>()

    init {
        taiKhoDoTuUserProfile()
    }

    fun taiKhoDoTuUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            errorMessage.value = "Người dùng chưa đăng nhập!"
            isLoading.value = false
            return
        }

        isLoading.value = true
        errorMessage.value = null

        // 1. Đọc trực tiếp từ Collection 'UserProfile' theo UID của Hạnh
        db.collection("UserProfile")
            .document(userId)
            .get()
            .addOnSuccessListener { userDocument ->
                if (!userDocument.exists()) {
                    errorMessage.value = "Không tìm thấy hồ sơ người dùng!"
                    isLoading.value = false
                    return@addOnSuccessListener
                }

                // Lấy ID đang dùng hiện tại
                currentEquippedId.value = userDocument.getString("idHouse") ?: ""

                // Lấy mảng ownedHouses và lọc sạch các phần tử không phải ID (như link URL bị lẫn)
                //  DÒNG MỚI ĐÃ THÊM <String> TƯỜNG MINH:
                val rawOwnedList = userDocument.get("ownedHouses") as? List<*> ?: emptyList<String>()
                val validOwnedIds = rawOwnedList.filterIsInstance<String>().filter { id ->
                    // Lọc bỏ các chuỗi là link URL, chỉ giữ lại ID thuần túy
                    !id.startsWith("http://") && !id.startsWith("https://") && id.isNotBlank()
                }

                if (validOwnedIds.isEmpty()) {
                    ownedHouses.clear()
                    isLoading.value = false
                    return@addOnSuccessListener
                }

                // 2. Truy vấn kho tổng 'house' để lấy đầy đủ Name, ImageUrl, Rarity
                db.collection("house")
                    .get()
                    .addOnSuccessListener { houseDocuments ->
                        ownedHouses.clear()
                        for (doc in houseDocuments) {
                            // 🔥 SỬA TẠI ĐÂY: Sử dụng cách ép kiểu Java truyền thống kết hợp toán tử an toàn của Kotlin
                            val house = doc.toObject(HouseData::class.java) as? HouseData

                            // Nếu ép kiểu thành công và ID nằm trong danh sách đã sở hữu thì hiển thị
                            if (house != null && validOwnedIds.contains(house.idHouse)) {
                                ownedHouses.add(house)
                            }
                        }
                        isLoading.value = false
                    }
                    .addOnFailureListener { e ->
                        errorMessage.value = "Lỗi lấy chi tiết kho tổng: ${e.localizedMessage}"
                        isLoading.value = false
                    }
            }
            .addOnFailureListener { e ->
                errorMessage.value = "Lỗi kết nối UserProfile: ${e.localizedMessage}"
                isLoading.value = false
            }
    }

    // Hàm cập nhật trạng thái thay đổi không gian nền đang dùng
    fun trangBiKhongGian(house: HouseData, onComplete: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        val updates = hashMapOf<String, Any>(
            "idHouse" to house.idHouse,
            "currentHouseImageUrl" to house.imageUrl
        )

        db.collection("UserProfile")
            .document(userId)
            .update(updates)
            .addOnSuccessListener {
                currentEquippedId.value = house.idHouse
                onComplete()
            }
    }
}