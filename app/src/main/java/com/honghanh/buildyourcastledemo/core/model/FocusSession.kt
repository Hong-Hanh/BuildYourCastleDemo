package com.honghanh.buildyourcastledemo.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

@Entity(tableName = "focus_sessions") // 👈 Thêm dòng này để Room nhận biết bảng
data class FocusSession(
    @PrimaryKey // 👈 Thêm dòng này để làm khóa chính cho Room
    val sessionId: String = "",
    val userId: DocumentReference? = null,
    val startTime: Timestamp = Timestamp.now(),
    val endTime: Timestamp = Timestamp.now(),
    val targetDuration: Int = 0,
    val actualDuration: Int = 0,
    val status: String = "",
    val targetText: String = "",
    val goldEarned: Int = 0,
    val ECEarned: Int = 0
)