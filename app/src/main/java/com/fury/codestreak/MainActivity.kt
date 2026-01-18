package com.fury.codestreak

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askNotificationPermission()
        askToDisableBatteryOptimizations()

        val startDestination = if (authRepository.getCurrentUser() != null) "home" else "auth"

        setContent {
            CodeStreakTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = BackgroundDark) {

                    var currentScreen by remember { mutableStateOf(startDestination) }

                    // --- ANIMATION BLOCK STARTS HERE ---
                    AnimatedContent(
                        targetState = currentScreen,
                        label = "Screen Transition",
                        transitionSpec = {
                            // Slide in from Right, Slide out to Left
                            (slideInHorizontally(initialOffsetX = { it }) + fadeIn(animationSpec = tween(300)))
                                .togetherWith(slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(animationSpec = tween(300)))
                        }
                    ) { targetScreen ->

                        // Note: We use 'targetScreen' here, not 'currentScreen'
                        when (targetScreen) {
                            "auth" -> {
                                com.fury.codestreak.presentation.auth.AuthScreen(
                                    onLoginSuccess = { currentScreen = "home" }
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
                                        // 1. Sign out Firebase
                                        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()

                                        // 2. Sign out Google
                                        val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
                                            com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
                                        ).build()
                                        val client = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(this@MainActivity, gso)
                                        client.signOut()

                                        viewModelStore.clear()

                                        currentScreen = "auth"
                                    },
                                    onNavigateToBookmarks = { currentScreen = "bookmarks" }
                                )
                            }
                            "bookmarks" -> {
                                com.fury.codestreak.presentation.bookmarks.BookmarksScreen(
                                    onBack = { currentScreen = "profile" },
                                    onQuestionClick = { _ ->
                                        currentScreen = "workspace"
                                    }
                                )
                            }
                        }
                    }
                    // --- ANIMATION BLOCK ENDS HERE ---
                }
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun askToDisableBatteryOptimizations() {
        val packageName = packageName
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager

        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            val intent = Intent()
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }
}