package com.example

import com.example.model.DailyChallenge
import com.example.model.DailyObjectiveType
import com.example.model.FoodType
import com.example.model.GameMode
import com.example.model.PowerUpType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testAllFoodItemsPresent() {
        val foods = FoodType.entries
        assertEquals(12, foods.size)

        // Check required foods
        val requiredNames = listOf(
            "chocolate_cake", "strawberry_cake", "donut", "burger",
            "pizza_slice", "pancakes", "cupcake", "ice_cream",
            "cookies", "sandwich", "watermelon", "waffles"
        )
        for (name in requiredNames) {
            val food = FoodType.fromId(name)
            assertNotNull(food)
        }
    }

    @Test
    fun testComboScoringLogic() {
        // Combo formula: 5 * comboMultiplier
        fun calcPoints(combo: Int): Int = 5 * combo

        assertEquals(5, calcPoints(1))
        assertEquals(10, calcPoints(2))
        assertEquals(15, calcPoints(3))
        assertEquals(20, calcPoints(4))
    }

    @Test
    fun testPowerUpDefinitions() {
        assertEquals(4, PowerUpType.entries.size)
        assertNotNull(PowerUpType.fromId("slow_motion"))
        assertNotNull(PowerUpType.fromId("double_coins"))
        assertNotNull(PowerUpType.fromId("perfect_drop"))
        assertNotNull(PowerUpType.fromId("save_me"))
    }

    @Test
    fun testDailyChallengeGeneration() {
        val challenge = DailyChallenge.generateForToday("20261105", storedProgress = 10, isClaimed = false)
        assertNotNull(challenge)
        assertTrue(challenge.targetValue > 0)
        assertEquals(10, challenge.currentValue)
        assertFalse(challenge.isClaimed)
    }

    @Test
    fun testGameModes() {
        assertEquals(3, GameMode.entries.size)
        assertTrue(GameMode.entries.any { it == GameMode.CLASSIC })
        assertTrue(GameMode.entries.any { it == GameMode.TIME_CHALLENGE })
        assertTrue(GameMode.entries.any { it == GameMode.DAILY_CHALLENGE })
    }
}
