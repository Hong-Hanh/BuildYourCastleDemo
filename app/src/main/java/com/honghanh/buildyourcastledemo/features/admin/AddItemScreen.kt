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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.honghanh.buildyourcastledemo.core.model.HouseData
import com.honghanh.buildyourcastledemo.core.model.Rarity
import com.honghanh.buildyourcastledemo.ui.theme.BuildYourCastleDemoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(onBack: () -> Unit) {
    val adminHelper = remember { FirestoreAdminHelper() }
    val scrollState = rememberScrollState()

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var nameHouse by remember { mutableStateOf("") }
    var rarity by remember { mutableStateOf(Rarity.COMMON) }
    var priceGold by remember { mutableStateOf("") }
    var priceEC by remember { mutableStateOf("") }
    var assetName by remember { mutableStateOf("") }
    var rawUnlockDialogues by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var imageUrl by remember { mutableStateOf("") } // ← thay selectedImageUri bằng cái này

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thêm Nhà / Không Gian") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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

            // Ô nhập URL ảnh từ GitHub
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Link ảnh (GitHub raw URL)") },
                placeholder = { Text("https://raw.githubusercontent.com/...") },
                modifier = Modifier.fillMaxWidth()
            )

            // Xem trước ảnh nếu đã nhập URL
            if (imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Preview ảnh",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Khung trống khi chưa nhập URL
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF3F4F6))
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📸", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Nhập link ảnh ở trên để xem trước",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            OutlinedTextField(
                value = nameHouse,
                onValueChange = { nameHouse = it },
                label = { Text("Tên nhà / Không gian") },
                modifier = Modifier.fillMaxWidth()
            )

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
                label = { Text("Câu thoại chào mừng (cách nhau bằng dấu phẩy)") },
                placeholder = { Text("Ví dụ: Chào mừng chủ nhân, Không gian mới thật trong lành!") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Button(
                onClick = {
                    isLoading = true
                    errorMessage = ""

                    val newHouse = HouseData(
                        idHouse = "",
                        nameHouse = nameHouse,
                        rarity = rarity,
                        priceGold = priceGold.toIntOrNull() ?: 0,
                        priceEC = priceEC.toIntOrNull() ?: 0,
                        assetName = assetName,
                        imageUrl = imageUrl  // ← dùng URL GitHub nhập tay
                    )

                    adminHelper.addHouse(
                        house = newHouse,
                        onSuccess = {
                            isLoading = false
                            onBack()
                        },
                        onError = { msg ->
                            isLoading = false
                            errorMessage = msg
                        }
                    )
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đang lưu...")
                } else {
                    Text("Lưu Nhà Lên Firebase")
                }
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 13.sp
                )
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