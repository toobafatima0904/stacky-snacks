package com.example.model

import androidx.compose.ui.graphics.Color

enum class ShopCategory {
    FOOD,
    BASE_PLATE,
    BACKGROUND,
    EFFECTS,
    MUSIC,
    POWER_UPS
}

enum class BasePlateTheme(
    val id: String,
    val displayName: String,
    val description: String,
    val cost: Int,
    val plateColor: Color,
    val rimColor: Color,
    val standColor: Color
) {
    CLASSIC_CERAMIC(
        id = "classic_ceramic",
        displayName = "Ceramic Diner Plate",
        description = "Crisp white porcelain platter with a retro cherry rim.",
        cost = 0,
        plateColor = Color(0xFFFFFFFF),
        rimColor = Color(0xFFFF5252),
        standColor = Color(0xFFE0E0E0)
    ),
    GOLDEN_BANQUET(
        id = "golden_banquet",
        displayName = "Golden Royal Platter",
        description = "Polished 24k gold platter fit for a royal pastry master.",
        cost = 80,
        plateColor = Color(0xFFFFE082),
        rimColor = Color(0xFFFFB300),
        standColor = Color(0xFFFFA000)
    ),
    KAWAII_PASTEL(
        id = "kawaii_pastel",
        displayName = "Kawaii Cloud Dish",
        description = "Fluffy pastel pink and mint dish with sweet cloud rim.",
        cost = 120,
        plateColor = Color(0xFFF8BBD0),
        rimColor = Color(0xFF80CBC4),
        standColor = Color(0xFFCE93D8)
    ),
    NEON_CYBER(
        id = "neon_cyber",
        displayName = "Cyber Neon Grid",
        description = "Sleek dark plate with pulsating electric cyan glow.",
        cost = 160,
        plateColor = Color(0xFF263238),
        rimColor = Color(0xFF00E5FF),
        standColor = Color(0xFF7C4DFF)
    );

    companion object {
        fun fromId(id: String): BasePlateTheme = entries.firstOrNull { it.id == id } ?: CLASSIC_CERAMIC
    }
}

enum class BackgroundTheme(
    val id: String,
    val displayName: String,
    val description: String,
    val cost: Int,
    val topColor: Color,
    val bottomColor: Color,
    val accentColor: Color
) {
    SUNNY_BAKERY(
        id = "sunny_bakery",
        displayName = "Sunny Bakery",
        description = "Warm cozy kitchen with sunny morning pastel tones.",
        cost = 0,
        topColor = Color(0xFFFFF3E0),
        bottomColor = Color(0xFFFFE0B2),
        accentColor = Color(0xFFFFCC80)
    ),
    CANDY_SKY(
        id = "candy_sky",
        displayName = "Candy Kingdom Sky",
        description = "Cotton candy clouds drifting in a sunset lilac atmosphere.",
        cost = 70,
        topColor = Color(0xFFF8BBD0),
        bottomColor = Color(0xFFE1BEE7),
        accentColor = Color(0xFFFF80AB)
    ),
    NEON_ARCADE(
        id = "neon_arcade",
        displayName = "Neon Arcade Diner",
        description = "Vibrant retro neon gradients under arcade spotlights.",
        cost = 110,
        topColor = Color(0xFF1A1A2E),
        bottomColor = Color(0xFF16213E),
        accentColor = Color(0xFFE94560)
    ),
    COSMIC_DESSERT(
        id = "cosmic_dessert",
        displayName = "Cosmic Sweet Orbit",
        description = "Deep celestial space with sparkling star-sprinkles.",
        cost = 150,
        topColor = Color(0xFF0D1B2A),
        bottomColor = Color(0xFF415A77),
        accentColor = Color(0xFF00F5D4)
    );

    companion object {
        fun fromId(id: String): BackgroundTheme = entries.firstOrNull { it.id == id } ?: SUNNY_BAKERY
    }
}

enum class EffectTheme(
    val id: String,
    val displayName: String,
    val description: String,
    val cost: Int,
    val primaryParticleColor: Color,
    val secondaryParticleColor: Color
) {
    SWEET_CRUMBS(
        id = "sweet_crumbs",
        displayName = "Sweet Crumbs",
        description = "Tasty biscuit and sugar crumbs burst on impact.",
        cost = 0,
        primaryParticleColor = Color(0xFFFFCA28),
        secondaryParticleColor = Color(0xFF8D6E63)
    ),
    RAINBOW_SPARKLES(
        id = "rainbow_sparkles",
        displayName = "Rainbow Sparkles",
        description = "Dazzling rainbow prismatic stars shimmer on drops.",
        cost = 60,
        primaryParticleColor = Color(0xFFFF4081),
        secondaryParticleColor = Color(0xFF00E5FF)
    ),
    GOLDEN_STARDUST(
        id = "golden_stardust",
        displayName = "Golden Stardust",
        description = "Glittering golden coins and prestige fireworks.",
        cost = 100,
        primaryParticleColor = Color(0xFFFFD700),
        secondaryParticleColor = Color(0xFFFFAB00)
    ),
    HEART_POPS(
        id = "heart_pops",
        displayName = "Sweet Hearts",
        description = "Adorable pink and crimson hearts burst with delight.",
        cost = 130,
        primaryParticleColor = Color(0xFFFF1744),
        secondaryParticleColor = Color(0xFFFF80AB)
    );

    companion object {
        fun fromId(id: String): EffectTheme = entries.firstOrNull { it.id == id } ?: SWEET_CRUMBS
    }
}

enum class MusicTheme(
    val id: String,
    val displayName: String,
    val description: String,
    val cost: Int,
    val baseFrequency: Float
) {
    CHIME_ARCADE(
        id = "chime_arcade",
        displayName = "Chime Arcade",
        description = "Bouncy bright melodic arpeggios that rise with combos.",
        cost = 0,
        baseFrequency = 440f
    ),
    RETRO_8BIT(
        id = "retro_8bit",
        displayName = "Retro 8-Bit Chiptune",
        description = "Crisp vintage square-wave melodies from classic handhelds.",
        cost = 50,
        baseFrequency = 523.25f
    ),
    LOFI_BAKERY(
        id = "lofi_bakery",
        displayName = "Lofi Bakery Chords",
        description = "Warm mellow Rhodes-like sine waves for relaxed stacking.",
        cost = 80,
        baseFrequency = 392f
    );

    companion object {
        fun fromId(id: String): MusicTheme = entries.firstOrNull { it.id == id } ?: CHIME_ARCADE
    }
}
