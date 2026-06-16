package com.honghanh.buildyourcastledemo.core.model

//enum là danh sách các lựa chọn tự đặt ra
//tại một thời điểm chỉ dđược chọn 1 trong các lựa chọn này
enum class FocusStatus {
    CHUAN_BI, //màn hình chọn thời gian
    DANG_CHAY, //màn hình đang đếm ngược
    NGHI_NGOI, //màn hình đang nghỉ ngơi
    BO_CUOC, //màn hình bỏ cuộc
    HOAN_THANH //màn hình chúc mừng hoàn thành
}