package com.example.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class DailyObjectiveType(
    val titleTemplate: String,
    val descriptionTemplate: String,
    val targetUnits: String
) {
    PERFECT_PLACEMENTS(
        titleTemplate = "Master of Precision",
        descriptionTemplate = "Land %d Perfect Placements in any game mode",
        targetUnits = "drops"
    ),
    TOWER_HEIGHT(
        titleTemplate = "Sky High Diner",
        descriptionTemplate = "Build a tower reaching %d delicious items",
        targetUnits = "items"
    ),
    TOTAL_SCORE(
        titleTemplate = "High Score Hunt",
        descriptionTemplate = "Score at least %d points in a single round",
        targetUnits = "pts"
    ),
    CONSECUTIVE_PERFECT(
        titleTemplate = "Flawless Combo",
        descriptionTemplate = "Achieve a combo of %d consecutive perfect drops",
        targetUnits = "combo"
    )
}

data class DailyChallenge(
    val dayKey: String,
    val type: DailyObjectiveType,
    val targetValue: Int,
    val currentValue: Int,
    val rewardCoins: Int,
    val isClaimed: Boolean
) {
    val isCompleted: Boolean get() = currentValue >= targetValue

    val progressFraction: Float get() = (currentValue.toFloat() / targetValue).coerceIn(0f, 1f)

    val title: String get() = type.titleTemplate

    val description: String get() = String.format(Locale.getDefault(), type.descriptionTemplate, targetValue)

    companion object {
        fun generateForToday(
            dayKey: String = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()),
            storedProgress: Int = 0,
            isClaimed: Boolean = false
        ): DailyChallenge {
            // Seed based on dayKey
            val hash = dayKey.hashCode().let { if (it < 0) -it else it }
            val types = DailyObjectiveType.entries
            val type = types[hash % types.size]

            val (target, coins) = when (type) {
                DailyObjectiveType.PERFECT_PLACEMENTS -> Pair(15 + (hash % 10), 100)
                DailyObjectiveType.TOWER_HEIGHT -> Pair(25 + (hash % 12), 120)
                DailyObjectiveType.TOTAL_SCORE -> Pair(100 + (hash % 50), 150)
                DailyObjectiveType.CONSECUTIVE_PERFECT -> Pair(4 + (hash % 3), 130)
            }

            return DailyChallenge(
                dayKey = dayKey,
                type = type,
                targetValue = target,
                currentValue = storedProgress,
                rewardCoins = coins,
                isClaimed = isClaimed
            )
        }
    }
}
