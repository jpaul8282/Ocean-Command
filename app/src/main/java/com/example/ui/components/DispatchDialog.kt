package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CommandType
import com.example.model.Vessel
import com.example.ui.theme.OceanAlertRed
import com.example.ui.theme.OceanOutlineDark
import com.example.ui.theme.OceanPrimary
import com.example.ui.theme.OceanSecondary
import com.example.ui.theme.OceanSurfaceDark

@Composable
fun DispatchDialog(
    vessel: Vessel,
    onDismiss: () -> Unit,
    onDispatch: (CommandType, String) -> Unit
) {
    var selectedCommandType by remember { mutableStateOf(CommandType.SONAR_SWEEP) }
    var extraNotes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = OceanSurfaceDark,
        title = {
            Column {
                Text(
                    text = "COMMAND TRANSMITTER",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OceanSecondary
                )
                Text(
                    text = "TARGET: ${vessel.name} (${vessel.callSign})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("dialog_dispatch_title")
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "SELECT COMMAND SIGNAL:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                CommandOptionTile(
                    type = CommandType.SONAR_SWEEP,
                    title = "Sonar Bathymetric Sweep",
                    desc = "Execute high-frequency multi-beam acoustic sweep",
                    icon = Icons.Default.Radar,
                    isSelected = selectedCommandType == CommandType.SONAR_SWEEP,
                    onSelect = { selectedCommandType = CommandType.SONAR_SWEEP }
                )
                Spacer(modifier = Modifier.height(6.dp))

                CommandOptionTile(
                    type = CommandType.COURSE_UPDATE,
                    title = "Course & Vector Correction",
                    desc = "Transmit new heading & speed waypoints",
                    icon = Icons.Default.Navigation,
                    isSelected = selectedCommandType == CommandType.COURSE_UPDATE,
                    onSelect = { selectedCommandType = CommandType.COURSE_UPDATE }
                )
                Spacer(modifier = Modifier.height(6.dp))

                CommandOptionTile(
                    type = CommandType.SAMPLE_COLLECT,
                    title = "Deploy Grab Sampler",
                    desc = "Deploy automated deep sea sediment sampler",
                    icon = Icons.Default.Science,
                    isSelected = selectedCommandType == CommandType.SAMPLE_COLLECT,
                    onSelect = { selectedCommandType = CommandType.SAMPLE_COLLECT }
                )
                Spacer(modifier = Modifier.height(6.dp))

                CommandOptionTile(
                    type = CommandType.EMERGENCY_STATION,
                    title = "General Emergency Stations",
                    desc = "Sound general emergency stations and sound alarms",
                    icon = Icons.Default.Warning,
                    isSelected = selectedCommandType == CommandType.EMERGENCY_STATION,
                    onSelect = { selectedCommandType = CommandType.EMERGENCY_STATION },
                    isDanger = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = extraNotes,
                    onValueChange = { extraNotes = it },
                    label = { Text("Transmission Remarks / Waypoints") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OceanPrimary,
                        unfocusedBorderColor = OceanOutlineDark,
                        focusedLabelColor = OceanPrimary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_dispatch_notes")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onDispatch(selectedCommandType, extraNotes) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedCommandType == CommandType.EMERGENCY_STATION) OceanAlertRed else OceanPrimary,
                    contentColor = if (selectedCommandType == CommandType.EMERGENCY_STATION) Color.White else Color(0xFF00363A)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_transmit_command")
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Transmit Signal", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_cancel_dispatch")
            ) {
                Text(text = "Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
private fun CommandOptionTile(
    type: CommandType,
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit,
    isDanger: Boolean = false
) {
    val activeColor = if (isDanger) OceanAlertRed else OceanPrimary
    val borderColor = if (isSelected) activeColor else OceanOutlineDark

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) activeColor.copy(alpha = 0.12f) else Color.Transparent)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onSelect() }
            .padding(10.dp)
            .testTag("command_opt_${type.name}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(activeColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = activeColor, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = desc,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
