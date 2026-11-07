package com.example.model

import androidx.compose.ui.graphics.Color

enum class FoodType(
    val id: String,
    val displayName: String,
    val description: String,
    val baseWidthDp: Float,
    val baseHeightDp: Float,
    val defaultUnlocked: Boolean,
    val unlockCost: Int,
    val themeColor: Color,
    val secondaryColor: Color
) {
    PANCAKES(
        id = "pancakes",
        displayName = "Fluffy Pancakes",
        description = "Stacked golden hotcakes dripping with rich maple syrup and butter.",
        baseWidthDp = 130f,
        baseHeightDp = 44f,
        defaultUnlocked = true,
        unlockCost = 0,
        themeColor = Color(0xFFFFB300),
        secondaryColor = Color(0xFFFFF176)
    ),
    DONUT(
        id = "donut",
        displayName = "Glazed Donut",
        description = "Fluffy ring pastry with glossy pink strawberry frosting and sprinkles.",
        baseWidthDp = 115f,
        baseHeightDp = 42f,
        defaultUnlocked = true,
        unlockCost = 0,
        themeColor = Color(0xFFFF4081),
        secondaryColor = Color(0xFFFF80AB)
    ),
    BURGER(
        id = "burger",
        displayName = "Juicy Burger",
        description = "Gourmet toasted bun with charbroiled patty, cheddar, tomato & lettuce.",
        baseWidthDp = 125f,
        baseHeightDp = 48f,
        defaultUnlocked = true,
        unlockCost = 0,
        themeColor = Color(0xFF8D6E63),
        secondaryColor = Color(0xFFFFCA28)
    ),
    CHOCOLATE_CAKE(
        id = "chocolate_cake",
        displayName = "Chocolate Cake",
        description = "Decadent dark cocoa cake layered with chocolate ganache and cherry.",
        baseWidthDp = 135f,
        baseHeightDp = 48f,
        defaultUnlocked = false,
        unlockCost = 50,
        themeColor = Color(0xFF4E342E),
        secondaryColor = Color(0xFFD7CCC8)
    ),
    STRAWBERRY_CAKE(
        id = "strawberry_cake",
        displayName = "Strawberry Shortcake",
        description = "Delicate sponge slice with sweet whipped cream and ruby strawberries.",
        baseWidthDp = 130f,
        baseHeightDp = 46f,
        defaultUnlocked = false,
        unlockCost = 75,
        themeColor = Color(0xFFFF5252),
        secondaryColor = Color(0xFFFFFFFF)
    ),
    PIZZA_SLICE(
        id = "pizza_slice",
        displayName = "Cheesy Pizza",
        description = "Crispy crust loaded with bubbling mozzarella and spicy pepperoni.",
        baseWidthDp = 120f,
        baseHeightDp = 44f,
        defaultUnlocked = false,
        unlockCost = 100,
        themeColor = Color(0xFFFF9800),
        secondaryColor = Color(0xFFE53935)
    ),
    CUPCAKE(
        id = "cupcake",
        displayName = "Vanilla Cupcake",
        description = "Sweet baked pastry in a ruffled cup with swirling buttercream swirl.",
        baseWidthDp = 100f,
        baseHeightDp = 46f,
        defaultUnlocked = false,
        unlockCost = 120,
        themeColor = Color(0xFFBA68C8),
        secondaryColor = Color(0xFFF06292)
    ),
    ICE_CREAM(
        id = "ice_cream",
        displayName = "Triple Ice Cream",
        description = "Three delicious scoops of mint, strawberry and vanilla in a crisp cone.",
        baseWidthDp = 110f,
        baseHeightDp = 50f,
        defaultUnlocked = false,
        unlockCost = 140,
        themeColor = Color(0xFF26A69A),
        secondaryColor = Color(0xFFFF4081)
    ),
    COOKIES(
        id = "cookies",
        displayName = "Choc Chip Cookie",
        description = "Warm golden brown chewy cookie packed with melting chocolate chunks.",
        baseWidthDp = 115f,
        baseHeightDp = 38f,
        defaultUnlocked = false,
        unlockCost = 160,
        themeColor = Color(0xFFA1887F),
        secondaryColor = Color(0xFF3E2723)
    ),
    SANDWICH(
        id = "sandwich",
        displayName = "Club Sandwich",
        description = "Triple-decker artisan sandwich with smoked deli ham, cheese and olives.",
        baseWidthDp = 125f,
        baseHeightDp = 46f,
        defaultUnlocked = false,
        unlockCost = 180,
        themeColor = Color(0xFF689F38),
        secondaryColor = Color(0xFFFFD54F)
    ),
    WATERMELON(
        id = "watermelon",
        displayName = "Watermelon Slice",
        description = "Cool refreshing melon wedge with sweet red fruit and dark seeds.",
        baseWidthDp = 120f,
        baseHeightDp = 42f,
        defaultUnlocked = false,
        unlockCost = 200,
        themeColor = Color(0xFFE91E63),
        secondaryColor = Color(0xFF4CAF50)
    ),
    WAFFLES(
        id = "waffles",
        displayName = "Belgian Waffle",
        description = "Crispy golden grid waffle loaded with fresh blueberries and syrup.",
        baseWidthDp = 125f,
        baseHeightDp = 42f,
        defaultUnlocked = false,
        unlockCost = 220,
        themeColor = Color(0xFFFFB300),
        secondaryColor = Color(0xFF3F51B5)
    );

    companion object {
        fun fromId(id: String): FoodType {
            return entries.firstOrNull { it.id == id } ?: PANCAKES
        }
    }
}
