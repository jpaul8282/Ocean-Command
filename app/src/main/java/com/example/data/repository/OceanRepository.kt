package com.example.data.repository

import com.example.data.db.ExpeditionDao
import com.example.data.db.VesselDao
import com.example.model.DispatchedCommand
import com.example.model.ExpeditionLog
import com.example.model.MarineWeather
import com.example.model.Vessel
import com.example.model.VesselStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class OceanRepository(
    private val vesselDao: VesselDao,
    private val expeditionDao: ExpeditionDao
) {
    val allVessels: Flow<List<Vessel>> = vesselDao.getAllVessels()
    val allExpeditionLogs: Flow<List<ExpeditionLog>> = expeditionDao.getAllExpeditionLogs()

    private val _currentWeather = MutableStateFlow(
        MarineWeather(
            seaSurfaceTempCelsius = 19.4,
            waveHeightMeters = 2.8,
            wavePeriodSeconds = 9,
            windSpeedKnots = 22.5,
            windDirectionCompass = "ENE (65°)",
            barometricPressureHpa = 1014.2,
            salinityPsu = 35.1,
            visibilityNauticalMiles = 14.0,
            seaConditionState = "Moderate Swell",
            stormWarningActive = true,
            stormAlertMessage = "Gale Warning: Tropical Depression forming 180nm East-Northeast of Mariana Sector."
        )
    )
    val currentWeather: Flow<MarineWeather> = _currentWeather.asStateFlow()

    private val _commandHistory = MutableStateFlow<List<DispatchedCommand>>(emptyList())
    val commandHistory: Flow<List<DispatchedCommand>> = _commandHistory.asStateFlow()

    suspend fun initializeDefaultDataIfNeeded() {
        val initialVessels = listOf(
            Vessel(
                id = "VSL-101",
                name = "R/V Nautilus IX",
                callSign = "NUX-99",
                vesselType = "Hydrographic Survey Cruiser",
                latitude = 14.2854,
                longitude = 145.1923,
                speedKnots = 14.8,
                headingDegrees = 45,
                depthMeters = 3840.0,
                status = VesselStatus.SURVEYING,
                fuelPercent = 88,
                batteryPercent = 95,
                crewCount = 24,
                captainName = "Cmdr. Sarah Vance"
            ),
            Vessel(
                id = "VSL-102",
                name = "Poseidon Voyager",
                callSign = "PSV-04",
                vesselType = "Deep Trench Submersible Tender",
                latitude = 11.3733,
                longitude = 142.2201,
                speedKnots = 8.2,
                headingDegrees = 190,
                depthMeters = 10920.0,
                status = VesselStatus.IN_TRANSIT,
                fuelPercent = 74,
                batteryPercent = 89,
                crewCount = 18,
                captainName = "Capt. Marcus Thorne"
            ),
            Vessel(
                id = "VSL-103",
                name = "Coral Sentinel",
                callSign = "CSL-22",
                vesselType = "Marine Biology Lab Vessel",
                latitude = 16.7410,
                longitude = 148.8890,
                speedKnots = 0.0,
                headingDegrees = 310,
                depthMeters = 420.0,
                status = VesselStatus.ANCHORED,
                fuelPercent = 92,
                batteryPercent = 100,
                crewCount = 15,
                captainName = "Dr. Elena Rostova"
            ),
            Vessel(
                id = "VSL-104",
                name = "Triton Observer",
                callSign = "TRO-88",
                vesselType = "Autonomous Wave Telemetry Drone",
                latitude = 13.5011,
                longitude = 144.7500,
                speedKnots = 18.5,
                headingDegrees = 115,
                depthMeters = 1560.0,
                status = VesselStatus.ALERT,
                fuelPercent = 42,
                batteryPercent = 68,
                crewCount = 0,
                captainName = "AI Navigation Module V4"
            )
        )
        vesselDao.insertAll(initialVessels)

        val initialLogs = listOf(
            ExpeditionLog(
                title = "Mariana Trench Abyssal Core Sampling",
                vesselName = "Poseidon Voyager",
                region = "Challenger Deep Sector",
                expeditionType = "Deep Trench Core",
                latitude = 11.3733,
                longitude = 142.2201,
                notes = "Retrieved 3.2m sediment core from 10,915m depth. High barophilic micro-organism density recorded.",
                waterDepthMeters = 10915.0,
                timestamp = System.currentTimeMillis() - 86400000 * 2
            ),
            ExpeditionLog(
                title = "Pacific Seamount Hydrothermal Vent Survey",
                vesselName = "R/V Nautilus IX",
                region = "North Mariana Ridge",
                expeditionType = "Thermal Vent Hydrography",
                latitude = 14.2854,
                longitude = 145.1923,
                notes = "Temperature spike detected near black smoker vent at 3,840m. Mineral deposits rich in copper-zinc sulphides.",
                waterDepthMeters = 3840.0,
                timestamp = System.currentTimeMillis() - 86400000 * 4
            )
        )
        for (log in initialLogs) {
            expeditionDao.insertLog(log)
        }
    }

    suspend fun addExpeditionLog(log: ExpeditionLog) {
        expeditionDao.insertLog(log)
    }

    suspend fun deleteExpeditionLog(id: Int) {
        expeditionDao.deleteLog(id)
    }

    suspend fun updateVesselStatus(vesselId: String, newStatus: VesselStatus) {
        val vessel = vesselDao.getVesselById(vesselId) ?: return
        val updated = vessel.copy(status = newStatus, lastPingTimestamp = System.currentTimeMillis())
        vesselDao.updateVessel(updated)
    }

    suspend fun updateVesselCourse(vesselId: String, heading: Int, speedKnots: Double) {
        val vessel = vesselDao.getVesselById(vesselId) ?: return
        val updated = vessel.copy(
            headingDegrees = heading,
            speedKnots = speedKnots,
            lastPingTimestamp = System.currentTimeMillis()
        )
        vesselDao.updateVessel(updated)
    }

    fun dispatchCommand(command: DispatchedCommand) {
        val currentList = _commandHistory.value.toMutableList()
        currentList.add(0, command)
        _commandHistory.value = currentList
    }

    fun updateWeather(newWeather: MarineWeather) {
        _currentWeather.value = newWeather
    }
}
