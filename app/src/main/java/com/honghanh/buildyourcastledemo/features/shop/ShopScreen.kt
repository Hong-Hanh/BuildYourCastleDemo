package com.honghanh.buildyourcastledemo.features.shop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.honghanh.buildyourcastledemo.R
import com.honghanh.buildyourcastledemo.core.model.HouseData
import com.honghanh.buildyourcastledemo.core.model.Rarity
import com.honghanh.buildyourcastledemo.features.shop.FeaturedBannerCard
import com.honghanh.buildyourcastledemo.ui.theme.BuildYourCastleDemoTheme


@Composable
fun ShopScreen(
    viewModel: ShopViewModel = viewModel(),
    onBack: () -> Unit
) {
    val wallet = viewModel.wallet.value
    val danhSachNhaTrongShop = viewModel.listVatPhamLT

    // Giả sử trong hệ thống bạn lưu ID căn nhà đang áp dụng làm hình nền chính
    val currentHouseIdUsed = "2"

    ShopContent(
        goldAmount = wallet.gold.toString(),
        gemsAmount = wallet.gems.toString(),
        ecAmount = wallet.ec.toString(),
        shopItems = danhSachNhaTrongShop,
        currentHouseIdUsed = currentHouseIdUsed,
        onBack = onBack,
        onBuyClick = { house -> viewModel.buyHouse(house) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopContent(
    goldAmount: String,
    gemsAmount: String,
    ecAmount: String,
    shopItems: List<HouseData>,
    currentHouseIdUsed: String,
    onBack: () -> Unit,
    onBuyClick: (HouseData) -> Unit
) {
    var selectedItemForDetail by remember { mutableStateOf<HouseData?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shop", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF0F342E)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color(0xFF0F342E))
                    }
                },
                actions = {
                    Row(
                        modifier = Modifier.padding(end = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WalletChip("🪙", goldAmount)
                        WalletChip("🌙", gemsAmount)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF8F9FA))
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // --- TIÊU ĐỀ CHÍNH ---
            item(span = { GridItemSpan(3) }) {
                Column(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)) {
                    Text("CƠ NGƠI CỦA BẠN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                    Text("Lựa chọn không gian tập trung", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F342E))
                }
            }

            // --- 1. Ô BANNER ƯU ĐÃI TUẦN (Chuẩn UI thiết kế 50/50 theo ảnh mẫu) ---
            item(span = { GridItemSpan(3) }) {
                FeaturedBannerCard(
                    tagText = "ƯU ĐÃI TUẦN",
                    title = "Căn Hộ Đám Mây",
                    description = "Nâng cấp không gian tập trung của bạn với phong cách tối giản từ tương lai.",
                    buttonText = "Xem chi tiết",
                    imageUrl = R.drawable.thungrac, // Thay bằng ID ảnh không gian mây
                    containerColor = Color(0xFF235347)
                )
            }

            // --- 2. Ô BANNER VẬT PHẨM SẮP RA MẮT (Màu xanh Mint nhạt theo ảnh mẫu) ---
            item(span = { GridItemSpan(3) }) {
                FeaturedBannerCard(
                    tagText = "Coming soon",
                    title = "Linh thú",
                    description = "Hệ thống trợ thủ rèn đúc, tăng tốc xây lâu đài sắp sửa ra mắt.",
                    buttonText = "Sắp ra mắt",
                    imageUrl = R.drawable.thungrac, // Thay bằng ID ảnh chú mèo thần tài tương ứng
                    containerColor = Color(0xFF90EED6),
                    isDarkTheme = false
                )
            }

            // --- TIÊU ĐỀ DANH SÁCH ---
            item(span = { GridItemSpan(3) }) {
                Text(
                    text = "DANH SÁCH KHÔNG GIAN",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F342E),
                    modifier = Modifier.padding(top = 12.dp, bottom = 2.dp)
                )
            }

            // --- 3. LƯỚI VẬT PHẨM (3 Ô MỘT HÀNG) ---
            items(items = shopItems, key = { it.idHouse }) { house ->
                // Giả định logic kiểm tra xem món này đã được mua hay chưa
                // Trong thực tế bạn có thể check: house.isOwned hoặc nằm trong danh sách đã mua của User
                val isPurchased = house.priceGold < 20000 || house.idHouse == currentHouseIdUsed
                val isCurrentEquipped = house.idHouse == currentHouseIdUsed

                ShopItemGridCard(
                    house = house,
                    isPurchased = isPurchased,
                    isCurrentEquipped = isCurrentEquipped,
                    onItemClick = { selectedItemForDetail = house }
                )
            }
        }
    }

    // --- POPUP MÔ TẢ CHI TIẾT ---
    selectedItemForDetail?.let { house ->
        val isPurchased = house.priceGold < 20000 || house.idHouse == currentHouseIdUsed
        val isCurrentEquipped = house.idHouse == currentHouseIdUsed

        DetailProductDialog(
            house = house,
            isPurchased = isPurchased,
            isCurrentEquipped = isCurrentEquipped,
            onDismiss = { selectedItemForDetail = null },
            onBuyClick = {
                onBuyClick(house)
                selectedItemForDetail = null
            }
        )
    }
}

