package com.honghanh.buildyourcastledemo.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.honghanh.buildyourcastledemo.core.model.FocusSession
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {

    // 1. Chèn hoặc cập nhật phiên tập trung mới xuống máy
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSession)

    // 2. Lấy toàn bộ lịch sử của 1 User cụ thể (Phục vụ trang cá nhân / Bảng thống kê)
    @Query("SELECT * FROM focus_sessions WHERE userId = :userId ORDER BY endTime DESC")
    fun getSessionsByUserId(userId: String): Flow<List<FocusSession>>

    // 3. Xóa dữ liệu (nếu cần thiết khi người dùng đăng xuất)
    @Query("DELETE FROM focus_sessions WHERE userId = :userId")
    suspend fun clearSessionsByUserId(userId: String)
}