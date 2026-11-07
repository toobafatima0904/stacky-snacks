package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameState
import com.example.model.GameMode
import com.example.model.PowerUpType
import kotlin.math.abs

@Composable
fun GameHud(
    gameState: GameState,
    totalCoins: Int,
    powerUpInventory: Map<PowerUpType, Int>,
    onPauseClick: () -> Unit,
    onPowerUpClick: (PowerUpType) -> Unit,
    onDropClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hud_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "combo_scale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top Info Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pause button
            IconButton(
                onClick = onPauseClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.9f), CircleShape)
                    .border(2.dp, Color(0xFFFF5964).copy(alpha = 0.3f), CircleShape)
                    .testTag("pause_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause Game",
                    tint = Color(0xFF2B2119)
                )
            }

            // Score Display
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.92f),
                shadowElevation = 6.dp,
                modifier = Modifier.border(2.dp, Color(0xFFFFD129), RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SCORE: ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF75685F)
                    )
                    Text(
                        text = "${gameState.score}",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = Color(0xFFFF5964)
                    )
                }
            }

            // Height and Coins Pills
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Height
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFF3E0),
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Text(
                        text = "🥞 ${gameState.heightCount}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFFE65100),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                // Coins
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFF9C4)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🪙 $totalCoins",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFF57F17)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Time Challenge Timer Bar
        if (gameState.gameMode == GameMode.TIME_CHALLENGE) {
            val progress = (gameState.remainingTimeSeconds / 60f).coerceIn(0f, 1f)
            val isUrgent = gameState.remainingTimeSeconds <= 10f

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "TIME REMAINING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUrgent) Color(0xFFFF1744) else Color(0xFF2B2119)
                    )
                    Text(
                        text = "${gameState.remainingTimeSeconds.toInt()}s",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isUrgent) Color(0xFFFF1744) else Color(0xFF2B2119)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (isUrgent) Color(0xFFFF1744) else Color(0xFFFFB300),
                    trackColor = Color.White.copy(alpha = 0.5f)
                )
            }
        }

        // Combo & Stability Indicators Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Combo Badge
            AnimatedVisibility(
                visible = gameState.comboMultiplier > 1,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFF3366),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .scale(pulseScale)
                        .padding(horizontal = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔥 COMBO x${gameState.comboMultiplier}!",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }

            // Wobble Alert
            if (abs(gameState.towerWobbleAngleDeg) > 7f) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFF9800),
                    shadowElevation = 6.dp,
                    modifier = Modifier.padding(horizontal = 6.dp)
                ) {
                    Text(
                        text = "⚠️ WOBBLING!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))
    }
}

@Composable
fun PowerUpToolbar(
    gameState: GameState,
    inventory: Map<PowerUpType, Int>,
    onPowerUpClick: (PowerUpType) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.95f),
        shadowElevation = 10.dp,
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(1.5.dp, Color(0xFFFFE0B2), RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PowerUpItem(
                powerUp = PowerUpType.SLOW_MOTION,
                icon = Icons.Default.Speed,
                count = inventory[PowerUpType.SLOW_MOTION] ?: 0,
                isActive = gameState.isSlowMotionActive,
                activeLabel = if (gameState.isSlowMotionActive) "${gameState.slowMotionRemainingSeconds.toInt()}s" else null,
                onClick = { onPowerUpClick(PowerUpType.SLOW_MOTION) },
                testTag = "powerup_slow_motion"
            )

            PowerUpItem(
                powerUp = PowerUpType.DOUBLE_COINS,
                icon = Icons.Default.MonetizationOn,
                count = inventory[PowerUpType.DOUBLE_COINS] ?: 0,
                isActive = gameState.isDoubleCoinsActive,
                activeLabel = if (gameState.isDoubleCoinsActive) "ON" else null,
                onClick = { onPowerUpClick(PowerUpType.DOUBLE_COINS) },
                testTag = "powerup_double_coins"
            )

            PowerUpItem(
                powerUp = PowerUpType.PERFECT_DROP,
                icon = Icons.Default.Stars,
                count = inventory[PowerUpType.PERFECT_DROP] ?: 0,
                isActive = gameState.isPerfectDropArmed,
                activeLabel = if (gameState.isPerfectDropArmed) "READY" else null,
                onClick = { onPowerUpClick(PowerUpType.PERFECT_DROP) },
                testTag = "powerup_perfect_drop"
            )

            PowerUpItem(
                powerUp = PowerUpType.SAVE_ME,
                icon = Icons.Default.Shield,
                count = inventory[PowerUpType.SAVE_ME] ?: 0,
                isActive = gameState.hasSaveMeAvailable,
                activeLabel = if (gameState.hasSaveMeAvailable) "ACTIVE" else null,
                onClick = { onPowerUpClick(PowerUpType.SAVE_ME) },
                testTag = "powerup_save_me"
            )
        }
    }
}

@Composable
private fun PowerUpItem(
    powerUp: PowerUpType,
    icon: ImageVector,
    count: Int,
    isActive: Boolean,
    activeLabel: String?,
    onClick: () -> Unit,
    testTag: String
) {
    val isAvailable = count > 0 && !isActive
    val bgColor = when {
        isActive -> powerUp.badgeColor
        isAvailable -> powerUp.badgeColor.copy(alpha = 0.15f)
        else -> Color(0xFFF5F5F5)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable(enabled = isAvailable) { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = powerUp.displayName,
                    tint = if (isActive) Color.White else if (isAvailable) powerUp.badgeColor else Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = activeLabel ?: "x$count",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    color = if (isActive) Color.White else if (isAvailable) Color(0xFF2B2119) else Color.Gray
                )
            }
            Text(
                text = powerUp.displayName,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = if (isActive) Color.White.copy(alpha = 0.9f) else Color(0xFF75685F)
            )
        }
    }
}
