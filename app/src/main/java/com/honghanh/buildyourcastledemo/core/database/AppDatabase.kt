package com.honghanh.buildyourcastledemo.core.database

import android.content.Context // 👈 1. Thêm import này để hết lỗi đỏ chữ Context
import androidx.room.Database
import androidx.room.Room // 👈 2. Thêm import này để hết lỗi đỏ chữ Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.honghanh.buildyourcastledemo.core.database.FocusSessionDao // 👈 3. Import cái DAO của bạn vào đây
import com.honghanh.buildyourcastledemo.core.model.FocusSession

@Database(entities = [FocusSession::class], version = 1, exportSchema = false)
@TypeConverters(FirestoreConverters::class)
abstract class AppDatabase : RoomDatabase() {

    // 👈 4. ĐÃ SỬA: Sửa lại để trả về DAO xịn xử lý câu lệnh SQL, thay vì trả về Model
    abstract fun focusSessionDao(): FocusSessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "castle_focus_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}