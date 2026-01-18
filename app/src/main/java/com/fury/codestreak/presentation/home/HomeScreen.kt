package com.fury.codestreak.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
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

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToWorkspace: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val state = viewModel.state.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()) // Make screen scrollable
    ) {
        // 1. Top Bar
        HomeTopBar(
            isNotificationsEnabled = state.isNotificationsEnabled,
            onNotificationClick = { viewModel.toggleNotifications() },
            onProfileClick = onNavigateToProfile
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Streak Card
        StreakCard(streak = state.streak)

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Weekly Progress
        Text("Weekly Progress", style = MaterialTheme.typography.titleLarge, color = TextWhite)
        Spacer(modifier = Modifier.height(12.dp))
        WeeklyProgressBar(progress = state.weeklyProgress)

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Today's Challenge
        Text("Today's Challenge", style = MaterialTheme.typography.titleLarge, color = TextWhite)
        Spacer(modifier = Modifier.height(12.dp))

        state.dailyQuestion?.let { question ->
            DailyChallengeCard(
                title = question.title,
                difficulty = question.difficulty,
                time = question.timeEstimate,
                description = question.description,
                onClick = onNavigateToWorkspace
            )
        } ?: run {
            // Loading State Placeholder
            Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(SurfaceDark, RoundedCornerShape(16.dp)))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Recommended (The Missing UI)
        Text("Recommended for You", style = MaterialTheme.typography.titleLarge, color = TextWhite)
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                RecommendedCard(
                    icon = Icons.Default.Functions,
                    title = "Arrays & Strings",
                    subtitle = "15 lessons • 45 mins",
                    color = Color(0xFFFFC107) // Amber
                )
            }
            item {
                RecommendedCard(
                    icon = Icons.Default.Psychology,
                    title = "Basic Logic",
                    subtitle = "8 lessons • 20 mins",
                    color = Color(0xFF9C27B0) // Purple
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// --- COMPONENTS ---

@Composable
fun HomeTopBar(
    isNotificationsEnabled: Boolean,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SurfaceHighlight)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = TextGray)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Daily Coding",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }
        // TOGGLE BUTTON
        IconButton(onClick = onNotificationClick) {
            Icon(
                imageVector = if (isNotificationsEnabled) Icons.Filled.NotificationsActive else Icons.Outlined.Notifications,
                contentDescription = "Alerts",
                tint = if (isNotificationsEnabled) Color(0xFFFFC107) else TextWhite // Gold if active
            )
        }
    }
}

@Composable
fun StreakCard(streak: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp), // More rounded
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceHighlight)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gradient Fire Icon Background
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFFF5722), Color(0xFFFF8A65))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text("Current Streak", style = MaterialTheme.typography.labelMedium, color = TextGray)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("$streak", style = MaterialTheme.typography.headlineLarge, color = TextWhite, fontWeight = FontWeight.Bold)
                    Text(" Days", style = MaterialTheme.typography.titleMedium, color = TextGray, modifier = Modifier.padding(bottom = 4.dp))
                }
                Text("Keep it up! +1 today", style = MaterialTheme.typography.bodySmall, color = SuccessGreen, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun WeeklyProgressBar(progress: List<Boolean>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val days = listOf("M", "T", "W", "T", "F", "S", "S")
        days.forEachIndexed { index, day ->
            val isCompleted = progress.getOrElse(index) { false }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = day, style = MaterialTheme.typography.labelSmall, color = TextGray)
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(
                        if (isCompleted) PrimaryBlue else SurfaceHighlight
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) Icon(Icons.Default.Check, null, tint = TextWhite, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun DailyChallengeCard(title: String, difficulty: String, time: String, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceHighlight)
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp).background(
                    Brush.verticalGradient(colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row {
                        Badge(text = difficulty, color = SuccessGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge(text = time, color = TextWhite)
                    }
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge, color = TextWhite)
                Spacer(modifier = Modifier.height(8.dp))
                Text(description, style = MaterialTheme.typography.bodyMedium, color = TextGray, maxLines = 3)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onClick, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue), shape = RoundedCornerShape(8.dp)) {
                    Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Coding")
                }
            }
        }
    }
}

@Composable
fun RecommendedCard(icon: ImageVector, title: String, subtitle: String, color: Color) {
    Card(
        modifier = Modifier.width(160.dp).height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceHighlight)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, color = TextWhite, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = TextGray)
            }
        }
    }
}

@Composable
fun Badge(text: String, color: Color) {
    Box(modifier = Modifier.background(color.copy(alpha = 0.1f), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
        Text(text, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
    }
}