package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.Vessel
import kotlinx.coroutines.flow.Flow

@Dao
interface VesselDao {
    @Query("SELECT * FROM vessels ORDER BY name ASC")
    fun getAllVessels(): Flow<List<Vessel>>

    @Query("SELECT * FROM vessels WHERE id = :id")
    suspend fun getVesselById(id: String): Vessel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vessels: List<Vessel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVessel(vessel: Vessel)

    @Update
    suspend fun updateVessel(vessel: Vessel)

    @Query("DELETE FROM vessels WHERE id = :id")
    suspend fun deleteVessel(id: String)
}
