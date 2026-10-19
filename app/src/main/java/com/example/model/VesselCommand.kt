package com.example.model

enum class CommandType {
    SONAR_SWEEP,
    COURSE_UPDATE,
    EMERGENCY_STATION,
    RENDEZVOUS_PING,
    SAMPLE_COLLECT,
    HOLD_POSITION
}

data class DispatchedCommand(
    val id: String,
    val vesselId: String,
    val vesselName: String,
    val commandType: CommandType,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "ACKNOWLEDGED"
)