// --- COMPONENT CHIP VÍ TÀI NGUYÊN TRÒN DẸT NỀN TRẮNG ---
@Composable
fun WalletChip(symbol: String, amount: String) {
    Row(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(20.dp))
            .border(0.5.dp, Color(0xFFE0E0E0), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = symbol, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = amount, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
    }
}

// --- COMPONENT BANNER ƯU ĐÃI & SẮP RA MẮT (Sao chép hoàn hảo tỉ lệ ảnh mẫu) ---
@Composable
fun FeaturedBannerCard(
    tagText: String,
    title: String,
    description: String,
    buttonText: String,
    imageUrl: Int,
    containerColor: Color,
    isDarkTheme: Boolean = true
) {
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F342E)
    val subTextColor = if (isDarkTheme) Color.White.copy(alpha = 0.7f) else Color(0xFF3E6B5F)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Cột trái: Text nội dung thông tin
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Tag nhỏ bên trên đầu
                    Surface(
                        color = if (isDarkTheme) Color(0xFF39A989) else Color(0xFF0F342E),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = tagText,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = description, fontSize = 11.sp, color = subTextColor, maxLines = 3, overflow = TextOverflow.Ellipsis, lineHeight = 15.sp)
                }

                // Nút hành động bo tròn
                Surface(
                    color = if (isDarkTheme) Color(0xFF0F342E) else Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.clickable { /* Xử lý sự kiện */ }
                ) {
                    Text(
                        text = buttonText,
                        color = textColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            // Cột phải: Khung ảnh cắt góc nghệ thuật chìm ra biên phải
            Box(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.thungrac),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

// --- HÀM ĐỔ MÀU KHUNG THEO ĐỘ HIẾM ÁNH KIM NHƯ YÊU CẦU ---
@Composable
fun getRarityColors(rarity: Rarity): Pair<Brush, Color> {
    return when (rarity) {
        Rarity.LEGENDARY -> Pair(
            Brush.linearGradient(listOf(Color(0xFFFFE259), Color(0xFFFFA751))), // Vàng ánh kim
            Color(0xFFD97706)
        )
        Rarity.EPIC -> Pair(
            Brush.linearGradient(listOf(Color(0xFFD442F5), Color(0xFF7303C0))), // Tím ánh kim
            Color(0xFF8B5CF6)
        )
        Rarity.RARE -> Pair(
            Brush.linearGradient(listOf(Color(0xFF4FACFE), Color(0xFF00F2FE))), // Xanh dương ánh kim
            Color(0xFF0284C7)
        )
        Rarity.COMMON -> Pair(
            Brush.linearGradient(listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))), // Xanh lá thuần
            Color(0xFF2E7D32)
        )
    }
}

