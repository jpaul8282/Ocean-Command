package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Water
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Vessel
import com.example.ui.components.AddLogDialog
import com.example.ui.components.DispatchDialog
import com.example.ui.screens.CommandDispatchScreen
import com.example.ui.screens.FleetOverviewScreen
import com.example.ui.screens.ExpeditionLogsScreen
import com.example.ui.screens.WeatherMonitorScreen
import com.example.ui.theme.OceanAlertRed
import com.example.ui.theme.OceanBackgroundDark
import com.example.ui.theme.OceanPrimary
import com.example.ui.theme.OceanSecondary
import com.example.ui.theme.OceanSurfaceDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OceanCommandApp(
    viewModel: OceanViewModel
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val vessels by viewModel.vesselsState.collectAsStateWithLifecycle()
    val selectedVessel by viewModel.selectedVessel.collectAsStateWithLifecycle()
    val expeditionLogs by viewModel.expeditionLogsState.collectAsStateWithLifecycle()
    val weather by viewModel.weatherState.collectAsStateWithLifecycle()
    val commandHistory by viewModel.commandHistoryState.collectAsStateWithLifecycle()
    val showAddLogDialog by viewModel.showAddLogDialog.collectAsStateWithLifecycle()
    val showDispatchDialog by viewModel.showDispatchDialog.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 600

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("ocean_command_root"),
        containerColor = OceanBackgroundDark,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OceanSurfaceDark,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(OceanSecondary)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "OCEAN COMMAND CENTER",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = OceanPrimary
                            )
                            Text(
                                text = "FLEET TELEMETRY • MARIANA SECTOR",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    if (weather.stormWarningActive) {
                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(OceanAlertRed.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "STORM WARN",
                                color = OceanAlertRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (!isWideScreen) {
                NavigationBar(
                    containerColor = OceanSurfaceDark,
                    contentColor = OceanPrimary,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavItemsList().forEach { (index, title, icon, tag) ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { viewModel.selectTab(index) },
                            icon = { Icon(imageVector = icon, contentDescription = title) },
                            label = { Text(text = title, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = OceanPrimary,
                                selectedTextColor = OceanPrimary,
                                indicatorColor = OceanPrimary.copy(alpha = 0.2f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag(tag)
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Adaptive Rail for Wide Screens / Tablets
            if (isWideScreen) {
                NavigationRail(
                    containerColor = OceanSurfaceDark,
                    header = {
                        Box(
                            modifier = Modifier
                                .padding(12.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(OceanPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.DirectionsBoat, contentDescription = null, tint = OceanPrimary)
                        }
                    },
                    modifier = Modifier.testTag("navigation_rail")
                ) {
                    NavItemsList().forEach { (index, title, icon, tag) ->
                        NavigationRailItem(
                            selected = selectedTab == index,
                            onClick = { viewModel.selectTab(index) },
                            icon = { Icon(imageVector = icon, contentDescription = title) },
                            label = { Text(text = title, fontSize = 10.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = OceanPrimary,
                                selectedTextColor = OceanPrimary,
                                indicatorColor = OceanPrimary.copy(alpha = 0.2f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag(tag)
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> FleetOverviewScreen(
                        vessels = vessels,
                        selectedVessel = selectedVessel,
                        weather = weather,
                        onSelectVessel = { viewModel.selectVessel(it) },
                        onDispatchCommand = { vessel ->
                            viewModel.selectVessel(vessel)
                            viewModel.setShowDispatchDialog(true)
                        },
                        onStatusChange = { vessel, newStatus ->
                            viewModel.updateVesselStatus(vessel.id, newStatus)
                        },
                        onMaydayTrigger = { viewModel.triggerEmergencyMaydayBroadcast() }
                    )

                    1 -> WeatherMonitorScreen(
                        weather = weather,
                        onMaydayTrigger = { viewModel.triggerEmergencyMaydayBroadcast() }
                    )

                    2 -> ExpeditionLogsScreen(
                        logs = expeditionLogs,
                        onAddLogClick = { viewModel.setShowAddLogDialog(true) },
                        onDeleteLog = { id -> viewModel.deleteExpeditionLog(id) }
                    )

                    3 -> CommandDispatchScreen(
                        commandHistory = commandHistory,
                        onTriggerMayday = { viewModel.triggerEmergencyMaydayBroadcast() }
                    )
                }
            }
        }
    }

    // Modal Dialogs
    if (showAddLogDialog) {
        AddLogDialog(
            vessels = vessels,
            onDismiss = { viewModel.setShowAddLogDialog(false) },
            onSubmit = { title, vesselName, region, type, lat, lon, notes, depth ->
                viewModel.addExpeditionLog(title, vesselName, region, type, lat, lon, notes, depth)
            }
        )
    }

    if (showDispatchDialog && selectedVessel != null) {
        DispatchDialog(
            vessel = selectedVessel!!,
            onDismiss = { viewModel.setShowDispatchDialog(false) },
            onDispatch = { cmdType, notes ->
                viewModel.dispatchCommand(selectedVessel!!, cmdType, notes)
            }
        )
    }
}

private data class NavItemData(
    val index: Int,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
)

private fun NavItemsList() = listOf(
    NavItemData(0, "Fleet & Radar", Icons.Default.Radar, "nav_tab_fleet"),
    NavItemData(1, "Weather", Icons.Default.Water, "nav_tab_weather"),
    NavItemData(2, "Expeditions", Icons.Default.Book, "nav_tab_expeditions"),
    NavItemData(3, "Dispatch", Icons.Default.Radio, "nav_tab_dispatch")
)
