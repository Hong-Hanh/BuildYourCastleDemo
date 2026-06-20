package com.honghanh.buildyourcastledemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.honghanh.buildyourcastledemo.features.focus.FocusScreen
import com.honghanh.buildyourcastledemo.features.shop.ShopScreen
import com.honghanh.buildyourcastledemo.features.admin.AdminScreen
import com.honghanh.buildyourcastledemo.ui.theme.BuildYourCastleDemoTheme
import com.honghanh.buildyourcastledemo.core.database.AppDatabase
import com.honghanh.buildyourcastledemo.features.focus.FocusViewModel
import com.honghanh.buildyourcastledemo.features.focus.FocusViewModelFactory
import com.honghanh.buildyourcastledemo.features.focus.data.FocusLocalRepository
import com.honghanh.buildyourcastledemo.features.auth.LoginScreen
import com.honghanh.buildyourcastledemo.features.auth.SignUpScreen
import com.honghanh.buildyourcastledemo.features.profile.ProfileScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.honghanh.buildyourcastledemo.core.database.FirebaseProvider.firestore
        enableEdgeToEdge()
        setContent {
            BuildYourCastleDemoTheme {

                // 🔥 ĐÃ SỬA: Kiểm tra xem user đã đăng nhập Firebase từ trước chưa.
                // Nếu rồi thì vào thẳng "focus", chưa thì bắt đầu từ màn "login".
                val currentUser = remember { FirebaseAuth.getInstance().currentUser }
                var currentScreen by remember {
                    mutableStateOf(if (currentUser != null) "focus" else "login")
                }

                val database = AppDatabase.getDatabase(this)

                val repository = FocusLocalRepository(database.focusSessionDao())
                val factory = FocusViewModelFactory(repository)
                val focusViewModel: FocusViewModel = viewModel(factory = factory)

                when (currentScreen) {
                    // 🔥 MÀN HÌNH ĐĂNG NHẬP
                    "login" -> LoginScreen(
                        onLoginSuccess = { currentScreen = "focus" },
                        onNavigateToSignUp = { currentScreen = "signup" }
                    )

                    // 🔥 MÀN HÌNH ĐĂNG KÝ
                    "signup" -> SignUpScreen(
                        onSignUpSuccess = { currentScreen = "focus" },
                        onNavigateToLogin = { currentScreen = "login" }
                    )

                    "focus" -> FocusScreen(
                        viewModel = focusViewModel,
                        onNavigateToProfile = { currentScreen = "profile" },
                        onNavigateToHome = { currentScreen = "focus" },
                        onNavigateToShop = { currentScreen = "shop" },
                        onNavigateToGacha = { currentScreen = "gacha" },
                        onNavigateToStorage = { currentScreen = "storage" },
                        onNavigateToStatistics = { currentScreen = "statistics" },
                        onNavigateToAdmin = { currentScreen = "admin" }
                    )

                    "shop" -> ShopScreen(onBack = { currentScreen = "focus" })

                    "admin" -> AdminScreen(onBack = { currentScreen = "focus" })

                    // --- CÁC MÀN HÌNH ĐANG DÙNG TẠM PLACEHOLDER ---
                    // Trong MainActivity.kt -> tìm đoạn "profile" cũ:
                    "profile" ->
                      ProfileScreen(onBack = { currentScreen = "focus" },
                            onLogoutSuccess = {
                                // Khi đăng xuất thành công, xóa ngăn xếp và đẩy người dùng ra lại màn hình Login
                                currentScreen = "login"
                            }
                        )


                    "gacha" -> {
                        PlaceholderScreen(title = "Màn hình Vòng quay Gacha") { currentScreen = "focus" }
                    }

                    "storage" -> {
                        PlaceholderScreen(title = "Kho (Thư viện Nhà & Linh thú)") { currentScreen = "focus" }
                    }

                    "statistics" -> {
                        PlaceholderScreen(title = "Màn hình Thống kê") { currentScreen = "focus" }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String, onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack) {
                Text("Quay lại Home")
            }
        }
    }
}