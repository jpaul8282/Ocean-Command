package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.MarineWeather
import com.example.model.Vessel
import com.example.model.VesselStatus
import com.example.ui.components.AlertBanner
import com.example.ui.components.SonarRadarView
import com.example.ui.components.VesselCard
import com.example.ui.theme.OceanSuccessGreen

@Composable
fun FleetOverviewScreen(
    vessels: List<Vessel>,
    selectedVessel: Vessel?,
    weather: MarineWeather,
    onSelectVessel: (Vessel) -> Unit,
    onDispatchCommand: (Vessel) -> Unit,
    onStatusChange: (Vessel, VesselStatus) -> Unit,
    onMaydayTrigger: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("fleet_overview_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Professional Polish Fleet Status Featured Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(20.dp)
                    .testTag("fleet_status_hero_card")
            ) {
                Column {
                    Text(
                        text = "FLEET OPERATIONAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Steady Seas • Mariana Sector",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(OceanSuccessGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${vessels.size} Active Fleet Vessels",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        item {
            // Hero Graphic Image Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .testTag("hero_banner_container")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_ocean_hero),
                    contentDescription = "Ocean Command Hero Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Active Storm Warning Alert Banner
        if (weather.stormWarningActive && weather.stormAlertMessage != null) {
            item {
                AlertBanner(
                    message = weather.stormAlertMessage,
                    onMaydayTrigger = onMaydayTrigger
                )
            }
        }

        // Live Sonar Radar Canvas
        item {
            SonarRadarView(
                vessels = vessels,
                selectedVessel = selectedVessel,
                onSelectVessel = onSelectVessel
            )
        }

        // Active Vessels Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TRACKED FLEET VESSELS (${vessels.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "STATUS: ONLINE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // List of Vessel Cards
        items(vessels, key = { it.id }) { vessel ->
            VesselCard(
                vessel = vessel,
                isSelected = selectedVessel?.id == vessel.id,
                onSelect = { onSelectVessel(vessel) },
                onDispatchCommand = { onDispatchCommand(vessel) },
                onStatusChange = { newStatus -> onStatusChange(vessel, newStatus) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
