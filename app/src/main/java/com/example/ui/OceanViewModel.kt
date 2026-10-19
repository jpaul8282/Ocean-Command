package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.repository.OceanRepository
import com.example.model.CommandType
import com.example.model.DispatchedCommand
import com.example.model.ExpeditionLog
import com.example.model.MarineWeather
import com.example.model.Vessel
import com.example.model.VesselStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class OceanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: OceanRepository

    val vesselsState: StateFlow<List<Vessel>>
    val expeditionLogsState: StateFlow<List<ExpeditionLog>>
    val weatherState: StateFlow<MarineWeather>
    val commandHistoryState: StateFlow<List<DispatchedCommand>>

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedVessel = MutableStateFlow<Vessel?>(null)
    val selectedVessel: StateFlow<Vessel?> = _selectedVessel.asStateFlow()

    private val _showAddLogDialog = MutableStateFlow(false)
    val showAddLogDialog: StateFlow<Boolean> = _showAddLogDialog.asStateFlow()

    private val _showDispatchDialog = MutableStateFlow(false)
    val showDispatchDialog: StateFlow<Boolean> = _showDispatchDialog.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = OceanRepository(database.vesselDao(), database.expeditionDao())

        vesselsState = repository.allVessels.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        expeditionLogsState = repository.allExpeditionLogs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        weatherState = repository.currentWeather.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MarineWeather(
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

        commandHistoryState = repository.commandHistory.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.initializeDefaultDataIfNeeded()
        }
    }

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun selectVessel(vessel: Vessel?) {
        _selectedVessel.value = vessel
    }

    fun setShowAddLogDialog(show: Boolean) {
        _showAddLogDialog.value = show
    }

    fun setShowDispatchDialog(show: Boolean) {
        _showDispatchDialog.value = show
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun updateVesselStatus(vesselId: String, newStatus: VesselStatus) {
        viewModelScope.launch {
            repository.updateVesselStatus(vesselId, newStatus)
            _userMessage.value = "Vessel $vesselId status updated to ${newStatus.name.replace("_", " ")}"
        }
    }

    fun updateVesselCourse(vesselId: String, heading: Int, speedKnots: Double) {
        viewModelScope.launch {
            repository.updateVesselCourse(vesselId, heading, speedKnots)
            _userMessage.value = "Vessel $vesselId heading updated to $heading° at $speedKnots kts"
        }
    }

    fun addExpeditionLog(
        title: String,
        vesselName: String,
        region: String,
        expeditionType: String,
        latitude: Double,
        longitude: Double,
        notes: String,
        waterDepthMeters: Double
    ) {
        viewModelScope.launch {
            val log = ExpeditionLog(
                title = title,
                vesselName = vesselName,
                region = region,
                expeditionType = expeditionType,
                latitude = latitude,
                longitude = longitude,
                notes = notes,
                waterDepthMeters = waterDepthMeters,
                timestamp = System.currentTimeMillis()
            )
            repository.addExpeditionLog(log)
            _showAddLogDialog.value = false
            _userMessage.value = "Expedition log '$title' recorded into database."
        }
    }

    fun deleteExpeditionLog(id: Int) {
        viewModelScope.launch {
            repository.deleteExpeditionLog(id)
            _userMessage.value = "Expedition log entry removed."
        }
    }

    fun dispatchCommand(vessel: Vessel, commandType: CommandType, notes: String) {
        viewModelScope.launch {
            val desc = when (commandType) {
                CommandType.SONAR_SWEEP -> "Initiated multi-beam acoustic sonar sweep down to ${vessel.depthMeters}m."
                CommandType.COURSE_UPDATE -> "Course correction signal transmitted. $notes"
                CommandType.EMERGENCY_STATION -> "Emergency stations signal sounded. Vessel status set to ALERT."
                CommandType.RENDEZVOUS_PING -> "Rendezvous telemetry ping transmitted to ${vessel.callSign}."
                CommandType.SAMPLE_COLLECT -> "Deep sea grab sampler deployment signal dispatched."
                CommandType.HOLD_POSITION -> "Dynamic positioning active. Vessel holding current coordinates."
            }

            val cmd = DispatchedCommand(
                id = "CMD-" + UUID.randomUUID().toString().take(6).uppercase(),
                vesselId = vessel.id,
                vesselName = vessel.name,
                commandType = commandType,
                description = desc,
                timestamp = System.currentTimeMillis()
            )
            repository.dispatchCommand(cmd)

            if (commandType == CommandType.EMERGENCY_STATION) {
                repository.updateVesselStatus(vessel.id, VesselStatus.ALERT)
            }

            _showDispatchDialog.value = false
            _userMessage.value = "Command dispatched to ${vessel.name}: ${commandType.name}"
        }
    }

    fun triggerEmergencyMaydayBroadcast() {
        viewModelScope.launch {
            val vessels = vesselsState.value
            for (v in vessels) {
                repository.updateVesselStatus(v.id, VesselStatus.ALERT)
            }
            val cmd = DispatchedCommand(
                id = "MAYDAY-" + UUID.randomUUID().toString().take(4).uppercase(),
                vesselId = "ALL",
                vesselName = "Fleet Broadcast",
                commandType = CommandType.EMERGENCY_STATION,
                description = "CRITICAL FLEET-WIDE MAYDAY BROADCAST: All vessels adopt emergency station posture.",
                timestamp = System.currentTimeMillis()
            )
            repository.dispatchCommand(cmd)
            _userMessage.value = "CRITICAL MAYDAY SIGNAL TRANSMITTED TO ALL FLEET VESSELS!"
        }
    }
}
