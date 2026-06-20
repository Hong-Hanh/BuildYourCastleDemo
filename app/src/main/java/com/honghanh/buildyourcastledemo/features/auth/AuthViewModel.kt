package com.honghanh.buildyourcastledemo.features.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.honghanh.buildyourcastledemo.core.database.FirebaseProvider
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseProvider.firestore

    var isLoading = mutableStateOf(false)
        private set
    var errorMessage = mutableStateOf("")
        private set
    var isLoggedIn = mutableStateOf(auth.currentUser != null)
        private set

    // Lấy userId hiện tại — dùng thay mockUserId ở toàn app
    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    // Đăng ký email/password
    fun signUp(name: String, email: String, password: String, onSuccess: () -> Unit) {
        isLoading.value = true
        errorMessage.value = ""

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val userId = result.user?.uid ?: return@addOnSuccessListener

                // Tạo UserProfile trên Firestore
                val newUser = mapOf(
                    "userId" to userId,
                    "name" to name,
                    "currentGold" to 0,
                    "currentEC" to 0,
                    "idHouse" to "",
                    "currentHouseImageUrl" to "",
                    "ownedHouses" to emptyList<String>()
                )
                firestore.collection("UserProfile").document(userId)
                    .set(newUser)
                    .addOnSuccessListener {
                        isLoading.value = false
                        isLoggedIn.value = true
                        onSuccess()
                    }
            }
            .addOnFailureListener { e ->
                isLoading.value = false
                errorMessage.value = when {
                    e.message?.contains("email address is already in use") == true ->
                        "Email này đã được đăng ký"
                    e.message?.contains("badly formatted") == true ->
                        "Email không hợp lệ"
                    else -> "Đăng ký thất bại: ${e.message}"
                }
            }
    }

    // Đăng nhập email/password
    fun login(email: String, password: String, onSuccess: () -> Unit) {
        isLoading.value = true
        errorMessage.value = ""

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                isLoading.value = false
                isLoggedIn.value = true
                onSuccess()
            }
            .addOnFailureListener { e ->
                isLoading.value = false
                errorMessage.value = when {
                    e.message?.contains("password is invalid") == true ->
                        "Sai mật khẩu"
                    e.message?.contains("no user record") == true ->
                        "Email chưa được đăng ký"
                    else -> "Đăng nhập thất bại: ${e.message}"
                }
            }
    }

    // Đăng nhập Google — nhận idToken từ GoogleSignIn
    fun loginWithGoogle(idToken: String, onSuccess: () -> Unit) {
        isLoading.value = true
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val user = result.user ?: return@addOnSuccessListener
                val isNewUser = result.additionalUserInfo?.isNewUser == true

                // Chỉ tạo UserProfile nếu là user mới
                if (isNewUser) {
                    val newUser = mapOf(
                        "userId" to user.uid,
                        "name" to (user.displayName ?: ""),
                        "currentGold" to 0,
                        "currentEC" to 0,
                        "idHouse" to "",
                        "currentHouseImageUrl" to "",
                        "ownedHouses" to emptyList<String>()
                    )
                    firestore.collection("UserProfile").document(user.uid).set(newUser)
                }

                isLoading.value = false
                isLoggedIn.value = true
                onSuccess()
            }
            .addOnFailureListener { e ->
                isLoading.value = false
                errorMessage.value = "Đăng nhập Google thất bại: ${e.message}"
            }
    }

    // Đăng xuất
    fun logout(onSuccess: () -> Unit) {
        auth.signOut()
        isLoggedIn.value = false
        onSuccess()
    }
}