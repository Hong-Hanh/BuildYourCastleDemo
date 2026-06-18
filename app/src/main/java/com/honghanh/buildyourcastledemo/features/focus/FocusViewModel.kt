package com.honghanh.buildyourcastledemo.features.focus

import android.os.CountDownTimer
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.honghanh.buildyourcastledemo.core.model.FocusStatus

// Lớp giữ nguyên trạng thái khi xoay màn hình hoặc thay đổi cấu hình ứng dụng
class FocusViewModel : ViewModel() {

    // Định nghĩa chuẩn thời gian bằng mili-giây (Phút * 60 giây * 1000 mili)
    private val tgTapTrungMotPhien = 25 * 100L // 25 phút cày castle (đang để demo chạy nhanh)
    private val thoiGianNghi = 5  * 1000L       // 5 phút giải lao nghỉ ngơi

    // Quản lý số vàng và kim cương cục bộ trong màn hình tập trung
    var goldAmount = mutableStateOf(0)
        private set
    var gemsAmount = mutableStateOf(0)
        private set
    var currentHouseImageUrl = mutableStateOf("https://link-to-your-server-image.com/house.png")

    // --------------
    // Thời gian tổng đã chọn để đếm ngược (đơn vị: mili-giây)
    var tongTGDaChon = mutableLongStateOf(0L)
        private set

    // Lưu lại tổng số phút ban đầu người dùng chọn để làm căn cứ tính tiền thưởng
    private var soPhutMucTieuBanDau = 0

    // Đồng hồ phụ đếm thời gian nghỉ (đơn vị: mili-giây)
    var tgNghi = mutableLongStateOf(0L)
        private set

    // Mốc để hệ thống nhận biết khi nào cần chuyển sang trạng thái giải lao
     var cotMocNghi = 0L
    private set

    // Trạng thái cốt lõi điều khiển toàn bộ bộ phát sóng UI
    var trangThaiHienTai = mutableStateOf(FocusStatus.CHUAN_BI)
        private set

    // THÊM MỚI: State thông báo để UI hứng và hiển thị Dialog chúc mừng
    var thongBaoThuong = mutableStateOf<String?>(null)

    private var boDemGio: CountDownTimer? = null



    // Hàm 1: Nhận số phút từ thanh kéo ngang (BottomSheet) để bắt đầu kích hoạt
    fun batDauTapTrung(tongTGPhut: Int) {
        if (trangThaiHienTai.value != FocusStatus.CHUAN_BI) return

        // Lưu lại số phút để tí nữa tính toán số vàng thưởng tương ứng
        soPhutMucTieuBanDau = tongTGPhut

        // Đổi số phút người dùng chọn từ giao diện sang mili-giây để nạp vào đồng hồ
        tongTGDaChon.longValue = tongTGPhut * 100L

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

        boDemGio = object : CountDownTimer(tongTGDaChon.longValue, 100L) {
            override fun onTick(millisUntilFinished: Long) {
                tongTGDaChon.longValue = millisUntilFinished

                // ĐÃ SỬA: Bỏ kiểm tra <= 0L ở đây để tránh bị gọi trùng với onFinish()
                if (tongTGDaChon.longValue <= cotMocNghi && cotMocNghi > 0L) {
                    boDemGio?.cancel()
                    kichHoatNghiNgoi()
                }
            }

            override fun onFinish() {
                tongTGDaChon.longValue = 0L
                trangThaiHienTai.value = FocusStatus.HOAN_THANH
                thuongVang() // Thực hiện cộng xu và kích hoạt thông báo khi kết thúc chuẩn xác
            }
        }.start()
    }

