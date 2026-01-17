package com.fury.codestreak.presentation.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fury.codestreak.presentation.home.Badge
import com.fury.codestreak.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceScreen(
    viewModel: WorkspaceViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    val state = viewModel.state.value
    val question = state.question ?: return

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
            // 1. Header Info
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

            // 3. Code Editor Area
            Text("Your Solution", style = MaterialTheme.typography.titleMedium, color = TextWhite)
            Spacer(modifier = Modifier.height(8.dp))

            CodeEditor(
                code = state.code,
                onValueChange = { viewModel.onCodeChange(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Submit Button
            Button(
                onClick = {
                    val isValid = viewModel.onSubmit()
                    if (isValid) {
                        onSubmitSuccess() // Navigate ONLY if valid
                    } else {
                        // Optional: Show a Snackbar or Toast here saying "Please attempt the problem!"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Submit to Reveal Solution",
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Hint Card (Static for now)
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

@Composable
fun CodeEditor(code: String, onValueChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) // Fixed height for editor
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0D1117)) // Darker editor background
            .border(1.dp, SurfaceHighlight, RoundedCornerShape(12.dp))
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Line Numbers Column
            Column(
                modifier = Modifier
                    .width(40.dp)
                    .fillMaxHeight()
                    .background(SurfaceDark)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val lineCount = code.lines().size
                repeat(lineCount.coerceAtLeast(10)) { index ->
                    Text(
                        text = "${index + 1}",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = TextGray.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            // Text Input Area
            BasicTextField(
                value = code,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = TextWhite,
                    lineHeight = 20.sp // Match line height with line numbers
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                cursorBrush = SolidColor(PrimaryBlue)
            )
        }
    }
}