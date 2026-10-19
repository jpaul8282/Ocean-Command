package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class VesselStatus {
    IN_TRANSIT,
    SURVEYING,
    ANCHORED,
    DOCKED,
    ALERT
}

@Entity(tableName = "vessels")
data class Vessel(
    @PrimaryKey val id: String,
    val name: String,
    val callSign: String,
    val vesselType: String, // e.g., "Research Submersible", "Hydrographic Ship", "Deepsea Surveyor"
    val latitude: Double,
    val longitude: Double,
    val speedKnots: Double,
    val headingDegrees: Int,
    val depthMeters: Double,
    val status: VesselStatus,
    val fuelPercent: Int,
    val batteryPercent: Int,
    val crewCount: Int,
    val captainName: String,
    val lastPingTimestamp: Long = System.currentTimeMillis()
)
