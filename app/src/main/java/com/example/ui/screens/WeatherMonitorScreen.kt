package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WaterDamage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MarineWeather
import com.example.ui.components.AlertBanner
import com.example.ui.components.WeatherMonitorSection
import com.example.ui.theme.OceanOutlineDark
import com.example.ui.theme.OceanPrimary
import com.example.ui.theme.OceanSecondary
import com.example.ui.theme.OceanSurfaceDark

@Composable
fun WeatherMonitorScreen(
    weather: MarineWeather,
    onMaydayTrigger: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("weather_monitor_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            if (weather.stormWarningActive && weather.stormAlertMessage != null) {
                AlertBanner(
                    message = weather.stormAlertMessage,
                    onMaydayTrigger = onMaydayTrigger
                )
            }
        }

        item {
            WeatherMonitorSection(weather = weather)
        }

        item {
            // Oceanic Sea Condition Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OceanOutlineDark, RoundedCornerShape(14.dp))
                    .testTag("sea_state_card"),
                colors = CardDefaults.cardColors(containerColor = OceanSurfaceDark),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(OceanSecondary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDamage,
                                contentDescription = null,
                                tint = OceanSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "SEA STATE & CURRENT DYNAMICS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OceanSecondary
                            )
                            Text(
                                text = "Douglas Sea Scale: State 4 (Moderate)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Surface current flowing West-Southwest at 1.4 knots. Thermocline boundary detected at 120m depth with a -4.2°C temperature drop. Acoustic propagation speeds calculated at 1,522 m/s.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
