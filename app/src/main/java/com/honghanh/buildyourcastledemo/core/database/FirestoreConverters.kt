package com.honghanh.buildyourcastledemo.core.database



import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import java.util.Date

class FirestoreConverters {

    // 1. Dịch xử lý cho Timestamp (Chuyển qua lại giữa Timestamp và Long)
    @TypeConverter
    fun fromTimestamp(timestamp: Timestamp?): Long? {
        return timestamp?.toDate()?.time
    }

    @TypeConverter
    fun toTimestamp(milliseconds: Long?): Timestamp? {
        return milliseconds?.let { Timestamp(Date(it)) }
    }

    // 2. Dịch xử lý cho DocumentReference (Chuyển qua lại giữa Reference và String đường dẫn)
    @TypeConverter
    fun fromReference(reference: DocumentReference?): String? {
        return reference?.path // Trả về dạng chuỗi "/userprofile/id_cua_ban"
    }

    @TypeConverter
    fun toReference(path: String?): DocumentReference? {
        return path?.let { FirebaseFirestore.getInstance().document(it) }
    }
}