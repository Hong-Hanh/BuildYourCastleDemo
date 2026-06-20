package com.honghanh.buildyourcastledemo.features.shop

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.honghanh.buildyourcastledemo.core.model.HouseData
import com.honghanh.buildyourcastledemo.core.model.Rarity
import com.honghanh.buildyourcastledemo.core.model.UserProfile

class ShopViewModel : ViewModel() {
    private val listHouse = mutableListOf<HouseData>()
    val listVatPhamLT = mutableStateListOf<HouseData>()
    var currentHouseId = mutableStateOf("")
        private set
    var wallet = mutableStateOf(UserProfile(currentGold = 0, currentEC = 0))
        private set

    private var listIdDaSoHuu = emptyList<String>()
    private val firestore = FirebaseFirestore.getInstance()
    private val mockUserId =  FirebaseAuth.getInstance().currentUser?.uid ?: ""

    init {
        layLtTuFb()
        taiThongTinUserFb()
    }

    private fun layLtTuFb() {
        firestore.collection("house")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    listHouse.clear()
                    for (doc in snapshot.documents) {
                        try {
                            // ← thêm load rarity từ Firebase
                            val rarityString = doc.getString("rarity") ?: "COMMON"
                            val rarity = try {
                                Rarity.valueOf(rarityString)
                            } catch (e: Exception) { Rarity.COMMON }

                            val item = HouseData(
                                idHouse = doc.id,
                                nameHouse = doc.getString("nameHouse") ?: "",
                                rarity = rarity,
                                priceGold = doc.getLong("priceGold")?.toInt() ?: 0,
                                priceEC = doc.getLong("priceEC")?.toInt() ?: 0,
                                assetName = doc.getString("assetName") ?: "",
                                imageUrl = doc.getString("imageUrl") ?: "",
                                isOwned = false
                            )
                            listHouse.add(item)
                        } catch (e: Exception) {
                            println("Bỏ qua 1 document lỗi: ${e.message}")
                        }
                    }
                    tronDuLieuSoHuu() // ← gộp isOwned sau khi load xong
                }
            }
    }

    private fun taiThongTinUserFb() {
        firestore.collection("UserProfile").document(mockUserId)
            .addSnapshotListener { doc, error ->
                if (error != null) return@addSnapshotListener
                if (doc != null && doc.exists()) {
                    wallet.value = UserProfile(
                        currentGold = doc.getLong("currentGold")?.toInt() ?: 0,
                        currentEC = doc.getLong("currentEC")?.toInt() ?: 0
                    )
                    currentHouseId.value = doc.getString("idHouse") ?: ""

                    // ← load danh sách đã mua → dùng cho kho vật phẩm sau này
                    listIdDaSoHuu = doc.get("ownedHouses") as? List<String> ?: emptyList()
                    tronDuLieuSoHuu()
                } else {
                    // Tạo user mới nếu chưa có
                    firestore.collection("UserProfile").document(mockUserId)
                        .set(mapOf(
                            "userId" to mockUserId,
                            "currentGold" to 0,
                            "currentEC" to 0,
                            "idHouse" to "",
                            "ownedHouses" to emptyList<String>()
                        ))
                }
            }
    }

    // Đánh dấu isOwned cho từng item — chạy sau khi cả 2 list đã load xong
    private fun tronDuLieuSoHuu() {
        if (listHouse.isEmpty()) return
        val updated = listHouse.map { house ->
            house.copy(isOwned = house.idHouse in listIdDaSoHuu)
        }
        listVatPhamLT.clear()
        listVatPhamLT.addAll(updated)
    }

    fun buyHouse(house: HouseData) {
        val currentWallet = wallet.value
        val isBuyWithEC = house.priceEC > 0

        val duTien = if (isBuyWithEC) {
            currentWallet.currentEC >= house.priceEC
        } else {
            currentWallet.currentGold >= house.priceGold
        }

        if (!duTien) {
            println("Không đủ tiền!")
            return
        }

        // Cập nhật UI ngay
        wallet.value = if (isBuyWithEC) {
            currentWallet.copy(currentEC = currentWallet.currentEC - house.priceEC)
        } else {
            currentWallet.copy(currentGold = currentWallet.currentGold - house.priceGold)
        }

        // Đánh dấu isOwned local ngay
        val index = listVatPhamLT.indexOfFirst { it.idHouse == house.idHouse }
        if (index != -1) listVatPhamLT[index] = listVatPhamLT[index].copy(isOwned = true)

        // Cập nhật Firebase
        firestore.collection("UserProfile").document(mockUserId)
            .update(mapOf(
                "currentGold" to wallet.value.currentGold,
                "currentEC" to wallet.value.currentEC,
                // ← thêm vào mảng ownedHouses để kho vật phẩm sau này dùng
                "ownedHouses" to com.google.firebase.firestore.FieldValue.arrayUnion(house.idHouse)
            ))
            .addOnSuccessListener { println("Mua thành công: ${house.nameHouse}") }
            .addOnFailureListener { e ->
                // Rollback nếu lỗi
                wallet.value = currentWallet
                if (index != -1) listVatPhamLT[index] = listVatPhamLT[index].copy(isOwned = false)
                println("Lỗi mua: ${e.message}")
            }
    }

    fun equipHouse(houseId: String, imageUrl: String) {
        currentHouseId.value = houseId

        firestore.collection("UserProfile").document(mockUserId)
            .update(mapOf(
                "idHouse" to houseId,
                "currentHouseImageUrl" to imageUrl  // ← thêm dòng này
            ))
            .addOnSuccessListener { println("Trang bị thành công") }
            .addOnFailureListener { e -> println("Lỗi trang bị: ${e.message}") }
    }

    fun setCurrentHouseIdFromServer(idFromServer: String) {
        currentHouseId.value = idFromServer
    }
}