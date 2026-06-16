package com.honghanh.buildyourcastledemo.features.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage // Sử dụng thư viện Coil để load ảnh Preview mượt mà
import com.honghanh.buildyourcastledemo.core.model.HouseData
import com.honghanh.buildyourcastledemo.core.model.Rarity
import com.honghanh.buildyourcastledemo.ui.theme.BuildYourCastleDemoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(onBack: () -> Unit) {
    // Sử dụng FirestoreAdminHelper để tương tác thêm nhà lên Firebase
    val adminHelper = remember { FirestoreAdminHelper() }
    val scrollState = rememberScrollState()

    // --- CÁC BIẾN TRẠNG THÁI QUẢN LÝ DỮ LIỆU HOUSE ---
    var nameHouse by remember { mutableStateOf("") }
    var rarity by remember { mutableStateOf(Rarity.COMMON) }
    var priceGold by remember { mutableStateOf("") }
    var priceEC by remember { mutableStateOf("") }
    var assetName by remember { mutableStateOf("") }
    var rawUnlockDialogues by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // 🖼️ Biến lưu trạng thái Uri của ảnh được chọn từ thiết bị
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher mở thư viện ảnh của thiết bị Android để nhặt ảnh
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri // Lưu Uri lại khi người dùng chọn xong ảnh
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thêm Nhà / Không Gian") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Thêm Không Gian Mới",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.primary
            )

            // 🖼️ KHU VỰC KHUNG NHẬP ẢNH XEM TRƯỚC (PREVIEW)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF3F4F6))
                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                    .clickable { galleryLauncher.launch("image/*") }, // Mở thư viện lọc chỉ hiện file ảnh
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    // Nếu đã chọn ảnh, hiển thị tấm ảnh đó lên toàn bộ khung
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "House Preview Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Trạng thái trống khi Admin chưa bấm chọn ảnh
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📸", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Bấm vào đây để chọn ảnh từ thiết bị",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = nameHouse,
                onValueChange = { nameHouse = it },
                label = { Text("Tên nhà / Không gian") },
                modifier = Modifier.fillMaxWidth()
            )

            // Dropdown chọn Rarity
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = rarity.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Độ hiếm (Rarity)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
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

            OutlinedTextField(
                value = priceGold,
                onValueChange = { priceGold = it },
                label = { Text("Giá Vàng (Gold)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = priceEC,
                onValueChange = { priceEC = it },
                label = { Text("Giá EC (Era Chronos)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = assetName,
                onValueChange = { assetName = it },
                label = { Text("Tên Asset (File Rive/Ảnh nền)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = rawUnlockDialogues,
                onValueChange = { rawUnlockDialogues = it },
                label = { Text("Câu thoại chào mừng khi mở khóa (Cách nhau bằng dấu phẩy)") },
                placeholder = { Text("Ví dụ: Chào mừng chủ nhân, Không gian mới thật trong lành!") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Button(
                onClick = {
                    // Xử lý chuỗi văn bản phân tách bằng dấu phẩy thành List<String>
                    val processedDialogues: List<String> = if (rawUnlockDialogues.isNotEmpty()) {
                        rawUnlockDialogues.split(",").map { it.trim() }
                    } else {
                        emptyList()
                    }

                    // Khởi tạo đối tượng HouseData đồng bộ cấu trúc mới
                    val newHouse = HouseData(
                        idHouse = "", // Để trống để Firestore tự động hash sinh mã Document ngẫu nhiên
                        nameHouse = nameHouse,
                        rarity = rarity,
                        priceGold = priceGold.toIntOrNull() ?: 0,
                        priceEC = priceEC.toIntOrNull() ?: 0,
                        assetName = assetName,
                        // 🛠️ Đóng gói đường dẫn ảnh tạm thời chuyển về chuỗi String lưu trữ
                        imageUrl = selectedImageUri?.toString() ?: ""
                    )

                    // Gọi helper bắn thực thể nhà lên cơ sở dữ liệu Firebase
                    adminHelper.addHouse(newHouse)

                    // Hoàn thành tác vụ quản trị, quay trở lại màn hình trước
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Lưu Nhà Lên Firebase")
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