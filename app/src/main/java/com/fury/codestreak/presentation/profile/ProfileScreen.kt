package com.fury.codestreak.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fury.codestreak.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onLogout: () -> Unit // <--- Added Logout Callback
) {
    val state = viewModel.state.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark),
                actions = {
                    // LOGOUT BUTTON
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = ErrorRed)
                    }
                }
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // 1. User Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(PrimaryBlue, Color(0xFF64B5F6))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.user?.email?.take(1)?.uppercase() ?: "G",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column {
                    Text(
                        text = state.user?.email?.split("@")?.get(0) ?: "Guest",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "Level ${(state.user?.score ?: 0) / 50}",
                        style = MaterialTheme.typography.titleMedium,
                        color = PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Stats Dashboard
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DashboardCard(
                    modifier = Modifier.weight(1f),
                    title = "Streak",
                    value = "${state.user?.currentStreak ?: 0}",
                    icon = Icons.Default.LocalFireDepartment,
                    color = Color(0xFFFF5722)
                )
                DashboardCard(
                    modifier = Modifier.weight(1f),
                    title = "Solved",
                    value = "${state.user?.totalSolved ?: 0}",
                    icon = Icons.Default.CheckCircle,
                    color = SuccessGreen
                )
                DashboardCard(
                    modifier = Modifier.weight(1f),
                    title = "Total XP",
                    value = "${state.user?.score ?: 0}",
                    icon = Icons.Default.Bolt,
                    color = Color(0xFFFFC107)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. ACTIVITY HEATMAP (The "Wow" Factor)
            Text("Activity Map", style = MaterialTheme.typography.titleMedium, color = TextWhite)
            Spacer(modifier = Modifier.height(12.dp))

            ActivityHeatmap(streak = state.user?.currentStreak ?: 0)

            Spacer(modifier = Modifier.height(32.dp))

            // 4. Codeforces Section
            Text("Competitive Coding", style = MaterialTheme.typography.titleMedium, color = TextWhite)
            Spacer(modifier = Modifier.height(12.dp))

            val isConnected = !state.user?.codeforcesHandle.isNullOrBlank()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { if (!isConnected) viewModel.onEvent(ProfileEvent.ShowDialog) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isConnected) PrimaryBlue.copy(0.3f) else SurfaceHighlight)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("CF", color = Color(0xFF1F8AC0), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    if (isConnected) {
                        // CONNECTED STATE
                        Column {
                            Text(
                                text = state.user?.codeforcesHandle ?: "Unknown",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = getRankColor(state.cfRank)
                            )
                            Text(
                                text = "${state.cfRank?.replaceFirstChar { it.uppercase() }} • ${state.cfRating ?: "Unrated"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextGray
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // THE NEW UNLINK BUTTON
                        IconButton(onClick = { viewModel.onEvent(ProfileEvent.DisconnectCodeforces) }) {
                            Icon(Icons.Default.Close, contentDescription = "Unlink", tint = ErrorRed)
                        }

                    } else {
                        // DISCONNECTED STATE
                        Column {
                            Text("Connect Codeforces", style = MaterialTheme.typography.titleMedium, color = TextWhite)
                            Text("Track your rating & rank", style = MaterialTheme.typography.bodySmall, color = TextGray)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryBlue)
                    }
                }
            }
        }
    }

    // Input Dialog (Keep exactly the same as before)
    if (state.isDialogVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(ProfileEvent.HideDialog) },
            containerColor = SurfaceDark,
            title = { Text("Link Account", color = TextWhite) },
            text = {
                Column {
                    Text("Enter your Codeforces handle to sync stats.", color = TextGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.tempHandleInput,
                        onValueChange = { viewModel.onEvent(ProfileEvent.UpdateHandleInput(it)) },
                        label = { Text("Handle (e.g. tourist)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = SurfaceHighlight
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onEvent(ProfileEvent.ConnectCodeforces) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Connect")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(ProfileEvent.HideDialog) }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }
}

// --- HEATMAP COMPONENT ---
@Composable
fun ActivityHeatmap(streak: Int) {
    // We visualize 12 weeks (columns) x 7 days (rows) = 84 blocks
    // We "light up" the last N blocks based on the streak
    val totalBlocks = 84
    val activeBlocks = streak.coerceAtMost(totalBlocks)

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceHighlight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                repeat(12) { col ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(7) { row ->
                            // Calculate if this specific block should be active
                            // Logic: The heatmap fills from Right-Bottom to Left-Top
                            val blockIndex = (col * 7) + row
                            // Reverse logic to fill from end
                            val isActive = blockIndex >= (totalBlocks - activeBlocks)

                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (isActive) SuccessGreen.copy(alpha = 0.8f)
                                        else SurfaceHighlight.copy(alpha = 0.5f)
                                    )
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Last 12 Weeks", style = MaterialTheme.typography.labelSmall, color = TextGray)
        }
    }
}

// ... Keep DashboardCard and getRankColor helpers exactly as they were ...
@Composable
fun DashboardCard(modifier: Modifier = Modifier, title: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceHighlight)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextWhite)
            Text(title, style = MaterialTheme.typography.labelSmall, color = TextGray)
        }
    }
}

fun getRankColor(rank: String?): Color {
    return when (rank?.lowercase()) {
        "newbie" -> Color(0xFF808080)
        "pupil" -> Color(0xFF008000)
        "specialist" -> Color(0xFF03A89E)
        "expert" -> Color(0xFF0000FF)
        "candidate master" -> Color(0xFFAA00AA)
        "master", "international master" -> Color(0xFFFF8C00)
        "grandmaster", "international grandmaster", "legendary grandmaster" -> Color(0xFFFF0000)
        else -> TextWhite
    }
}