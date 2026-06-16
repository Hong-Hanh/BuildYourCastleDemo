package com.honghanh.buildyourcastledemo.features.focus

import android.os.CountDownTimer
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.honghanh.buildyourcastledemo.core.model.FocusStatus

// Lớp này kế thừa từ lớp ViewModel có sẵn để giữ nguyên trạng thái khi xoay màn hình
class FocusViewModel : ViewModel() {

    // Định nghĩa chuẩn thời gian bằng mili-giây (Phút * 60 giây * 1000 mili)
    private val tgTapTrungMotPhien = 25 * 60 * 1000L // 25 phút cày castle
    private val thoiGianNghi = 5 * 60 * 1000L       // 5 phút giải lao nghỉ ngơi

    // Thời gian tổng đã chọn để đếm ngược (đơn vị: mili-giây)
    var tongTGDaChon = mutableLongStateOf(0L)
        private set

    // Đồng hồ phụ đếm thời gian nghỉ (đơn vị: mili-giây)
    var tgNghi = mutableLongStateOf(0L)
        private set

    // Mốc để hệ thống nhận biết khi nào cần chuyển sang trạng thái giải lao
    private var cotMocNghi = 0L

    // Trạng thái cốt lõi điều khiển toàn bộ bộ phát sóng UI
    var trangThaiHienTai = mutableStateOf(FocusStatus.CHUAN_BI)
        private set

    private var boDemGio: CountDownTimer? = null

    // Hàm 1: Nhận số phút từ thanh kéo ngang (BottomSheet) để bắt đầu kích hoạt
    fun batDauTapTrung(tongTGPhut: Int) {
        if (trangThaiHienTai.value != FocusStatus.CHUAN_BI) return

        // Đổi số phút người dùng chọn từ giao diện sang mili-giây để nạp vào đồng hồ
        tongTGDaChon.longValue = tongTGPhut * 60 * 1000L

        // Tính toán xem trong khoảng thời gian tổng này thì bao giờ được nghỉ
        tinhMocNghi()
        kichHoatTapTrung()
    }

    // Hàm bổ trợ: Tính toán mốc nghỉ dựa trên thời gian còn lại
    private fun tinhMocNghi() {
        cotMocNghi = tongTGDaChon.longValue - tgTapTrungMotPhien
        // Nếu tổng thời gian còn lại ngắn hơn 25 phút, cày một mạch về 0 luôn không nghỉ giữa chừng
        if (cotMocNghi < 0) cotMocNghi = 0L
    }

    // Hàm 2: Kích hoạt chạy đếm ngược tập trung
    private fun kichHoatTapTrung() {
        trangThaiHienTai.value = FocusStatus.DANG_CHAY
        boDemGio?.cancel()

        boDemGio = object : CountDownTimer(tongTGDaChon.longValue, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                tongTGDaChon.longValue = millisUntilFinished

                // Kiểm tra các điều kiện để chuyển trạng thái ngắt quãng
                if (tongTGDaChon.longValue <= 0L) {
                    boDemGio?.cancel()
                    trangThaiHienTai.value = FocusStatus.HOAN_THANH
                    thuongVang() // Đạt mục tiêu thành công thì cộng thưởng vật phẩm
                } else if (tongTGDaChon.longValue <= cotMocNghi && cotMocNghi > 0L) {
                    boDemGio?.cancel()
                    kichHoatNghiNgoi()
                }
            }

            override fun onFinish() {
                tongTGDaChon.longValue = 0L
                trangThaiHienTai.value = FocusStatus.HOAN_THANH
                thuongVang()
            }
        }.start()
    }

    // Hàm cộng vàng thưởng cho tài khoản khi hoàn thành xuất sắc phiên Deep Work
    fun thuongVang() {
        println("Chúc mừng bạn được cộng vàng tăng tài nguyên xây lâu đài!")
    }

    // Hàm 3: Kích hoạt đếm ngược thời gian nghỉ ngơi giải lao
    private fun kichHoatNghiNgoi() {
        trangThaiHienTai.value = FocusStatus.NGHI_NGOI
        tgNghi.longValue = thoiGianNghi // Nạp đầy 5 phút vào bộ nhớ đếm ngược phụ
        boDemGio?.cancel()

        boDemGio = object : CountDownTimer(thoiGianNghi, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                tgNghi.longValue = millisUntilFinished
            }

            override fun onFinish() {
                hetGioNghiNgoi()
            }
        }.start()
    }

    // Hàm 4: Hết giờ giải lao, tự động quay trở lại tiến trình làm việc
    private fun hetGioNghiNgoi() {
        tinhMocNghi()
        kichHoatTapTrung()
    }

    // Hàm 5: Người dùng không muốn nghỉ mà chọn bỏ qua để cày tiếp tục
    fun boQuaNghi() {
        if (trangThaiHienTai.value == FocusStatus.NGHI_NGOI) {
            println("Bỏ qua nghỉ để tập trung tiếp")
            hetGioNghiNgoi()
        }
    }

    // Hàm 6: Huỷ tiến trình đếm ngược giữa chừng khi người dùng bấm nút Close (Đỏ) trên UI
    fun boCuoc() {
        if (trangThaiHienTai.value == FocusStatus.DANG_CHAY ||
            trangThaiHienTai.value == FocusStatus.NGHI_NGOI
        ) {
            boDemGio?.cancel()
            trangThaiHienTai.value = FocusStatus.BO_CUOC
            println("Bạn đã hủy phiên tập trung hiện tại!")
        }
    }

    // Hàm 7: Trở về màn hình trạng thái ban đầu để người dùng thiết lập phiên mới
    fun resetVeChuanBi() {
        boDemGio?.cancel()
        trangThaiHienTai.value = FocusStatus.CHUAN_BI
        tongTGDaChon.longValue = 0L
        tgNghi.longValue = 0L
    }

    // Hàm helper: Định dạng mili-giây sang chuỗi hiển thị trực quan mm:ss trên màn hình
    fun dinhDangThoiGian(tgMiliGiay: Long): String {
        val tongGiay = tgMiliGiay / 1000
        val phut = tongGiay / 60
        val giay = tongGiay % 60
        return String.format("%02d:%02d", phut, giay)
    }

    // Dọn dẹp luồng chạy ngầm của bộ đếm khi ViewModel bị hủy để tránh rò rỉ bộ nhớ (Memory Leak)
    override fun onCleared() {
        super.onCleared()
        boDemGio?.cancel()
    }
}