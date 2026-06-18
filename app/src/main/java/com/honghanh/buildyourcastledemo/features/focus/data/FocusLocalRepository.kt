package com.honghanh.buildyourcastledemo.features.focus.data


import com.honghanh.buildyourcastledemo.core.database.FocusSessionDao
import com.honghanh.buildyourcastledemo.core.model.FocusSession
import kotlinx.coroutines.flow.Flow

class FocusLocalRepository(private val focusSessionDao: FocusSessionDao) {

    // Lệnh lưu phiên tập trung xuống Room (chạy bất đồng bộ qua Coroutine suspend)
    suspend fun luuPhienLocal(localSession: FocusSession) {
        focusSessionDao.insertSession(localSession)
    }

    // Lấy danh sách lịch sử local dạng Flow để UI tự động cập nhật mỗi khi có dữ liệu mới
    fun layDanhSachPhienLocal(userId: String): Flow<List<FocusSession>> {
        return focusSessionDao.getSessionsByUserId(userId)
    }

    // Xóa sạch dữ liệu của user này dưới máy khi cần
    suspend fun xoaDuLieuLocal(userId: String) {
        focusSessionDao.clearSessionsByUserId(userId)
    }
}