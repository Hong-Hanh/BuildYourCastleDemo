package com.honghanh.buildyourcastledemo.features.profile

// 🔥 ĐÃ SỬA: Import đúng R của dự án, XOÁ bỏ hoàn toàn 'import android.R'
import com.honghanh.buildyourcastledemo.R
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.honghanh.buildyourcastledemo.core.database.FirebaseProvider
import com.honghanh.buildyourcastledemo.core.model.UserProfile

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseProvider.firestore

    // Thông tin tài khoản từ Firebase Auth (Tên, Email)
    var accountName = mutableStateOf("")
        private set
    var accountEmail = mutableStateOf("")
        private set

    // Thông tin tiến trình từ Firestore
    var userProfile = mutableStateOf<UserProfile?>(null)
        private set

    var isLoading = mutableStateOf(false)
        private set

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        val userId = currentUser?.uid ?: return

        accountName.value = currentUser.displayName ?: "Alex Gardener" // Tên mặc định nếu chưa cập nhật
        accountEmail.value = currentUser.email ?: ""
        isLoading.value = true

        firestore.collection("UserProfile").document(userId)
            .addSnapshotListener { snapshot, error ->
                isLoading.value = false
                if (snapshot != null && snapshot.exists()) {
                    // Map trực tiếp từ Firestore sang Data Class của bạn
                    val profile = snapshot.toObject(UserProfile::class.java)
                    userProfile.value = profile
                }
            }
    }

    fun getAvatarResource(avatarName: String?): Int {
        // 🔥 ĐÃ SỬA: Bỏ đuôi '.png', chỉ giữ lại tên file định danh viết thường, không dấu
        return when (avatarName) {
            "avatar_hopsua" -> R.drawable.hopsua
            "avatar_maycafe" -> R.drawable.maycafe
            "avatar_banhcozy" -> R.drawable.banhcozy
            "avatar_dienthoai" -> R.drawable.dienthoai
            "avatar_hopbut" -> R.drawable.hopbut

            // Nếu không khớp tên nào ở trên hoặc dữ liệu Firebase chưa về kịp, trả về ảnh mặc định an toàn
            else -> R.drawable.thungrac
        }
    }

    fun updateAvatarName(avatarName: String) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("UserProfile")
            .document(uid)
            .update("currentAvatarUrl", avatarName) // 🔥 ĐÃ SỬA: Lưu vào currentAvatarUrl
            .addOnSuccessListener {
                // Cập nhật thành công
            }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        auth.signOut()
        onLogoutSuccess()
    }
}