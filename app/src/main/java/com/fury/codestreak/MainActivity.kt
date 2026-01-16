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

                    // Simple State-based Navigation
                    var currentScreen by remember { mutableStateOf("home") } // Start at Home for now

                    when (currentScreen) {
                        "auth" -> {
                            com.fury.codestreak.presentation.auth.AuthScreen()
                        }
                        "home" -> {
                            com.fury.codestreak.presentation.home.HomeScreen(
                                onNavigateToWorkspace = { currentScreen = "workspace" },
                                onNavigateToProfile = { currentScreen = "profile" }
                            )
                        }
                        "workspace" -> {
                            com.fury.codestreak.presentation.workspace.WorkspaceScreen(
                                onBack = { currentScreen = "home" },
                                // NEW: Navigate to Solution on Submit
                                onSubmitSuccess = { currentScreen = "solution" }
                            )
                        }
                        "solution" -> {
                            com.fury.codestreak.presentation.solution.SolutionScreen(
                                onBack = { currentScreen = "workspace" }, // Go back to code
                                onContinue = { currentScreen = "home" }   // Loop finished!
                            )
                        }

                        "profile" -> {
                            com.fury.codestreak.presentation.profile.ProfileScreen(
                                onLogout = { currentScreen = "auth" }
                            )
                        }
                    }
                }
            }
        }
    }
}