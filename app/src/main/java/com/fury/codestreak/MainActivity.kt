package com.fury.codestreak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fury.codestreak.presentation.theme.BackgroundDark
import com.fury.codestreak.presentation.theme.CodeStreakTheme
import dagger.hilt.android.AndroidEntryPoint
import com.fury.codestreak.presentation.auth.AuthScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CodeStreakTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = BackgroundDark) {

                    // Simple Navigation for Testing
                    var currentScreen by remember { mutableStateOf("home") }

                    if (currentScreen == "auth") {
                        // Pass a lambda to switch screens on success
                        com.fury.codestreak.presentation.auth.AuthScreen()
                        // NOTE: In a real app, we observe the ViewModel event.
                        // For now, to SEE the dashboard, change "auth" to "home" in the line above:
                        // var currentScreen by remember { mutableStateOf("home") }
                    } else {
                        com.fury.codestreak.presentation.home.HomeScreen(
                            onNavigateToWorkspace = { /* Navigate to workspace */ }
                        )
                    }
                }
            }
        }
    }
}