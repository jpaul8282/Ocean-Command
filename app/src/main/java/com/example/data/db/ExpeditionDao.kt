package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.ExpeditionLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpeditionDao {
    @Query("SELECT * FROM expedition_logs ORDER BY timestamp DESC")
    fun getAllExpeditionLogs(): Flow<List<ExpeditionLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ExpeditionLog)

    @Query("DELETE FROM expedition_logs WHERE id = :id")
    suspend fun deleteLog(id: Int)

    @Query("DELETE FROM expedition_logs")
    suspend fun deleteAllLogs()
}
