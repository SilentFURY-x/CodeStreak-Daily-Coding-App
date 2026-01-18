package com.fury.codestreak.presentation.workspace

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fury.codestreak.presentation.home.Badge
import com.fury.codestreak.presentation.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceScreen(
    viewModel: WorkspaceViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    val state = viewModel.state.value
    val question = state.question ?: return

    // --- DELAY HEAVY RENDERING ---
    // We wait 400ms (animation duration) before rendering the heavy CodeEditor.
    // This ensures the slide animation never stutters.
    var isTransitionFinished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(450) // Wait slightly longer than the 400ms transition
        isTransitionFinished = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Problem Workspace",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark),
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = "Streak", tint = Color(0xFFFF5722))
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Header Info (Lightweight - Renders immediately)
            Text(question.title, style = MaterialTheme.typography.headlineSmall, color = TextWhite)
            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Badge(text = question.difficulty, color = DifficultyEasy)
                Spacer(modifier = Modifier.width(8.dp))
                Badge(text = question.timeEstimate, color = TextGray)
                Spacer(modifier = Modifier.width(8.dp))
                Badge(text = question.topic, color = PrimaryBlue)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Description
            Text(
                text = question.description,
                style = MaterialTheme.typography.bodyLarge,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Code Editor Area (HEAVY)
            Text("Your Solution", style = MaterialTheme.typography.titleMedium, color = TextWhite)
            Spacer(modifier = Modifier.height(8.dp))

            // Only render the heavy editor AFTER the animation is done
            if (isTransitionFinished) {
                // Fade it in smoothly so it doesn't just "pop"
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(300))
                ) {
                    CodeEditor(
                        value = state.code,
                        onValueChange = { viewModel.onCodeChange(it) },
                        modifier = Modifier.height(300.dp)
                    )
                }
            } else {
                // Placeholder while sliding (Prevents lag)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(2.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Submit Button
            Button(
                onClick = {
                    if (state.isSubmitted) {
                        onSubmitSuccess()
                    } else {
                        val isValid = viewModel.onSubmit()
                        if (isValid) onSubmitSuccess()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    if (state.isSubmitted) Icons.Default.Visibility else Icons.Default.Send,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (state.isSubmitted) "View Solution" else "Submit to Reveal Solution")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Hint Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = PrimaryBlue)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Hint: Try using two pointers, one at the start and one at the end.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray
                    )
                }
            }
        }
    }
}