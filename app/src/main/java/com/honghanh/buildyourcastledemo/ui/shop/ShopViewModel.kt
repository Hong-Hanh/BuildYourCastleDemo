package com.honghanh.buildyourcastledemo.ui.shop

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.honghanh.buildyourcastledemo.core.model.CurencyType
import com.honghanh.buildyourcastledemo.core.model.LinhThuData
import com.honghanh.buildyourcastledemo.core.model.UserWallet

// ShopViewModel kế thừa ViewModel để quản lý dữ liệu Shop
class ShopViewModel : ViewModel() {
    // Danh sách linh thú lấy từ Firebase
    val listVatPhamLT = mutableStateListOf<LinhThuData>()
    
    // Giả lập ví tiền của người dùng (Sau này sẽ lấy từ Firebase/User Profile)
    var wallet = mutableStateOf(UserWallet(gold = 1000, ec = 50, gems = 100))
        private set

    private val firestore = FirebaseFirestore.getInstance()

    init {
        layLtTuFb()
    }

    private fun layLtTuFb() {
        firestore.collection("linh_thu")
            .get()
            .addOnSuccessListener { result ->
                listVatPhamLT.clear()
                for (document in result) {
                    val linhthu = document.toObject<LinhThuData>()
                    listVatPhamLT.add(linhthu)
                }
            }
    }
    
    fun buyLinhThu(linhThu: LinhThuData) {
        // Logic xử lý mua thú (trừ tiền, v.v.)
        println("Đang mua: ${linhThu.nameLinhThu}")
    }
}
