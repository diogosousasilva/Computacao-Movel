package com.diogo.replog.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Navigation routes for RepLog app.
 */
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val WORKOUT_LOGGER = "workout_logger"
    const val PROGRESS = "progress"
    const val EXERCISE_DETAIL = "exercise_detail/{exerciseId}"
    const val FRIENDS = "friends"
    const val AI_COACH = "ai_coach"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
    const val HELP = "help"

    fun exerciseDetail(exerciseId: String) = "exercise_detail/$exerciseId"
}

/**
 * Bottom navigation items.
 */
enum class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME(Routes.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    LOG(Routes.WORKOUT_LOGGER, "Log", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter),
    PROGRESS(Routes.PROGRESS, "Progress", Icons.AutoMirrored.Filled.TrendingUp, Icons.AutoMirrored.Outlined.TrendingUp),
    FRIENDS(Routes.FRIENDS, "Friends", Icons.Filled.People, Icons.Outlined.People)
}
