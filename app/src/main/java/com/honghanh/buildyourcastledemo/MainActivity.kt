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
import com.honghanh.buildyourcastledemo.features.focus.FocusScreen
import com.honghanh.buildyourcastledemo.features.shop.ShopScreen
import com.honghanh.buildyourcastledemo.features.admin.AdminScreen
import com.honghanh.buildyourcastledemo.ui.theme.BuildYourCastleDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.honghanh.buildyourcastledemo.core.database.FirebaseProvider.firestore
        enableEdgeToEdge()
        setContent {
            BuildYourCastleDemoTheme {
                // Quản lý trạng thái màn hình hiện tại
                var currentScreen by remember { mutableStateOf("focus") }

                when (currentScreen) {
                    "focus" -> FocusScreen(
                        onNavigateToProfile = { currentScreen = "profile" },
                        onNavigateToHome = { currentScreen = "focus" },
                        onNavigateToShop = { currentScreen = "shop" },
                        onNavigateToGacha = { currentScreen = "gacha" },
                        onNavigateToStorage = { currentScreen = "storage" },
                        onNavigateToStatistics = { currentScreen = "statistics" },
                        onNavigateToAdmin = { currentScreen = "admin" }
                    )

                    "shop" -> ShopScreen(onBack = { currentScreen = "focus" })

                    // --- CÁC MÀN HÌNH ĐANG DÙNG TẠM PLACEHOLDER ---
                    "profile" -> {
                        PlaceholderScreen(title = "Màn hình Profile") { currentScreen = "focus" }
                    }

                    "gacha" -> {
                        PlaceholderScreen(title = "Màn hình Vòng quay Gacha") { currentScreen = "focus" }
                    }

                    "storage" -> {
                        PlaceholderScreen(title = "Kho (Thư viện Nhà & Linh thú)") { currentScreen = "focus" }
                    }
                    "admin" -> AdminScreen(onBack = { currentScreen = "focus" })// Bấm nút Back trên thanh TopAppBar sẽ quay về màn hình Focus




                    "statistics" -> {
                        PlaceholderScreen(title = "Màn hình Thống kê") { currentScreen = "focus" }
                    }
                }
            }
        }
    }
}

/**
 * Hàm hiển thị màn hình tạm thời (Placeholder) để tránh lỗi biên dịch.
 * Khi bạn tạo file code chính thức cho từng màn hình, chỉ cần xóa nhánh tương ứng
 * ở trên và thay bằng Composable thật của bạn.
 */
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