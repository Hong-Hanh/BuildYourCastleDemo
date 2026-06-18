package com.honghanh.buildyourcastledemo.features.focus

import android.os.CountDownTimer
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.honghanh.buildyourcastledemo.core.model.FocusSession
import com.honghanh.buildyourcastledemo.core.model.FocusStatus
import java.util.Date
import java.util.UUID

class FocusViewModel : ViewModel() {

    // Khởi tạo trực tiếp instance Firestore để giải quyết triệt để lỗi Unresolved reference
    private val db = FirebaseFirestore.getInstance()

    private val tgTapTrungMotPhien = 25 * 100L
    private val thoiGianNghi = 5 * 1000L

    var goldAmount = mutableStateOf(0)
        private set
    var ECAmount = mutableStateOf(0)
        private set
    var currentHouseImageUrl = mutableStateOf("https://link-to-your-server-image.com/house.png")

    var tongTGDaChon = mutableLongStateOf(0L)
        private set

    private var soPhutMucTieuBanDau = 0

    // THÊM MỚI: Biến lưu trữ ID người dùng hiện tại (sẽ được truyền từ UI vào lúc bắt đầu)
    private var currentUserId: String = ""

    // THÊM MỚI: Chuỗi mục tiêu do người dùng nhập từ TextField ở UI
    private var currentTargetText: String = ""

    var tgNghi = mutableLongStateOf(0L)
        private set

    var cotMocNghi = 0L
        private set

    var trangThaiHienTai = mutableStateOf(FocusStatus.CHUAN_BI)
        private set

    var thongBaoThuong = mutableStateOf<String?>(null)

    private var boDemGio: CountDownTimer? = null

    // ĐÃ SỬA: Nhận thêm userId và mục tiêuText động từ UI ném xuống khi bấm bắt đầu
    fun batDauTapTrung(tongTGPhut: Int, userId: String, mucTieuText: String) {
        if (trangThaiHienTai.value != FocusStatus.CHUAN_BI) return

        this.currentUserId = userId
        this.currentTargetText = mucTieuText
        this.soPhutMucTieuBanDau = tongTGPhut

        tongTGDaChon.longValue = tongTGPhut * 100L

        tinhMocNghi()
        kichHoatTapTrung()
    }

    private fun tinhMocNghi() {
        cotMocNghi = tongTGDaChon.longValue - tgTapTrungMotPhien
        if (cotMocNghi < 0) cotMocNghi = 0L
    }

    private fun kichHoatTapTrung() {
        trangThaiHienTai.value = FocusStatus.DANG_CHAY
        boDemGio?.cancel()

        boDemGio = object : CountDownTimer(tongTGDaChon.longValue, 100L) {
            override fun onTick(millisUntilFinished: Long) {
                tongTGDaChon.longValue = millisUntilFinished

                if (tongTGDaChon.longValue <= cotMocNghi && cotMocNghi > 0L) {
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

    // ĐÃ SỬA: Hàm tự động đóng gói dữ liệu và đẩy lên Firestore dạng "Reference" khi HOÀN THÀNH
    fun thuongVang() {
        val soVangThuong = soPhutMucTieuBanDau * 12 // Thưởng theo số phút thực tế

        goldAmount.value = goldAmount.value + soVangThuong
        thongBaoThuong.value = "Chúc mừng! Bạn đã hoàn thành xuất sắc $soPhutMucTieuBanDau phút tập trung và nhận được $soVangThuong xu vàng để xây dựng lâu đài!"

        // Nếu không có userId truyền vào từ trước (chưa đăng nhập/lỗi), không đẩy dữ liệu
        if (currentUserId.isEmpty()) return

        // 1. Tạo liên kết con trỏ Reference động
        val userConnectRef = db.collection("userprofile").document(currentUserId)

        // 2. Tính toán thời gian thực tế dựa trên số phút mục tiêu ban đầu
        val currentTimestamp = Timestamp(Date())
        val startTimestamp = Timestamp(Date(System.currentTimeMillis() - (soPhutMucTieuBanDau * 60000L)))

        // 3. Đóng gói Model FocusSession
        val newSession = FocusSession(
            sessionId = UUID.randomUUID().toString(),
            userId = userConnectRef, // Truyền Reference xịn
            startTime = startTimestamp,
            endTime = currentTimestamp,
            targetDuration = soPhutMucTieuBanDau,
            actualDuration = soPhutMucTieuBanDau,
            status = "HOAN_THANH",
            targetText = currentTargetText, // Chuỗi động nhập từ UI
            goldEarned = soVangThuong,
            ECEarned = 0
        )

        // 4. Bắn thẳng lên bảng "focussession"
        db.collection("focussession")
            .document(newSession.sessionId)
            .set(newSession)
    }

    fun xoaThongBao() {
        thongBaoThuong.value = null
    }

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
                thongBaoThuong.value = "☕ Hết giờ nghỉ ngơi rồi! Hãy bấm xác nhận để bắt đầu phiên làm việc tiếp theo nào."
            }
        }.start()
    }

    fun xacNhanVaoPhienTiepTheo() {
        tinhMocNghi()
        kichHoatTapTrung()
    }

    fun boQuaNghi() {
        if (trangThaiHienTai.value == FocusStatus.NGHI_NGOI) {
            boDemGio?.cancel()
            tgNghi.longValue = 0L
            tinhMocNghi()
            kichHoatTapTrung()
        }
    }

    // ĐÃ SỬA: Đẩy luôn lịch sử hủy phiên lên Firebase để sau này hiển thị lên Trang cá nhân dạng "Bỏ cuộc"
    fun boCuoc(thongBao: String = "Bạn đã hủy phiên tập trung!") {
        if (trangThaiHienTai.value == FocusStatus.DANG_CHAY ||
            trangThaiHienTai.value == FocusStatus.NGHI_NGOI
        ) {
            boDemGio?.cancel()
            trangThaiHienTai.value = FocusStatus.BO_CUOC
            thongBaoThuong.value = thongBao

            if (currentUserId.isNotEmpty()) {
                val userConnectRef = db.collection("userprofile").document(currentUserId)

                // Tính số phút thực tế cày được trước khi bấm nút Hủy
                val soGiayDaChay = (soPhutMucTieuBanDau * 100L - tongTGDaChon.longValue) / 100
                val soPhutThucTe = (soGiayDaChay / 60).toInt()

                val cancelSession = FocusSession(
                    sessionId = UUID.randomUUID().toString(),
                    userId = userConnectRef,
                    startTime = Timestamp(Date(System.currentTimeMillis() - (soPhutThucTe * 60000L))),
                    endTime = Timestamp(Date()),
                    targetDuration = soPhutMucTieuBanDau,
                    actualDuration = soPhutThucTe, // Chỉ ghi nhận số phút thực tế làm được
                    status = "BO_CUOC", // Đánh dấu trạng thái tạch
                    targetText = currentTargetText,
                    goldEarned = 0, // Bỏ cuộc thì không có quà
                    ECEarned = 0
                )

                db.collection("focussession").document(cancelSession.sessionId).set(cancelSession)
            }

            tongTGDaChon.longValue = 0L
            tgNghi.longValue = 0L
        }
    }

    fun resetVeChuanBi() {
        boDemGio?.cancel()
        trangThaiHienTai.value = FocusStatus.CHUAN_BI
        tongTGDaChon.longValue = 0L
        tgNghi.longValue = 0L
        soPhutMucTieuBanDau = 0
    }

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