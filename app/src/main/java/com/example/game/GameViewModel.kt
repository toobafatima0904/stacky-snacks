package com.example.game

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundEffectManager
import com.example.data.GameRepository
import com.example.data.UserGameProfile
import com.example.model.DailyObjectiveType
import com.example.model.FoodType
import com.example.model.GameMode
import com.example.model.PowerUpType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val repository = GameRepository(application)
    val soundManager = SoundEffectManager(application)

    val profile: StateFlow<UserGameProfile> = repository.profile

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var gameLoopJob: Job? = null
    private var nextEntityId: Long = 1000L

    init {
        // Sync audio settings with profile
        viewModelScope.launch {
            repository.profile.collect { prof ->
                soundManager.isSoundEnabled = prof.soundEnabled
                soundManager.isHapticsEnabled = prof.hapticsEnabled
                soundManager.currentMusicTheme = prof.selectedMusic
            }
        }
    }

    fun startGame(mode: GameMode) {
        val prof = repository.profile.value
        val hasSave = (prof.powerUpInventory[PowerUpType.SAVE_ME] ?: 0) > 0

        _gameState.value = GameState(
            status = GameStatus.PLAYING,
            gameMode = mode,
            score = 0,
            heightCount = 0,
            comboMultiplier = 1,
            consecutivePerfectCount = 0,
            maxConsecutiveCombo = 0,
            totalPerfectDropsInRound = 0,
            coinsEarnedInRound = 0,
            remainingTimeSeconds = if (mode == GameMode.TIME_CHALLENGE) 60f else 0f,
            hasSaveMeAvailable = hasSave,
            isDoubleCoinsActive = false,
            isSlowMotionActive = false,
            isPerfectDropArmed = false
        )

        spawnNextSwingingItem()
        startGameLoop()
    }

    fun resumeGame() {
        if (_gameState.value.status == GameStatus.PAUSED) {
            _gameState.value = _gameState.value.copy(status = GameStatus.PLAYING)
            startGameLoop()
        }
    }

    fun pauseGame() {
        if (_gameState.value.status == GameStatus.PLAYING) {
            _gameState.value = _gameState.value.copy(status = GameStatus.PAUSED)
            gameLoopJob?.cancel()
        }
    }

    fun exitToMenu() {
        gameLoopJob?.cancel()
        _gameState.value = _gameState.value.copy(status = GameStatus.MENU)
    }

    fun onDropTapped() {
        val state = _gameState.value
        if (state.status != GameStatus.PLAYING) return
        val item = state.swingingItem ?: return
        if (item.isDropping) return

        soundManager.playDrop()

        val adjustedX = if (state.isPerfectDropArmed) {
            // Auto align with top of stack
            if (state.stack.isEmpty()) 0f else state.stack.last().x
        } else {
            item.x
        }

        _gameState.value = state.copy(
            isPerfectDropArmed = false,
            swingingItem = item.copy(
                x = adjustedX,
                isDropping = true,
                dropY = 0f,
                dropVy = 750f
            )
        )
    }

    fun activatePowerUp(powerUp: PowerUpType) {
        val prof = repository.profile.value
        val state = _gameState.value
        if (state.status != GameStatus.PLAYING) return

        if ((prof.powerUpInventory[powerUp] ?: 0) > 0) {
            if (repository.consumePowerUp(powerUp)) {
                soundManager.playPowerUp()
                when (powerUp) {
                    PowerUpType.SLOW_MOTION -> {
                        _gameState.value = state.copy(
                            isSlowMotionActive = true,
                            slowMotionRemainingSeconds = 8f
                        )
                        addPopup("SLOW MOTION!", "50% Speed", Color(0xFF29B6F6))
                    }
                    PowerUpType.DOUBLE_COINS -> {
                        _gameState.value = state.copy(isDoubleCoinsActive = true)
                        addPopup("2X COINS!", "Double Rewards", Color(0xFFFFD54F))
                    }
                    PowerUpType.PERFECT_DROP -> {
                        _gameState.value = state.copy(isPerfectDropArmed = true)
                        addPopup("PERFECT DROP!", "Target Locked", Color(0xFF66BB6A))
                    }
                    PowerUpType.SAVE_ME -> {
                        _gameState.value = state.copy(hasSaveMeAvailable = true)
                        addPopup("SAVE ME READY!", "Protected", Color(0xFFFF7043))
                    }
                }
            }
        }
    }

    private fun spawnNextSwingingItem() {
        val prof = repository.profile.value
        val unlockedFoodList = FoodType.entries.filter { prof.unlockedFoods.contains(it.id) }
        val food = if (unlockedFoodList.isNotEmpty() && Random.nextFloat() > 0.35f) {
            unlockedFoodList.random()
        } else {
            prof.selectedFood
        }

        val baseSpeed = 260f + minOf(_gameState.value.heightCount * 5f, 240f)
        val initialDir = if (Random.nextBoolean()) 1f else -1f

        _gameState.value = _gameState.value.copy(
            swingingItem = SwingingItem(
                foodType = food,
                x = 0f,
                vx = baseSpeed * initialDir,
                width = food.baseWidthDp,
                height = food.baseHeightDp,
                isDropping = false,
                dropY = 0f,
                dropVy = 0f
            )
        )
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            var lastTime = System.nanoTime()
            while (isActive && _gameState.value.status == GameStatus.PLAYING) {
                val now = System.nanoTime()
                val dt = ((now - lastTime) / 1_000_000_000.0).toFloat().coerceIn(0.001f, 0.05f)
                lastTime = now

                updateGamePhysics(dt)
                delay(16) // ~60 FPS
            }
        }
    }

    private fun updateGamePhysics(dt: Float) {
        var state = _gameState.value
        if (state.status != GameStatus.PLAYING) return

        // 1. Time mode countdown
        var newRemainingTime = state.remainingTimeSeconds
        if (state.gameMode == GameMode.TIME_CHALLENGE) {
            newRemainingTime -= dt
            if (newRemainingTime <= 0f) {
                newRemainingTime = 0f
                endGame(reason = "Time's Up!")
                return
            }
        }

        // 2. Powerups decay
        var slowMoActive = state.isSlowMotionActive
        var slowMoTime = state.slowMotionRemainingSeconds
        if (slowMoActive) {
            slowMoTime -= dt
            if (slowMoTime <= 0f) {
                slowMoActive = false
                slowMoTime = 0f
            }
        }

        // 3. Popups & particles decay
        val updatedPopups = state.popups.mapNotNull { p ->
            val newLife = p.lifetime - dt * 1.2f
            if (newLife <= 0f) null
            else p.copy(
                y = p.y - 45f * dt,
                alpha = (newLife).coerceIn(0f, 1f),
                scale = 1.0f + (1f - newLife) * 0.3f,
                lifetime = newLife
            )
        }

        val updatedParticles = state.particles.mapNotNull { part ->
            val newAlpha = part.alpha - dt * 1.5f
            if (newAlpha <= 0f) null
            else part.copy(
                x = part.x + part.vx * dt,
                y = part.y + part.vy * dt,
                vy = part.vy + 300f * dt, // gravity
                alpha = newAlpha
            )
        }

        // 4. Swinging / Falling Snack
        var currentSwinging = state.swingingItem
        val topItem = state.stack.lastOrNull()
        val topY = topItem?.let { it.y + it.height } ?: 0f
        val topX = topItem?.x ?: 0f

        if (currentSwinging != null) {
            if (!currentSwinging.isDropping) {
                // Horizontal motion
                val speedMultiplier = if (slowMoActive) 0.5f else 1.0f
                var newX = currentSwinging.x + currentSwinging.vx * speedMultiplier * dt
                var newVx = currentSwinging.vx

                val bound = 160f // swing bounds in dp
                if (newX > bound) {
                    newX = bound
                    newVx = -abs(newVx)
                } else if (newX < -bound) {
                    newX = -bound
                    newVx = abs(newVx)
                }

                currentSwinging = currentSwinging.copy(x = newX, vx = newVx)
            } else {
                // Falling motion
                val newVy = currentSwinging.dropVy + 2400f * dt
                val newDropY = currentSwinging.dropY + newVy * dt

                // Target landing drop height (relative from drop start down to top of tower)
                val dropDistanceTarget = 240f // Lane height

                if (newDropY >= dropDistanceTarget) {
                    // Collision check
                    val diff = currentSwinging.x - topX
                    val overlapLimit = currentSwinging.width * 0.75f
                    val perfectThreshold = 14f
                    val goodThreshold = currentSwinging.width * 0.35f

                    if (abs(diff) <= overlapLimit) {
                        // Successful placement!
                        val isPerfect = abs(diff) <= perfectThreshold
                        val isGood = !isPerfect && abs(diff) <= goodThreshold

                        var newCombo = if (isPerfect) state.comboMultiplier + 1 else 1
                        val newConsecutive = if (isPerfect) state.consecutivePerfectCount + 1 else 0
                        val maxCombo = maxOf(state.maxConsecutiveCombo, newConsecutive)
                        val totalPerfect = state.totalPerfectDropsInRound + (if (isPerfect) 1 else 0)

                        val points = when {
                            isPerfect -> 5 * (if (newCombo > 1) newCombo - 1 else 1)
                            isGood -> 3
                            else -> 1
                        }

                        val baseCoins = when {
                            isPerfect -> 4 * (if (newCombo > 1) 2 else 1)
                            isGood -> 2
                            else -> 1
                        }
                        val coinMultiplier = if (state.isDoubleCoinsActive) 2 else 1
                        val coinsEarned = baseCoins * coinMultiplier

                        // Audio & Haptics
                        if (isPerfect) {
                            soundManager.playPerfect(newCombo)
                            spawnParticles(currentSwinging.x, topY + currentSwinging.height / 2f, 25, isRainbow = true)
                            addPopup("+${points} PERFECT!", "x$newCombo Combo!", Color(0xFFFF3366))
                        } else if (isGood) {
                            soundManager.playLandingGood()
                            spawnParticles(currentSwinging.x, topY + currentSwinging.height / 2f, 12, isRainbow = false)
                            addPopup("+3 GOOD", null, Color(0xFFFFB300))
                        } else {
                            soundManager.playLandingNormal()
                            spawnParticles(currentSwinging.x, topY + currentSwinging.height / 2f, 8, isRainbow = false)
                            addPopup("+1 NORMAL", null, Color(0xFF8D6E63))
                        }

                        soundManager.playCoin()

                        // Stability update
                        var newImbalance = state.cumulativeImbalance
                        if (isPerfect) {
                            newImbalance *= 0.5f // Stabilizes tower
                        } else if (isGood) {
                            newImbalance += diff * 0.08f
                        } else {
                            newImbalance += diff * 0.22f
                        }

                        // Check collapse
                        if (abs(newImbalance) > 130f) {
                            soundManager.playGameOver()
                            endGame(reason = "Tower Collapsed! Too Wobbly!")
                            return
                        } else if (abs(newImbalance) > 80f) {
                            soundManager.playWobble()
                        }

                        val landedItem = StackedItem(
                            id = nextEntityId++,
                            foodType = currentSwinging.foodType,
                            x = if (isPerfect) topX else currentSwinging.x,
                            y = topY,
                            width = currentSwinging.width,
                            height = currentSwinging.height,
                            squashY = 0.72f,
                            isPerfect = isPerfect
                        )

                        val newHeight = state.heightCount + 1
                        val newScore = state.score + points
                        val totalCoins = state.coinsEarnedInRound + coinsEarned
                        repository.addCoins(coinsEarned)

                        _gameState.value = state.copy(
                            score = newScore,
                            heightCount = newHeight,
                            comboMultiplier = newCombo,
                            consecutivePerfectCount = newConsecutive,
                            maxConsecutiveCombo = maxCombo,
                            totalPerfectDropsInRound = totalPerfect,
                            coinsEarnedInRound = totalCoins,
                            remainingTimeSeconds = newRemainingTime,
                            cumulativeImbalance = newImbalance,
                            towerWobbleAngleDeg = (newImbalance * 0.12f).coerceIn(-18f, 18f),
                            cameraTargetY = (newHeight * 32f).coerceAtLeast(0f),
                            stack = state.stack + landedItem,
                            swingingItem = null,
                            isSlowMotionActive = slowMoActive,
                            slowMotionRemainingSeconds = slowMoTime
                        )

                        spawnNextSwingingItem()
                        return
                    } else {
                        // Missed completely!
                        if (state.hasSaveMeAvailable) {
                            // Save Me activates!
                            soundManager.playSaveMe()
                            repository.consumePowerUp(PowerUpType.SAVE_ME)
                            addPopup("SAVE ME! 👼", "Saved by golden spatula!", Color(0xFFFF7043))

                            val savedItem = StackedItem(
                                id = nextEntityId++,
                                foodType = currentSwinging.foodType,
                                x = topX,
                                y = topY,
                                width = currentSwinging.width,
                                height = currentSwinging.height,
                                squashY = 0.65f,
                                isPerfect = false
                            )

                            _gameState.value = state.copy(
                                score = state.score + 1,
                                heightCount = state.heightCount + 1,
                                hasSaveMeAvailable = false,
                                cameraTargetY = ((state.heightCount + 1) * 32f).coerceAtLeast(0f),
                                stack = state.stack + savedItem,
                                swingingItem = null
                            )

                            spawnNextSwingingItem()
                            return
                        } else {
                            soundManager.playGameOver()
                            endGame(reason = "Missed the tower!")
                            return
                        }
                    }
                } else {
                    currentSwinging = currentSwinging.copy(dropY = newDropY, dropVy = newVy)
                }
            }
        }

        // Camera follow lerp
        val currentCamY = state.cameraCurrentY
        val targetCamY = state.cameraTargetY
        val newCamY = currentCamY + (targetCamY - currentCamY) * 0.1f

        // Stack items squash recover
        val updatedStack = state.stack.map { item ->
            if (item.squashY < 1.0f) {
                item.copy(squashY = minOf(1.0f, item.squashY + dt * 4f))
            } else item
        }

        // Dynamic slight tower wobble oscillation
        val wobbleTime = (System.currentTimeMillis() % 10000) / 1000f
        val dynamicWobble = (state.cumulativeImbalance * 0.1f) + sin(wobbleTime * 3.5f) * (abs(state.cumulativeImbalance) * 0.05f)

        _gameState.value = state.copy(
            remainingTimeSeconds = newRemainingTime,
            isSlowMotionActive = slowMoActive,
            slowMotionRemainingSeconds = slowMoTime,
            swingingItem = currentSwinging,
            stack = updatedStack,
            popups = updatedPopups,
            particles = updatedParticles,
            cameraCurrentY = newCamY,
            towerWobbleAngleDeg = dynamicWobble.coerceIn(-18f, 18f)
        )
    }

    private fun addPopup(text: String, subText: String?, color: Color) {
        val newPopup = FloatingPopup(
            id = nextEntityId++,
            text = text,
            subText = subText,
            x = 0f,
            y = 100f,
            color = color,
            alpha = 1f,
            scale = 1f,
            lifetime = 1f
        )
        _gameState.value = _gameState.value.copy(
            popups = _gameState.value.popups + newPopup
        )
    }

    private fun spawnParticles(originX: Float, originY: Float, count: Int, isRainbow: Boolean) {
        val rainbowColors = listOf(
            Color(0xFFFF3366), Color(0xFFFFD129), Color(0xFF00E676), Color(0xFF29B6F6), Color(0xFFE040FB)
        )
        val selectedEffect = repository.profile.value.selectedEffect

        val newParticles = (0 until count).map {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = 120f + Random.nextFloat() * 260f
            val col = if (isRainbow) rainbowColors.random() else {
                if (Random.nextBoolean()) selectedEffect.primaryParticleColor else selectedEffect.secondaryParticleColor
            }
            GameParticle(
                x = originX + (Random.nextFloat() - 0.5f) * 40f,
                y = originY + (Random.nextFloat() - 0.5f) * 20f,
                vx = cos(angle) * speed,
                vy = sin(angle) * speed - 120f,
                color = col,
                size = 6f + Random.nextFloat() * 8f,
                alpha = 1f,
                shape = if (selectedEffect.id == "heart_pops") 2 else if (isRainbow) 1 else 0
            )
        }

        _gameState.value = _gameState.value.copy(
            particles = _gameState.value.particles + newParticles
        )
    }

    private fun endGame(reason: String) {
        gameLoopJob?.cancel()
        val s = _gameState.value

        repository.recordGameFinished(
            score = s.score,
            height = s.heightCount,
            perfectCount = s.totalPerfectDropsInRound,
            isTimeMode = s.gameMode == GameMode.TIME_CHALLENGE,
            maxConsecutiveCombo = s.maxConsecutiveCombo
        )

        _gameState.value = s.copy(
            status = GameStatus.GAME_OVER,
            popups = s.popups + FloatingPopup(
                id = nextEntityId++,
                text = reason,
                subText = "Final Score: ${s.score}",
                x = 0f,
                y = 120f,
                color = Color(0xFFFF5252),
                alpha = 1f,
                scale = 1.2f,
                lifetime = 3f
            )
        )
    }
}
