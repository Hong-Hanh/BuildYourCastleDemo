package com.honghanh.buildyourcastledemo.ui.focus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.*
import com.honghanh.buildyourcastledemo.R
import com.honghanh.buildyourcastledemo.ui.theme.BuildYourCastleDemoTheme

@Composable
fun FocusScreen(viewModel: FocusViewModel = viewModel()) {
    // Đăng ký lắng nghe các trạng thái từ ViewModel
    val trangThai by viewModel.trangThaiHienTai
    val thoiGianTong by viewModel.tongTGDaChon
    val thoiGianNghi by viewModel.tgNghi

    // Cấu hình Lottie
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.house))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = trangThai == FocusStatus.DANG_CHAY
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        // 1. Khu vực hiển thị trạng thái xây lâu đài
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (trangThai) {
                    FocusStatus.CHUAN_BI -> "Chọn thời gian tập trung"
                    FocusStatus.DANG_CHAY -> "Thợ đang xây lâu đài!"
                    FocusStatus.NGHI_NGOI -> "Thợ đang nghỉ ngơi!"
                    FocusStatus.HOAN_THANH -> "Thợ đã xây xong!"
                    FocusStatus.BO_CUOC -> "Thợ đã bỏ cuộc!"
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = when (trangThai) {
                    FocusStatus.DANG_CHAY -> Color.Blue
                    FocusStatus.BO_CUOC -> Color.Red
                    else -> Color.Gray
                }
            )
        }

        // 2. Khu vực Lottie và Đồng hồ
        Column(
            modifier = Modifier.weight(3f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .size(250.dp)
                    .padding(bottom = 16.dp)
            )

            if (trangThai != FocusStatus.CHUAN_BI) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = viewModel.dinhDangThoiGian(thoiGianTong),
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Black,
                        color = if (trangThai == FocusStatus.DANG_CHAY) Color.Black else Color.Gray
                    )
                    Text(text = "Tổng thời gian còn lại", fontSize = 14.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedVisibility(visible = trangThai == FocusStatus.NGHI_NGOI) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = viewModel.dinhDangThoiGian(thoiGianNghi),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                            Text("Thời gian nghỉ tạm thời", fontSize = 14.sp, color = Color(0xFF4CAF50))
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(100.dp)) // Giữ chỗ khi chưa bắt đầu
            }
        }

        // 3. Khu vực các nút điều khiển
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            // Gọi hàm nút bấm ở đây
            ControlButtons(trangThai, viewModel)
        }
    }
}

// HÀM NÀY PHẢI Ở NGOÀI FocusScreen
@Composable
fun ControlButtons(trangThai: FocusStatus, viewModel: FocusViewModel) {
    when (trangThai) {
        FocusStatus.CHUAN_BI -> {
            var selectedTime by remember { mutableIntStateOf(25) }
            val times = listOf(5, 25, 50, 75, 100, 125, 150)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Kéo để chọn thời gian:", fontSize = 16.sp, color = Color.Gray)

                TimeHorizontalPicker(
                    times = times,
                    initialIndex = 1, // Mặc định chọn 25 phút (index 1 trong danh sách)
                    onTimeSelected = { selectedTime = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.batDauTapTrung(selectedTime) },
                    modifier = Modifier.fillMaxWidth(0.7f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0e3c87))
                ) {
                    Text("Bắt đầu", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        FocusStatus.DANG_CHAY -> {
            Button(
                onClick = { viewModel.boCuoc() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Bỏ Cuộc", color = Color.White)
            }
        }
        FocusStatus.NGHI_NGOI -> {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { viewModel.boCuoc() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("Bỏ Cuộc")
                }
                Button(onClick = { viewModel.boQuaNghi() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                    Text("Bỏ Qua Nghỉ")
                }
            }
        }
        FocusStatus.HOAN_THANH, FocusStatus.BO_CUOC -> {
            Button(onClick = { viewModel.resetVeChuanBi() }) {
                Text("Xây Lâu Đài Mới")
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
    val itemWidth = 80.dp

    // Tính toán index đang ở chính giữa
    val centeredIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) 0
            else {
                val center = layoutInfo.viewportStartOffset +
                        (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2
                visibleItemsInfo.minByOrNull { Math.abs((it.offset + it.size / 2) - center) }?.index ?: 0
            }
        }
    }

    // Cuộn đến vị trí ban đầu khi vừa mở
    LaunchedEffect(Unit) {
        listState.scrollToItem(initialIndex)
    }

    // Trả kết quả về cho UI chính khi người dùng kéo qua lại
    LaunchedEffect(centeredIndex) {
        if (centeredIndex < times.size) {
            onTimeSelected(times[centeredIndex])
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
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
                val scale by animateFloatAsState(targetValue = if (isSelected) 1.6f else 1.0f)
                val alpha by animateFloatAsState(targetValue = if (isSelected) 1.0f else 0.3f)
                val color by animateColorAsState(targetValue = if (isSelected) Color(0xFF1B365D) else Color.Gray)

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
                        fontSize = 24.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                        color = color
                    )
                }
            }
        }
    }
}

// PREVIEW PHẢI LÀ HÀM RIÊNG, KHÔNG CÓ THAM SỐ
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FocusScreenPreview() {
    BuildYourCastleDemoTheme {
        FocusScreen()
    }
}
