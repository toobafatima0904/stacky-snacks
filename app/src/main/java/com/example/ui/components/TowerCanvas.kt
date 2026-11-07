package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameState
import com.example.model.BackgroundTheme
import com.example.model.BasePlateTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TowerCanvas(
    gameState: GameState,
    backgroundTheme: BackgroundTheme,
    plateTheme: BasePlateTheme,
    onTapToDrop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(gameState.status) {
                detectTapGestures {
                    onTapToDrop()
                }
            }
    ) {
        val widthPx = size.width
        val heightPx = size.height
        val centerX = widthPx / 2f

        val densityFactor = density.density
        // Base plate anchor near bottom
        val basePlateY = heightPx - 140f * densityFactor

        // 1. Draw rich background
        drawGameBackground(backgroundTheme, widthPx, heightPx)

        // Height scale indicators in the background
        drawHeightMarkers(
            textMeasurer = textMeasurer,
            baseY = basePlateY,
            cameraY = gameState.cameraCurrentY * densityFactor,
            density = densityFactor
        )

        // 2. World Coordinate System with Camera & Tower Wobble
        // Everything in the tower is affected by camera and wobble!
        val camYPx = gameState.cameraCurrentY * densityFactor

        translate(top = camYPx) {
            // Draw Base Plate
            drawBasePlate(
                theme = plateTheme,
                cx = centerX,
                cy = basePlateY,
                density = densityFactor
            )

            // Draw stacked items with wobble rotation
            rotate(
                degrees = gameState.towerWobbleAngleDeg,
                pivot = Offset(centerX, basePlateY)
            ) {
                var currentStackY = basePlateY
                for (item in gameState.stack) {
                    val itemWPx = item.width * densityFactor
                    val itemHPx = item.height * densityFactor
                    val itemXPx = centerX + (item.x * densityFactor)
                    val itemYCenter = currentStackY - (itemHPx / 2f)

                    FoodDrawables.drawFoodItem(
                        drawScope = this,
                        foodType = item.foodType,
                        centerX = itemXPx,
                        centerY = itemYCenter,
                        width = itemWPx,
                        height = itemHPx,
                        squashY = item.squashY,
                        tiltAngleDeg = item.tiltDeg
                    )

                    // Advance stack height
                    currentStackY -= itemHPx
                }
            }
        }

        // 3. Draw Drop Zone Lane & Top Target
        val dropLaneY = 160f * densityFactor // fixed on screen
        val topItem = gameState.stack.lastOrNull()
        val topItemCenterXPx = if (topItem != null) {
            centerX + (topItem.x * densityFactor)
        } else centerX

        // Drop guide laser or dotted line when Perfect Drop is active or gentle alignment guide
        if (gameState.isPerfectDropArmed) {
            drawLine(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF00E676), Color(0x2200E676))
                ),
                start = Offset(topItemCenterXPx, dropLaneY),
                end = Offset(topItemCenterXPx, basePlateY - (gameState.stack.size * 36f * densityFactor) + camYPx),
                strokeWidth = 3f * densityFactor
            )
            // Target reticle
            drawCircle(
                color = Color(0xFF00E676),
                radius = 12f * densityFactor,
                center = Offset(topItemCenterXPx, dropLaneY),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
            )
        }

        // 4. Draw Swinging / Falling Item
        val swinging = gameState.swingingItem
        if (swinging != null) {
            val itemWPx = swinging.width * densityFactor
            val itemHPx = swinging.height * densityFactor
            val itemXPx = centerX + (swinging.x * densityFactor)

            val itemYCenter = if (swinging.isDropping) {
                dropLaneY + (swinging.dropY * densityFactor)
            } else {
                dropLaneY
            }

            // Halo glow if Save Me is ready
            if (gameState.hasSaveMeAvailable) {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color(0x66FFD54F), Color(0x00FFD54F)),
                        center = Offset(itemXPx, itemYCenter),
                        radius = itemWPx * 0.7f
                    ),
                    radius = itemWPx * 0.7f,
                    center = Offset(itemXPx, itemYCenter)
                )
            }

            // Draw swinging/dropping food
            FoodDrawables.drawFoodItem(
                drawScope = this,
                foodType = swinging.foodType,
                centerX = itemXPx,
                centerY = itemYCenter,
                width = itemWPx,
                height = itemHPx,
                squashY = 1.0f
            )

            // Drop lane rail visual
            if (!swinging.isDropping) {
                drawLine(
                    color = Color(0x33FFFFFF),
                    start = Offset(centerX - 160f * densityFactor, dropLaneY),
                    end = Offset(centerX + 160f * densityFactor, dropLaneY),
                    strokeWidth = 2f
                )
            }
        }

        // 5. Draw Particles
        for (p in gameState.particles) {
            val px = centerX + (p.x * densityFactor)
            val py = basePlateY - (p.y * densityFactor) + camYPx

            when (p.shape) {
                1 -> { // Star / diamond
                    val path = Path().apply {
                        moveTo(px, py - p.size)
                        lineTo(px + p.size, py)
                        lineTo(px, py + p.size)
                        lineTo(px - p.size, py)
                        close()
                    }
                    drawPath(path, color = p.color.copy(alpha = p.alpha))
                }
                2 -> { // Heart
                    val s = p.size * 0.8f
                    val path = Path().apply {
                        moveTo(px, py + s)
                        cubicTo(px - s, py, px - s, py - s, px, py - s * 0.5f)
                        cubicTo(px + s, py - s, px + s, py, px, py + s)
                        close()
                    }
                    drawPath(path, color = p.color.copy(alpha = p.alpha))
                }
                else -> { // Round crumb
                    drawCircle(
                        color = p.color.copy(alpha = p.alpha),
                        radius = p.size,
                        center = Offset(px, py)
                    )
                }
            }
        }

        // 6. Draw Floating Popups (Combo, Perfect, etc.)
        for (popup in gameState.popups) {
            val popX = centerX + (popup.x * densityFactor)
            val popY = (240f * densityFactor) - (popup.y * densityFactor)

            val textLayout = textMeasurer.measure(
                text = popup.text,
                style = TextStyle(
                    fontSize = (20 * popup.scale).sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = popup.color.copy(alpha = popup.alpha)
                )
            )

            drawText(
                textLayoutResult = textLayout,
                topLeft = Offset(popX - (textLayout.size.width / 2f), popY)
            )

            popup.subText?.let { sub ->
                val subLayout = textMeasurer.measure(
                    text = sub,
                    style = TextStyle(
                        fontSize = (14 * popup.scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = popup.alpha)
                    )
                )
                drawText(
                    textLayoutResult = subLayout,
                    topLeft = Offset(popX - (subLayout.size.width / 2f), popY + textLayout.size.height + 4f)
                )
            }
        }
    }
}

