package com.example.fpwn

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//https://developer.android.com/training/data-storage/room
// as I was reading documentation it was saying for kotlin
// try using room instead of an sqlite api
// also in our emails you asked me to try using it
// it looks like I was successful in building this out in
// kotlin, which I am proud of, but the documentation is
// what really carried me through.

@Database(entities = [User::class,InventoryItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun InventoryDao() : InventoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}