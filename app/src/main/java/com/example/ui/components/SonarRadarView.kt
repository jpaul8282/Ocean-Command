package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Vessel
import com.example.model.VesselStatus
import com.example.ui.theme.OceanAlertRed
import com.example.ui.theme.OceanPrimary
import com.example.ui.theme.OceanSecondary
import com.example.ui.theme.OceanSuccessGreen
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun SonarRadarView(
    vessels: List<Vessel>,
    selectedVessel: Vessel?,
    onSelectVessel: (Vessel) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SweepAngle"
    )

    val pulseRadiusFraction by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Pulse"
    )

    val textMeasurer = rememberTextMeasurer()
    val gridColor = OceanPrimary.copy(alpha = 0.3f)
    val sweepColor = OceanPrimary.copy(alpha = 0.4f)
    val pulseColor = OceanSecondary.copy(alpha = 0.2f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF040E1B))
            .testTag("sonar_radar_canvas_container"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("sonar_radar_canvas")
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = min(size.width, size.height) / 2f * 0.9f

            // Concentric Radar Rings
            val ringCount = 4
            for (i in 1..ringCount) {
                val r = radius * (i.toFloat() / ringCount)
                drawCircle(
                    color = gridColor,
                    radius = r,
                    center = center,
                    style = Stroke(
                        width = 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )
                )
            }

            // Crosshair Axes
            drawLine(
                color = gridColor,
                start = Offset(center.x - radius, center.y),
                end = Offset(center.x + radius, center.y),
                strokeWidth = 1.5f
            )
            drawLine(
                color = gridColor,
                start = Offset(center.x, center.y - radius),
                end = Offset(center.x, center.y + radius),
                strokeWidth = 1.5f
            )

            // Dynamic Pulse Ring
            drawCircle(
                color = pulseColor,
                radius = radius * pulseRadiusFraction,
                center = center,
                style = Stroke(width = 2f)
            )

            // Compass Markers (N, E, S, W)
            val textStyle = TextStyle(
                color = OceanPrimary.copy(alpha = 0.8f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            val nLayout = textMeasurer.measure("N 0°", textStyle)
            drawText(nLayout, topLeft = Offset(center.x - nLayout.size.width / 2f, center.y - radius - 18f))

            val sLayout = textMeasurer.measure("S 180°", textStyle)
            drawText(sLayout, topLeft = Offset(center.x - sLayout.size.width / 2f, center.y + radius + 4f))

            val eLayout = textMeasurer.measure("E 90°", textStyle)
            drawText(eLayout, topLeft = Offset(center.x + radius + 4f, center.y - eLayout.size.height / 2f))

            val wLayout = textMeasurer.measure("W 270°", textStyle)
            drawText(wLayout, topLeft = Offset(center.x - radius - wLayout.size.width - 4f, center.y - wLayout.size.height / 2f))

            // Rotating Radar Sweep Line
            val sweepRad = Math.toRadians(sweepAngle.toDouble() - 90.0)
            val sweepEnd = Offset(
                x = center.x + (radius * cos(sweepRad)).toFloat(),
                y = center.y + (radius * sin(sweepRad)).toFloat()
            )
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(OceanSecondary, sweepColor, Color.Transparent),
                    start = center,
                    end = sweepEnd
                ),
                start = center,
                end = sweepEnd,
                strokeWidth = 3f
            )

            // Draw Vessels on Radar
            vessels.forEachIndexed { index, vessel ->
                // Map Lat/Long relative offsets to radar coordinates
                val relLat = (vessel.latitude - 13.5) * 20.0
                val relLon = (vessel.longitude - 145.0) * 15.0

                val normX = (relLon.coerceIn(-1.0, 1.0)).toFloat()
                val normY = (-relLat.coerceIn(-1.0, 1.0)).toFloat()

                val blipX = center.x + (normX * radius * 0.75f)
                val blipY = center.y + (normY * radius * 0.75f)

                val blipColor = when (vessel.status) {
                    VesselStatus.ALERT -> OceanAlertRed
                    VesselStatus.SURVEYING -> OceanPrimary
                    VesselStatus.IN_TRANSIT -> OceanSecondary
                    VesselStatus.ANCHORED, VesselStatus.DOCKED -> OceanSuccessGreen
                }

                val isSelected = selectedVessel?.id == vessel.id

                if (isSelected) {
                    drawCircle(
                        color = blipColor.copy(alpha = 0.4f),
                        radius = 16f,
                        center = Offset(blipX, blipY)
                    )
                }

                drawCircle(
                    color = blipColor,
                    radius = if (isSelected) 8f else 6f,
                    center = Offset(blipX, blipY)
                )

                // Vessel Label
                val tagStyle = TextStyle(
                    color = if (isSelected) Color.White else blipColor,
                    fontSize = 9.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold
                )
                val labelLayout = textMeasurer.measure(vessel.callSign, tagStyle)
                drawText(
                    labelLayout,
                    topLeft = Offset(blipX + 10f, blipY - labelLayout.size.height / 2f)
                )
            }
        }

        // Radar Overlay Header Text
        Text(
            text = "LIVE SONAR RADAR • RANGE: 50 NM",
            color = OceanPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        )
    }
}
