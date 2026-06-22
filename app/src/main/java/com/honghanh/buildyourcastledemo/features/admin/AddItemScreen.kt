package com.honghanh.buildyourcastledemo.features.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.honghanh.buildyourcastledemo.core.model.HouseData
import com.honghanh.buildyourcastledemo.core.model.Rarity
import com.honghanh.buildyourcastledemo.ui.theme.BuildYourCastleDemoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(onBack: () -> Unit) {
    val adminHelper = remember { FirestoreAdminHelper() }
    val scrollState = rememberScrollState()

    // State điều khiển phân tách 2 Tab: 0 = Thêm vào Shop, 1 = Thêm vào Gacha Pool
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Cửa Hàng (Shop)", "Vòng Quay (Gacha)")

    // Các State trường nhập liệu chung
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var nameHouse by remember { mutableStateOf("") }
    var rarity by remember { mutableStateOf(Rarity.COMMON) }
    var assetName by remember { mutableStateOf("") }
    var rawUnlockDialogues by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // State đặc thù cho Shop
    var priceGold by remember { mutableStateOf("") }
    var priceEC by remember { mutableStateOf("") }

    // State đặc thù cho Gacha
    var dropRate by remember { mutableStateOf("5.0") }

    // Hàm reset form sau khi thêm thành công để admin nhập tiếp item khác
    fun resetForm() {
        nameHouse = ""
        rarity = Rarity.COMMON
        assetName = ""
        rawUnlockDialogues = ""
        imageUrl = ""
        priceGold = ""
        priceEC = ""
        dropRate = "5.0"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hệ Thống Quản Trị Viên", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isLoading) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // KHỐI THANH TAB PHÂN CHIA HÀNG NỘI DUNG
            TabRow(selectedTabIndex = selectedTab, modifier = Modifier.fillMaxWidth()) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            if (!isLoading) {
                                selectedTab = index
                                errorMessage = ""
                                successMessage = ""
                            }
                        },
                        text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                    )
                }
            }

            // PHẦN FORM NHẬP LIỆU CUỘN dọc
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (selectedTab == 0) "Đăng Vật Phẩm Lên Cửa Hàng" else "Cấu Hình Vật Phẩm Vào Gacha",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // 1. Ô nhập URL ảnh từ GitHub (Dùng chung)
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Link ảnh không gian (GitHub raw URL)") },
                    placeholder = { Text("https://raw.githubusercontent.com/...") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                // Khung xem trước ảnh (Dùng chung)
                if (imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF3F4F6))
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Chưa có ảnh xem trước", fontSize = 13.sp, color = Color.Gray)
                    }
                }

                // 2. Tên và Độ hiếm (Dùng chung)
                OutlinedTextField(
                    value = nameHouse,
                    onValueChange = { nameHouse = it },
                    label = { Text("Tên nhà / Không gian kiến trúc") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { if (!isLoading) expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = rarity.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Độ hiếm phân cấp (Rarity)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        enabled = !isLoading
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        Rarity.entries.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption.name) },
                                onClick = {
                                    rarity = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // ================= BIẾN ĐỔI THEO TAB ĐƯỢC CHỌN =================
                if (selectedTab == 0) {
                    // CÁC TRƯỜNG THUỘC TAB SHOP
                    OutlinedTextField(
                        value = priceGold,
                        onValueChange = { priceGold = it },
                        label = { Text("Giá Bán Xu Vàng (Gold)") },
                        placeholder = { Text("Nhập số xu vàng cần mua") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )

                    OutlinedTextField(
                        value = priceEC,
                        onValueChange = { priceEC = it },
                        label = { Text("Giá Bán Tinh Thể Aether (AC)") },
                        placeholder = { Text("Nhập số AC cần mua") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                } else {
                    // CÁC TRƯỜNG THUỘC TAB GACHA POOL
                    OutlinedTextField(
                        value = dropRate,
                        onValueChange = { dropRate = it },
                        label = { Text("Tỷ lệ xuất hiện trong vòng quay (%)") },
                        placeholder = { Text("Ví dụ: 3.5 hoặc 15.0") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                }

                // 3. Tên file Asset & Lời thoại chào mừng (Dùng chung)
                OutlinedTextField(
                    value = assetName,
                    onValueChange = { assetName = it },
                    label = { Text("Mã định danh Asset (File Rive hoặc Tên Hệ Thống)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                OutlinedTextField(
                    value = rawUnlockDialogues,
                    onValueChange = { rawUnlockDialogues = it },
                    label = { Text("Câu thoại chúc mừng khi mở khóa (Cách nhau bằng dấu phẩy)") },
                    placeholder = { Text("Chủ nhân ơi không gian này đẹp quá!, Chào mừng bạn...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    enabled = !isLoading
                )

                // BUTTON XỬ LÝ LƯU RIÊNG THEO LỰA CHỌN
                Button(
                    onClick = {
                        if (nameHouse.isBlank()) {
                            errorMessage = "Vui lòng nhập tên vật phẩm kiến trúc!"
                            return@Button
                        }

                        isLoading = true
                        errorMessage = ""
                        successMessage = ""

                        val baseHouse = HouseData(
                            idHouse = "",
                            nameHouse = nameHouse,
                            rarity = rarity,
                            priceGold = if (selectedTab == 0) (priceGold.toIntOrNull() ?: 0) else 0,
                            priceEC = if (selectedTab == 0) (priceEC.toIntOrNull() ?: 0) else 0,
                            assetName = assetName,
                            imageUrl = imageUrl
                        )

                        if (selectedTab == 0) {
                            // Thực hiện đẩy lên bảng Cửa hàng
                            adminHelper.addOnlyToShop(
                                house = baseHouse,
                                onSuccess = {
                                    isLoading = false
                                    successMessage = "Đã đăng thành công vật phẩm lên Cửa Hàng!"
                                    resetForm()
                                },
                                onError = { msg ->
                                    isLoading = false
                                    errorMessage = msg
                                }
                            )
                        } else {
                            // 🔥 THAY ĐỔI: Thực hiện đẩy đồng thời lên cả Gacha Pool và Kho tổng house
                            val rate = dropRate.toDoubleOrNull() ?: 5.0
                            adminHelper.addGachaAndBaseHouse(
                                house = baseHouse,
                                dropRate = rate,
                                onSuccess = {
                                    isLoading = false
                                    successMessage = "Đã cấu hình vật phẩm vào Gacha Pool & Kho tổng thành công!"
                                    resetForm()
                                },
                                onError = { msg ->
                                    isLoading = false
                                    errorMessage = msg
                                }
                            )
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Đang xử lý dữ liệu...")
                    } else {
                        Text(if (selectedTab == 0) "Đăng Bán Lên Shop" else "Đưa Vào Vòng Quay Gacha")
                    }
                }

                // Khối thông báo trạng thái
                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red, fontSize = 14.sp, textAlign = TextAlign.Center)
                }
                if (successMessage.isNotEmpty()) {
                    Text(text = successMessage, color = Color(0xFF10B981), fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminScreenPreview() {
    BuildYourCastleDemoTheme {
        AdminScreen(onBack = {})
    }
}

