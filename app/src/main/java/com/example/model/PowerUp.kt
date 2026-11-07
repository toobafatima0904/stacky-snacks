package com.example.model

import androidx.compose.ui.graphics.Color

enum class PowerUpType(
    val id: String,
    val displayName: String,
    val description: String,
    val cost: Int,
    val durationSeconds: Int,
    val badgeColor: Color
) {
    SLOW_MOTION(
        id = "slow_motion",
        displayName = "Slow Motion",
        description = "Slows swinging snack velocity by 50% for 8 seconds.",
        cost = 30,
        durationSeconds = 8,
        badgeColor = Color(0xFF29B6F6)
    ),
    DOUBLE_COINS(
        id = "double_coins",
        displayName = "2x Coins",
        description = "Doubles all coins earned for the entire round!",
        cost = 40,
        durationSeconds = 0, // lasts whole round
        badgeColor = Color(0xFFFFD54F)
    ),
    PERFECT_DROP(
        id = "perfect_drop",
        displayName = "Perfect Drop",
        description = "Guides your very next drop with laser dead-center alignment!",
        cost = 50,
        durationSeconds = 0, // one-time use
        badgeColor = Color(0xFF66BB6A)
    ),
    SAVE_ME(
        id = "save_me",
        displayName = "Save Me",
        description = "Bounces back 1 fatal miss with an angelic golden spatula cushion!",
        cost = 60,
        durationSeconds = 0, // passive per round
        badgeColor = Color(0xFFFF7043)
    );

    companion object {
        fun fromId(id: String): PowerUpType? {
            return entries.firstOrNull { it.id == id }
        }
    }
}
