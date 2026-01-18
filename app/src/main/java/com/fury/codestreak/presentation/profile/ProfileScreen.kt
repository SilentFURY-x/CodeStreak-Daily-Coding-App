package com.fury.codestreak.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
    onBack: () -> Unit
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
                    IconButton(onClick = { /* Settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = TextGray)
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
                // Avatar (Simple Gradient for now, could be Image)
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
                        text = "Level ${(state.user?.score ?: 0) / 50}", // XP Level Calculation
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

            // 3. Codeforces Section
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
                    // Codeforces Icon/Logo
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        // Simple Text Logo replacement
                        Text("CF", color = Color(0xFF1F8AC0), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    if (isConnected) {
                        Column {
                            Text(
                                text = state.user?.codeforcesHandle ?: "Unknown",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = getRankColor(state.cfRank) // Dynamic Color!
                            )
                            Text(
                                text = "${state.cfRank?.replaceFirstChar { it.uppercase() }} • ${state.cfRating ?: "Unrated"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextGray
                            )
                        }
                    } else {
                        Column {
                            Text("Connect Codeforces", style = MaterialTheme.typography.titleMedium, color = TextWhite)
                            Text("Track your rating & rank", style = MaterialTheme.typography.bodySmall, color = TextGray)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryBlue)
                    }
                }
            }

            if (state.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = PrimaryBlue)
            }
        }
    }

    // 4. Input Dialog (Reused & Polished)
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
                    if (state.error != null) {
                        Text(state.error, color = ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    }
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

// --- HELPER COMPOSABLES ---

@Composable
fun DashboardCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceHighlight)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextWhite)
            Text(title, style = MaterialTheme.typography.labelSmall, color = TextGray)
        }
    }
}

// Codeforces Standard Colors
fun getRankColor(rank: String?): Color {
    return when (rank?.lowercase()) {
        "newbie" -> Color(0xFF808080) // Grey
        "pupil" -> Color(0xFF008000) // Green
        "specialist" -> Color(0xFF03A89E) // Cyan
        "expert" -> Color(0xFF0000FF) // Blue
        "candidate master" -> Color(0xFFAA00AA) // Violet
        "master", "international master" -> Color(0xFFFF8C00) // Orange
        "grandmaster", "international grandmaster", "legendary grandmaster" -> Color(0xFFFF0000) // Red
        else -> TextWhite
    }
}