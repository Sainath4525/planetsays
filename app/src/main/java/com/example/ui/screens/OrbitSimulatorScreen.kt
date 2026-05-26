package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.data.CelestialDatabase
import com.example.data.Planet
import com.example.viewmodel.SpaceViewModel
import java.text.DecimalFormat
import kotlin.math.*

@Composable
fun OrbitSimulatorScreen(
    viewModel: SpaceViewModel,
    modifier: Modifier = Modifier
) {
    val simulatedDays by viewModel.simulatedEarthDays.collectAsState()
    val isSimulating by viewModel.isSimulatingOrbits.collectAsState()
    val speedMultiplier by viewModel.orbitSpeedMultiplier.collectAsState()
    val selectedPlanet by viewModel.selectedPlanet.collectAsState()

    val density = LocalDensity.current
    var canvasSize by remember { mutableStateOf(Offset.Zero) }

    // Coordinates cache for selecting planets via canvas clicks
    val planetCoordinates = remember { mutableMapOf<String, Offset>() }

    // Convert simulated days to a readable years + days string
    val years = (simulatedDays / 365.25).toInt()
    val days = (simulatedDays % 365.25).toInt()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F1524).copy(alpha = 0.85f),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Orreries Orbit Simulator",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF38BDF8)
                    )
                    Text(
                        text = "Simulated Time: ${years}y ${days}d",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
                IconButton(
                    onClick = { viewModel.toggleOrbitSimulation() },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (isSimulating) Color(0xFF0369A1) else Color(0xFF334155)
                    )
                ) {
                    Icon(
                        imageVector = if (isSimulating) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause Orbits"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // The Concentric Solar System interactive Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF070B14))
                    .pointerInput(canvasSize) {
                        // Detect taps near specific planet coordinates
                        detectTapGestures { tapOffset ->
                            var selected: Planet? = null
                            var minDistance = Float.MAX_VALUE
                            val tapThresholdPx = with(density) { 32.dp.toPx() } // Click tolerance size

                            for ((pid, coord) in planetCoordinates) {
                                val dist = sqrt((tapOffset.x - coord.x).pow(2) + (tapOffset.y - coord.y).pow(2))
                                if (dist < tapThresholdPx && dist < minDistance) {
                                    minDistance = dist
                                    selected = CelestialDatabase.Planets.find { it.id == pid }
                                }
                            }
                            selected?.let {
                                viewModel.selectPlanet(it)
                            }
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    canvasSize = Offset(width, height)
                    val center = Offset(width / 2f, height / 2f)

                    // Draw deep space starry texture backdrop
                    for (i in 0..12) {
                        val starX = (sin(i * 142.1) + 1.0) / 2.0 * width
                        val starY = (cos(i * 94.2) + 1.0) / 2.0 * height
                        drawCircle(
                            color = Color.White.copy(alpha = 0.25f),
                            radius = 2f,
                            center = Offset(starX.toFloat(), starY.toFloat())
                        )
                    }

                    val maxRadiusBoundary = (width.coerceAtMost(height) / 2f) - 30f
                    // Spacing orbits evenly to guarantee high visibility
                    val orbitSpacing = (maxRadiusBoundary - 25f) / CelestialDatabase.Planets.size

                    // 1. Draw central Sun
                    val sunBrush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFDE047), Color(0xFFEA580C), Color.Transparent),
                        center = center,
                        radius = 28f
                    )
                    drawCircle(brush = sunBrush, radius = 28f, center = center)
                    drawCircle(color = Color(0xFFFEF08A), radius = 10f, center = center)

                    // 2. Loop through orbits
                    CelestialDatabase.Planets.forEachIndexed { index, planet ->
                        // Calculate radius of this planet's concentric orbit loop
                        val orbitRadius = 35f + (index * orbitSpacing)

                        // Draw orbital guide path line
                        drawCircle(
                            color = if (selectedPlanet.id == planet.id) Color(0xFF38BDF8).copy(alpha = 0.35f) else Color(0xFF334155).copy(alpha = 0.3f),
                            radius = orbitRadius,
                            center = center,
                            style = Stroke(width = if (selectedPlanet.id == planet.id) 2f else 1f)
                        )

                        // Calculate current orbital angle based on elapsed simulated design days
                        val period = planet.orbitData.orbitalPeriodDays
                        val currentAngleDegrees = (simulatedDays / period) * 360.0
                        val angleRad = Math.toRadians(currentAngleDegrees % 360.0)

                        // Cartesian conversions
                        val planetX = center.x + cos(angleRad).toFloat() * orbitRadius
                        val planetY = center.y + sin(angleRad).toFloat() * orbitRadius
                        val planetPos = Offset(planetX, planetY)

                        // Cache coordinate for interactivity tapping
                        planetCoordinates[planet.id] = planetPos

                        // Highlight selected planet orbit path
                        if (selectedPlanet.id == planet.id) {
                            drawCircle(
                                color = Color(0xFF38BDF8).copy(alpha = 0.15f),
                                radius = 16f,
                                center = planetPos
                            )
                        }

                        // Draw the celestial body representation
                        drawCircle(
                            color = Color(android.graphics.Color.parseColor(planet.colorHex)),
                            radius = max(5f, planet.radiusKm.toFloat() / 15000f + 2.5f), // Relative scaling that is visible
                            center = planetPos
                        )
                    }
                }

                // Bottom banner hint
                Text(
                    text = "Tap on any planet's orbit to explore!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray.copy(alpha = 0.8f),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Control sliders
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val df = DecimalFormat("#.##")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Speed,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Orbits progression speed multiplier",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                    }
                    Text(
                        text = "${df.format(speedMultiplier)} Days/Sec",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF38BDF8)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Slider(
                    value = speedMultiplier,
                    onValueChange = { viewModel.setSpeedMultiplier(it) },
                    valueRange = 0.1f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF0284C7),
                        activeTrackColor = Color(0xFF0284C7),
                        inactiveTrackColor = Color(0xFF1E293B)
                    )
                )

                // Quick buttons for selected planet
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AssistChip(
                        onClick = { viewModel.setSpeedMultiplier(1f) },
                        label = { Text("1x Speed") },
                        colors = AssistChipDefaults.assistChipColors(labelColor = Color.White)
                    )
                    AssistChip(
                        onClick = { viewModel.setSpeedMultiplier(10f) },
                        label = { Text("10x Sync") },
                        colors = AssistChipDefaults.assistChipColors(labelColor = Color.White)
                    )
                    AssistChip(
                        onClick = { viewModel.setSpeedMultiplier(50f) },
                        label = { Text("50x Hyper") },
                        colors = AssistChipDefaults.assistChipColors(labelColor = Color.White)
                    )
                }
            }
        }
    }
}
