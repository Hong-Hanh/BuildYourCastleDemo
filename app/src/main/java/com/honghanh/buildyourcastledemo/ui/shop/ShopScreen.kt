package com.honghanh.buildyourcastledemo.ui.shop


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.honghanh.buildyourcastledemo.core.model.CurencyType
import com.honghanh.buildyourcastledemo.core.model.LinhThuData
import com.honghanh.buildyourcastledemo.core.model.Rarity
import com.honghanh.buildyourcastledemo.core.model.UserWallet
import com.honghanh.buildyourcastledemo.ui.focus.FocusScreen
import com.honghanh.buildyourcastledemo.ui.theme.BuildYourCastleDemoTheme

@Composable
fun ShopScreen(
    wallet: UserWallet,
    danhSachLinhThuTrongShop: List<LinhThuData>,
    onBuyClick: (LinhThuData) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFBF7)) // Nền kem nhạt ấm áp
            .padding(16.dp)
    ) {
        // --- THANH TRÊN CÙNG: HIỂN THỊ VÍ TIỀN (UserWallet) ---
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Cửa Hàng", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WalletItem(CurencyType.GOLD.symbol, wallet.gold.toString())
                WalletItem(CurencyType.GEMS.symbol, wallet.gems.toString())
                WalletItem(CurencyType.EC.symbol, wallet.ec.toString())
            }
        }

        Text("Linh Thú Thợ Xây", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 12.dp))

        // --- DANH SÁCH LINH THÚ (LazyVerticalGrid Chia 2 Cột) ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(danhSachLinhThuTrongShop) { linhThu ->
                ShopItemCard(linhThu = linhThu, onBuyClick = { onBuyClick(linhThu) })
            }
        }
    }
}

@Composable
fun WalletItem(symbol: String, amount: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFEFEBE9)),
        shadowElevation = 2.dp
    ) {
        Text(
            text = "$symbol $amount",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ShopItemCard(linhThu: LinhThuData, onBuyClick: () -> Unit) {
    // Xác định màu sắc viền dựa trên Rarity
    val borderCol = when (linhThu.rarity) {
        Rarity.COMMON -> Color.Gray
        Rarity.RARE -> Color(0xFF1E88E5)
        Rarity.EPIC -> Color(0xFF8E24AA)
        Rarity.LEGENDARY -> Color(0xFFFBC02D)
    }

    Card(
        modifier = Modifier.fillMaxWidth().border(1.5.dp, borderCol, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Khối giả lập hình ảnh nhân vật (sau này nhét Rive vào đây)
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(borderCol.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when(linhThu.rarity) {
                        Rarity.LEGENDARY -> "👑🐉"
                        Rarity.EPIC -> "🦊✨"
                        else -> "🐱🔨"
                    },
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = linhThu.nameLinhThu, fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Center)
            Text(text = linhThu.personalityType, fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            // Nút bấm mua tự động nhận biết loại tiền tệ dựa trên giá trị > 0
            val (priceText, currencySymbol) = when {
                linhThu.priceEC > 0 -> Pair(linhThu.priceEC.toString(), CurencyType.EC.symbol)
                linhThu.priceGems > 0 -> Pair(linhThu.priceGems.toString(), CurencyType.GEMS.symbol)
                else -> Pair(linhThu.priceGold.toString(), CurencyType.GOLD.symbol)
            }

            Button(
                onClick = onBuyClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D4037)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "$currencySymbol $priceText", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ShopScreenPreview() {
    BuildYourCastleDemoTheme {
        ShopScreen(
            // 1. Truyền ví tiền giả
            wallet = UserWallet(gold = 999, ec = 10, gems = 50),

            // 2. Truyền danh sách thú giả (để nó hiện lên màn hình preview)
            danhSachLinhThuTrongShop = listOf(
                LinhThuData(
                    idLinhThu = 1,
                    nameLinhThu = "Mèo Thợ Xây",
                    rarity = Rarity.COMMON,
                    priceGold = 100, priceGems = 0, priceEC = 0,
                    assetName = "cat", personalityType = "Vui vẻ", aiSystemPrompt = "",
                    unlockdDialogues = emptyList()
                )
            ),

            // 3. Hàm xử lý khi bấm nút (để trống vì chỉ xem giao diện)
            onBuyClick = { }
        )
    }
}
