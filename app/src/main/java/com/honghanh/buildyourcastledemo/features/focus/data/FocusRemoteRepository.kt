package com.honghanh.buildyourcastledemo.features.focus.data


import com.honghanh.buildyourcastledemo.core.database.FirebaseProvider
import com.honghanh.buildyourcastledemo.core.model.FocusSession
class FocusRemoteRepository {


    class FocusRemoteRepository {

        private val db = FirebaseProvider.firestore

        // Hàm lưu phiên tập trung lên Firestore
        fun luuPhienTapTrung(session: FocusSession, onComplete: (Boolean) -> Unit) {
            db.collection("focusession") // Sửa lại cho đúng tên bảng trên Console của bạn
                .document(session.sessionId)
                .set(session)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        }
    }
}