    // ĐÃ SỬA: Hàm cộng vàng và bắn thông báo lên màn hình UI
    fun thuongVang() {
        // Thuật toán: Cứ 1 phút tập trung đổi lấy 10 xu vàng
        val soVangThuong = soPhutMucTieuBanDau * 10

        // 1. Thực hiện cộng dồn tiền vào State để cập nhật UI
        goldAmount.value = goldAmount.value + soVangThuong

        // 2. Gán nội dung thông báo vào State thông báo thưởng
        thongBaoThuong.value = "Chúc mừng! Bạn đã hoàn thành xuất sắc $soPhutMucTieuBanDau phút tập trung và nhận được $soVangThuong xu vàng để xây dựng lâu đài!"

        println("Log Hệ Thống: Đã cộng $soVangThuong vàng. Số vàng hiện tại: ${goldAmount.value}")
    }

    // Hàm xoá thông báo sau khi người dùng bấm nút "Đóng" hoặc "Nhận" trên Dialog UI
    fun xoaThongBao() {
        thongBaoThuong.value = null
    }

    // Hàm 3: Kích hoạt đếm ngược thời gian nghỉ ngơi giải lao
    private fun kichHoatNghiNgoi() {
        trangThaiHienTai.value = FocusStatus.NGHI_NGOI
        tgNghi.longValue = thoiGianNghi
        boDemGio?.cancel()
        thongBaoThuong.value = "Bạn đã làm việc rất tốt! Hãy nghỉ ngơi một chút nhé 🎉"

        boDemGio = object : CountDownTimer(thoiGianNghi, 100L) {
            override fun onTick(millisUntilFinished: Long) {
                tgNghi.longValue = millisUntilFinished
            }

            override fun onFinish() {
                tgNghi.longValue = 0L
                // Khi hết 5 phút nghỉ, CHỈ bắn thông báo, KHÔNG tự động kích hoạt chạy tiếp ngầm nữa
                thongBaoThuong.value = "☕ Hết giờ nghỉ ngơi rồi! Hãy bấm xác nhận để bắt đầu phiên làm việc tiếp theo nào."
            }
        }.start()
    }
    fun xacNhanVaoPhienTiepTheo() {
        tinhMocNghi()
        kichHoatTapTrung()
    }



    // Hàm 5: Người dùng không muốn nghỉ mà chọn bỏ qua để cày tiếp tục
    fun boQuaNghi() {
        if (trangThaiHienTai.value == FocusStatus.NGHI_NGOI) {
            boDemGio?.cancel() // Huỷ đếm ngược 5 phút nghỉ
            tgNghi.longValue = 0L
            tinhMocNghi()
            kichHoatTapTrung() // Vào việc luôn
            println("Log: Đã chủ động bỏ qua phiên nghỉ ngơi.")
        }
    }

    // Hàm 6: Huỷ tiến trình đếm ngược giữa chừng
    fun boCuoc(thongBao: String = "Bạn đã hủy phiên tập trung!") {
        if (trangThaiHienTai.value == FocusStatus.DANG_CHAY ||
            trangThaiHienTai.value == FocusStatus.NGHI_NGOI
        ) {
            boDemGio?.cancel()
            trangThaiHienTai.value = FocusStatus.BO_CUOC
            thongBaoThuong.value = thongBao  // ← dùng thông báo được truyền vào
            tongTGDaChon.longValue = 0L
            tgNghi.longValue = 0L
        }
    }

    // Hàm 7: Trở về màn hình trạng thái ban đầu để thiết lập phiên mới
    fun resetVeChuanBi() {
        boDemGio?.cancel()
        trangThaiHienTai.value = FocusStatus.CHUAN_BI
        tongTGDaChon.longValue = 0L
        tgNghi.longValue = 0L
        soPhutMucTieuBanDau = 0
    }


    // Hàm helper: Định dạng mm:ss
    fun dinhDangThoiGian(tgMiliGiay: Long): String {
        val tongGiay = tgMiliGiay / 100
        val phut = tongGiay / 60
        val giay = tongGiay % 60
        return String.format("%02d:%02d", phut, giay)
    }

    override fun onCleared() {
        super.onCleared()
        boDemGio?.cancel()
    }
}