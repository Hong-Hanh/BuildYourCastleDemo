package com.honghanh.buildyourcastledemo.features.inventory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.honghanh.buildyourcastledemo.core.model.HouseData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onBack: () -> Unit,
    viewModel: InventoryViewModel = viewModel()
) {
    val isLoading = viewModel.isLoading.value
    val errorMessage = viewModel.errorMessage.value
    val ownedHouses = viewModel.ownedHouses
    // Lấy ID đang dùng thực tế từ UserProfile để check trạng thái gắn nhãn
    val currentEquippedId = viewModel.currentEquippedId.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kho Đã Sở Hữu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF9FAFB))
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else if (ownedHouses.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📦", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Kho đồ trống rỗng!", fontWeight = FontWeight.Medium, color = Color.Gray)
                    Text("Hãy ghé Shop hoặc Vòng quay để sắm nhà nhé.", fontSize = 13.sp, color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ownedHouses) { house ->
                        // Kiểm tra xem ID của item này có trùng với ID đang được sử dụng hay không
                        val isEquipped = house.idHouse == currentEquippedId

                        InventoryItemCard(
                            house = house,
                            isEquipped = isEquipped,
                            onEquipClick = {
                                viewModel.trangBiKhongGian(house) {
                                    // Xử lý callback sau khi đổi thành công nếu cần (VD: thông báo Toast)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryItemCard(
    house: HouseData,
    isEquipped: Boolean,
    onEquipClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        // Nếu đang dùng thì vẽ thêm viền màu Primary làm nổi bật hẳn lên
        border = if (isEquipped) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, Color(0xFFE5E7EB)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                model = house.imageUrl,
                contentDescription = house.nameHouse,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = house.nameHouse,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Nhãn Độ hiếm
                Surface(
                    color = when (house.rarity) {
                        com.honghanh.buildyourcastledemo.core.model.Rarity.LEGENDARY -> Color(0xFFFEF3C7)
                        com.honghanh.buildyourcastledemo.core.model.Rarity.EPIC -> Color(0xFFF3E8FF)
                        com.honghanh.buildyourcastledemo.core.model.Rarity.RARE -> Color(0xFFE0F2FE)
                        else -> Color(0xFFF3F4F6)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = house.rarity.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (house.rarity) {
                            com.honghanh.buildyourcastledemo.core.model.Rarity.LEGENDARY -> Color(0xD9B45309)
                            com.honghanh.buildyourcastledemo.core.model.Rarity.EPIC -> Color(0xD96B21A8)
                            com.honghanh.buildyourcastledemo.core.model.Rarity.RARE -> Color(0xD90369A1)
                            else -> Color(0xD9374151)
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 🔥 NÚT BẤM THAY ĐỔI TRẠNG THÁI TRANG BỊ
                Button(
                    onClick = onEquipClick,
                    enabled = !isEquipped, // Đang dùng rồi thì khóa tương tác bấm
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEquipped) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color(0xFFE5E7EB) // Màu xám nhẹ khi nút bị khóa
                    )
                ) {
                    Text(
                        text = if (isEquipped) "Đang dùng" else "Sử dụng",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isEquipped) Color.White else MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}