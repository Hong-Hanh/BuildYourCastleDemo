package com.honghanh.buildyourcastledemo.features.focus

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.honghanh.buildyourcastledemo.core.model.FocusSession
import com.honghanh.buildyourcastledemo.core.model.FocusStatus
import com.honghanh.buildyourcastledemo.core.model.UserProfile
import com.honghanh.buildyourcastledemo.features.focus.data.FocusLocalRepository
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class FocusViewModel(private val repository: FocusLocalRepository) : ViewModel() {

    private val db = com.honghanh.buildyourcastledemo.core.database.FirebaseProvider.firestore

    private val tgTapTrungMotPhien = 25 * 100L
    private val thoiGianNghi = 5 * 100L

    var goldAmount = mutableIntStateOf(0)
        private set
    var ECAmount = mutableIntStateOf(0)
        private set
    var isLoadingWallet = mutableStateOf(true)
        private set

    var tongTGDaChon = mutableLongStateOf(0L)
        private set

    private var soPhutMucTieuBanDau = 0
    private var currentUserId: String = ""
    private var currentTargetText: String = ""

    var tgNghi = mutableLongStateOf(0L)
        private set

    var cotMocNghi = 0L
        private set

    var trangThaiHienTai = mutableStateOf(FocusStatus.CHUAN_BI)
        private set

    var thongBaoThuong = mutableStateOf<String?>(null)

    private var boDemGio: CountDownTimer? = null

    // Biến lưu trữ URL ảnh ngôi nhà hiển thị ở giữa màn hình Focus (Dùng AsyncImage)
    var currentHouseImageUrl = mutableStateOf("")
        private set

    // 🔥 THÊM MỚI: Biến quan sát toàn bộ Object UserProfile để lấy trường avatar cục bộ theo thời gian thực
    var userProfile = mutableStateOf<UserProfile?>(null)
        private set

    // Biến quản lý listener của Firestore để hủy lắng nghe khi hủy ViewModel
    private var userProfileListener: ListenerRegistration? = null

    init {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            taiThongTinViUser(firebaseUser.uid)
        }
    }

    // 🔥 ĐÃ SỬA: Đồng bộ hóa chuẩn xác dữ liệu real-time từ Firestore đổ về
    fun taiThongTinViUser(userId: String) {
        if (userId.isEmpty()) return
        this.currentUserId = userId

        // Hủy listener cũ nếu có trước khi đăng ký listener mới để tránh rò rỉ bộ nhớ
        userProfileListener?.remove()

        isLoadingWallet.value = true

        // Đăng ký lắng nghe sự thay đổi tài liệu UserProfile theo thời gian thực từ Firestore
        userProfileListener = db.collection("UserProfile").document(userId)
            .addSnapshotListener { snapshot, error ->
                isLoadingWallet.value = false
                if (error != null) {
                    Log.e("FocusViewModel", "Lỗi lắng nghe dữ liệu ví: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val profile = snapshot.toObject(UserProfile::class.java)
                    userProfile.value = profile

                    goldAmount.value = snapshot.getLong("currentGold")?.toInt() ?: 0
                    ECAmount.value = snapshot.getLong("currentEC")?.toInt() ?: 0

                    // 🔥 ĐÃ SỬA: Kiểm tra nếu trường URL bị trống, lấy URL từ object profile hoặc gán một link ảnh nhà mặc định cố định từ Firebase Storage của bạn
                    val dbHouseUrl = snapshot.getString("currentHouseImageUrl")
                    if (!dbHouseUrl.isNullOrEmpty()) {
                        currentHouseImageUrl.value = dbHouseUrl
                    } else if (!profile?.currentHouseImageUrl.isNullOrEmpty()) {
                        currentHouseImageUrl.value = profile!!.currentHouseImageUrl
                    } else {
                        // Link ảnh nhà cấp 1 mặc định trên Firebase Storage của bạn phòng trường hợp tài khoản mới tinh chưa có nhà
                        currentHouseImageUrl.value = "https://firebasestorage.googleapis.com/.../house_default.png"
                    }
                }
            }
    }

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

    fun thuongVang() {
        val soVangThuong = soPhutMucTieuBanDau * 12

        goldAmount.value = goldAmount.value + soVangThuong
        thongBaoThuong.value = "Chúc mừng! Bạn đã hoàn thành xuất sắc $soPhutMucTieuBanDau phút tập trung và nhận được $soVangThuong xu vàng để xây dựng lâu đài!"

        if (currentUserId.isEmpty()) {
            currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        }

        val userConnectRef = db.collection("UserProfile").document(currentUserId)

        val updateData = mapOf(
            "currentGold" to FieldValue.increment(soVangThuong.toLong()),
            "currentEC" to FieldValue.increment(0L)
        )

        userConnectRef.set(updateData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("Firebase_Success", "Đã cập nhật/Tạo mới thành công userprofile!")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase_Error", "Lỗi cập nhật userprofile: ${e.message}")
            }

        val currentTimestamp = Timestamp(Date())
        val startTimestamp = Timestamp(Date(System.currentTimeMillis() - (soPhutMucTieuBanDau * 60000L)))

        val newSession = FocusSession(
            sessionId = UUID.randomUUID().toString(),
            userId = userConnectRef,
            startTime = startTimestamp,
            endTime = currentTimestamp,
            targetDuration = soPhutMucTieuBanDau,
            actualDuration = soPhutMucTieuBanDau,
            status = "HOAN_THANH",
            targetText = currentTargetText,
            goldEarned = soVangThuong,
            ECEarned = 0
        )
        viewModelScope.launch {
            repository.luuPhienLocal(newSession)
        }

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

    fun boCuoc(thongBao: String = "Bạn đã hủy phiên tập trung!") {
        if (trangThaiHienTai.value == FocusStatus.DANG_CHAY ||
            trangThaiHienTai.value == FocusStatus.NGHI_NGOI
        ) {
            boDemGio?.cancel()
            trangThaiHienTai.value = FocusStatus.BO_CUOC
            thongBaoThuong.value = thongBao

            if (currentUserId.isNotEmpty()) {
                val userConnectRef = db.collection("UserProfile").document(currentUserId)

                val soGiayDaChay = (soPhutMucTieuBanDau * 100L - tongTGDaChon.longValue) / 100
                val soPhutThucTe = (soGiayDaChay / 60).toInt()

                val cancelSession = FocusSession(
                    sessionId = UUID.randomUUID().toString(),
                    userId = userConnectRef,
                    startTime = Timestamp(Date(System.currentTimeMillis() - (soPhutThucTe * 60000L))),
                    endTime = Timestamp(Date()),
                    targetDuration = soPhutMucTieuBanDau,
                    actualDuration = soPhutThucTe,
                    status = "BO_CUOC",
                    targetText = currentTargetText,
                    goldEarned = 0,
                    ECEarned = 0
                )
                viewModelScope.launch {
                    repository.luuPhienLocal(cancelSession)
                }

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
        // Gỡ bỏ lắng nghe hoàn toàn khi hủy ViewModel để giải phóng tài nguyên hệ thống
        userProfileListener?.remove()
    }
}