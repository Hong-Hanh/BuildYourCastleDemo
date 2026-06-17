package com.honghanh.buildyourcastledemo.features.admin

import com.google.firebase.firestore.FirebaseFirestore
import com.honghanh.buildyourcastledemo.core.model.HouseData

class FirestoreAdminHelper {
    private val db = FirebaseFirestore.getInstance()

    fun addHouse(
        house: HouseData,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        db.collection("house")
            .add(house)
            .addOnSuccessListener { docRef ->
                docRef.update("idHouse", docRef.id)
                println("Thêm nhà thành công: ${docRef.id}")
                onSuccess()
            }
            .addOnFailureListener { e: Exception ->
                onError("Lỗi: ${e.message ?: "Không xác định"}")
            }
    }
}