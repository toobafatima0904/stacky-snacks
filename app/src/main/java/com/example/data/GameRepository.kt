package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.BackgroundTheme
import com.example.model.BasePlateTheme
import com.example.model.DailyChallenge
import com.example.model.EffectTheme
import com.example.model.FoodType
import com.example.model.MusicTheme
import com.example.model.PowerUpType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UserGameProfile(
    val coins: Int,
    val selectedFood: FoodType,
    val unlockedFoods: Set<String>,
    val selectedPlate: BasePlateTheme,
    val unlockedPlates: Set<String>,
    val selectedBackground: BackgroundTheme,
    val unlockedBackgrounds: Set<String>,
    val selectedEffect: EffectTheme,
    val unlockedEffects: Set<String>,
    val selectedMusic: MusicTheme,
    val unlockedMusic: Set<String>,
    val classicHighScore: Int,
    val classicBestHeight: Int,
    val timeRushHighScore: Int,
    val totalLifetimeDrops: Int,
    val totalPerfectDrops: Int,
    val powerUpInventory: Map<PowerUpType, Int>,
    val dailyChallenge: DailyChallenge,
    val soundEnabled: Boolean,
    val hapticsEnabled: Boolean
)

class GameRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("stacky_snacks_prefs", Context.MODE_PRIVATE)

    private val _profile = MutableStateFlow(loadProfile())
    val profile: StateFlow<UserGameProfile> = _profile.asStateFlow()

    private fun loadProfile(): UserGameProfile {
        val todayKey = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val savedDayKey = prefs.getString("daily_day_key", todayKey) ?: todayKey
        val dailyProgress = if (savedDayKey == todayKey) prefs.getInt("daily_progress", 0) else 0
        val dailyClaimed = if (savedDayKey == todayKey) prefs.getBoolean("daily_claimed", false) else false

        val daily = DailyChallenge.generateForToday(
            dayKey = todayKey,
            storedProgress = dailyProgress,
            isClaimed = dailyClaimed
        )

        val unlockedFoodsSet = prefs.getStringSet(
            "unlocked_foods",
            setOf(FoodType.PANCAKES.id, FoodType.DONUT.id, FoodType.BURGER.id)
        ) ?: setOf(FoodType.PANCAKES.id, FoodType.DONUT.id, FoodType.BURGER.id)

        val unlockedPlatesSet = prefs.getStringSet(
            "unlocked_plates",
            setOf(BasePlateTheme.CLASSIC_CERAMIC.id)
        ) ?: setOf(BasePlateTheme.CLASSIC_CERAMIC.id)

        val unlockedBgSet = prefs.getStringSet(
            "unlocked_backgrounds",
            setOf(BackgroundTheme.SUNNY_BAKERY.id)
        ) ?: setOf(BackgroundTheme.SUNNY_BAKERY.id)

        val unlockedEffectsSet = prefs.getStringSet(
            "unlocked_effects",
            setOf(EffectTheme.SWEET_CRUMBS.id)
        ) ?: setOf(EffectTheme.SWEET_CRUMBS.id)

        val unlockedMusicSet = prefs.getStringSet(
            "unlocked_music",
            setOf(MusicTheme.CHIME_ARCADE.id)
        ) ?: setOf(MusicTheme.CHIME_ARCADE.id)

        val inventory = mutableMapOf<PowerUpType, Int>()
        for (powerUp in PowerUpType.entries) {
            inventory[powerUp] = prefs.getInt("powerup_${powerUp.id}", if (powerUp == PowerUpType.SLOW_MOTION) 1 else 0)
        }

        return UserGameProfile(
            coins = prefs.getInt("coins", 50), // 50 starter coins
            selectedFood = FoodType.fromId(prefs.getString("selected_food", FoodType.PANCAKES.id) ?: FoodType.PANCAKES.id),
            unlockedFoods = unlockedFoodsSet,
            selectedPlate = BasePlateTheme.fromId(prefs.getString("selected_plate", BasePlateTheme.CLASSIC_CERAMIC.id) ?: BasePlateTheme.CLASSIC_CERAMIC.id),
            unlockedPlates = unlockedPlatesSet,
            selectedBackground = BackgroundTheme.fromId(prefs.getString("selected_bg", BackgroundTheme.SUNNY_BAKERY.id) ?: BackgroundTheme.SUNNY_BAKERY.id),
            unlockedBackgrounds = unlockedBgSet,
            selectedEffect = EffectTheme.fromId(prefs.getString("selected_effect", EffectTheme.SWEET_CRUMBS.id) ?: EffectTheme.SWEET_CRUMBS.id),
            unlockedEffects = unlockedEffectsSet,
            selectedMusic = MusicTheme.fromId(prefs.getString("selected_music", MusicTheme.CHIME_ARCADE.id) ?: MusicTheme.CHIME_ARCADE.id),
            unlockedMusic = unlockedMusicSet,
            classicHighScore = prefs.getInt("classic_high_score", 0),
            classicBestHeight = prefs.getInt("classic_best_height", 0),
            timeRushHighScore = prefs.getInt("time_rush_high_score", 0),
            totalLifetimeDrops = prefs.getInt("total_lifetime_drops", 0),
            totalPerfectDrops = prefs.getInt("total_perfect_drops", 0),
            powerUpInventory = inventory,
            dailyChallenge = daily,
            soundEnabled = prefs.getBoolean("sound_enabled", true),
            hapticsEnabled = prefs.getBoolean("haptics_enabled", true)
        )
    }

    fun addCoins(amount: Int) {
        val newAmount = (_profile.value.coins + amount).coerceAtLeast(0)
        prefs.edit().putInt("coins", newAmount).apply()
        _profile.value = _profile.value.copy(coins = newAmount)
    }

    fun spendCoins(amount: Int): Boolean {
        if (_profile.value.coins >= amount) {
            val newAmount = _profile.value.coins - amount
            prefs.edit().putInt("coins", newAmount).apply()
            _profile.value = _profile.value.copy(coins = newAmount)
            return true
        }
        return false
    }

    fun unlockFood(food: FoodType): Boolean {
        if (_profile.value.unlockedFoods.contains(food.id)) return true
        if (spendCoins(food.unlockCost)) {
            val newSet = _profile.value.unlockedFoods + food.id
            prefs.edit().putStringSet("unlocked_foods", newSet).apply()
            _profile.value = _profile.value.copy(unlockedFoods = newSet, selectedFood = food)
            prefs.edit().putString("selected_food", food.id).apply()
            return true
        }
        return false
    }

    fun selectFood(food: FoodType) {
        if (_profile.value.unlockedFoods.contains(food.id)) {
            prefs.edit().putString("selected_food", food.id).apply()
            _profile.value = _profile.value.copy(selectedFood = food)
        }
    }

    fun unlockPlate(plate: BasePlateTheme): Boolean {
        if (_profile.value.unlockedPlates.contains(plate.id)) return true
        if (spendCoins(plate.cost)) {
            val newSet = _profile.value.unlockedPlates + plate.id
            prefs.edit().putStringSet("unlocked_plates", newSet).apply()
            _profile.value = _profile.value.copy(unlockedPlates = newSet, selectedPlate = plate)
            prefs.edit().putString("selected_plate", plate.id).apply()
            return true
        }
        return false
    }

    fun selectPlate(plate: BasePlateTheme) {
        if (_profile.value.unlockedPlates.contains(plate.id)) {
            prefs.edit().putString("selected_plate", plate.id).apply()
            _profile.value = _profile.value.copy(selectedPlate = plate)
        }
    }

    fun unlockBackground(bg: BackgroundTheme): Boolean {
        if (_profile.value.unlockedBackgrounds.contains(bg.id)) return true
        if (spendCoins(bg.cost)) {
            val newSet = _profile.value.unlockedBackgrounds + bg.id
            prefs.edit().putStringSet("unlocked_backgrounds", newSet).apply()
            _profile.value = _profile.value.copy(unlockedBackgrounds = newSet, selectedBackground = bg)
            prefs.edit().putString("selected_bg", bg.id).apply()
            return true
        }
        return false
    }

    fun selectBackground(bg: BackgroundTheme) {
        if (_profile.value.unlockedBackgrounds.contains(bg.id)) {
            prefs.edit().putString("selected_bg", bg.id).apply()
            _profile.value = _profile.value.copy(selectedBackground = bg)
        }
    }

    fun unlockEffect(effect: EffectTheme): Boolean {
        if (_profile.value.unlockedEffects.contains(effect.id)) return true
        if (spendCoins(effect.cost)) {
            val newSet = _profile.value.unlockedEffects + effect.id
            prefs.edit().putStringSet("unlocked_effects", newSet).apply()
            _profile.value = _profile.value.copy(unlockedEffects = newSet, selectedEffect = effect)
            prefs.edit().putString("selected_effect", effect.id).apply()
            return true
        }
        return false
    }

    fun selectEffect(effect: EffectTheme) {
        if (_profile.value.unlockedEffects.contains(effect.id)) {
            prefs.edit().putString("selected_effect", effect.id).apply()
            _profile.value = _profile.value.copy(selectedEffect = effect)
        }
    }

    fun unlockMusic(music: MusicTheme): Boolean {
        if (_profile.value.unlockedMusic.contains(music.id)) return true
        if (spendCoins(music.cost)) {
            val newSet = _profile.value.unlockedMusic + music.id
            prefs.edit().putStringSet("unlocked_music", newSet).apply()
            _profile.value = _profile.value.copy(unlockedMusic = newSet, selectedMusic = music)
            prefs.edit().putString("selected_music", music.id).apply()
            return true
        }
        return false
    }

    fun selectMusic(music: MusicTheme) {
        if (_profile.value.unlockedMusic.contains(music.id)) {
            prefs.edit().putString("selected_music", music.id).apply()
            _profile.value = _profile.value.copy(selectedMusic = music)
        }
    }

    fun buyPowerUp(powerUp: PowerUpType): Boolean {
        if (spendCoins(powerUp.cost)) {
            val current = _profile.value.powerUpInventory[powerUp] ?: 0
            val updated = _profile.value.powerUpInventory.toMutableMap()
            updated[powerUp] = current + 1
            prefs.edit().putInt("powerup_${powerUp.id}", current + 1).apply()
            _profile.value = _profile.value.copy(powerUpInventory = updated)
            return true
        }
        return false
    }

    fun consumePowerUp(powerUp: PowerUpType): Boolean {
        val current = _profile.value.powerUpInventory[powerUp] ?: 0
        if (current > 0) {
            val updated = _profile.value.powerUpInventory.toMutableMap()
            updated[powerUp] = current - 1
            prefs.edit().putInt("powerup_${powerUp.id}", current - 1).apply()
            _profile.value = _profile.value.copy(powerUpInventory = updated)
            return true
        }
        return false
    }

    fun recordGameFinished(
        score: Int,
        height: Int,
        perfectCount: Int,
        isTimeMode: Boolean,
        maxConsecutiveCombo: Int
    ) {
        var classicHigh = _profile.value.classicHighScore
        var classicHeight = _profile.value.classicBestHeight
        var timeHigh = _profile.value.timeRushHighScore

        val editor = prefs.edit()

        if (!isTimeMode) {
            if (score > classicHigh) {
                classicHigh = score
                editor.putInt("classic_high_score", classicHigh)
            }
            if (height > classicHeight) {
                classicHeight = height
                editor.putInt("classic_best_height", classicHeight)
            }
        } else {
            if (score > timeHigh) {
                timeHigh = score
                editor.putInt("time_rush_high_score", timeHigh)
            }
        }

        val totalDrops = _profile.value.totalLifetimeDrops + height
        val totalPerfect = _profile.value.totalPerfectDrops + perfectCount
        editor.putInt("total_lifetime_drops", totalDrops)
        editor.putInt("total_perfect_drops", totalPerfect)

        // Update Daily Challenge progress if applicable
        val currentDaily = _profile.value.dailyChallenge
        var newDailyProgress = currentDaily.currentValue
        when (currentDaily.type) {
            com.example.model.DailyObjectiveType.PERFECT_PLACEMENTS -> {
                newDailyProgress += perfectCount
            }
            com.example.model.DailyObjectiveType.TOWER_HEIGHT -> {
                newDailyProgress = maxOf(newDailyProgress, height)
            }
            com.example.model.DailyObjectiveType.TOTAL_SCORE -> {
                newDailyProgress = maxOf(newDailyProgress, score)
            }
            com.example.model.DailyObjectiveType.CONSECUTIVE_PERFECT -> {
                newDailyProgress = maxOf(newDailyProgress, maxConsecutiveCombo)
            }
        }

        editor.putString("daily_day_key", currentDaily.dayKey)
        editor.putInt("daily_progress", newDailyProgress)
        editor.apply()

        _profile.value = _profile.value.copy(
            classicHighScore = classicHigh,
            classicBestHeight = classicHeight,
            timeRushHighScore = timeHigh,
            totalLifetimeDrops = totalDrops,
            totalPerfectDrops = totalPerfect,
            dailyChallenge = currentDaily.copy(currentValue = newDailyProgress)
        )
    }

    fun claimDailyReward(): Boolean {
        val daily = _profile.value.dailyChallenge
        if (daily.isCompleted && !daily.isClaimed) {
            addCoins(daily.rewardCoins)
            prefs.edit().putBoolean("daily_claimed", true).apply()
            _profile.value = _profile.value.copy(
                dailyChallenge = daily.copy(isClaimed = true)
            )
            return true
        }
        return false
    }

    fun toggleSound(enabled: Boolean) {
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
        _profile.value = _profile.value.copy(soundEnabled = enabled)
    }

    fun toggleHaptics(enabled: Boolean) {
        prefs.edit().putBoolean("haptics_enabled", enabled).apply()
        _profile.value = _profile.value.copy(hapticsEnabled = enabled)
    }
}
