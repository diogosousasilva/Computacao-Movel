package com.topodroid.couroutines2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.topodroid.couroutines2.ui.theme.Couroutines2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Couroutines2Theme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(onNavigateToDetail = {
                                navController.navigate("detail")
                            })
                        }
                        composable("detail") {
                            DetailScreen(onBack = {
                                navController.popBackStack()
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onNavigateToDetail: () -> Unit, mainViewModel: MainViewModel = viewModel()) {
    // Observing timerValue from MainViewModel
    val timerValue by mainViewModel.timerValue.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Home Screen",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Timer: $timerValue",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "This timer runs in MainViewModel's viewModelScope. It persists even if you navigate to Detail and back, as the MainViewModel is tied to the Activity's lifecycle.",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = onNavigateToDetail) {
            Text("Go to Detail Screen")
        }
    }
}

@Composable
fun DetailScreen(onBack: () -> Unit, detailViewModel: DetailViewModel = viewModel()) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Detail Screen Active",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Check Logcat (filter: 'CoroutineDemo'). You will see two logs running.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(text = "1. ✅ viewModelScope", fontWeight = FontWeight.Bold)
        Text(text = "2. ⚠️ GlobalScope", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Click 'Go Back'. The viewModelScope will stop because the DetailViewModel is cleared. The GlobalScope will continue running (LEAKING!).",
            style = MaterialTheme.typography.bodySmall
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = onBack) {
            Text("Go Back & Close ViewModel")
        }
    }
}
