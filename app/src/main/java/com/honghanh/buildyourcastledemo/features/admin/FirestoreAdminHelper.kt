package com.honghanh.buildyourcastledemo.features.admin

import com.google.firebase.firestore.FirebaseFirestore
import com.honghanh.buildyourcastledemo.core.model.HouseData
import com.honghanh.buildyourcastledemo.core.model.LinhThuData

class FirestoreAdminHelper {
    private val db = FirebaseFirestore.getInstance()

    // 🔨 Hàm thêm linh thú (Tự sinh ID ngẫu nhiên)
    fun addHouse(house: HouseData) {
        db.collection("house")
            .add(house) // 🌟 Đổi sang .add() để Firebase tự sinh ID Document ngẫu nhiên
            .addOnSuccessListener { docRef ->
                println("Thêm nhà thành công với ID ngẫu nhiên: ${docRef.id}")

                // Cập nhật ngược cái ID tự sinh đó vào trường idLinhThu trong ruột JSON
                docRef.update("idHouse", docRef.id)
                    .addOnSuccessListener { println("Đồng bộ ID vào ruột thành công!") }
            }
            .addOnFailureListener { e ->
                println("Lỗi thêm : ${e.message}")
            }
    }



}