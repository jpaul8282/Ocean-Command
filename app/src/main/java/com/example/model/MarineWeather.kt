package com.example.model

data class MarineWeather(
    val seaSurfaceTempCelsius: Double,
    val waveHeightMeters: Double,
    val wavePeriodSeconds: Int,
    val windSpeedKnots: Double,
    val windDirectionCompass: String,
    val barometricPressureHpa: Double,
    val salinityPsu: Double, // Practical Salinity Unit
    val visibilityNauticalMiles: Double,
    val seaConditionState: String, // e.g. "Slight Waves", "Moderate Sea", "Rough Ocean"
    val stormWarningActive: Boolean,
    val stormAlertMessage: String? = null
)
