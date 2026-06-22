package com.honghanh.buildyourcastledemo.features.performance


import androidx.annotation.RestrictTo
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Arrangement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceScreen(
    viewModel: PerformanceViewModel = viewModel(),
    onBack: () -> Unit
) {
    val state = viewModel.performanceState.value
    val isLoading = viewModel.isLoading.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thống Kê Hiệu Suất", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. KHỐI CARD TỔNG QUAN
                Text("Tổng Quan Lâu Đài", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(title = "Phút Tập Trung", value = "${state.totalFocusMinutes}m", modifier = Modifier.weight(1f))
                    StatCard(title = "Nhiệm Vụ Xong", value = "${state.totalTasksCompleted}", modifier = Modifier.weight(1f))
                    // SỬA LẠI DÒNG NÀY TRONG PERFORMANCE SCREEN:
                    StatCard(
                        title = "Tinh Thể AC",
                        value = "+${state.totalAetherCrystalsEarned}",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.tertiary // 🔥 Lấy tông màu thứ 3 trong Theme của Hạnh
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. KHỐI BIỂU ĐỒ CỘT TUẦN
                Text("Thời Gian Tập Trung Trong Tuần", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Card(
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val days = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
                        val maxMinutes = state.weeklyFocusMinutes.maxOrNull()?.coerceAtLeast(1) ?: 1

                        state.weeklyFocusMinutes.forEachIndexed { index, minutes ->
                            // Tính tỷ lệ chiều cao của cột (Max tương ứng với 150.dp)
                            val barHeightRatio = minutes.toFloat() / maxMinutes
                            val animatedHeight by animateFloatAsState(
                                targetValue = barHeightRatio * 150f,
                                animationSpec = tween(durationMillis = 1000), label = ""
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(text = if(minutes > 0) "${minutes}m" else "", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                // Cột biểu đồ
                                Box(
                                    modifier = Modifier
                                        .width(18.dp)
                                        .height(animatedHeight.dp)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(if(minutes > 0) MaterialTheme.colorScheme.primary else Color.LightGray)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = days[index], fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}