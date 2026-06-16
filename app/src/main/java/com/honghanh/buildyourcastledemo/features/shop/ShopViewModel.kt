package com.honghanh.buildyourcastledemo.features.shop

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.honghanh.buildyourcastledemo.core.model.HouseData

import com.honghanh.buildyourcastledemo.core.model.UserWallet

// ShopViewModel kế thừa ViewModel để quản lý dữ liệu Shop
class ShopViewModel : ViewModel() {
    // Danh sách linh thú lấy từ Firebase
    val listVatPhamLT = mutableStateListOf<HouseData>()

    //  ví tiền của người dùng
    var wallet = mutableStateOf(UserWallet(gold = 0, ec = 0, gems = 0))
        private set

    private val firestore = FirebaseFirestore.getInstance()

    init {
        layLtTuFb()
    }

    private fun layLtTuFb() {
        firestore.collection("linh_thu")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Lỗi lấy dữ liệu: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    listVatPhamLT.clear()
                    for (doc in snapshot.documents) {
                        try {
                            // Cách bốc dữ liệu an toàn tuyệt đối:
                            // Ta lấy id chuẩn từ doc.id (luôn là String), các trường còn lại bốc từ map về
                            val item = HouseData(
                                idHouse = doc.id, // 🌟 Lấy thẳng ID của Document, chấp tất cả các thể loại lỗi trong ruột!
                                nameHouse = doc.getString("nameHouse") ?: "",
                                priceGold = doc.getLong("priceGold")?.toInt() ?: 0,
                                priceEC = doc.getLong("priceEC")?.toInt() ?: 0,
                                assetName = doc.getString("assetName") ?: "",
                            )

                            listVatPhamLT.add(item)
                        } catch (e: Exception) {
                            // Lỡ như có document nào lỗi quá nặng, app cũng chỉ log ra chứ không bị sập (Crash-free)
                            println("Bỏ qua 1 document lỗi: ${e.message}")
                        }
                    }
                }
            }
    }

    fun buyHouse(linhThu: HouseData) {
        // Logic xử lý mua thú (trừ tiền, v.v.)
        val currentWallet = wallet.value
        if (currentWallet.gold >= linhThu.priceGold || currentWallet.ec >= linhThu.priceEC) {
            wallet.value = currentWallet.copy(gold = currentWallet.gold - linhThu.priceGold,
                ec = currentWallet.ec + linhThu.priceEC,)
            println("Mua thành công: ${linhThu.nameHouse}")
        } else {
            println("Không đủ tiền")
        }
    }
}