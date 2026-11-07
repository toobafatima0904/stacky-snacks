package com.example.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.model.FoodType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object FoodDrawables {

    fun drawFoodItem(
        drawScope: DrawScope,
        foodType: FoodType,
        centerX: Float,
        centerY: Float,
        width: Float,
        height: Float,
        squashY: Float = 1.0f,
        tiltAngleDeg: Float = 0f
    ) {
        val effectiveHeight = height * squashY
        val effectiveWidth = width * (1.0f + (1.0f - squashY) * 0.4f) // squash & stretch

        drawScope.apply {
            val count = if (tiltAngleDeg != 0f) {
                // Compose DrawScope rotate
                // We'll rotate around center
                var saved = false
                // rotate block
            } else null

            // Draw based on type
            when (foodType) {
                FoodType.PANCAKES -> drawPancakes(centerX, centerY, effectiveWidth, effectiveHeight)
                FoodType.DONUT -> drawDonut(centerX, centerY, effectiveWidth, effectiveHeight)
                FoodType.BURGER -> drawBurger(centerX, centerY, effectiveWidth, effectiveHeight)
                FoodType.CHOCOLATE_CAKE -> drawChocolateCake(centerX, centerY, effectiveWidth, effectiveHeight)
                FoodType.STRAWBERRY_CAKE -> drawStrawberryCake(centerX, centerY, effectiveWidth, effectiveHeight)
                FoodType.PIZZA_SLICE -> drawPizza(centerX, centerY, effectiveWidth, effectiveHeight)
                FoodType.CUPCAKE -> drawCupcake(centerX, centerY, effectiveWidth, effectiveHeight)
                FoodType.ICE_CREAM -> drawIceCream(centerX, centerY, effectiveWidth, effectiveHeight)
                FoodType.COOKIES -> drawCookie(centerX, centerY, effectiveWidth, effectiveHeight)
                FoodType.SANDWICH -> drawSandwich(centerX, centerY, effectiveWidth, effectiveHeight)
                FoodType.WATERMELON -> drawWatermelon(centerX, centerY, effectiveWidth, effectiveHeight)
                FoodType.WAFFLES -> drawWaffle(centerX, centerY, effectiveWidth, effectiveHeight)
            }
        }
    }

    private fun DrawScope.drawPancakes(cx: Float, cy: Float, w: Float, h: Float) {
        val left = cx - w / 2f
        val top = cy - h / 2f
        val layerH = h * 0.42f

        // Bottom pancake
        drawRoundRect(
            color = Color(0xFFE6A117),
            topLeft = Offset(left + w * 0.03f, top + h * 0.58f),
            size = Size(w * 0.94f, layerH),
            cornerRadius = CornerRadius(layerH / 2f, layerH / 2f)
        )
        drawRoundRect(
            color = Color(0xFFFFCC33),
            topLeft = Offset(left + w * 0.03f, top + h * 0.58f),
            size = Size(w * 0.94f, layerH * 0.65f),
            cornerRadius = CornerRadius(layerH / 2f, layerH / 2f)
        )

        // Middle pancake
        drawRoundRect(
            color = Color(0xFFE6A117),
            topLeft = Offset(left + w * 0.01f, top + h * 0.32f),
            size = Size(w * 0.98f, layerH),
            cornerRadius = CornerRadius(layerH / 2f, layerH / 2f)
        )
        drawRoundRect(
            color = Color(0xFFFFD54F),
            topLeft = Offset(left + w * 0.01f, top + h * 0.32f),
            size = Size(w * 0.98f, layerH * 0.65f),
            cornerRadius = CornerRadius(layerH / 2f, layerH / 2f)
        )

        // Top pancake
        drawRoundRect(
            color = Color(0xFFD98200),
            topLeft = Offset(left, top + h * 0.06f),
            size = Size(w, layerH),
            cornerRadius = CornerRadius(layerH / 2f, layerH / 2f)
        )
        drawRoundRect(
            color = Color(0xFFFFE082),
            topLeft = Offset(left, top + h * 0.06f),
            size = Size(w, layerH * 0.65f),
            cornerRadius = CornerRadius(layerH / 2f, layerH / 2f)
        )

        // Maple Syrup drip
        val syrupPath = Path().apply {
            moveTo(cx - w * 0.35f, top + h * 0.15f)
            cubicTo(cx - w * 0.2f, top + h * 0.6f, cx - w * 0.15f, top + h * 0.85f, cx - w * 0.1f, top + h * 0.7f)
            cubicTo(cx, top + h * 0.45f, cx + w * 0.15f, top + h * 0.95f, cx + w * 0.22f, top + h * 0.6f)
            cubicTo(cx + w * 0.3f, top + h * 0.3f, cx + w * 0.35f, top + h * 0.2f, cx + w * 0.35f, top + h * 0.15f)
            close()
        }
        drawPath(syrupPath, color = Color(0xFFB25900))
        drawPath(syrupPath, color = Color(0x66FFE082), style = Stroke(width = 2f))

        // Butter cube
        val butterSize = w * 0.16f
        drawRoundRect(
            color = Color(0xFFFFEE58),
            topLeft = Offset(cx - butterSize / 2f, top - butterSize * 0.3f),
            size = Size(butterSize, butterSize * 0.7f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = Color(0xFFFFF9C4),
            topLeft = Offset(cx - butterSize / 2f + 2f, top - butterSize * 0.3f + 2f),
            size = Size(butterSize * 0.5f, butterSize * 0.35f),
            cornerRadius = CornerRadius(2f, 2f)
        )
    }

    private fun DrawScope.drawDonut(cx: Float, cy: Float, w: Float, h: Float) {
        val left = cx - w / 2f
        val top = cy - h / 2f

        // Golden Dough Ring
        drawRoundRect(
            color = Color(0xFFD79B5A),
            topLeft = Offset(left, top),
            size = Size(w, h),
            cornerRadius = CornerRadius(h / 2f, h / 2f)
        )
        drawRoundRect(
            color = Color(0xFFF3C185),
            topLeft = Offset(left + 3f, top + 2f),
            size = Size(w - 6f, h * 0.7f),
            cornerRadius = CornerRadius(h / 2.2f, h / 2.2f)
        )

        // Pink Strawberry Glaze
        val glazePath = Path().apply {
            moveTo(left + w * 0.08f, cy)
            cubicTo(left + w * 0.15f, top + h * 0.08f, left + w * 0.4f, top + h * 0.05f, cx, top + h * 0.05f)
            cubicTo(left + w * 0.6f, top + h * 0.05f, left + w * 0.85f, top + h * 0.08f, left + w * 0.92f, cy)
            cubicTo(left + w * 0.88f, top + h * 0.88f, left + w * 0.7f, top + h * 0.95f, cx + w * 0.15f, top + h * 0.82f)
            cubicTo(cx, top + h * 0.95f, left + w * 0.3f, top + h * 0.88f, left + w * 0.08f, cy)
            close()
        }
        drawPath(glazePath, color = Color(0xFFFF4081))
        // Glaze shine
        drawOval(
            color = Color(0x66FFFFFF),
            topLeft = Offset(left + w * 0.2f, top + h * 0.12f),
            size = Size(w * 0.35f, h * 0.2f)
        )

        // Donut center hole
        val holeW = w * 0.28f
        val holeH = h * 0.36f
        drawOval(
            color = Color(0x33000000),
            topLeft = Offset(cx - holeW / 2f, cy - holeH / 2f + 2f),
            size = Size(holeW, holeH)
        )
        drawOval(
            color = Color(0xFFFFF8F0), // match bg cutout
            topLeft = Offset(cx - holeW / 2f, cy - holeH / 2f),
            size = Size(holeW, holeH)
        )

        // Rainbow sprinkles
        val sprinkleColors = listOf(Color(0xFF00E5FF), Color(0xFFFFEB3B), Color(0xFF76FF03), Color(0xFFFFFFFF), Color(0xFFD500F9))
        val sprinklePositions = listOf(
            Offset(left + w * 0.22f, top + h * 0.35f),
            Offset(left + w * 0.32f, top + h * 0.2f),
            Offset(left + w * 0.68f, top + h * 0.22f),
            Offset(left + w * 0.78f, top + h * 0.4f),
            Offset(left + w * 0.25f, top + h * 0.68f),
            Offset(left + w * 0.72f, top + h * 0.65f),
            Offset(left + w * 0.5f, top + h * 0.15f)
        )
        sprinklePositions.forEachIndexed { idx, pos ->
            drawRoundRect(
                color = sprinkleColors[idx % sprinkleColors.size],
                topLeft = pos,
                size = Size(w * 0.06f, h * 0.1f),
                cornerRadius = CornerRadius(2f, 2f)
            )
        }
    }

    private fun DrawScope.drawBurger(cx: Float, cy: Float, w: Float, h: Float) {
        val left = cx - w / 2f
        val top = cy - h / 2f

        // Bottom Bun
        drawRoundRect(
            color = Color(0xFFD9822B),
            topLeft = Offset(left + w * 0.05f, top + h * 0.78f),
            size = Size(w * 0.9f, h * 0.22f),
            cornerRadius = CornerRadius(6f, 6f)
        )

        // Patty
        drawRoundRect(
            color = Color(0xFF5D4037),
            topLeft = Offset(left + w * 0.02f, top + h * 0.58f),
            size = Size(w * 0.96f, h * 0.22f),
            cornerRadius = CornerRadius(8f, 8f)
        )

        // Melted Cheese
        val cheesePath = Path().apply {
            moveTo(left + w * 0.06f, top + h * 0.58f)
            lineTo(left + w * 0.25f, top + h * 0.75f)
            lineTo(left + w * 0.45f, top + h * 0.58f)
            lineTo(left + w * 0.65f, top + h * 0.78f)
            lineTo(left + w * 0.85f, top + h * 0.58f)
            lineTo(left + w * 0.94f, top + h * 0.58f)
            lineTo(left + w * 0.06f, top + h * 0.58f)
            close()
        }
        drawPath(cheesePath, color = Color(0xFFFFC107))

        // Tomato Slices
        drawRoundRect(
            color = Color(0xFFE53935),
            topLeft = Offset(left + w * 0.1f, top + h * 0.44f),
            size = Size(w * 0.38f, h * 0.15f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = Color(0xFFD32F2F),
            topLeft = Offset(left + w * 0.52f, top + h * 0.44f),
            size = Size(w * 0.38f, h * 0.15f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // Wavy Lettuce
        val lettucePath = Path().apply {
            moveTo(left, top + h * 0.46f)
            for (i in 0..5) {
                val segW = w / 6f
                cubicTo(
                    left + i * segW + segW * 0.25f, top + h * 0.38f,
                    left + i * segW + segW * 0.75f, top + h * 0.52f,
                    left + (i + 1) * segW, top + h * 0.46f
                )
            }
            lineTo(left + w, top + h * 0.48f)
            lineTo(left, top + h * 0.48f)
            close()
        }
        drawPath(lettucePath, color = Color(0xFF4CAF50))

        // Top Bun (Dome)
        val bunPath = Path().apply {
            moveTo(left + w * 0.02f, top + h * 0.44f)
            cubicTo(
                left + w * 0.05f, top + h * 0.05f,
                left + w * 0.95f, top + h * 0.05f,
                left + w * 0.98f, top + h * 0.44f
            )
            close()
        }
        drawPath(bunPath, color = Color(0xFFE69138))

        // Sesame Seeds
        val seedColor = Color(0xFFFFF9C4)
        listOf(
            Offset(left + w * 0.25f, top + h * 0.22f),
            Offset(left + w * 0.4f, top + h * 0.15f),
            Offset(left + w * 0.6f, top + h * 0.16f),
            Offset(left + w * 0.75f, top + h * 0.25f),
            Offset(left + w * 0.5f, top + h * 0.26f)
        ).forEach {
            drawOval(seedColor, topLeft = it, size = Size(w * 0.04f, h * 0.08f))
        }
    }

    private fun DrawScope.drawChocolateCake(cx: Float, cy: Float, w: Float, h: Float) {
        val left = cx - w / 2f
        val top = cy - h / 2f

        // Bottom cocoa sponge layer
        drawRoundRect(
            color = Color(0xFF3E2723),
            topLeft = Offset(left, top + h * 0.55f),
            size = Size(w, h * 0.45f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Chocolate cream filling
        drawRect(
            color = Color(0xFF795548),
            topLeft = Offset(left, top + h * 0.46f),
            size = Size(w, h * 0.12f)
        )
        // Top cocoa sponge
        drawRoundRect(
            color = Color(0xFF4E342E),
            topLeft = Offset(left, top + h * 0.18f),
            size = Size(w, h * 0.3f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Dark Ganache glaze with drips
        val ganachePath = Path().apply {
            moveTo(left, top + h * 0.25f)
            lineTo(left, top + h * 0.15f)
            lineTo(left + w, top + h * 0.15f)
            lineTo(left + w, top + h * 0.25f)
            cubicTo(left + w * 0.85f, top + h * 0.38f, left + w * 0.75f, top + h * 0.22f, left + w * 0.65f, top + h * 0.35f)
            cubicTo(left + w * 0.5f, top + h * 0.22f, left + w * 0.35f, top + h * 0.42f, left + w * 0.2f, top + h * 0.25f)
            close()
        }
        drawPath(ganachePath, color = Color(0xFF271510))

        // Whipped cream swirl
        drawCircle(
            color = Color(0xFFFFFFFF),
            radius = h * 0.18f,
            center = Offset(cx, top + h * 0.12f)
        )
        // Cherry on top
        drawCircle(
            color = Color(0xFFD50000),
            radius = h * 0.13f,
            center = Offset(cx, top + h * 0.05f)
        )
        // Cherry shine
        drawCircle(
            color = Color(0xAAFFFFFF),
            radius = h * 0.04f,
            center = Offset(cx - h * 0.04f, top + h * 0.02f)
        )
    }

    private fun DrawScope.drawStrawberryCake(cx: Float, cy: Float, w: Float, h: Float) {
        val left = cx - w / 2f
        val top = cy - h / 2f

        // Yellow Sponge base
        drawRoundRect(
            color = Color(0xFFFFE082),
            topLeft = Offset(left, top + h * 0.55f),
            size = Size(w, h * 0.45f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Strawberry jam filling
        drawRect(
            color = Color(0xFFFF1744),
            topLeft = Offset(left, top + h * 0.48f),
            size = Size(w, h * 0.1f)
        )
        // Top sponge
        drawRoundRect(
            color = Color(0xFFFFF176),
            topLeft = Offset(left, top + h * 0.18f),
            size = Size(w, h * 0.32f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Whipped cream top
        drawRoundRect(
            color = Color(0xFFFFFFFF),
            topLeft = Offset(left, top + h * 0.12f),
            size = Size(w, h * 0.16f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Strawberry halves
        for (i in -1..1) {
            val sx = cx + i * (w * 0.28f)
            val sy = top + h * 0.1f
            val berryPath = Path().apply {
                moveTo(sx, sy - h * 0.15f)
                cubicTo(sx + w * 0.1f, sy, sx + w * 0.05f, sy + h * 0.1f, sx, sy + h * 0.1f)
                cubicTo(sx - w * 0.05f, sy + h * 0.1f, sx - w * 0.1f, sy, sx, sy - h * 0.15f)
                close()
            }
            drawPath(berryPath, color = Color(0xFFFF1744))
            drawCircle(Color(0xFF4CAF50), radius = 3f, center = Offset(sx, sy - h * 0.15f))
        }
    }

    private fun DrawScope.drawPizza(cx: Float, cy: Float, w: Float, h: Float) {
        val left = cx - w / 2f
        val top = cy - h / 2f

        // Crust (golden edge)
        drawRoundRect(
            color = Color(0xFFD79B5A),
            topLeft = Offset(left, top + h * 0.15f),
            size = Size(w, h * 0.85f),
            cornerRadius = CornerRadius(12f, 12f)
        )
        // Melted Cheese
        drawRoundRect(
            color = Color(0xFFFFCA28),
            topLeft = Offset(left + w * 0.04f, top + h * 0.22f),
            size = Size(w * 0.92f, h * 0.72f),
            cornerRadius = CornerRadius(10f, 10f)
        )
        // Pepperoni slices
        val pepperoniColor = Color(0xFFC62828)
        val pList = listOf(
            Offset(cx - w * 0.25f, cy),
            Offset(cx + w * 0.22f, cy - h * 0.05f),
            Offset(cx, cy + h * 0.18f),
            Offset(cx - w * 0.1f, cy - h * 0.12f)
        )
        pList.forEach {
            drawCircle(pepperoniColor, radius = h * 0.18f, center = it)
            drawCircle(Color(0xFF8E0000), radius = h * 0.08f, center = Offset(it.x - 2f, it.y - 2f))
        }
        // Green oregano flecks
        drawCircle(Color(0xFF2E7D32), radius = 2f, center = Offset(cx + 8f, cy + 5f))
        drawCircle(Color(0xFF2E7D32), radius = 2f, center = Offset(cx - 15f, cy + 12f))
    }

    private fun DrawScope.drawCupcake(cx: Float, cy: Float, w: Float, h: Float) {
        val left = cx - w / 2f
        val top = cy - h / 2f

        // Ribbed wrapper base
        val cupPath = Path().apply {
            moveTo(left + w * 0.18f, top + h)
            lineTo(left + w * 0.82f, top + h)
            lineTo(left + w * 0.9f, top + h * 0.5f)
            lineTo(left + w * 0.1f, top + h * 0.5f)
            close()
        }
        drawPath(cupPath, color = Color(0xFF42A5F5))
        // Ribbed lines
        for (i in 1..4) {
            val xTop = left + w * 0.1f + i * (w * 0.16f)
            val xBot = left + w * 0.18f + i * (w * 0.128f)
            drawLine(Color(0xFF1E88E5), Offset(xTop, top + h * 0.5f), Offset(xBot, top + h), strokeWidth = 2f)
        }

        // Fluffy Muffin cake peak
        drawOval(
            color = Color(0xFFFFCA28),
            topLeft = Offset(left + w * 0.08f, top + h * 0.38f),
            size = Size(w * 0.84f, h * 0.25f)
        )

        // Swirled Buttercream frosting
        drawOval(
            color = Color(0xFFF48FB1),
            topLeft = Offset(left + w * 0.06f, top + h * 0.22f),
            size = Size(w * 0.88f, h * 0.28f)
        )
        drawOval(
            color = Color(0xFFF06292),
            topLeft = Offset(left + w * 0.18f, top + h * 0.1f),
            size = Size(w * 0.64f, h * 0.24f)
        )
        drawOval(
            color = Color(0xFFEC407A),
            topLeft = Offset(left + w * 0.32f, top + h * 0.02f),
            size = Size(w * 0.36f, h * 0.18f)
        )

        // Glossy red cherry
        drawCircle(Color(0xFFD50000), radius = h * 0.12f, center = Offset(cx, top - h * 0.04f))
        drawCircle(Color(0xAAFFFFFF), radius = h * 0.04f, center = Offset(cx - 3f, top - h * 0.06f))
    }

    private fun DrawScope.drawIceCream(cx: Float, cy: Float, w: Float, h: Float) {
        val left = cx - w / 2f
        val top = cy - h / 2f

        // Waffle cone or dish
        val conePath = Path().apply {
            moveTo(left + w * 0.15f, top + h * 0.55f)
            lineTo(cx, top + h)
            lineTo(left + w * 0.85f, top + h * 0.55f)
            close()
        }
        drawPath(conePath, color = Color(0xFFD79B5A))
        // Cone crosshatch
        drawLine(Color(0xFFA1662F), Offset(left + w * 0.25f, top + h * 0.65f), Offset(left + w * 0.75f, top + h * 0.85f), 1.5f)
        drawLine(Color(0xFFA1662F), Offset(left + w * 0.75f, top + h * 0.65f), Offset(left + w * 0.25f, top + h * 0.85f), 1.5f)

        // Scoop 1 (Left - Mint Chocolate)
        drawCircle(Color(0xFF80CBC4), radius = w * 0.22f, center = Offset(cx - w * 0.18f, top + h * 0.45f))
        drawCircle(Color(0xFF3E2723), radius = 3f, center = Offset(cx - w * 0.25f, top + h * 0.45f))

        // Scoop 2 (Right - Strawberry)
        drawCircle(Color(0xFFFF80AB), radius = w * 0.22f, center = Offset(cx + w * 0.18f, top + h * 0.45f))

        // Scoop 3 (Center Top - French Vanilla)
        drawCircle(Color(0xFFFFF9C4), radius = w * 0.24f, center = Offset(cx, top + h * 0.22f))

        // Chocolate fudge drip
        drawOval(Color(0xFF3E2723), topLeft = Offset(cx - w * 0.12f, top + h * 0.05f), size = Size(w * 0.24f, h * 0.16f))
    }

    private fun DrawScope.drawCookie(cx: Float, cy: Float, w: Float, h: Float) {
        val left = cx - w / 2f
        val top = cy - h / 2f

        // Baked Golden Cookie base
        drawOval(
            color = Color(0xFFC48847),
            topLeft = Offset(left, top),
            size = Size(w, h)
        )
        drawOval(
            color = Color(0xFFE5B074),
            topLeft = Offset(left + 3f, top + 2f),
            size = Size(w - 6f, h * 0.75f)
        )

        // Chocolate Chips
        val chipColor = Color(0xFF3E2723)
        val chips = listOf(
            Offset(cx - w * 0.25f, cy - h * 0.1f),
            Offset(cx + w * 0.22f, cy - h * 0.15f),
            Offset(cx, cy - h * 0.2f),
            Offset(cx - w * 0.1f, cy + h * 0.15f),
            Offset(cx + w * 0.2f, cy + h * 0.18f),
            Offset(cx - w * 0.32f, cy + h * 0.08f)
        )
        chips.forEach {
            drawRoundRect(chipColor, topLeft = it, size = Size(w * 0.12f, h * 0.22f), cornerRadius = CornerRadius(4f, 4f))
        }
    }

    private fun DrawScope.drawSandwich(cx: Float, cy: Float, w: Float, h: Float) {
        val left = cx - w / 2f
        val top = cy - h / 2f

        // Bottom Bread slice
        drawRoundRect(
            color = Color(0xFFE0A96D),
            topLeft = Offset(left, top + h * 0.7f),
            size = Size(w, h * 0.3f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Ham layer
        drawRoundRect(
            color = Color(0xFFFF8A80),
            topLeft = Offset(left + w * 0.04f, top + h * 0.55f),
            size = Size(w * 0.92f, h * 0.18f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        // Cheddar layer
        drawRoundRect(
            color = Color(0xFFFFD54F),
            topLeft = Offset(left + w * 0.02f, top + h * 0.44f),
            size = Size(w * 0.96f, h * 0.14f),
            cornerRadius = CornerRadius(3f, 3f)
        )
        // Lettuce
        drawRoundRect(
            color = Color(0xFF66BB6A),
            topLeft = Offset(left + w * 0.05f, top + h * 0.32f),
            size = Size(w * 0.9f, h * 0.14f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        // Top bread slice
        drawRoundRect(
            color = Color(0xFFE0A96D),
            topLeft = Offset(left, top + h * 0.08f),
            size = Size(w, h * 0.28f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        drawRoundRect(
            color = Color(0xFFF5DEB3),
            topLeft = Offset(left + 3f, top + h * 0.1f),
            size = Size(w - 6f, h * 0.18f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        // Festive toothpick with green olive
        drawLine(Color(0xFFD7CCC8), Offset(cx, top + h * 0.15f), Offset(cx, top - h * 0.2f), strokeWidth = 3f)
        drawCircle(Color(0xFF558B2F), radius = h * 0.12f, center = Offset(cx, top - h * 0.15f))
        drawCircle(Color(0xFFD50000), radius = h * 0.04f, center = Offset(cx, top - h * 0.15f))
    }

    private fun DrawScope.drawWatermelon(cx: Float, cy: Float, w: Float, h: Float) {
        val left = cx - w / 2f
        val top = cy - h / 2f

        // Dark green striped rind
        drawRoundRect(
            color = Color(0xFF2E7D32),
            topLeft = Offset(left, top + h * 0.65f),
            size = Size(w, h * 0.35f),
            cornerRadius = CornerRadius(8f, 8f)
        )
        // White inner rind
        drawRoundRect(
            color = Color(0xFFC8E6C9),
            topLeft = Offset(left + w * 0.03f, top + h * 0.58f),
            size = Size(w * 0.94f, h * 0.16f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        // Ruby red watermelon flesh (semicircle wedge)
        val fleshPath = Path().apply {
            moveTo(left + w * 0.05f, top + h * 0.62f)
            lineTo(left + w * 0.95f, top + h * 0.62f)
            cubicTo(
                left + w * 0.85f, top + h * 0.05f,
                left + w * 0.15f, top + h * 0.05f,
                left + w * 0.05f, top + h * 0.62f
            )
            close()
        }
        drawPath(fleshPath, color = Color(0xFFFF1744))

        // Watermelon black seeds
        val seeds = listOf(
            Offset(cx - w * 0.2f, top + h * 0.42f),
            Offset(cx, top + h * 0.3f),
            Offset(cx + w * 0.2f, top + h * 0.42f),
            Offset(cx - w * 0.08f, top + h * 0.48f),
            Offset(cx + w * 0.08f, top + h * 0.48f)
        )
        seeds.forEach {
            drawOval(Color(0xFF212121), topLeft = it, size = Size(w * 0.035f, h * 0.08f))
        }
    }

    private fun DrawScope.drawWaffle(cx: Float, cy: Float, w: Float, h: Float) {
        val left = cx - w / 2f
        val top = cy - h / 2f

        // Belgian waffle golden grid
        drawRoundRect(
            color = Color(0xFFD79B5A),
            topLeft = Offset(left, top),
            size = Size(w, h),
            cornerRadius = CornerRadius(10f, 10f)
        )
        drawRoundRect(
            color = Color(0xFFFFB74D),
            topLeft = Offset(left + 2f, top + 2f),
            size = Size(w - 4f, h - 4f),
            cornerRadius = CornerRadius(8f, 8f)
        )

        // Grid pockets
        val pocketW = (w - 24f) / 4f
        val pocketH = (h - 16f) / 2f
        for (col in 0..3) {
            for (row in 0..1) {
                val px = left + 8f + col * (pocketW + 3f)
                val py = top + 5f + row * (pocketH + 3f)
                drawRoundRect(
                    color = Color(0xFFB56A1E),
                    topLeft = Offset(px, py),
                    size = Size(pocketW, pocketH),
                    cornerRadius = CornerRadius(3f, 3f)
                )
            }
        }

        // Butter square
        val butterS = w * 0.16f
        drawRoundRect(
            color = Color(0xFFFFEE58),
            topLeft = Offset(cx - butterS / 2f, cy - butterS / 2f),
            size = Size(butterS, butterS * 0.7f),
            cornerRadius = CornerRadius(3f, 3f)
        )

        // Blueberries
        drawCircle(Color(0xFF283593), radius = 6f, center = Offset(cx - w * 0.25f, cy))
        drawCircle(Color(0xFF3949AB), radius = 5.5f, center = Offset(cx + w * 0.28f, cy - 3f))
    }
}
