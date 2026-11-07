package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameRepository
import com.example.data.UserGameProfile
import com.example.model.BackgroundTheme
import com.example.model.BasePlateTheme
import com.example.model.EffectTheme
import com.example.model.FoodType
import com.example.model.MusicTheme
import com.example.model.PowerUpType
import com.example.ui.components.FoodDrawables

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    profile: UserGameProfile,
    repository: GameRepository,
    onBack: () -> Unit
) {
    val tabs = listOf("Foods", "Plates", "Backdrops", "Effects", "Music", "Power-Ups")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SNACK SHOP",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = Color(0xFF2B2119)
                        )
                        // Coins pill
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFFF9C4),
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🪙 ${profile.coins}", fontWeight = FontWeight.Black, color = Color(0xFFF57F17), fontSize = 14.sp)
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("shop_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF2B2119))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFF8F0))
            )
        },
        containerColor = Color(0xFFFFF8F0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Category Tabs
            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = Color(0xFFFF5964),
                edgePadding = 12.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Black else FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTabIndex) {
                0 -> FoodShopList(profile = profile, repository = repository)
                1 -> PlateShopList(profile = profile, repository = repository)
                2 -> BackgroundShopList(profile = profile, repository = repository)
                3 -> EffectShopList(profile = profile, repository = repository)
                4 -> MusicShopList(profile = profile, repository = repository)
                5 -> PowerUpShopList(profile = profile, repository = repository)
            }
        }
    }
}

@Composable
private fun FoodShopList(profile: UserGameProfile, repository: GameRepository) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(FoodType.entries) { food ->
            val isUnlocked = profile.unlockedFoods.contains(food.id)
            val isEquipped = profile.selectedFood == food

            ShopCard(
                title = food.displayName,
                subtitle = food.description,
                cost = food.unlockCost,
                isUnlocked = isUnlocked,
                isEquipped = isEquipped,
                canAfford = profile.coins >= food.unlockCost,
                preview = {
                    Canvas(modifier = Modifier.size(72.dp, 52.dp)) {
                        FoodDrawables.drawFoodItem(
                            drawScope = this,
                            foodType = food,
                            centerX = size.width / 2f,
                            centerY = size.height / 2f,
                            width = size.width * 0.9f,
                            height = size.height * 0.8f
                        )
                    }
                },
                onAction = {
                    if (isUnlocked) {
                        repository.selectFood(food)
                    } else {
                        repository.unlockFood(food)
                    }
                }
            )
        }
    }
}

@Composable
private fun PlateShopList(profile: UserGameProfile, repository: GameRepository) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(BasePlateTheme.entries) { plate ->
            val isUnlocked = profile.unlockedPlates.contains(plate.id)
            val isEquipped = profile.selectedPlate == plate

            ShopCard(
                title = plate.displayName,
                subtitle = plate.description,
                cost = plate.cost,
                isUnlocked = isUnlocked,
                isEquipped = isEquipped,
                canAfford = profile.coins >= plate.cost,
                preview = {
                    Box(
                        modifier = Modifier
                            .size(64.dp, 36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(plate.rimColor)
                            .padding(3.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(plate.plateColor)
                    )
                },
                onAction = {
                    if (isUnlocked) {
                        repository.selectPlate(plate)
                    } else {
                        repository.unlockPlate(plate)
                    }
                }
            )
        }
    }
}

@Composable
private fun BackgroundShopList(profile: UserGameProfile, repository: GameRepository) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(BackgroundTheme.entries) { bg ->
            val isUnlocked = profile.unlockedBackgrounds.contains(bg.id)
            val isEquipped = profile.selectedBackground == bg

            ShopCard(
                title = bg.displayName,
                subtitle = bg.description,
                cost = bg.cost,
                isUnlocked = isUnlocked,
                isEquipped = isEquipped,
                canAfford = profile.coins >= bg.cost,
                preview = {
                    Box(
                        modifier = Modifier
                            .size(54.dp, 54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bg.topColor)
                            .border(2.dp, bg.bottomColor, RoundedCornerShape(12.dp))
                    )
                },
                onAction = {
                    if (isUnlocked) {
                        repository.selectBackground(bg)
                    } else {
                        repository.unlockBackground(bg)
                    }
                }
            )
        }
    }
}

