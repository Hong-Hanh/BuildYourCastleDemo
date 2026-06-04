package com.honghanh.buildyourcastledemo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.honghanh.buildyourcastledemo.R


// 1. Khai báo FontFamily với font Comfortaa
val Comfortaa = FontFamily(
    Font(R.font.comfortaa, FontWeight.Normal),
    Font(R.font.comfortaa, FontWeight.Medium),
    Font(R.font.comfortaa, FontWeight.SemiBold),
    Font(R.font.comfortaa, FontWeight.Bold),
    Font(R.font.comfortaa, FontWeight.Black)
)

// 2. Cấu hình Typography để áp dụng Comfortaa cho toàn bộ hệ thống
val Typography = Typography(
    displayLarge = TextStyle(fontFamily = Comfortaa),
    displayMedium = TextStyle(fontFamily = Comfortaa),
    displaySmall = TextStyle(fontFamily = Comfortaa),
    headlineLarge = TextStyle(fontFamily = Comfortaa),
    headlineMedium = TextStyle(fontFamily = Comfortaa),
    headlineSmall = TextStyle(fontFamily = Comfortaa),
    titleLarge = TextStyle(fontFamily = Comfortaa),
    titleMedium = TextStyle(fontFamily = Comfortaa),
    titleSmall = TextStyle(fontFamily = Comfortaa),
    bodyLarge = TextStyle(
        fontFamily = Comfortaa,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(fontFamily = Comfortaa),
    bodySmall = TextStyle(fontFamily = Comfortaa),
    labelLarge = TextStyle(fontFamily = Comfortaa),
    labelMedium = TextStyle(fontFamily = Comfortaa),
    labelSmall = TextStyle(fontFamily = Comfortaa)
)
