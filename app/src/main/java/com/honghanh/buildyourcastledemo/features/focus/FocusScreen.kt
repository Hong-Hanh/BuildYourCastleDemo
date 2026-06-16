package com.honghanh.buildyourcastledemo.features.focus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.honghanh.buildyourcastledemo.R
import com.honghanh.buildyourcastledemo.core.model.FocusStatus
import com.honghanh.buildyourcastledemo.ui.theme.BuildYourCastleDemoTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    viewModel: FocusViewModel = viewModel(),
    onNavigateToProfile: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToShop: () -> Unit,
    onNavigateToGacha: () -> Unit,
    onNavigateToStorage: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val trangThai by viewModel.trangThaiHienTai
    val thoiGianTong by viewModel.tongTGDaChon

    var showTimePickerSheet by remember { mutableStateOf(false) }
    var selectedTimeForStart by remember { mutableIntStateOf(25) }
    var targetText by remember { mutableStateOf("") }

    // Quản lý trạng thái đóng/mở của thanh vuốt ngang Drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFFF8F9FA),
                modifier = Modifier.width(280.dp) // Giới hạn chiều rộng thanh vuốt gọn gàng
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "TÍNH NĂNG",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF064E3B),
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp), color = Color(0xFFE5E7EB))

                // Các item điều hướng trong Drawer
                NavigationDrawerItem(
                    label = { Text("🏠 Home", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        onNavigateToHome()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                NavigationDrawerItem(
                    label = { Text("🛒 Cửa hàng", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        onNavigateToShop()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                NavigationDrawerItem(
                    label = { Text("🎲 Gacha", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        onNavigateToGacha()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                NavigationDrawerItem(
                    label = { Text("📦 Kho vật phẩm", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        onNavigateToStorage()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                NavigationDrawerItem(
                    label = { Text("📊 Thống kê hiệu suất", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        onNavigateToStatistics()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                // Đường kẻ phân tách phân vùng Admin hệ thống
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp), color = Color(0xFFE5E7EB))

                // 🛠️ NÚT ADMIN THIẾT KẾ ĐẸP MẮT NỔI BẬT NẰM Ở TRONG THANH VUỐT
                NavigationDrawerItem(
                    label = { Text("🛠️ Quản trị viên (Thêm nhà)", fontWeight = FontWeight.Bold, color = Color(0xFF78350F)) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        onNavigateToAdmin()
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .border(1.dp, Color(0xFFFEF3C7), RoundedCornerShape(12.dp)),
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color(0xFFFFFBEB))
                )
            }
        }
    ) {
        // --- TOÀN BỘ GIAO DIỆN CHÍNH MÀN HÌNH FOCUS ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 1. TOP BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // AVATAR (Tích hợp Profile)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable { onNavigateToProfile() }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // RESOURCE CHIPS
                Row(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .border(0.5.dp, Color(0xFFE0E0E0), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🟡", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("1,025", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.width(1.dp).height(16.dp).background(Color(0xFFEEEEEE)))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌙", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("585", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // NÚT BA THANH (Bấm vào để mở rộng Thanh vuốt ngang cạnh màn hình)
                IconButton(onClick = {
                    coroutineScope.launch { drawerState.open() }
                }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Mở Menu", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // --- 2. MAIN ILLUSTRATION ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.thungrac),
                    contentDescription = null,
                    modifier = Modifier.size(320.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // --- 3. NÚT CHUYỂN MẪU ---
            Button(
                onClick = onNavigateToStorage,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA7F3D0)),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF064E3B)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "CHUYỂN MẪU",
                    color = Color(0xFF064E3B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 4. TIMER ---
            Text(
                text = if (trangThai == FocusStatus.CHUAN_BI) "25:00" else viewModel.dinhDangThoiGian(thoiGianTong),
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF064E3B)
            )

            Text(
                text = if (targetText.isEmpty() || trangThai == FocusStatus.CHUAN_BI) "SESSION: DEEP WORK" else "MỤC TIÊU: ${targetText.uppercase()}",
                fontSize = 12.sp,
                color = Color.LightGray,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- 5. PLAY/STOP DYNAMIC BUTTON ---
            val buttonColor = when (trangThai) {
                FocusStatus.CHUAN_BI -> Color(0xFF064E3B)
                FocusStatus.DANG_CHAY -> Color(0xFFBA1A1A)
                else -> Color(0xFF0E3C87)
            }

            val buttonIcon = when (trangThai) {
                FocusStatus.CHUAN_BI -> Icons.Default.PlayArrow
                FocusStatus.DANG_CHAY -> Icons.Default.Close
                else -> Icons.Default.Refresh
            }

            Box(
                modifier = Modifier
                    .padding(bottom = 48.dp)
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(buttonColor)
                    .clickable {
                        when (trangThai) {
                            FocusStatus.CHUAN_BI -> showTimePickerSheet = true
                            FocusStatus.DANG_CHAY -> viewModel.boCuoc()
                            else -> {
                                viewModel.resetVeChuanBi()
                                targetText = ""
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = buttonIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.weight(0.2f))

            // --- BẢNG TRƯỢT CHỌN THỜI GIAN & MỤC TIÊU (BOTTOM SHEET) ---
            if (showTimePickerSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showTimePickerSheet = false },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Thiết lập phiên tập trung",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF064E3B)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = targetText,
                            onValueChange = { targetText = it },
                            label = { Text("Mục tiêu của bạn là gì?") },
                            placeholder = { Text("Ví dụ: Học code, Đọc sách...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF064E3B),
                                focusedLabelColor = Color(0xFF064E3B)
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Chọn thời gian tập trung (Phút)",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val times = listOf(5, 15, 25, 35, 45, 50, 60, 90, 120)
                        TimeHorizontalPicker(
                            times = times,
                            initialIndex = 2,
                            onTimeSelected = { selectedTimeForStart = it }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                showTimePickerSheet = false
                                viewModel.batDauTapTrung(selectedTimeForStart)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF064E3B)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "XÁC NHẬN BẮT ĐẦU",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimeHorizontalPicker(
    times: List<Int>,
    initialIndex: Int,
    onTimeSelected: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    val snappingLayout = rememberSnapFlingBehavior(lazyListState = listState)
    val itemWidth = 60.dp

    val centeredIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) 0
            else {
                val center = layoutInfo.viewportStartOffset +
                        (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2
                visibleItemsInfo.minByOrNull { Math.abs((it.offset + it.size / 2) - center) }?.index
                    ?: 0
            }
        }
    }

    LaunchedEffect(Unit) {
        listState.scrollToItem(initialIndex)
    }

    LaunchedEffect(centeredIndex) {
        if (centeredIndex < times.size) {
            onTimeSelected(times[centeredIndex])
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        contentAlignment = Alignment.Center
    ) {
        val halfWidth = maxWidth / 2

        LazyRow(
            state = listState,
            flingBehavior = snappingLayout,
            contentPadding = PaddingValues(horizontal = halfWidth - (itemWidth / 2)),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(times) { index, time ->
                val isSelected = index == centeredIndex
                val scale by animateFloatAsState(targetValue = if (isSelected) 1.4f else 0.9f)
                val alpha by animateFloatAsState(targetValue = if (isSelected) 1.0f else 0.4f)
                val color by animateColorAsState(targetValue = if (isSelected) Color(0xFF064E3B) else Color.Gray)

                Box(
                    modifier = Modifier
                        .width(itemWidth)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$time",
                        fontSize = 18.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = color
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FocusScreenPreview() {
    BuildYourCastleDemoTheme {
        FocusScreen(
            onNavigateToProfile = {},
            onNavigateToHome = {},
            onNavigateToShop = {},
            onNavigateToGacha = {},
            onNavigateToStorage = {},
            onNavigateToStatistics = {},
            onNavigateToAdmin = {}
        )
    }
}