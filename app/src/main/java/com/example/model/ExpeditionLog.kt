package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expedition_logs")
data class ExpeditionLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val vesselName: String,
    val region: String, // e.g., "Mariana Trench Sector 4", "Pacific Equatorial Ridge", "Bering Strait"
    val expeditionType: String, // "Deep Trench Sonar Mapping", "Microplastic Sampling", "Hydrothermal Vent Study"
    val latitude: Double,
    val longitude: Double,
    val notes: String,
    val waterDepthMeters: Double,
    val timestamp: Long = System.currentTimeMillis()
)
