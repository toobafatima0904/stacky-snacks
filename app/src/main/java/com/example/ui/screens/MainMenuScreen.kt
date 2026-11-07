package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserGameProfile
import com.example.model.GameMode
import com.example.ui.components.FoodDrawables

@Composable
fun MainMenuScreen(
    profile: UserGameProfile,
    onStartGame: (GameMode) -> Unit,
    onOpenShop: () -> Unit,
    onOpenDaily: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "title_wobble")
    val titleScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "title_scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2), Color(0xFFFFCC80))
                )
            )
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top bar with Coins & Settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🪙 ${profile.coins}", fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color(0xFFF57F17))
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onOpenDaily,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.95f), CircleShape)
                        .testTag("menu_daily_button")
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = "Daily Mission", tint = Color(0xFFFFB300))
                }

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.95f), CircleShape)
                        .testTag("menu_settings_button")
                ) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF5D4037))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title Mascot & Header
        Box(
            modifier = Modifier
                .scale(titleScale)
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Snack Showcase Canvas
                Canvas(modifier = Modifier.size(140.dp, 60.dp)) {
                    FoodDrawables.drawFoodItem(
                        drawScope = this,
                        foodType = profile.selectedFood,
                        centerX = size.width / 2f,
                        centerY = size.height / 2f,
                        width = size.width * 0.9f,
                        height = size.height * 0.85f
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "STACKY\nSNACKS!",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFF5964),
                    textAlign = TextAlign.Center,
                    lineHeight = 40.sp
                )

                Text(
                    text = "Delicious Wobbly Tower Arcade",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF75685F),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Mode Cards
        // 1. Classic Mode (Primary)
        ModeCard(
            title = "CLASSIC TOWER",
            subtitle = "Stack endlessly! One miss and your tower topples.",
            badge = "BEST: ${profile.classicBestHeight} ITEMS",
            emoji = "🥞",
            themeColor = Color(0xFFFF5964),
            testTag = "play_classic_mode",
            onClick = { onStartGame(GameMode.CLASSIC) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Time Rush (60s)
        ModeCard(
            title = "TIME RUSH (60s)",
            subtitle = "Speed stacking frenzy! Score max points before time expires.",
            badge = "BEST: ${profile.timeRushHighScore} PTS",
            emoji = "⏱️",
            themeColor = Color(0xFFFF9800),
            testTag = "play_time_mode",
            onClick = { onStartGame(GameMode.TIME_CHALLENGE) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Daily Mission Banner
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenDaily() }
                .testTag("play_daily_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFFFF9C4), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⭐", fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "DAILY MISSION",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = Color(0xFF2B2119)
                    )
                    Text(
                        text = profile.dailyChallenge.description,
                        fontSize = 12.sp,
                        color = Color(0xFF75685F),
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (profile.dailyChallenge.isCompleted && !profile.dailyChallenge.isClaimed) Color(0xFFFFD129) else Color(0xFFFFF3E0)
                ) {
                    Text(
                        text = if (profile.dailyChallenge.isCompleted && !profile.dailyChallenge.isClaimed) "CLAIM!" else "${(profile.dailyChallenge.progressFraction * 100).toInt()}%",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = Color(0xFFE65100),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Big Shop Button
        Button(
            onClick = onOpenShop,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(2.dp, Color(0xFFFFD129), RoundedCornerShape(20.dp))
                .testTag("open_shop_button")
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = null,
                tint = Color(0xFFE65100),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "SNACK SHOP & UNLOCKS",
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = Color(0xFF2B2119)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ModeCard(
    title: String,
    subtitle: String,
    badge: String,
    emoji: String,
    themeColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(themeColor.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = Color(0xFF2B2119)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF75685F),
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF5F5F5)
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = themeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(themeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