@Composable
private fun EffectShopList(profile: UserGameProfile, repository: GameRepository) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(EffectTheme.entries) { eff ->
            val isUnlocked = profile.unlockedEffects.contains(eff.id)
            val isEquipped = profile.selectedEffect == eff

            ShopCard(
                title = eff.displayName,
                subtitle = eff.description,
                cost = eff.cost,
                isUnlocked = isUnlocked,
                isEquipped = isEquipped,
                canAfford = profile.coins >= eff.cost,
                preview = {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(16.dp).background(eff.primaryParticleColor, CircleShape))
                        Box(modifier = Modifier.size(16.dp).background(eff.secondaryParticleColor, CircleShape))
                    }
                },
                onAction = {
                    if (isUnlocked) {
                        repository.selectEffect(eff)
                    } else {
                        repository.unlockEffect(eff)
                    }
                }
            )
        }
    }
}

@Composable
private fun MusicShopList(profile: UserGameProfile, repository: GameRepository) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(MusicTheme.entries) { mus ->
            val isUnlocked = profile.unlockedMusic.contains(mus.id)
            val isEquipped = profile.selectedMusic == mus

            ShopCard(
                title = mus.displayName,
                subtitle = mus.description,
                cost = mus.cost,
                isUnlocked = isUnlocked,
                isEquipped = isEquipped,
                canAfford = profile.coins >= mus.cost,
                preview = {
                    Text(text = "🎵", fontSize = 28.sp)
                },
                onAction = {
                    if (isUnlocked) {
                        repository.selectMusic(mus)
                    } else {
                        repository.unlockMusic(mus)
                    }
                }
            )
        }
    }
}

@Composable
private fun PowerUpShopList(profile: UserGameProfile, repository: GameRepository) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(PowerUpType.entries) { powerUp ->
            val inventoryCount = profile.powerUpInventory[powerUp] ?: 0

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(powerUp.badgeColor.copy(alpha = 0.15f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (powerUp) {
                                PowerUpType.SLOW_MOTION -> "⏳"
                                PowerUpType.DOUBLE_COINS -> "🪙"
                                PowerUpType.PERFECT_DROP -> "🎯"
                                PowerUpType.SAVE_ME -> "👼"
                            },
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = powerUp.displayName,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = Color(0xFF2B2119)
                        )
                        Text(
                            text = powerUp.description,
                            fontSize = 12.sp,
                            color = Color(0xFF75685F),
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "In Inventory: x$inventoryCount",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = powerUp.badgeColor
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { repository.buyPowerUp(powerUp) },
                        enabled = profile.coins >= powerUp.cost,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                        modifier = Modifier.testTag("buy_powerup_${powerUp.id}")
                    ) {
                        Text(
                            text = "🪙 ${powerUp.cost}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF2B2119)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShopCard(
    title: String,
    subtitle: String,
    cost: Int,
    isUnlocked: Boolean,
    isEquipped: Boolean,
    canAfford: Boolean,
    preview: @Composable () -> Unit,
    onAction: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEquipped) Color(0xFFFFF1EB) else Color.White
        ),
        border = if (isEquipped) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFF5964)) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFFFF8F0), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                preview()
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = Color(0xFF2B2119)
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF75685F),
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            when {
                isEquipped -> {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFF5964)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "EQUIPPED", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
                isUnlocked -> {
                    Button(
                        onClick = onAction,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0))
                    ) {
                        Text(text = "EQUIP", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF2B2119))
                    }
                }
                else -> {
                    Button(
                        onClick = onAction,
                        enabled = canAfford,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD129))
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFF5D4037), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "🪙 $cost", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF5D4037))
                    }
                }
            }
        }
    }
}