private fun DrawScope.drawGameBackground(theme: BackgroundTheme, w: Float, h: Float) {
    // Gradient Sky/Room
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(theme.topColor, theme.bottomColor),
            startY = 0f,
            endY = h
        ),
        size = Size(w, h)
    )

    // Decorative background pattern
    when (theme) {
        BackgroundTheme.SUNNY_BAKERY -> {
            // Warm bakery wallpaper stripes
            val stripeWidth = 40f
            var x = 0f
            while (x < w) {
                drawRect(
                    color = Color(0x0D000000),
                    topLeft = Offset(x, 0f),
                    size = Size(stripeWidth / 2f, h)
                )
                x += stripeWidth
            }
        }
        BackgroundTheme.CANDY_SKY -> {
            // Fluffy clouds
            drawCircle(Color(0x33FFFFFF), radius = 60f, center = Offset(w * 0.2f, h * 0.3f))
            drawCircle(Color(0x33FFFFFF), radius = 80f, center = Offset(w * 0.35f, h * 0.28f))
            drawCircle(Color(0x33FFFFFF), radius = 70f, center = Offset(w * 0.8f, h * 0.5f))
        }
        BackgroundTheme.NEON_ARCADE -> {
            // Neon grid lines
            for (i in 1..8) {
                val y = h * (i / 10f)
                drawLine(Color(0x1AE94560), Offset(0f, y), Offset(w, y), 1.5f)
            }
        }
        BackgroundTheme.COSMIC_DESSERT -> {
            // Sparkling sweet stars
            val stars = listOf(
                Offset(w * 0.15f, h * 0.15f),
                Offset(w * 0.85f, h * 0.22f),
                Offset(w * 0.35f, h * 0.45f),
                Offset(w * 0.75f, h * 0.65f),
                Offset(w * 0.2f, h * 0.75f)
            )
            stars.forEach {
                drawCircle(Color(0x88FFFFFF), radius = 3f, center = it)
                drawCircle(Color(0x4400F5D4), radius = 6f, center = it)
            }
        }
    }
}

private fun DrawScope.drawBasePlate(
    theme: BasePlateTheme,
    cx: Float,
    cy: Float,
    density: Float
) {
    val plateW = 180f * density
    val plateH = 34f * density
    val standW = 80f * density
    val standH = 26f * density

    // Stand / Plinth
    drawRoundRect(
        color = theme.standColor,
        topLeft = Offset(cx - standW / 2f, cy + plateH * 0.4f),
        size = Size(standW, standH),
        cornerRadius = CornerRadius(6f * density, 6f * density)
    )

    // Base Dish
    drawRoundRect(
        color = theme.rimColor,
        topLeft = Offset(cx - plateW / 2f, cy - plateH / 2f),
        size = Size(plateW, plateH),
        cornerRadius = CornerRadius(plateH / 2f, plateH / 2f)
    )
    drawRoundRect(
        color = theme.plateColor,
        topLeft = Offset(cx - (plateW - 12f * density) / 2f, cy - (plateH - 8f * density) / 2f),
        size = Size(plateW - 12f * density, plateH - 8f * density),
        cornerRadius = CornerRadius((plateH - 8f * density) / 2f, (plateH - 8f * density) / 2f)
    )

    // Gloss shine
    drawOval(
        color = Color(0x44FFFFFF),
        topLeft = Offset(cx - plateW * 0.35f, cy - plateH * 0.35f),
        size = Size(plateW * 0.7f, plateH * 0.35f)
    )
}

private fun DrawScope.drawHeightMarkers(
    textMeasurer: TextMeasurer,
    baseY: Float,
    cameraY: Float,
    density: Float
) {
    val stepFloors = 5
    for (floor in 5..100 step stepFloors) {
        val yPos = baseY - (floor * 36f * density) + cameraY
        if (yPos in -100f..(size.height + 100f)) {
            // Milestone line
            drawLine(
                color = Color(0x22FFFFFF),
                start = Offset(20f, yPos),
                end = Offset(size.width - 20f, yPos),
                strokeWidth = 1f
            )

            val label = when (floor) {
                5 -> "5F • Bakery Counter"
                10 -> "10F • Kitchen Roof"
                15 -> "15F • Tree Tops"
                25 -> "25F • Cloud Kingdom"
                50 -> "50F • Stratosphere"
                75 -> "75F • Space Snacks!"
                else -> "${floor}F • ${(floor * 0.4f).toInt()}m"
            }

            val measured = textMeasurer.measure(
                text = label,
                style = TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0x6675685F)
                )
            )
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(24f, yPos - measured.size.height - 2f)
            )
        }
    }
}
