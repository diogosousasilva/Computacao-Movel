package com.diogo.replog.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.diogo.replog.data.cache.AppDataCache
import com.diogo.replog.ui.auth.LoginScreen
import com.diogo.replog.ui.auth.RegisterScreen
import com.diogo.replog.ui.auth.SplashScreen
import com.diogo.replog.ui.coach.AiCoachScreen
import com.diogo.replog.ui.home.HomeScreen
import com.diogo.replog.ui.onboarding.OnboardingScreen
import com.diogo.replog.ui.profile.AboutScreen
import com.diogo.replog.ui.profile.HelpScreen
import com.diogo.replog.ui.profile.ProfileScreen
import com.diogo.replog.ui.profile.SettingsScreen
import com.diogo.replog.ui.progress.ProgressScreen
import com.diogo.replog.ui.social.FriendsScreen
import com.diogo.replog.ui.workout.WorkoutLoggerScreen

/**
 * Main navigation graph for RepLog.
 */
@Composable
fun RepLogNavGraph(
    navController: NavHostController,
    startDestination: String = Routes.SPLASH,
    paddingValues: PaddingValues = PaddingValues(),
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300),
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300, delayMillis = 0))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(300),
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300))
        },
    ) {
        // Auth flow
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(Routes.ONBOARDING) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.ONBOARDING) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.ONBOARDING) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
            )
        }

        // Main tabs
        composable(Routes.HOME) {
            // Kick off background pre-fetch as soon as the user is authenticated
            LaunchedEffect(Unit) { AppDataCache.prefetchAll() }
            HomeScreen(
                onNavigateToWorkout = { navController.navigate(Routes.WORKOUT_LOGGER) },
                onNavigateToCoach = { navController.navigate(Routes.AI_COACH) },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                scaffoldPadding = paddingValues,
            )
        }

        composable(Routes.WORKOUT_LOGGER) {
            WorkoutLoggerScreen(
                onWorkoutFinished = { navController.popBackStack() },
            )
        }

        composable(Routes.PROGRESS) {
            val context = LocalContext.current
            val activity = remember(context) { context as ComponentActivity }
            val progressViewModel: com.diogo.replog.viewmodel.ProgressViewModel = androidx.lifecycle.viewmodel.compose.viewModel(activity)
            ProgressScreen(
                progressViewModel = progressViewModel,
                paddingValues = paddingValues,
            )
        }

        composable(Routes.FRIENDS) {
            FriendsScreen(paddingValues = paddingValues)
        }

        composable(Routes.AI_COACH) {
            AiCoachScreen()
        }

        // Profile section
        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                onNavigateToAbout = { navController.navigate(Routes.ABOUT) },
                onNavigateToHelp = { navController.navigate(Routes.HELP) },
                onNavigateToOnboarding = {
                    navController.navigate(Routes.ONBOARDING) {
                        popUpTo(Routes.PROFILE) { inclusive = true }
                    }
                },
                onSignOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen { navController.popBackStack() }
        }

        composable(Routes.ABOUT) {
            AboutScreen { navController.popBackStack() }
        }

        composable(Routes.HELP) {
            HelpScreen { navController.popBackStack() }
        }
    }
}
