package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.Planet3DView
import com.example.viewmodel.SpaceViewModel
import com.example.viewmodel.LiveMetricsPayload
import java.text.DecimalFormat
import kotlin.random.Random

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainSpaceScreen(
    viewModel: SpaceViewModel,
    modifier: Modifier = Modifier
) {
    val currentMode by viewModel.explorationMode.collectAsState()
    val selectedPlanet by viewModel.selectedPlanet.collectAsState()
    val apiStates by viewModel.apiSyncStates.collectAsState()
    val apiMetrics by viewModel.livePlanetMetrics.collectAsState()

    // Confetti and Rocket systems
    val confettiTrigger by viewModel.confettiTrigger.collectAsState()
    val rocketDestination by viewModel.activeRocketDestination.collectAsState()
    val rocketProgress by viewModel.rocketFlightProgress.collectAsState()

    var showOrrerySimulator by remember { mutableStateOf(false) }

    // Floating dynamic starry background
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF060913),
                        Color(0xFF0F1524),
                        Color(0xFF05070D)
                    )
                )
            )
    ) {
        // Falling meteor / parallax ambient background details
        MeteorAnimationBg()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Screen Title & Mode selection controls
            SpaceTopNavigationBar(
                activeMode = currentMode,
                onModeToggle = { viewModel.setExplorationMode(it) },
                isSimulatorActive = showOrrerySimulator,
                onToggleSimulator = { showOrrerySimulator = !showOrrerySimulator }
            )

            if (showOrrerySimulator) {
                // Rentention of simulator view as a high-fidelity visual sub-module
                OrbitSimulatorScreen(
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f)
                )
            } else {
                // Interactive core planetary workspace
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // 1. Horizontal Planet carousel selector
                    PlanetScrollerRow(
                        selectedId = selectedPlanet.id,
                        onPlanetSelected = { viewModel.selectPlanet(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Beautiful 3D Interactive Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(290.dp)
                            .shadow(24.dp, RoundedCornerShape(24.dp))
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF1E293B).copy(alpha = 0.5f),
                                        Color.Transparent
                                    ),
                                    radius = 450f
                                ),
                                shape = RoundedCornerShape(24.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Spinning spherical projection
                        Planet3DView(
                            planet = selectedPlanet,
                            spinSpeedMultiplier = if (currentMode == "KIDS") 1.8f else 0.8f,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Kids visual overlay overlays
                        if (currentMode == "KIDS") {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(16.dp)
                                    .background(Color(0xFFF97316), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = selectedPlanet.kidsProfile.nickname,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }

                            // Speech bubble of characters
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 12.dp)
                                    .background(Color.White, RoundedCornerShape(16.dp))
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "\"${selectedPlanet.kidsProfile.cartoonQuote}\" 💬",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF1E293B),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            // Adults tech overlay
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                                    .background(Color(0xFF0F172A).copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val sync = apiStates[selectedPlanet.id] ?: ApiSyncState.Latent
                                val statusCol = when (sync) {
                                    is ApiSyncState.Synced -> Color(0xFF10B981)
                                    is ApiSyncState.Syncing -> Color(0xFF38BDF8)
                                    else -> Color(0xFFF59E0B)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(statusCol)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when (sync) {
                                        is ApiSyncState.Synced -> "API Online Synced"
                                        is ApiSyncState.Syncing -> "Syncing space API..."
                                        else -> "Using Cached Data"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.LightGray
                                )
                            }

                            // Chemical formulas monospace display tag
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp)
                                    .background(Color(0xFF3B82F6).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFF3B82F6), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Spectroscopy: ${selectedPlanet.atmosphereProperties.mainComponents.joinToString(", ") { it.formula }}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                    color = Color(0xFF93C5FD)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. Adaptive Dashboards depending on active settings
                    AnimatedContent(
                        targetState = currentMode,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(220)) with fadeOut(animationSpec = tween(220))
                        },
                        label = "mode_switch_content"
                    ) { mode ->
                        if (mode == "KIDS") {
                            KidsDashboard(
                                planet = selectedPlanet,
                                viewModel = viewModel,
                                activeRocket = rocketDestination,
                                rocketProgress = rocketProgress
                            )
                        } else {
                            AdultsDashboard(
                                planet = selectedPlanet,
                                apiMetric = apiMetrics[selectedPlanet.id],
                                apiState = apiStates[selectedPlanet.id] ?: ApiSyncState.Latent
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Active Full-Screen Confetti particles overlay
        if (confettiTrigger > 0) {
            ConfettiOverlay(triggerId = confettiTrigger)
        }
    }
}

@Composable
fun SpaceTopNavigationBar(
    activeMode: String,
    onModeToggle: (String) -> Unit,
    isSimulatorActive: Boolean,
    onToggleSimulator: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Cosmic Odyssey",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                ),
                color = Color.White
            )
            Text(
                text = "Atmospheric 3D Spectrometers",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Interactive time tracer orbits toggle
            IconButton(
                onClick = onToggleSimulator,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (isSimulatorActive) Color(0xFF0369A1) else Color(0xFF1E293B)
                ),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(40.dp)
                    .testTag("simulator_toggle_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Language, // Orrery sphere logo mapping
                    contentDescription = "Toggle Orbits Orrery",
                    tint = Color.White
                )
            }

            // Mode Toggle segmented button
            Row(
                modifier = Modifier
                    .background(Color(0xFF1E293B), RoundedCornerShape(20.dp))
                    .padding(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (activeMode == "KIDS") Color(0xFFF97316) else Color.Transparent)
                        .clickable { onModeToggle("KIDS") }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Face,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Kids", style = MaterialTheme.typography.labelSmall, color = Color.White)
                }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (activeMode == "ADULTS") Color(0xFF2563EB) else Color.Transparent)
                        .clickable { onModeToggle("ADULTS") }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Insights,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Adults", style = MaterialTheme.typography.labelSmall, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun PlanetScrollerRow(
    selectedId: String,
    onPlanetSelected: (Planet) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().testTag("planet_carousel_row"),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(CelestialDatabase.Planets) { planet ->
            val isSelected = planet.id == selectedId
            val col = Color(android.graphics.Color.parseColor(planet.colorHex))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) col.copy(alpha = 0.25f) else Color(0xFF0F172A).copy(alpha = 0.7f))
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) col else Color(0xFF334155),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onPlanetSelected(planet) }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("planet_chip_${planet.id}")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(col)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = planet.name,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) Color.White else Color.Gray
                    )
                }
            }
        }
    }
}

