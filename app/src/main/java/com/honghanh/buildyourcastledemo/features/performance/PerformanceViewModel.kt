package com.honghanh.buildyourcastledemo.features.performance

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.honghanh.buildyourcastledemo.core.model.FocusSession
import com.honghanh.buildyourcastledemo.core.model.PerformanceState
import java.util.Calendar

class PerformanceViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    var performanceState = mutableStateOf(PerformanceState())
        private set
    var isLoading = mutableStateOf(true)

    init {
        taiDuLieuThongKe()
    }

    fun taiDuLieuThongKe() {
        val currentUid = auth.currentUser?.uid ?: return
        isLoading.value = true

        // Lấy toàn bộ dữ liệu trong bảng focus_sessions về để tự lọc ngầm (Tránh lỗi lệch kiểu dữ liệu)
        db.collection("focussession")
            .get()
            .addOnSuccessListener { documents ->
                var totalMinutes = 0
                var totalTasks = 0
                var totalAC = 0
                val weeklyMinutes = mutableListOf(0, 0, 0, 0, 0, 0, 0) // T2 -> CN

                for (doc in documents) {
                    try {
                        // Kiểm tra trường userId trên Firebase bất kể là kiểu String hay Reference
                        val userIdRaw = doc.get("userId")
                        val isBelongsToUser = when (userIdRaw) {
                            is String -> userIdRaw == currentUid
                            is DocumentReference -> userIdRaw.id == currentUid
                            else -> false
                        }

                        // Kiểm tra xem trạng thái có phải là hoàn thành hay không
                        val status = doc.getString("status") ?: ""
                        val isCompleted = status.equals("HOAN_THANH", ignoreCase = true)

                        if (isBelongsToUser && isCompleted) {
                            val actualDuration = doc.getLong("actualDuration")?.toInt() ?: 0
                            val ecEarned = doc.getLong("ECEarned")?.toInt() ?: 0

                            // Lấy an toàn trường thời gian kết thúc
                            val timestamp = doc.getTimestamp("endTime")

                            totalMinutes += actualDuration
                            totalTasks++
                            totalAC += ecEarned

                            if (timestamp != null) {
                                val sessionDate = timestamp.toDate()
                                val cal = Calendar.getInstance()
                                cal.time = sessionDate

                                val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                                val index = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - 2

                                if (index in 0..6) {
                                    weeklyMinutes[index] += actualDuration
                                }
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e(
                            "PERFORMANCE_LOG",
                            "Lỗi phân tích 1 dòng session: ${e.localizedMessage}"
                        )
                    }
                }

                // Cập nhật State lên màn hình UI
                performanceState.value = PerformanceState(
                    totalFocusMinutes = totalMinutes,
                    totalTasksCompleted = totalTasks,
                    totalAetherCrystalsEarned = totalAC,
                    weeklyFocusMinutes = weeklyMinutes
                )
                isLoading.value = false
            }
            .addOnFailureListener { e ->
                android.util.Log.e("PERFORMANCE_LOG", "Lỗi kết nối bộ sưu tập focus_sessions: ", e)
                isLoading.value = false
            }
    }
}