package com.fury.codestreak.presentation.solution

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fury.codestreak.presentation.theme.*
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolutionScreen(
    viewModel: SolutionViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    // 1. Define the Confetti Configuration
    val party = remember {
        Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
            position = Position.Relative(0.5, 0.3)
        )
    }

    // State to trigger confetti
    var showConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showConfetti = true // Trigger animation on entry
        // Force a data refresh every time the screen is shown
        viewModel.refreshData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solution Reveal", color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark),
                actions = {
                    IconButton(onClick = { viewModel.onBookmarkClick() }) {
                        Icon(
                            // Toggle Icon based on state: Filled vs Border
                            if (viewModel.isBookmarked.value) Icons.Filled.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (viewModel.isBookmarked.value) PrimaryBlue else TextWhite
                        )
                    }
                }
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {

            // 2. Main Content (Scrollable)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Success Banner
                SuccessBanner()

                Spacer(modifier = Modifier.height(24.dp))

                // Official Solution Code
                Text("Official Solution", style = MaterialTheme.typography.titleMedium, color = TextWhite)
                Spacer(modifier = Modifier.height(12.dp))

                SolutionCodeBlock(
                    code = viewModel.solutionCode.value
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Key Takeaways
                Text("Key Takeaways", style = MaterialTheme.typography.titleMedium, color = TextWhite)
                Spacer(modifier = Modifier.height(12.dp))

                TakeawayCard(
                    icon = Icons.Default.Speed,
                    title = "Time Complexity: O(n)",
                    description = "By using a Hash Map, we only traverse the list once, making the search significantly faster than nested loops."
                )

                Spacer(modifier = Modifier.height(12.dp))

                TakeawayCard(
                    icon = Icons.Default.Memory,
                    title = "Space-Time Trade-off",
                    description = "We use extra memory O(n) to store the dictionary to achieve faster execution time."
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Continue Button
                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Continue Learning", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }

            // 3. The Confetti Overlay (Z-Index is higher by default in Box)
            if (showConfetti) {
                KonfettiView(
                    modifier = Modifier.fillMaxSize(),
                    parties = listOf(party)
                )
            }
        }
    }
}

// --- COMPONENTS ---

@Composable
fun SuccessBanner() {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Challenge Completed", style = MaterialTheme.typography.titleMedium, color = TextWhite)
                Spacer(modifier = Modifier.weight(1f))
                Text("100%", color = TextGray, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = SuccessGreen,
                trackColor = SurfaceHighlight,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                // Note: This Streak text is still static for now.
                // We will hook this up to real user data in the final "Streak" step.
                Text("DAILY STREAK: 13 DAYS", style = MaterialTheme.typography.labelSmall, color = TextGray)
            }
        }
    }
}

@Composable
fun SolutionCodeBlock(code: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0D1117))
            .padding(16.dp)
    ) {
        Text(
            text = code,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                lineHeight = 22.sp
            ),
            color = TextWhite
        )
    }
}

@Composable
fun TakeawayCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = PrimaryBlue)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleSmall, color = TextWhite, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, style = MaterialTheme.typography.bodySmall, color = TextGray)
            }
        }
    }
}