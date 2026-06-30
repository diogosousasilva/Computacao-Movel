package com.diogo.replog.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.diogo.replog.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

import com.diogo.replog.data.preferences.OnboardingDataStore

/**
 * Splash screen with animated logo and tagline.
 * Auto-redirects based on auth state and onboarding completion.
 */
@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(800),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        isVisible = true
        delay(2000)
        try {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                // Check if this user has completed onboarding
                val onboardingDone = OnboardingDataStore.isOnboardingDoneSnapshot(context)
                if (onboardingDone) {
                    onNavigateToHome()
                } else {
                    onNavigateToOnboarding()
                }
            } else {
                onNavigateToLogin()
            }
        } catch (e: Exception) {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Navy5, Navy10, Navy20)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale)
        ) {
            // App icon
            Icon(
                imageVector = Icons.Filled.FitnessCenter,
                contentDescription = "RepLog",
                modifier = Modifier.size(80.dp),
                tint = Blue40
            )

            Spacer(modifier = Modifier.height(16.dp))

            // App name
            Text(
                text = "RepLog",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = alpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(1200, delayMillis = 400))
            ) {
                Text(
                    text = "Track. Progress. Compete.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
