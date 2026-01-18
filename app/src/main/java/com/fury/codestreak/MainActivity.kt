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
        // Permission logic
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

                    // 1. Helper to define Screen Depth (For animation direction)
                    fun getScreenLevel(screen: String): Int {
                        return when (screen) {
                            "auth" -> 0
                            "home" -> 1
                            "workspace", "profile", "bookmarks" -> 2
                            "solution" -> 3
                            else -> 1
                        }
                    }

                    // 2. The Smart Animation Block
                    AnimatedContent(
                        targetState = currentScreen,
                        label = "Screen Transition",
                        transitionSpec = {
                            val initialLevel = getScreenLevel(initialState)
                            val targetLevel = getScreenLevel(targetState)

                            if (targetLevel > initialLevel) {
                                // Going Deeper (Forward): Slide In from Right, Push Out to Left
                                (slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(400)) +
                                        fadeIn(animationSpec = tween(400)))
                                    .togetherWith(
                                        slideOutHorizontally(targetOffsetX = { -it / 3 }, animationSpec = tween(400)) +
                                                fadeOut(animationSpec = tween(400))
                                    )
                            } else {
                                // Going Back: Slide In from Left, Push Out to Right
                                (slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(400)) +
                                        fadeIn(animationSpec = tween(400)))
                                    .togetherWith(
                                        slideOutHorizontally(targetOffsetX = { it / 3 }, animationSpec = tween(400)) +
                                                fadeOut(animationSpec = tween(400))
                                    )
                            }
                        }
                    ) { targetScreen ->

                        // 3. Screen Navigation Logic
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
                                        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                                        val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
                                            com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
                                        ).build()
                                        val client = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(this@MainActivity, gso)
                                        client.signOut()

                                        // Clear ViewModels on Logout
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