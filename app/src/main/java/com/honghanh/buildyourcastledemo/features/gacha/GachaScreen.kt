package com.honghanh.buildyourcastledemo.features.gacha

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.honghanh.buildyourcastledemo.R
import com.honghanh.buildyourcastledemo.core.model.HouseData

@Composable
fun GachaScreen(
    viewModel: GachaViewModel,
    onBackClick: () -> Unit
) {
    GachaScreenContent(
        currentGold = viewModel.currentGold.value,
        currentEC = viewModel.currentEC.value,
        isSpinning = viewModel.isSpinning.value,
        isLoadingPool = viewModel.isLoadingPool.value,
        gachaMessage = viewModel.gachaMessage.value,
        rewardList = viewModel.rewardList,
        costX1 = viewModel.costX1,
        costX10 = viewModel.costX10,
        onQuayClick = { soLuot -> viewModel.kichHoatQuayGacha(soLuot) },
        onDismissMessage = { viewModel.xoaThongBao() },
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GachaScreenContent(
    currentGold: Int,
    currentEC: Int,
    isSpinning: Boolean,
    isLoadingPool: Boolean,
    gachaMessage: String?,
    rewardList: List<HouseData>,
    costX1: Int,
    costX10: Int,
    onQuayClick: (Int) -> Unit,
    onDismissMessage: () -> Unit,
    onBackClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "GachaRotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Angle"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vòng Quay Kiến Trúc", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !isSpinning) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF064E3B),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF9FAFB))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔥 KHỐI NỘI DUNG PHÍA TRÊN: Chiếm trọn không gian khả dụng, đẩy cụm nút xuống đáy
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ================= KHỐI TÀI SẢN (XU VÀNG & AC) =================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_gold),
                                contentDescription = "Gold",
                                tint = Color(0xFFEAB308),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "$currentGold", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_ec),
                                contentDescription = "AC",
                                tint = Color(0xFF3B82F6),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "$currentEC AC", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }

                // ================= KHỐI TRUNG TÂM: HIỂN THỊ KẾT QUẢ QUAY SỐ =================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 16.dp)
                        .border(2.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isLoadingPool -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Color(0xFF064E3B))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Đang thiết lập vòng quay...", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                        isSpinning -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_gacha_box),
                                    contentDescription = "Spinning",
                                    modifier = Modifier.size(100.dp).rotate(angle),
                                    tint = Color(0xFF064E3B)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Đang khai phá không gian...", fontWeight = FontWeight.Bold, color = Color(0xFF064E3B), fontSize = 16.sp)
                            }
                        }
                        rewardList.isNotEmpty() -> {
                            if (rewardList.size == 1) {
                                val item = rewardList.first()
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text("VẬT PHẨM ĐẠT ĐƯỢC", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981), letterSpacing = 1.5.sp)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    AsyncImage(
                                        model = item.imageUrl.ifEmpty { R.drawable.ic_default_avatar },
                                        contentDescription = null,
                                        modifier = Modifier.size(140.dp).clip(RoundedCornerShape(16.dp))
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(text = item.nameHouse, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF1F2937))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = item.description, fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            } else {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    Text("DANH SÁCH 10 PHẦN THƯỞNG", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706), modifier = Modifier.padding(bottom = 8.dp), letterSpacing = 1.sp)

                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(5),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f) // 🔥 ĐÃ SỬA: Dùng weight(1f) để cuộn êm mượt bên trong Box mà không lấn layout
                                    ) {
                                        items(rewardList) { item ->
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier
                                                    .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
                                                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                                                    .padding(6.dp)
                                            ) {
                                                AsyncImage(
                                                    model = item.imageUrl.ifEmpty { R.drawable.ic_default_avatar },
                                                    contentDescription = null,
                                                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(8.dp))
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(text = item.nameHouse, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(painter = painterResource(id = R.drawable.ic_gacha_box), contentDescription = "Box Ready", modifier = Modifier.size(120.dp), tint = Color(0xFF9CA3AF))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = "Chọn chế độ quay bên dưới để thử vận may của bạn!", textAlign = TextAlign.Center, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 32.dp))
                            }
                        }
                    }
                }

                // ================= KHỐI THÔNG BÁO KẾT QUẢ BANNER =================
                if (!gachaMessage.isNullOrEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = gachaMessage, color = Color(0xFF92400E), fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                            TextButton(onClick = onDismissMessage) {
                                Text("Đóng", color = Color(0xFF92400E), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // ================= KHỐI NÚT HÀNH ĐỘNG DƯỚI CÙNG: LUÔN CỐ ĐỊNH AN TOÀN =================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onQuayClick(1) },
                    enabled = !isSpinning && !isLoadingPool,
                    modifier = Modifier.weight(1f).height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF064E3B)),
                    shape = RoundedCornerShape(14.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("QUAY X1", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "$costX1 AC", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }

                Button(
                    onClick = { onQuayClick(10) },
                    enabled = !isSpinning && !isLoadingPool,
                    modifier = Modifier.weight(1f).height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                    shape = RoundedCornerShape(14.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("QUAY X10", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "$costX10 AC", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }
    }
}

// =======================================================
// 🔥 HỆ THỐNG DANH SÁCH PREVIEWS ĐA TRẠNG THÁI
// =======================================================

@Preview(showBackground = true, name = "1. Trạng thái tĩnh ban đầu")
@Composable
fun GachaScreenPreviewIdle() {
    GachaScreenContent(
        currentGold = 1350,
        currentEC = 120,
        isSpinning = false,
        isLoadingPool = false,
        gachaMessage = null,
        rewardList = emptyList(),
        costX1 = 20,
        costX10 = 200,
        onQuayClick = {},
        onDismissMessage = {},
        onBackClick = {}
    )
}

@Preview(showBackground = true, name = "2. Trạng thái Đang quay")
@Composable
fun GachaScreenPreviewSpinning() {
    GachaScreenContent(
        currentGold = 1330,
        currentEC = 100,
        isSpinning = true,
        isLoadingPool = false,
        gachaMessage = null,
        rewardList = emptyList(),
        costX1 = 20,
        costX10 = 200,
        onQuayClick = {},
        onDismissMessage = {},
        onBackClick = {}
    )
}

@Preview(showBackground = true, name = "3. Kết quả Quay X1 Đơn lẻ")
@Composable
fun GachaScreenPreviewResultX1() {
    val mockSingleHouse = HouseData(
        idHouse = "h4",
        nameHouse = "Biệt Thự G20 M Sport",
        assetName = "house_bmw",
        description = "Mẫu thiết kế cao cấp mang phong cách khỏe khoắn thể thao vượt trội.",
        imageUrl = ""
    )
    GachaScreenContent(
        currentGold = 1330,
        currentEC = 100,
        isSpinning = false,
        isLoadingPool = false,
        gachaMessage = "🎉 Chúc mừng! Bạn đã nhận được bản thiết kế mới.",
        rewardList = listOf(mockSingleHouse),
        costX1 = 20,
        costX10 = 200,
        onQuayClick = {},
        onDismissMessage = {},
        onBackClick = {}
    )
}

@Preview(showBackground = true, name = "4. Kết quả Lưới Quay X10")
@Composable
fun GachaScreenPreviewResultX10() {
    val mockList = List(10) { index ->
        HouseData(
            idHouse = "h_$index",
            nameHouse = if (index % 3 == 0) "Biệt Thự G20" else "Tiệm Espresso",
            assetName = "house_asset",
            description = "Mô tả giả lập",
            imageUrl = ""
        )
    }
    GachaScreenContent(
        currentGold = 1150,
        currentEC = 45,
        isSpinning = false,
        isLoadingPool = false,
        gachaMessage = "Đã nhận vật phẩm mới thành công! Hoàn lại +300 Xu Vàng cho các vật phẩm thiết kế bị trùng.",
        rewardList = mockList,
        costX1 = 20,
        costX10 = 200,
        onQuayClick = {},
        onDismissMessage = {},
        onBackClick = {}
    )
}