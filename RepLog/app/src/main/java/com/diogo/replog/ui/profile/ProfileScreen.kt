package com.diogo.replog.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import android.graphics.BitmapFactory
import android.util.Base64
import java.util.Locale
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import coil.compose.AsyncImage
import com.diogo.replog.R
import com.diogo.replog.ui.components.*
import com.diogo.replog.ui.theme.*
import com.diogo.replog.viewmodel.ProfileViewModel

import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onSignOut: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel(),
) {
    val context = LocalContext.current
    val state by profileViewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        LoadingIndicator()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Profile photo
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Blue40.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            val photoUrl = state.user?.photoUrl
            if (photoUrl != null) {
                if (photoUrl.startsWith("data:image")) {
                    val base64String = photoUrl.substringAfter(",")
                    val imageBitmap = remember(base64String) {
                        try {
                            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                            bitmap?.asImageBitmap()
                        } catch (_: Exception) {
                            null
                        }
                    }
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = "Profile photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Fallback fallback
                        Icon(
                            Icons.Filled.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = Blue40
                        )
                    }
                } else {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Profile photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                Text(
                    text = (state.user?.displayName ?: "?").take(1).uppercase(),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Blue40
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name & email
        Text(
            text = state.user?.displayName ?: "",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = state.user?.email ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // PRO badge
        if (state.user?.isPro == true) {
            Spacer(modifier = Modifier.height(8.dp))
            Badge(containerColor = Gold40) {
                Text(
                    " PRO ",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Navy5
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = state.totalWorkouts.toString(),
                    label = stringResource(R.string.workouts)
                )
                StatItem(
                    value = (state.user?.streakDays ?: 0).toString(),
                    label = stringResource(R.string.streak),
                    valueColor = Gold40
                )
                StatItem(
                    value = String.format(Locale.getDefault(), "%.0f", state.totalVolumeKg),
                    label = "kg",
                    valueColor = Green40
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Menu items
        ProfileMenuItem(
            icon = Icons.Filled.Settings,
            title = stringResource(R.string.settings),
            onClick = onNavigateToSettings
        )
        ProfileMenuItem(
            icon = Icons.Filled.Info,
            title = stringResource(R.string.about),
            onClick = onNavigateToAbout
        )
        ProfileMenuItem(
            icon = Icons.AutoMirrored.Filled.Help,
            title = stringResource(R.string.help),
            onClick = onNavigateToHelp
        )
        ProfileMenuItem(
            icon = Icons.Filled.RestartAlt,
            title = "Recalibrar Dados Biométricos",
            onClick = {
                profileViewModel.resetProfile(context) {
                    onNavigateToOnboarding()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sign out
        OutlinedButton(
            onClick = {
                profileViewModel.signOut()
                onSignOut()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = ErrorRed
            )
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.sign_out))
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
