package com.honghanh.buildyourcastledemo.ui.focus

import android.os.CountDownTimer
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusState
import androidx.lifecycle.ViewModel

//bản thân class này cũng là một đối tượng
//lớp này kế thừa từ lớp ViewModel có sẵn
//dùng viewmodel để khi xoay điện thoại nó vẫn lưu trữ thời gian
//không bị reset về trạng thái ban đầu
class FocusViewModel: ViewModel() {

    //định nghĩa các hằng số thời gian chuẩn bằng mili
    //chữ L của 1000L là để nó hiểu là số Long
    //tức là nó báo cho máy tính biết cấp phát một vùng
    // nhớ lớn dành cho kiểu Long để lưu trữ con số này
    //chứ không dùng int. để bảo vệ bộ nhớ không bị tràn
    //vì int chỉ lưu trữ được khoảng 2 tỷ
    //mà 1 phút là khoảng 60 giây = 1000mili
    //sau này nếu cộng dồn, sẽ dễ vượt mức 2 tỷ
    //dùng val vì cố định 25p nghỉ một lần 5p không đổi
    private val tgTapTrungMotPhien = 25 * 1000L
    private val thoiGianNghi = 5  * 1000L
    //thời gian phiên hiện tại không cố định
    //vì khi thì là phiên nghỉ khi thì là phiên đang chạy

    //thời gian tổng đã chọn
    var tongTGDaChon = mutableLongStateOf(0L)
        private set

    //đồng hồ phụ đếm thời gian nghỉ
    var tgNghi = mutableLongStateOf(0L)
        private set

    //mốc để biết khi nào thì user được nghỉ
    private var cotMocNghi = 0L

    //trạng thái hiện tại của hệ thống
    //bọc một biến vào trong mutableStateOf là biến đó có khả năng phát sóng
    //để file giao diện đang ký lắng nghe sóng này
    //mutableStateOf để quản lý các kiểu dữ liệu như enum, string, class...
    //mutableLongStateOf để tối ưu bộ nhớ cho kiểu long
    var trangThaiHienTai = mutableStateOf(FocusStatus.CHUAN_BI)
        private set

    //vì hàm CountDownTimer cần một thông số kiểu Long
    //nên phải viết là 1000L
    private var boDemGio: CountDownTimer? = null


    //bắt đầu hàm 1: user chọn thời gian
    //chỉ là chọn số trên giao diện nên dùng int
    //không cần dùng long tránh lãng phí tài nguyên
    fun batDauTapTrung(tongTG: Int) {
        if (trangThaiHienTai.value != FocusStatus.CHUAN_BI) return
        //khởi tạo đồng hồ
        tongTGDaChon.longValue = tongTG * 1000L//đổi về kiểu long
        //tính mốc nghỉ
        tinhMocNghi()
        kichHoatTapTrung()

    }

    //hàm 1 tính toán nghỉ
    // mốc nghỉ này cũng có nghĩa là thời gian hiện tại
    private fun tinhMocNghi() {
        cotMocNghi = tongTGDaChon.longValue - tgTapTrungMotPhien
        //nếu tg còn lại < 25p thì cày về 0 mà không có quãng nghỉ
        // không dùng < 25p hay < tgTapTrungMotPhien
        //vì nếu thời gian hiện tại dừng ở 10p, nó <25p thì bị kéo tụt về 0 luôn
        //nó sẽ bỏ qua lần nghỉ ở mốc 10p đó mà không nghỉ
        if (cotMocNghi < 0) cotMocNghi = 0L
    }

    //hàm 2 kích hoạt chay tập trung
    private fun kichHoatTapTrung() {
        trangThaiHienTai.value = FocusStatus.DANG_CHAY
        boDemGio?.cancel()
        //object : CountDownTimer(A, B)
        //A là tổng thời gian câần đếm
        //b là khoảng thời gian giãn cách nhảy số mỗi lần
        //chạy bộ đếm
        boDemGio = object : CountDownTimer(tongTGDaChon.longValue, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                tongTGDaChon.longValue = millisUntilFinished


                //kiểm tra đã chạm mốc nghỉ ch
                if (tongTGDaChon.longValue <= 0L) {
                    boDemGio?.cancel()//dừng đồng hồ lại
                    trangThaiHienTai.value = FocusStatus.HOAN_THANH
                    println("Đã hoàn thành!")
                } else if (tongTGDaChon.longValue <= cotMocNghi) {
                    boDemGio?.cancel()
                    kichHoatNghiNgoi()
                }
            }


            override fun onFinish() {
                tongTGDaChon.longValue = 0L
                    trangThaiHienTai.value = FocusStatus.HOAN_THANH

            }

        }.start()
    }
    fun thuongVang(){

    }


    //hàm 3 kích hoạt nghỉ ngơi
    private fun kichHoatNghiNgoi() {
        trangThaiHienTai.value = FocusStatus.NGHI_NGOI
        tgNghi.longValue = thoiGianNghi//nạp 5p vào đồng hồ phụ
        println("Đã đến giờ nghỉ")
        boDemGio?.cancel()
        boDemGio = object : CountDownTimer(thoiGianNghi, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tgNghi.longValue = millisUntilFinished
            }

            override fun onFinish() {
                hetGioNghiNgoi()
            }
        }.start()
    }
    //hàm 5 hết giờ nghỉ
    private fun hetGioNghiNgoi(){
        tinhMocNghi()
        kichHoatTapTrung()

    }
    //hàm 4 bỏ qua tạm nghỉ
    fun boQuaNghi() {
        if (trangThaiHienTai.value == FocusStatus.NGHI_NGOI) {
          println("Bỏ qua nghỉ")
            hetGioNghiNgoi()
        }
    }
    //hàm bỏ cuộc
    fun boCuoc(){
      if ( trangThaiHienTai.value == FocusStatus.DANG_CHAY||
          trangThaiHienTai.value == FocusStatus.NGHI_NGOI){
          boDemGio?.cancel()
          trangThaiHienTai.value = FocusStatus.BO_CUOC
          println("Bạn đã bỏ cuộc!")
      }

    }
    fun resetVeChuanBi(){
        boDemGio?.cancel()
        trangThaiHienTai.value = FocusStatus.CHUAN_BI
        tongTGDaChon.longValue = 0L
        tgNghi.longValue = 0L
    }
    //hàm format thời gian, chuyển sang dạng mm:ss
    fun dinhDangThoiGian(tg: Long): String {
        val tongGiay = tg / 1000
        val phut = tongGiay / 60
        val giay = tongGiay % 60
        return String.format("%02d:%02d", phut, giay)
    }
    override fun onCleared() {
        super.onCleared()
        boDemGio?.cancel()
    }
}









