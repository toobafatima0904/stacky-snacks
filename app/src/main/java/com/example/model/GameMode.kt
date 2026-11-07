package com.example.model

enum class GameMode(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconName: String
) {
    CLASSIC(
        id = "classic",
        title = "Classic Tower",
        subtitle = "Endless stacking! One fatal miss and your tower topples.",
        iconName = "layers"
    ),
    TIME_CHALLENGE(
        id = "time_challenge",
        title = "Time Rush (60s)",
        subtitle = "Speed stack! Build as high as you can before time expires.",
        iconName = "timer"
    ),
    DAILY_CHALLENGE(
        id = "daily_challenge",
        title = "Daily Mission",
        subtitle = "Complete today's challenge to win juicy coin rewards!",
        iconName = "star"
    )
}