// Adults deep scientific statistics dashboard
@Composable
fun AdultsDashboard(
    planet: Planet,
    apiMetric: LiveMetricsPayload?,
    apiState: ApiSyncState
) {
    val df = DecimalFormat("#.###")
    val dfPressure = DecimalFormat("#.####")

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Spectroscopy section
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.8f)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Atmospheric Spectroscopy Configuration",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF38BDF8),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = planet.atmosphereProperties.densityDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(12.dp))

                planet.atmosphereProperties.mainComponents.forEach { component ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .background(Color(0xFF1E293B), RoundedCornerShape(4.dp))
                                .padding(vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = component.formula,
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = component.name,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1.0f),
                            color = Color.LightGray
                        )
                        Text(
                            text = "${component.percentage}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    LinearProgressIndicator(
                        progress = { (component.percentage / 100f).toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color(0xFF2563EB),
                        trackColor = Color(0xFF1E293B)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ambient Pressure Rating", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(
                        text = "${dfPressure.format(apiMetric?.gravityScore ?: planet.atmosphereProperties.atmosphericPressureBar)} bar",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Thermal Range Index", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(
                        text = planet.atmosphereProperties.temperatureProfile,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(start = 24.dp)
                    )
                }
            }
        }

        // Keplerian orbit specs
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.8f)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Orbit & Physical Analytical Data",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF38BDF8),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Orbital Period", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text("${planet.orbitData.orbitalPeriodDays} Days", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Semi-Major Axis", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        val sa = apiMetric?.eccentricity?.let { e ->
                            val scale = planet.orbitData.semiMajorAxisAU
                            df.format(scale)
                        } ?: planet.orbitData.semiMajorAxisAU.toString()
                        Text("$sa AU", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    }
                }

                Divider(color = Color(0xFF1E293B), modifier = Modifier.padding(vertical = 10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Orbital Eccentricity", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text(df.format(apiMetric?.eccentricity ?: planet.orbitData.eccentricity), style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Axial Obliquity Tilt", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text("${apiMetric?.axialTiltDegrees ?: planet.orbitData.axialTiltDegrees}°", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    }
                }

                Divider(color = Color(0xFF1E293B), modifier = Modifier.padding(vertical = 10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mean Velocity", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text("${planet.orbitData.meanVelocityKms} km/s", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Local Gravity (9.8g)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text("${apiMetric?.gravityScore ?: (planet.adultsProfile.escapeVelocityKms * 0.9)} m/s²", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    }
                }
            }
        }

        // Discovery chronology and probes log
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.8f)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Discovery Log & Historical Exploration",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF38BDF8),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HistoryEdu,
                        contentDescription = null,
                        tint = Color(0xFF60A5FA),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Discovered By / Record", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        val discoveredByResolved = apiMetric?.discoveredBy?.takeIf { it.isNotBlank() } ?: planet.adultsProfile.discoveryRecord
                        Text(
                            text = discoveredByResolved,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = Color(0xFF60A5FA),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Discovery Date Epoch", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        val discoveryDateResolved = apiMetric?.discoveryDate?.takeIf { it.isNotBlank() } ?: planet.adultsProfile.discoveryYear
                        Text(
                            text = discoveryDateResolved,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }

                Divider(color = Color(0xFF1E293B), modifier = Modifier.padding(vertical = 12.dp))

                Text(
                    text = "Key Scientific Summary",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF93C5FD),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = planet.adultsProfile.technicalSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Pivotal Probes / Historic Missions",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF93C5FD),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                planet.adultsProfile.significantMissions.forEach { mission ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = mission, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                    }
                }
            }
        }
    }
}

