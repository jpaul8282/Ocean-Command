package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.ExpeditionLog
import com.example.model.Vessel

@Database(entities = [Vessel::class, ExpeditionLog::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vesselDao(): VesselDao
    abstract fun expeditionDao(): ExpeditionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ocean_command_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
