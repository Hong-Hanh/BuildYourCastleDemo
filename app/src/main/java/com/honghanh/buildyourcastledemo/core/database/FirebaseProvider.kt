package com.honghanh.buildyourcastledemo.core.database

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.persistentCacheSettings

object FirebaseProvider {

    // Khai báo instance của Firestore kèm cấu hình tối ưu Cache Offline
    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance().apply {
            // 🔥 CẤU HÌNH: Ép Firebase bật tính năng lưu trữ dữ liệu offline dưới máy
            firestoreSettings = firestoreSettings {
                // Sử dụng Persistent Cache (Lưu vào bộ nhớ trong của điện thoại)
                // Giúp dữ liệu không bị mất đi kể cả khi người dùng kill app hoàn toàn
                setLocalCacheSettings(persistentCacheSettings {
                    // Bạn có thể thiết lập thêm kích thước bộ nhớ đệm nếu muốn (mặc định là 100MB)
                })
            }
        }
    }

    // Nếu bạn có dùng thêm Firebase Auth hay Database khác thì khai báo thêm ở dưới...
}