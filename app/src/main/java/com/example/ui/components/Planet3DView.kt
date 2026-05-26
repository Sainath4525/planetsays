package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import com.example.data.Planet
import kotlin.math.sin

@Composable
fun Planet3DView(
    planet: Planet,
    modifier: Modifier = Modifier,
    spinSpeedMultiplier: Float = 1.0f
) {
    // Infinite rotation for planet surface textures
    val infiniteTransition = rememberInfiniteTransition(label = "planet_rotation")
    val rotationOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "surface_translation"
    )

    // Twinkling for space stars
    val starAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = EaseInOutCirc),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_twinkle"
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val center = Offset(width / 2f, height / 2f)

            // Dynamic sizing based on dimensions
            val baseRadius = (width.coerceAtMost(height) / 3.4f).coerceAtLeast(80f)
            val atmRadius = baseRadius * 1.25f

            // 1. Draw Star Constellations behind planet
            val randomStars = listOf(
                Offset(width * 0.15f, height * 0.2f),
                Offset(width * 0.85f, height * 0.15f),
                Offset(width * 0.1f, height * 0.75f),
                Offset(width * 0.75f, height * 0.8f),
                Offset(width * 0.25f, height * 0.45f),
                Offset(width * 0.8f, height * 0.5f),
                Offset(width * 0.5f, height * 0.15f)
            )
            randomStars.forEachIndexed { i, offset ->
                val sizeVal = if (i % 2 == 0) 5f else 3f
                drawCircle(
                    color = Color.White.copy(alpha = if (i % 2 == 0) starAlpha else 1f - starAlpha),
                    radius = sizeVal,
                    center = offset
                )
            }

            // 2. BACK PART OF PLANETARY RINGS (Saturn, Uranus, Neptune)
            // Tilted ellipses drawn before planet if rings exist
            if (planet.hasRings) {
                val ringColor = Color(android.graphics.Color.parseColor(planet.ringColorHex ?: "#22FFFFFF"))
                val ringWidth = baseRadius * 1.8f
                val ringHeight = baseRadius * 0.4f
                val angleTilt = when (planet.id) {
                    "saturn" -> -15f
                    "uranus" -> 70f // Extreme tilt
                    else -> 10f
                }

                withTransform({
                    rotate(angleTilt, center)
                }) {
                    // Back half of outer ring
                    drawArc(
                        color = ringColor.copy(alpha = 0.4f),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(center.x - ringWidth, center.y - ringHeight),
                        size = Size(ringWidth * 2f, ringHeight * 2f),
                        style = Stroke(width = baseRadius * 0.18f)
                    )
                    // Back half of inner ring
                    drawArc(
                        color = ringColor.copy(alpha = 0.2f),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(center.x - (ringWidth * 0.8f), center.y - (ringHeight * 0.8f)),
                        size = Size(ringWidth * 1.6f, ringHeight * 1.6f),
                        style = Stroke(width = baseRadius * 0.12f)
                    )
                }
            }

            // 3. ATMOSPHERIC OUTER GLOW (Gaseous envelope)
            val glowColor = Color(android.graphics.Color.parseColor(planet.atmosphereProperties.visualAtmosphereColor))
            val atmBrush = Brush.radialGradient(
                colors = listOf(
                    glowColor.copy(alpha = 0.55f),
                    glowColor.copy(alpha = 0.22f),
                    Color.Transparent
                ),
                center = center,
                radius = atmRadius
            )
            drawCircle(
                brush = atmBrush,
                radius = atmRadius,
                center = center
            )

            // 4. PLANET BASE CORE
            val baseColor = Color(android.graphics.Color.parseColor(planet.colorHex))
            val coreShadingBrush = Brush.radialGradient(
                colors = listOf(
                    baseColor.copy(alpha = 0.85f),
                    baseColor.copy(alpha = 0.35f),
                    Color.Black.copy(alpha = 0.7f)
                ),
                center = Offset(center.x - (baseRadius * 0.2f), center.y - (baseRadius * 0.2f)),
                radius = baseRadius * 1.1f
            )

            // We clip to the circular bounds of the planet core to draw spinning textures
            val clipPath = Path().apply {
                addOval(androidx.compose.ui.geometry.Rect(center, baseRadius))
            }

            clipPath(clipPath) {
                // Draw bottom base
                drawCircle(color = baseColor, radius = baseRadius, center = center)

                // Draw planet-specific features that scroll across
                val scrollShift = rotationOffset * spinSpeedMultiplier
                when (planet.id) {
                    "mercury", "mars" -> {
                        // Drawing simulated rotating crater craters/splotches
                        val featureColor = if (planet.id == "mars") Color(0xFF421008) else Color(0xFF555555)
                        val craterOffsets = listOf(
                            Offset(-200f, -40f), Offset(-100f, 60f), Offset(0f, -80f),
                            Offset(100f, 40f), Offset(200f, -20f), Offset(300f, 70f)
                        )
                        craterOffsets.forEach { craftOffset ->
                            val dx = (craftOffset.x + scrollShift) % 500f - 250f
                            drawCircle(
                                color = featureColor.copy(alpha = 0.35f),
                                radius = baseRadius * 0.16f,
                                center = Offset(center.x + dx, center.y + craftOffset.y)
                            )
                        }
                    }
                    "venus" -> {
                        // Dense sulfur vortexes
                        val featureColor = Color(0xFFA67123)
                        for (y in -4..4) {
                            val dy = y * (baseRadius * 0.22f)
                            val amplitude = baseRadius * 0.1f
                            val wavelength = baseRadius * 0.8f
                            val strokeWidth = baseRadius * 0.08f
                            val path = Path()
                            var started = false
                            for (x in -250..250 step 15) {
                                val tx = x.toFloat() + scrollShift
                                val ty = center.y + dy + sin(tx / wavelength) * amplitude
                                if (!started) {
                                    path.moveTo(center.x + x, ty)
                                    started = true
                                } else {
                                    path.lineTo(center.x + x, ty)
                                }
                            }
                            drawPath(
                                path = path,
                                color = featureColor.copy(alpha = 0.4f),
                                style = Stroke(width = strokeWidth)
                            )
                        }
                    }
                    "earth" -> {
                        // Draw continents / landforms scrolling
                        val landColor = Color(0xFF3B7A57)
                        val cloudColor = Color.White.copy(alpha = 0.55f)
                        
                        // Abstract land mass paths rotating
                        for (i in -2..4) {
                            val dx = (i * baseRadius * 1.5f + scrollShift) % (baseRadius * 6f) - (baseRadius * 3f)
                            drawCircle(
                                color = landColor,
                                radius = baseRadius * 0.45f,
                                center = Offset(center.x + dx, center.y - 10f)
                            )
                            drawCircle(
                                color = landColor,
                                radius = baseRadius * 0.3f,
                                center = Offset(center.x + dx + (baseRadius * 0.4f), center.y + 30f)
                            )
                            // Decorative swirly clouds
                            drawCircle(
                                color = cloudColor,
                                radius = baseRadius * 0.35f,
                                center = Offset(center.x + dx - (baseRadius * 0.2f) + (scrollShift * 0.2f), center.y - 40f)
                            )
                        }
                    }
                    "jupiter", "saturn", "uranus", "neptune" -> {
                        // Gas Bands
                        val bandColors = when (planet.id) {
                            "jupiter" -> listOf(Color(0xFF8D5B4C), Color(0xFFF5EBE6), Color(0xFFC58E73), Color(0xFFE4C3AD))
                            "saturn" -> listOf(Color(0xFFD2B392), Color(0xFFF1E3D3), Color(0xFFB59374), Color(0xFFDFCCB9))
                            "uranus" -> listOf(Color(0xFF99D1CD), Color(0xFFC7ECE9), Color(0xFF86BDBA))
                            else -> listOf(Color(0xFF264EC0), Color(0xFF538AFF), Color(0xFF1B2E75), Color(0xFF4C7FFF))
                        }

                        var yCursor = center.y - baseRadius
                        val step = (baseRadius * 2f) / bandColors.size
                        bandColors.forEachIndexed { idx, col ->
                            val heightOffset = step * 1.1f
                            // Layer some moving cloud bands that scale nicely
                            val yCenter = yCursor + (step / 2f)
                            drawRect(
                                color = col.copy(alpha = 0.65f),
                                topLeft = Offset(center.x - baseRadius, yCursor),
                                size = Size(baseRadius * 2f, heightOffset)
                            )
                            // Little turbulent waves inside bands
                            val waveStrength = baseRadius * 0.12f
                            val path = Path()
                            var isInit = false
                            for (x in -200..200 step 10) {
                                val wx = x.toFloat() + (scrollShift * if (idx % 2 == 0) 1.2f else 0.8f)
                                val wy = yCenter + sin(wx / 45f) * waveStrength
                                if (!isInit) {
                                    path.moveTo(center.x + x, wy)
                                    isInit = true
                                } else {
                                    path.lineTo(center.x + x, wy)
                                }
                            }
                            drawPath(
                                path = path,
                                color = col.copy(alpha = 0.85f),
                                style = Stroke(width = baseRadius * 0.15f)
                            )
                            yCursor += step
                        }

                        // Giant Red Spot on Jupiter specific overlay!
                        if (planet.id == "jupiter") {
                            val spotOffset = (scrollShift % 600f) - 300f
                            drawCircle(
                                color = Color(0xFFA93226),
                                radius = baseRadius * 0.22f,
                                center = Offset(center.x + spotOffset, center.y + (baseRadius * 0.35f))
                            )
                            drawCircle(
                                color = Color(0xFFE6B0AA),
                                radius = baseRadius * 0.12f,
                                center = Offset(center.x + spotOffset + 4f, center.y + (baseRadius * 0.35f) + 2f)
                            )
                        }
                    }
                }

                // Shading layer to provide intense 3D Spherical Volume
                drawCircle(
                    brush = coreShadingBrush,
                    radius = baseRadius,
                    center = center
                )
            }

            // 5. FRONT PART OF PLANETARY RINGS (Saturn, Uranus, Neptune)
            // Overlaying of front section creates the 3D occlusion wrapping effect!
            if (planet.hasRings) {
                val ringColor = Color(android.graphics.Color.parseColor(planet.ringColorHex ?: "#22FFFFFF"))
                val ringWidth = baseRadius * 1.8f
                val ringHeight = baseRadius * 0.4f
                val angleTilt = when (planet.id) {
                    "saturn" -> -15f
                    "uranus" -> 70f
                    else -> 10f
                }

                withTransform({
                    rotate(angleTilt, center)
                }) {
                    // Front half of outer ring
                    drawArc(
                        color = ringColor.copy(alpha = 0.85f),
                        startAngle = 0f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(center.x - ringWidth, center.y - ringHeight),
                        size = Size(ringWidth * 2f, ringHeight * 2f),
                        style = Stroke(width = baseRadius * 0.18f)
                    )
                    // Front half of inner ring
                    drawArc(
                        color = ringColor.copy(alpha = 0.45f),
                        startAngle = 0f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(center.x - (ringWidth * 0.8f), center.y - (ringHeight * 0.8f)),
                        size = Size(ringWidth * 1.6f, ringHeight * 1.6f),
                        style = Stroke(width = baseRadius * 0.12f)
                    )
                }
            }
        }
    }
}
