package com.fury.codestreak

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.fury.codestreak.domain.repository.AuthRepository
import com.fury.codestreak.presentation.theme.BackgroundDark
import com.fury.codestreak.presentation.theme.CodeStreakTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository // 1. Inject Auth Repo

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted. Notifications will work.
        } else {
            // Permission denied. Logic to handle denial (optional)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askNotificationPermission()

        askToDisableBatteryOptimizations()

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
                                onBack = { currentScreen = "home" },
                                onLogout = {
                                    // 1. Sign out from Firebase
                                    com.google.firebase.auth.FirebaseAuth.getInstance().signOut()

                                    // 2. Sign out from Google (if used) - Optional but good practice
                                    val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
                                        com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
                                    ).build()
                                    val client = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(this, gso)
                                    client.signOut()

                                    // 3. Navigate back to Auth
                                    currentScreen = "auth"
                                },
                                onNavigateToBookmarks = { currentScreen = "bookmarks" } // <--- Handle navigation
                            )
                        }

                        "bookmarks" -> {
                            com.fury.codestreak.presentation.bookmarks.BookmarksScreen(
                                onBack = { currentScreen = "profile" },
                                onQuestionClick = { questionId ->
                                    // For MVP: We will just go to workspace.
                                    // NOTE: In the next polish step, we will make Workspace load specific IDs.
                                    // For now, let's just open the daily workspace as a placeholder.
                                    currentScreen = "workspace"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    private fun askNotificationPermission() {
        // Only necessary for API level >= 33 (Tiramisu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Already granted
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun askToDisableBatteryOptimizations() {
        val packageName = packageName
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager

        // Check if we are already whitelisted
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            val intent = Intent()
            // This intent opens the specific dialog for your app
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }

}