package com.fury.codestreak.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    onNavigateToWorkspace: () -> Unit // Callback to go to coding screen
) {
    val state = viewModel.state.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // 1. Top Bar
        HomeTopBar()

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Streak Card
        StreakCard(streak = state.streak)

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Weekly Progress
        Text("Weekly Progress", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))
        WeeklyProgressBar(progress = state.weeklyProgress)

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Today's Challenge
        Text("Today's Challenge", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))

        state.dailyQuestion?.let { question ->
            DailyChallengeCard(
                title = question.title,
                difficulty = question.difficulty,
                time = question.timeEstimate,
                description = question.description,
                onClick = onNavigateToWorkspace
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Recommended (Placeholder for now)
        Text("Recommended for You", style = MaterialTheme.typography.titleLarge)
        // We will add the small cards here later
    }
}

// --- COMPONENTS ---

@Composable
fun HomeTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar Placeholder
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SurfaceHighlight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = TextGray)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Daily Coding",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        IconButton(onClick = { /* TODO */ }) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Alerts", tint = TextWhite)
        }
    }
}

@Composable
fun StreakCard(streak: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceHighlight)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("CURRENT STREAK", style = MaterialTheme.typography.labelSmall, color = TextGray)
                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF5722)) // Orange Fire
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text("$streak Days", style = MaterialTheme.typography.headlineMedium, color = TextWhite)
                Spacer(modifier = Modifier.width(8.dp))
                Text("+1 today", color = SuccessGreen, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { 0.7f }, // Hardcoded for UI demo
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = PrimaryBlue,
                trackColor = SurfaceHighlight,
            )
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
            val isToday = index == 4 // Fake "Today" for Friday

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = day, style = MaterialTheme.typography.labelSmall, color = TextGray)
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCompleted) PrimaryBlue
                            else if (isToday) Color.Transparent
                            else SurfaceHighlight
                        )
                        .then(
                            if (isToday) Modifier.background(
                                Brush.verticalGradient(listOf(PrimaryBlue, PrimaryBlueDark)),
                                alpha = 0.3f
                            ) else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = TextWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    } else if (isToday) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyChallengeCard(
    title: String,
    difficulty: String,
    time: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceHighlight)
    ) {
        Column {
            // Placeholder Image Area (Gradient for now)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                        )
                    )
            ) {
                // Badges
                Row(modifier = Modifier.padding(12.dp)) {
                    Badge(text = difficulty, color = SuccessGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Badge(text = time, color = TextGray)
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge, color = TextWhite)
                Spacer(modifier = Modifier.height(8.dp))
                Text(description, style = MaterialTheme.typography.bodyMedium, color = TextGray, maxLines = 3)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Coding")
                }
            }
        }
    }
}

@Composable
fun Badge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
    }
}