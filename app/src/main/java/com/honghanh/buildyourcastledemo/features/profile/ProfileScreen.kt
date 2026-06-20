package com.honghanh.buildyourcastledemo.features.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.honghanh.buildyourcastledemo.R
import com.honghanh.buildyourcastledemo.core.model.UserProfile

// Danh sách định danh các ảnh avatar cục bộ dùng khi Hạnh click chọn trong BottomSheet
val AVAILABLE_AVATARS = listOf(
    "avatar_banhcozy",
    "avatar_batlua",
    "avatar_dienthoai",
    "avatar_hopsua",
    "avatar_maycafe",
    "avatar_thungrac",
    "avatar_hopbut"
)

// 🔥 ĐÃ SỬA: Hàm ánh xạ cứng an toàn tuyệt đối, loại bỏ hẳn getIdentifier để tránh lỗi ID #0x0
@Composable
fun getAvatarResource(avatarName: String?): Int {
    return when (avatarName) {
        "avatar_banhcozy"  -> R.drawable.banhcozy
        "avatar_batlua"    -> R.drawable.batlua
        "avatar_dienthoai" -> R.drawable.dienthoai
        "avatar_hopsua"    -> R.drawable.hopsua
        "avatar_maycafe"   -> R.drawable.maycafe
        "avatar_thungrac"  -> R.drawable.thungrac
        "avatar_hopbut"    -> R.drawable.hopbut
        // Trường hợp trống hoặc dữ liệu cũ không khớp, trả về ảnh mặc định này
        else -> R.drawable.hopsua
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onBack: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val name by viewModel.accountName
    val email by viewModel.accountEmail
    val profile by viewModel.userProfile
    val isLoading by viewModel.isLoading

    // Trạng thái đóng/mở BottomSheet chọn avatar
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Box(modifier = Modifier.fillMaxSize()) {
        ProfileContent(
            name = name,
            email = email,
            profile = profile,
            isLoading = isLoading,
            onBack = onBack,
            onAvatarClick = { showBottomSheet = true },
            onLogoutClick = { viewModel.logout { onLogoutSuccess() } }
        )

        // 🔥 BOTTOM SHEET: Khối hiển thị bảng chọn lựa Avatar
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Chọn Avatar của bạn",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B4332),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(bottom = 32.dp)
                    ) {
                        items(AVAILABLE_AVATARS) { avatarName ->
                            val resId = getAvatarResource(avatarName)

                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFF0F4F0))
                                    .border(
                                        width = if (profile?.currentAvatarUrl == avatarName) 3.dp else 1.dp,
                                        color = if (profile?.currentAvatarUrl == avatarName) Color(0xFFA3E635) else Color(0xFFE5E7EB),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        // Lưu tên định danh của ảnh vào trường riêng biệt trên Firestore
                                        viewModel.updateAvatarName(avatarName)
                                        showBottomSheet = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = resId),
                                    contentDescription = avatarName,
                                    modifier = Modifier.fillMaxSize().padding(8.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- TẦNG HIỂN THỊ GIAO DIỆN THUẦN TÚY (STATELESS) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    name: String,
    email: String,
    profile: UserProfile?,
    isLoading: Boolean,
    onBack: () -> Unit,
    onAvatarClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("", color = Color(0xFF1B4332)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1B4332)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF0F4F0))
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF0F4F0)),
            contentAlignment = Alignment.TopCenter
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF1B4332))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // --- KHỐI AVATAR VÀ THÔNG TIN CÁ NHÂN ---
                    Box(contentAlignment = Alignment.BottomCenter) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFA3E635).copy(alpha = 0.3f))
                                .border(3.dp, Color(0xFFA3E635), CircleShape)
                                .clickable { onAvatarClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            // 🔥 ĐÃ SỬA: Đổi hẳn sang dùng Image kết hợp với hàm getAvatarResource()
                            // để đọc ảnh cục bộ chính xác theo lựa chọn thay vì load URL lỗi qua AsyncImage
                            Image(
                                painter = painterResource(id = getAvatarResource(profile?.currentAvatarUrl)),
                                contentDescription = "Avatar Cá Nhân",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Badge hiển thị Level
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1B4332),
                            modifier = Modifier.offset(y = 10.dp)
                        ) {
                            Text(
                                text = "Level ${1 + (profile?.ownedHouses?.size ?: 0)}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B4332))
                    Text(
                        text = if(email.isNotBlank()) email else "Architect of Serenity",
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF6B7280)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- KHỐI THÔNG SỐ GAME (STATS CARDS) ---
                    StatCard(icon = "🕒", title = "TOTAL FOCUS", value = "124h")
                    Spacer(modifier = Modifier.height(12.dp))
                    StatCard(icon = "🏗️", title = "HOUSES BUILT", value = "${profile?.ownedHouses?.size ?: 0}")
                    Spacer(modifier = Modifier.height(12.dp))
                    StatCard(icon = "✨", title = "SPIRITS FOUND", value = "5")

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- KHỐI THÀNH TỰU (ACHIEVEMENTS) ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Achievements", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF374151))
                        Text(
                            "View All",
                            color = Color(0xFF1B4332),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) { AchievementItem(icon = "🌲", label = "First Sapling") }
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(modifier = Modifier.weight(1f)) { AchievementItem(icon = "🏆", label = "7 Day Streak") }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) { AchievementItem(icon = "🏰", label = "Mansion Lord") }
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(modifier = Modifier.weight(1f)) { AchievementItem(icon = "🔒", label = "???") }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // --- KHỐI CÀI ĐẶT (SETTINGS MENU) ---
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF374151),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B4332).copy(alpha = 0.2f))
                    ) {
                        Column {
                            SettingMenuItem(icon = Icons.Default.AccountCircle, title = "Account") {}
                            HorizontalDivider(color = Color(0xFFF0F4F0), thickness = 1.dp)
                            SettingMenuItem(icon = Icons.Default.Notifications, title = "Notifications") {}
                            HorizontalDivider(color = Color(0xFFF0F4F0), thickness = 1.dp)
                            SettingMenuItem(icon = Icons.Default.Lock, title = "Support") {}
                            HorizontalDivider(color = Color(0xFFF0F4F0), thickness = 1.dp)

                            SettingMenuItem(
                                icon = Icons.Default.Person,
                                title = "Logout",
                                isLogout = true
                            ) {
                                onLogoutClick()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(icon: String, title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B4332).copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFA3E635).copy(alpha = 0.2f),
                modifier = Modifier.size(36.dp)
            ) { Box(contentAlignment = Alignment.Center) { Text(icon, fontSize = 18.sp) } }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B4332))
        }
    }
}

@Composable
fun AchievementItem(icon: String, label: String) {
    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(1.2f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151))
        }
    }
}

@Composable
fun SettingMenuItem(icon: ImageVector, title: String, isLogout: Boolean = false, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = if (isLogout) Color(0xFFB91C1C) else Color(0xFF1B4332), modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = if (isLogout) Color(0xFFB91C1C) else Color(0xFF1F2937), modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Go", tint = if (isLogout) Color(0xFFB91C1C) else Color(0xFF9CA3AF), modifier = Modifier.size(20.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    val mockProfile = UserProfile(
        userId = "preview_user",
        currentGold = 1000,
        currentEC = 200,
        idHouse = "castle_01",
        currentAvatarUrl = "avatar_castle",
        ownedHouses = listOf("h1", "h2")
    )
    ProfileContent(
        name = "Alex Gardener",
        email = "Architect of Serenity",
        profile = mockProfile,
        isLoading = false,
        onBack = {},
        onAvatarClick = {},
        onLogoutClick = {}
    )
}