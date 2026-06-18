package com.honghanh.buildyourcastledemo.core.database

import com.google.firebase.firestore.FirebaseFirestore

object FirebaseProvider {

    // Khai báo instance của Firestore để các file khác gọi sử dụng
    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    // Nếu bạn có dùng thêm Firebase Auth hay Database khác thì khai báo thêm ở dưới...
}