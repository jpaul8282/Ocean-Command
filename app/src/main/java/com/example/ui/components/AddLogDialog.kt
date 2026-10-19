package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Vessel
import com.example.ui.theme.OceanOutlineDark
import com.example.ui.theme.OceanPrimary
import com.example.ui.theme.OceanSurfaceDark

@Composable
fun AddLogDialog(
    vessels: List<Vessel>,
    onDismiss: () -> Unit,
    onSubmit: (
        title: String,
        vesselName: String,
        region: String,
        expeditionType: String,
        latitude: Double,
        longitude: Double,
        notes: String,
        waterDepthMeters: Double
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedVesselName by remember { mutableStateOf(vessels.firstOrNull()?.name ?: "R/V Nautilus IX") }
    var region by remember { mutableStateOf("Mariana Sector North") }
    var expeditionType by remember { mutableStateOf("Oceanographic Water Sampling") }
    var latitudeText by remember { mutableStateOf("13.4443") }
    var longitudeText by remember { mutableStateOf("144.7937") }
    var depthText by remember { mutableStateOf("4250.0") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = OceanSurfaceDark,
        title = {
            Text(
                text = "LOG NEW EXPEDITION RECORD",
                fontWeight = FontWeight.Bold,
                color = OceanPrimary,
                fontSize = 16.sp,
                modifier = Modifier.testTag("dialog_add_log_title")
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Expedition Title") },
                    colors = textFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_log_title")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = selectedVesselName,
                    onValueChange = { selectedVesselName = it },
                    label = { Text("Vessel Name") },
                    colors = textFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_log_vessel")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = region,
                    onValueChange = { region = it },
                    label = { Text("Ocean Region / Sector") },
                    colors = textFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_log_region")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = expeditionType,
                    onValueChange = { expeditionType = it },
                    label = { Text("Expedition Mission Type") },
                    colors = textFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_log_type")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = depthText,
                    onValueChange = { depthText = it },
                    label = { Text("Bathymetric Depth (Meters)") },
                    colors = textFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_log_depth")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Research Notes & Observations") },
                    minLines = 3,
                    colors = textFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_log_notes")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val lat = latitudeText.toDoubleOrNull() ?: 13.0
                    val lon = longitudeText.toDoubleOrNull() ?: 144.0
                    val depth = depthText.toDoubleOrNull() ?: 2000.0
                    if (title.isNotBlank()) {
                        onSubmit(title, selectedVesselName, region, expeditionType, lat, lon, notes, depth)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = OceanPrimary, contentColor = Color(0xFF00363A)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_submit_log")
            ) {
                Text(text = "Save Record", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_cancel_log")
            ) {
                Text(text = "Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = OceanPrimary,
    unfocusedBorderColor = OceanOutlineDark,
    focusedLabelColor = OceanPrimary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
)