// --- Ô CHIA LƯỚI TỐI GIẢN (HIỆN TRẠNG THÁI ĐÃ MUA / ĐANG CHỌN) ---
@Composable
fun ShopItemGridCard(
    house: HouseData,
    isPurchased: Boolean,
    isCurrentEquipped: Boolean,
    onItemClick: () -> Unit
) {
    val (rarityBrush, mainColor) = getRarityColors(house.rarity)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .border(2.dp, rarityBrush, RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = house.imageUrl,
                contentDescription = null,
                placeholder = painterResource(R.drawable.thungrac),
                error = painterResource(R.drawable.thungrac),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // NHÃN GÓC PHẢI TRÊN: HIỂN THỊ ĐÃ MUA HOẶC ĐANG DÙNG THEO ẢNH MẪU
            if (isPurchased) {
                Surface(
                    color = if (isCurrentEquipped) Color(0xFF39A989) else Color(0xFF708090),
                    shape = RoundedCornerShape(bottomStart = 10.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = if (isCurrentEquipped) "ĐANG CHỌN" else "ĐÃ MỞ KHÓA",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Giá tiền hoặc text trạng thái bên dưới ảnh chân thực
        if (isCurrentEquipped) {
            Text(text = "ĐANG DÙNG", fontSize = 11.sp, color = Color(0xFF39A989), fontWeight = FontWeight.Bold)
        } else if (isPurchased) {
            Text(text = "SẴN SÀNG", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Normal)
        } else {
            val (priceText, currencySymbol) = when {
                house.priceEC > 0 -> Pair(house.priceEC.toString(), "EC")
                else -> Pair(house.priceGold.toString(), "🟡")
            }
            Text(text = "$priceText $currencySymbol", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
        }
    }
}

// --- POPUP POPUP CHI TIẾT + COI ANIMATION QUA MẮT THẦN ---
@Composable
fun DetailProductDialog(
    house: HouseData,
    isPurchased: Boolean,
    isCurrentEquipped: Boolean,
    onDismiss: () -> Unit,
    onBuyClick: () -> Unit
) {
    var playAnimationByEyeMenu by remember { mutableStateOf(false) }
    val (rarityBrush, mainColor) = getRarityColors(house.rarity)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .border(2.dp, rarityBrush, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = house.nameHouse, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF0F342E))
                Text(
                    text = "${house.rarity.name} GRADE",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    color = mainColor,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                // Khung Media
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF3F4F6)),
                    contentAlignment = Alignment.Center
                ) {
                    if (playAnimationByEyeMenu) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🎬", fontSize = 36.sp)
                            Text(text = "[ SKELETAL RIVE ANIMATION RUNNING ]", fontSize = 10.sp, color = mainColor, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.thungrac),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Icon Mắt thần xem hoạt họa chuyển động ở góc phải dưới
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (playAnimationByEyeMenu) mainColor else Color.Black.copy(
                                    alpha = 0.6f
                                )
                            )
                            .clickable { playAnimationByEyeMenu = !playAnimationByEyeMenu },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👁️", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))


                Spacer(modifier = Modifier.height(20.dp))

                // XỬ LÝ NÚT BẤM DỰA TRÊN THÀNH TỰU ĐÃ MUA HOẶC CHƯA MƯỢT MÀ
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "ĐÓNG", color = Color.Gray)
                    }

                    if (isCurrentEquipped) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39A989)),
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "ĐANG SỬ DỤNG", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    } else if (isPurchased) {
                        Button(
                            onClick = { /* Thực hiện hàm áp dụng hình nền này lên màn chính */ onDismiss() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF235347)),
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "ÁP DỤNG NGAY", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        val (priceText, currencySymbol) = when {
                            house.priceEC > 0 -> Pair(house.priceEC.toString(), "EC")
                            else -> Pair(house.priceGold.toString(), "🟡")
                        }
                        Button(
                            onClick = onBuyClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF78350F)),
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "MỞ KHÓA: $priceText $currencySymbol", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ShopScreenPreview() {
    BuildYourCastleDemoTheme {
        val mockItems = listOf(
            HouseData(idHouse = "1", nameHouse = "Căn Hộ Zen", rarity = Rarity.RARE, priceGold = 1200),
            HouseData(idHouse = "2", nameHouse = "Trạm Gác Núi",  rarity = Rarity.EPIC, priceGold = 5000),
            HouseData(idHouse = "3", nameHouse = "Căn Hộ Neon", rarity = Rarity.RARE, priceGold = 1200),
            HouseData(idHouse = "4", nameHouse = "Lều Vải", rarity = Rarity.COMMON, priceGold = 500),
            HouseData(idHouse = "5", nameHouse = "Nhà Kính",  rarity = Rarity.LEGENDARY, priceEC = 6)
        )

        ShopContent(
            goldAmount = "1,025",
            gemsAmount = "585",
            ecAmount = "0",
            shopItems = mockItems,
            currentHouseIdUsed = "2",
            onBack = {},
            onBuyClick = {}
        )
    }
}