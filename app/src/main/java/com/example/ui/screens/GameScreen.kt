package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameState
import com.example.game.GameStatus
import com.example.game.GameViewModel
import com.example.model.GameMode
import com.example.ui.components.DailyChallengeDialog
import com.example.ui.components.GameHud
import com.example.ui.components.GameOverDialog
import com.example.ui.components.PauseDialog
import com.example.ui.components.PowerUpToolbar
import com.example.ui.components.SettingsDialog
import com.example.ui.components.TowerCanvas

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onOpenShop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.gameState.collectAsState()
    val profile by viewModel.profile.collectAsState()

    var showDailyDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // System Back Navigation
    BackHandler {
        when (gameState.status) {
            GameStatus.PLAYING -> viewModel.pauseGame()
            GameStatus.PAUSED -> viewModel.exitToMenu()
            GameStatus.GAME_OVER -> viewModel.exitToMenu()
            GameStatus.MENU -> { /* Let activity finish */ }
        }
    }

    if (gameState.status == GameStatus.MENU) {
        MainMenuScreen(
            profile = profile,
            onStartGame = { mode -> viewModel.startGame(mode) },
            onOpenShop = onOpenShop,
            onOpenDaily = { showDailyDialog = true },
            onOpenSettings = { showSettingsDialog = true }
        )
    } else {
        // Active Game Arena
        Box(modifier = modifier.fillMaxSize()) {
            // Interactive Physics Canvas
            TowerCanvas(
                gameState = gameState,
                backgroundTheme = profile.selectedBackground,
                plateTheme = profile.selectedPlate,
                onTapToDrop = { viewModel.onDropTapped() }
            )

            // Top HUD
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                GameHud(
                    gameState = gameState,
                    totalCoins = profile.coins,
                    powerUpInventory = profile.powerUpInventory,
                    onPauseClick = { viewModel.pauseGame() },
                    onPowerUpClick = { powerUp -> viewModel.activatePowerUp(powerUp) },
                    onDropClick = { viewModel.onDropTapped() }
                )

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Controls & Big Drop Button
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Quick Power-ups Toolbar
                    PowerUpToolbar(
                        gameState = gameState,
                        inventory = profile.powerUpInventory,
                        onPowerUpClick = { powerUp -> viewModel.activatePowerUp(powerUp) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Prominent Drop Button
                    Button(
                        onClick = { viewModel.onDropTapped() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5964)),
                        shape = RoundedCornerShape(22.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(56.dp)
                            .border(2.5.dp, Color(0xFFFFD129), RoundedCornerShape(22.dp))
                            .testTag("drop_button")
                    ) {
                        Text(
                            text = "🥞 TAP TO DROP!",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // Modal Dialogs
    when (gameState.status) {
        GameStatus.PAUSED -> {
            PauseDialog(
                soundEnabled = profile.soundEnabled,
                onToggleSound = { viewModel.repository.toggleSound(it) },
                onResume = { viewModel.resumeGame() },
                onRestart = { viewModel.startGame(gameState.gameMode) },
                onMenu = { viewModel.exitToMenu() }
            )
        }
        GameStatus.GAME_OVER -> {
            GameOverDialog(
                gameState = gameState,
                highScore = if (gameState.gameMode == GameMode.TIME_CHALLENGE) profile.timeRushHighScore else profile.classicHighScore,
                onPlayAgain = { viewModel.startGame(gameState.gameMode) },
                onGoToMenu = { viewModel.exitToMenu() },
                onOpenShop = onOpenShop
            )
        }
        else -> {}
    }

    if (showDailyDialog) {
        DailyChallengeDialog(
            challenge = profile.dailyChallenge,
            onClaimReward = { viewModel.repository.claimDailyReward() },
            onDismiss = { showDailyDialog = false }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            soundEnabled = profile.soundEnabled,
            hapticsEnabled = profile.hapticsEnabled,
            onToggleSound = { viewModel.repository.toggleSound(it) },
            onToggleHaptics = { viewModel.repository.toggleHaptics(it) },
            onDismiss = { showSettingsDialog = false }
        )
    }
}
