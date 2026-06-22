package com.honghanh.buildyourcastledemo.features.gacha

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.honghanh.buildyourcastledemo.core.database.FirebaseProvider
import com.honghanh.buildyourcastledemo.core.model.HouseData
import kotlin.random.Random

class GachaViewModel : ViewModel() {

    private val db = FirebaseProvider.firestore
    private val auth = FirebaseAuth.getInstance()

    var currentGold = mutableIntStateOf(0)
    var currentEC = mutableIntStateOf(0)

    // 🔥 ĐÃ SỬA: Chuyển sang List để chứa được cả 1 kết quả (x1) hoặc 10 kết quả (x10)
    var rewardList = mutableStateListOf<HouseData>()
        private set

    var isSpinning = mutableStateOf(false)
    var gachaMessage = mutableStateOf<String?>(null)

    val costX1 = 20
    val costX10 = 200 // Chi phí quay 10 lượt

    var gachaPool = mutableStateListOf<HouseData>()
        private set

    // SỬA: Đảm bảo giá trị Value nhận vào là Double
    private val itemDropRates = mutableMapOf<String, Double>()

    var isLoadingPool = mutableStateOf(true)
        private set

    init {
        taiThongTinVi()
        taiDuLieuVongQuayGacha()
    }

    private fun taiThongTinVi() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("UserProfile").document(uid)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    currentGold.value = snapshot.getLong("currentGold")?.toInt() ?: 0
                    currentEC.value = snapshot.getLong("currentEC")?.toInt() ?: 0
                }
            }
    }

    private fun taiDuLieuVongQuayGacha() {
        isLoadingPool.value = true // Bật loading khi bắt đầu tải

        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        db.collection("gachapool")
            .get()
            .addOnSuccessListener { documents ->
                gachaPool.clear()
                itemDropRates.clear()

                if (documents.isEmpty) {
                    gachaMessage.value = "Hệ thống vòng quay hiện tại chưa có dữ liệu vật phẩm!"
                    isLoadingPool.value = false // Tắt ngay nếu bảng rỗng
                    return@addOnSuccessListener
                }

                for (doc in documents) {
                    try {
                        // Lấy an toàn các trường dữ liệu để tránh crash/treo do ép kiểu Double
                        val idHouse = doc.getString("idHouse") ?: ""
                        val nameHouse = doc.getString("nameHouse") ?: ""
                        val imageUrl = doc.getString("imageUrl") ?: ""

                        // Xử lý an toàn trường dropRate (Firestore hay tự hiểu nhầm Long/Double)
                        val dropRateRaw = doc.get("dropRate")
                        val dropRate = when (dropRateRaw) {
                            is Number -> dropRateRaw.toDouble()
                            else -> 0.0
                        }

                        if (idHouse.isNotEmpty()) {
                            val item = HouseData(
                                idHouse = idHouse,
                                nameHouse = nameHouse,
                                imageUrl = imageUrl,
                                rarity = com.honghanh.buildyourcastledemo.core.model.Rarity.COMMON // Tạm thời mặc định hoặc lấy từ Firestore
                            )
                            gachaPool.add(item)
                            itemDropRates[idHouse] = dropRate
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // 🔥 TẮT LOADING: Đặt ở ngoài vòng lặp để chắc chắn luôn chạy khi tải xong dữ liệu thành công
                isLoadingPool.value = false
            }
            .addOnFailureListener { exception ->
                gachaMessage.value = "Lỗi tải vòng quay: ${exception.localizedMessage}"
                // 🔥 TẮT LOADING: Thất bại cũng phải tắt để không treo giao diện
                isLoadingPool.value = false
            }
    }

    // 🔥 HÀM TỔNG HỢP: Xử lý quay tùy chọn theo số lượt truyền vào (1 hoặc 10)
    fun kichHoatQuayGacha(soLuotQuay: Int) {
        val uid = auth.currentUser?.uid ?: return
        val tongChiPhi = if (soLuotQuay == 10) costX10 else costX1

        if (gachaPool.isEmpty() || itemDropRates.isEmpty()) {
            gachaMessage.value = "Hệ thống đang tải dữ liệu vòng quay!"
            return
        }

        if (currentEC.value < tongChiPhi) {
            gachaMessage.value = "Bạn không đủ Tinh thể Aether (AC) để thực hiện lượt quay này."
            return
        }

        isSpinning.value = true
        rewardList.clear() // Xóa danh sách quà cũ
        gachaMessage.value = null

        // 1. Trừ tổng tiền trên Firestore
        db.collection("UserProfile").document(uid)
            .update("currentEC", FieldValue.increment(-tongChiPhi.toLong()))
            .addOnSuccessListener {

                // 2. Chạy vòng lặp để tạo ra danh sách vật phẩm trúng thưởng ngẫu nhiên
                val danhSachTam = mutableListOf<HouseData>()
                repeat(soLuotQuay) {
                    tinhToanQuayTheoTyLe()?.let { danhSachTam.add(it) }
                }

                // Giả lập hiệu ứng chờ quay
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    isSpinning.value = false
                    rewardList.addAll(danhSachTam)

                    // 3. Tiến hành lưu tất cả vật phẩm vừa quay được vào kho
                    capNhatNhieuVatPhamVaoKho(uid, danhSachTam)
                }, 2500)
            }
            .addOnFailureListener {
                isSpinning.value = false
                gachaMessage.value = "Lỗi kết nối mạng!"
            }
    }

    private fun tinhToanQuayTheoTyLe(): HouseData? {
        // 1. Tính tổng tỷ lệ (Dùng 0.0 thay vì 0)
        var tongTyLe = 0.0
        for (house in gachaPool) {
            tongTyLe += itemDropRates[house.idHouse] ?: 0.0 // 🔥 ĐÃ SỬA: 0.0 chứ không phải 0
        }

        if (tongTyLe <= 0.0) return gachaPool.randomOrNull()

        // 2. Quay số thực ngẫu nhiên
        val xucXac = kotlin.random.Random.nextDouble(0.0, tongTyLe)
        var mốcChạy = 0.0

        // 3. Tìm vật phẩm trúng thưởng
        for (house in gachaPool) {
            val tyLeCuaItem = itemDropRates[house.idHouse] ?: 0.0 // 🔥 ĐÃ SỬA: 0.0 chứ không phải 0
            mốcChạy += tyLeCuaItem
            if (xucXac <= mốcChạy) {
                return house
            }
        }
        return gachaPool.randomOrNull()
    }

    // 🔥 XỬ LÝ LƯU TOÀN BỘ DANH SÁCH (Hỗ trợ x10 chống trùng lặp tối ưu)
    private fun capNhatNhieuVatPhamVaoKho(userId: String, items: List<HouseData>) {
        val userRef = db.collection("UserProfile").document(userId)

        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val ownedAvatars = snapshot.get("ownedAvatars") as? List<*> ?: emptyList<Any>()
                val ownedHouses = snapshot.get("ownedHouses") as? List<*> ?: emptyList<Any>()

                val listAvatarMoi = mutableListOf<String>()
                val listHouseMoi = mutableListOf<String>()
                var tongVangHoanLai = 0

                // Duyệt qua 10 món quà vừa quay được để phân loại xem món nào trùng, món nào chưa có
                for (item in items) {
                    val isAvatar = item.imageUrl.isEmpty()
                    val itemIdentifier = if (isAvatar) item.assetName else item.imageUrl

                    if (isAvatar) {
                        if (ownedAvatars.contains(itemIdentifier) || listAvatarMoi.contains(itemIdentifier)) {
                            tongVangHoanLai += 150 // Trùng đổi ra vàng
                        } else {
                            listAvatarMoi.add(itemIdentifier)
                        }
                    } else {
                        if (ownedHouses.contains(itemIdentifier) || listHouseMoi.contains(itemIdentifier)) {
                            tongVangHoanLai += 150
                        } else {
                            listHouseMoi.add(itemIdentifier)
                        }
                    }
                }

                // Thực hiện 1 lệnh update duy nhất lên Firestore để tiết kiệm băng thông và tối ưu hiệu năng
                val updates = mutableMapOf<String, Any>()
                if (listAvatarMoi.isNotEmpty()) updates["ownedAvatars"] = FieldValue.arrayUnion(*listAvatarMoi.toTypedArray())
                if (listHouseMoi.isNotEmpty()) updates["ownedHouses"] = FieldValue.arrayUnion(*listHouseMoi.toTypedArray())
                if (tongVangHoanLai > 0) updates["currentGold"] = FieldValue.increment(tongVangHoanLai.toLong())

                if (updates.isNotEmpty()) {
                    userRef.update(updates).addOnSuccessListener {
                        gachaMessage.value = if (tongVangHoanLai > 0) {
                            "Đã nhận vật phẩm mới thành công! Hoàn lại +$tongVangHoanLai Xu Vàng cho các vật phẩm bị trùng."
                        } else {
                            "🎉 Chúc mừng! Toàn bộ vật phẩm hiếm đã được chuyển vào kho."
                        }
                    }
                }
            }
        }
    }

    fun xoaThongBao() {
        gachaMessage.value = null
        rewardList.clear()
    }
}