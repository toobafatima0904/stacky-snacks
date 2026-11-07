package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.game.GameViewModel
import com.example.ui.screens.GameScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val gameViewModel: GameViewModel = viewModel()
                var currentScreen by remember { mutableStateOf("game") }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFFFF8F0)
                ) {
                    when (currentScreen) {
                        "shop" -> {
                            val profile by gameViewModel.profile.collectAsState()
                            ShopScreen(
                                profile = profile,
                                repository = gameViewModel.repository,
                                onBack = { currentScreen = "game" }
                            )
                        }
                        else -> {
                            GameScreen(
                                viewModel = gameViewModel,
                                onOpenShop = { currentScreen = "shop" }
                            )
                        }
                    }
                }
            }
        }
    }
}
