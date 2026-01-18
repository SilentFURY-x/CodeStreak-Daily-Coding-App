package com.fury.codestreak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.fury.codestreak.domain.repository.AuthRepository
import com.fury.codestreak.presentation.theme.BackgroundDark
import com.fury.codestreak.presentation.theme.CodeStreakTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository // 1. Inject Auth Repo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. Check Login Status immediately
        val startDestination = if (authRepository.getCurrentUser() != null) "home" else "auth"

        setContent {
            CodeStreakTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = BackgroundDark) {

                    // 3. Use the dynamic start destination
                    var currentScreen by remember { mutableStateOf(startDestination) }

                    when (currentScreen) {
                        "auth" -> {
                            com.fury.codestreak.presentation.auth.AuthScreen(
                                onLoginSuccess = { currentScreen = "home" } // <--- Pass the action here!
                            )
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
                                onSubmitSuccess = { currentScreen = "solution" }
                            )
                        }
                        "solution" -> {
                            com.fury.codestreak.presentation.solution.SolutionScreen(
                                onBack = { currentScreen = "workspace" },
                                onContinue = { currentScreen = "home" }
                            )
                        }
                        "profile" -> {
                            com.fury.codestreak.presentation.profile.ProfileScreen(
                                onBack = { currentScreen = "home" }
                            )
                        }
                    }
                }
            }
        }
    }
}