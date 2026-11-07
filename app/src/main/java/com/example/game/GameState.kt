package com.example.game

import androidx.compose.ui.graphics.Color
import com.example.model.FoodType
import com.example.model.GameMode

enum class GameStatus {
    MENU,
    PLAYING,
    PAUSED,
    GAME_OVER
}

data class StackedItem(
    val id: Long,
    val foodType: FoodType,
    val x: Float, // offset from center of base (0 = perfect alignment)
    val y: Float, // world height in dp from base
    val width: Float,
    val height: Float,
    val squashY: Float = 1.0f,
    val tiltDeg: Float = 0f,
    val isPerfect: Boolean = false
)

data class SwingingItem(
    val foodType: FoodType,
    val x: Float,
    val vx: Float,
    val width: Float,
    val height: Float,
    val isDropping: Boolean = false,
    val dropY: Float = 0f,
    val dropVy: Float = 0f
)

data class FloatingPopup(
    val id: Long,
    val text: String,
    val subText: String? = null,
    val x: Float,
    val y: Float,
    val color: Color,
    val alpha: Float = 1f,
    val scale: Float = 1f,
    val lifetime: Float = 1f // 1 to 0
)

data class GameParticle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val size: Float,
    val alpha: Float,
    val shape: Int // 0: circle, 1: star/sparkle, 2: heart
)

data class FloatingCoinEntity(
    val id: Long,
    val x: Float,
    val y: Float,
    var isCollected: Boolean = false
)

data class GameState(
    val status: GameStatus = GameStatus.MENU,
    val gameMode: GameMode = GameMode.CLASSIC,
    val score: Int = 0,
    val heightCount: Int = 0,
    val comboMultiplier: Int = 1,
    val consecutivePerfectCount: Int = 0,
    val maxConsecutiveCombo: Int = 0,
    val totalPerfectDropsInRound: Int = 0,
    val coinsEarnedInRound: Int = 0,
    val remainingTimeSeconds: Float = 60f, // For Time Challenge mode
    val towerWobbleAngleDeg: Float = 0f,
    val cumulativeImbalance: Float = 0f,
    val cameraTargetY: Float = 0f,
    val cameraCurrentY: Float = 0f,
    val swingingItem: SwingingItem? = null,
    val stack: List<StackedItem> = emptyList(),
    val popups: List<FloatingPopup> = emptyList(),
    val particles: List<GameParticle> = emptyList(),
    val floatingCoins: List<FloatingCoinEntity> = emptyList(),
    // Active powerups
    val isSlowMotionActive: Boolean = false,
    val slowMotionRemainingSeconds: Float = 0f,
    val isDoubleCoinsActive: Boolean = false,
    val isPerfectDropArmed: Boolean = false,
    val hasSaveMeAvailable: Boolean = false,
    val isSaveMeTriggeredAnimation: Boolean = false
)