// Kids colorful, interactive gaming dashboard
@Composable
fun KidsDashboard(
    planet: Planet,
    viewModel: SpaceViewModel,
    activeRocket: Planet?,
    rocketProgress: Float
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Space Cartoon Profile Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEA580C).copy(alpha = 0.15f)),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(2.dp, Color(0xFFEA580C)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEA580C)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Planet Profile: ${planet.name}! 🌟",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFF97316),
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = planet.kidsProfile.funFact,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    lineHeight = 22.sp
                )

                Divider(color = Color(0xFFEA580C).copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "🌌 Atmospheric Blanket: ",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFFDBA74)
                    )
                    Text(
                        text = planet.kidsProfile.simpleAtmosphere,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "🪂 Gravity Score: ",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFFDBA74)
                    )
                    Text(
                        text = planet.kidsProfile.gravityComparison,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
            }
        }

        // Active animated rocket expedition!
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0369A1).copy(alpha = 0.15f)),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(2.dp, Color(0xFF0284C7)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Launch Celestial Rocket Journey! 🚀🚀",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF38BDF8),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "How long would it take a standard terrestrial rocket to fly from Earth to ${planet.name}?",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (activeRocket == planet) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (rocketProgress < 1.0f) "Cruising through the orbits... 🌠" else "Landed successfully on ${planet.name}! 🎉",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Custom rocket flight pathway
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F172A))
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(rocketProgress)
                                    .fillMaxHeight()
                                    .background(Color(0xFF38BDF8).copy(alpha = 0.3f))
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🌎 Earth", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                Text("🪐 ${planet.name}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }

                            // Spinning flying rocket
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(rocketProgress)
                                    .align(Alignment.CenterStart),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RocketLaunch,
                                    contentDescription = null,
                                    tint = Color(0xFFF97316),
                                    modifier = Modifier
                                        .size(24.dp)
                                        .rotate(90f)
                                )
                            }
                        }

                        if (rocketProgress >= 1.0f) {
                            Spacer(modifier = Modifier.height(10.dp))
                            val travelYears = when (planet.id) {
                                "mercury" -> 5
                                "venus" -> 3
                                "mars" -> 1
                                "jupiter" -> 6
                                "saturn" -> 7
                                "uranus" -> 9
                                "neptune" -> 12
                                else -> 0
                            }
                            Text(
                                text = "Fun Fact: An astronaut rocket takes about $travelYears years to cruise here!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFFEF08A),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = { viewModel.launchRocketJourney(planet) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        modifier = Modifier.fillMaxWidth().testTag("launch_rocket_btn"),
                        enabled = planet.id != "earth"
                    ) {
                        Text(
                            text = if (planet.id == "earth") "You are already home on Earth! 🌍" else "Shoot Rocket Boosters! 🚀",
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Galactic Trivia Quiz Game
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B).copy(alpha = 0.15f)),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(2.dp, Color(0xFF4338CA)),
            modifier = Modifier.fillMaxWidth()
        ) {
            val qIdx by viewModel.quizQuestionIndex.collectAsState()
            val score by viewModel.quizScore.collectAsState()
            val answerIdx by viewModel.selectedAnswerIndex.collectAsState()
            val completed by viewModel.quizCompleted.collectAsState()

            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Galactic trivia space quiz! 🛸🎓",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF818CF8),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (completed) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Amazing! You finished the Cosmic Quiz!",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Score: $score / ${viewModel.quizQuestions.size}",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFFBBF24)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.resetQuiz() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                        ) {
                            Text("Play Again! 🔄", color = Color.White)
                        }
                    }
                } else {
                    val activeQ = viewModel.quizQuestions[qIdx]
                    Text(
                        text = "Question ${qIdx + 1}/${viewModel.quizQuestions.size}:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = activeQ.question,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    activeQ.options.forEachIndexed { optIdx, option ->
                        val isSelected = answerIdx == optIdx
                        val isCorrect = optIdx == activeQ.correctAnswerIndex
                        val btnCol = when {
                            answerIdx != null && isCorrect -> Color(0xFF16A34A)
                            answerIdx != null && isSelected -> Color(0xFFDC2626)
                            else -> Color(0xFF111827)
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(btnCol)
                                .border(1.dp, Color(0xFF4F46E5).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .clickable(enabled = answerIdx == null) { viewModel.selectQuizAnswer(optIdx) }
                                .padding(12.dp)
                        ) {
                            Text(text = option, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                        }
                    }

                    if (answerIdx != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = activeQ.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { viewModel.advanceQuizQuestion() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                            modifier = Modifier.fillMaxWidth().testTag("next_quiz_q_btn")
                        ) {
                            Text("Next Question! ➡️", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// Falling stars ambient animations
@Composable
fun MeteorAnimationBg() {
    val infiniteTransition = rememberInfiniteTransition(label = "meteor_transition")
    val meteorY by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "meteor_translation"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Simple diagonal meteor trace
        val xStart = width * 0.7f
        val xEnd = width * 0.1f
        val progress = (meteorY + 100f) / 1000f

        val currentX = xStart + (xEnd - xStart) * progress
        val currentY = meteorY

        if (currentY in 0f..height) {
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color(0xFF0EA5E9), Color.White),
                    start = Offset(currentX + 30f, currentY - 30f),
                    end = Offset(currentX, currentY)
                ),
                start = Offset(currentX + 30f, currentY - 30f),
                end = Offset(currentX, currentY),
                strokeWidth = 3f
            )
        }
    }
}

// Confetti visual burst component
@Composable
fun ConfettiOverlay(triggerId: Int) {
    var animatePercent by remember(triggerId) { mutableStateOf(0f) }

    LaunchedEffect(triggerId) {
        animateTo(
            state = 0f,
            target = 1.0f,
            anim = tween(durationMillis = 1500, easing = LinearEasing)
        ) {
            animatePercent = it
        }
    }

    if (animatePercent < 1.0f) {
        val particleColors = listOf(Color.Red, Color.Green, Color.Yellow, Color.Cyan, Color.Magenta, Color(0xFFF97316))
        val particles = remember(triggerId) {
            List(30) {
                Offset(
                    x = Random.nextFloat(),
                    y = Random.nextFloat() * 0.3f // burst standard height limits
                ) to particleColors.random()
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            particles.forEach { (offset, col) ->
                // parabolic calculation of dynamic dispersion
                val dx = (offset.x - 0.5f) * w * animatePercent
                val dy = h * 0.3f + (offset.y * h * 0.5f) * animatePercent + (9.8f * animatePercent * animatePercent * 100) // gravity acceleration

                drawCircle(
                    color = col.copy(alpha = 1.0f - animatePercent),
                    radius = 8.dp.toPx() + (1f - animatePercent) * 6.dp.toPx(),
                    center = Offset(w / 2f + dx, dy)
                )
            }
        }
    }
}

private suspend fun animateTo(
    state: Float,
    target: Float,
    anim: AnimationSpec<Float>,
    block: (Float) -> Unit
) {
    AnimationState(initialValue = state).animateTo(
        targetValue = target,
        animationSpec = anim
    ) {
        block(value)
    }
}
