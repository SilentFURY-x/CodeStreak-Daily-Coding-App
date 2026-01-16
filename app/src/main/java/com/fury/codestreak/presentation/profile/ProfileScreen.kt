package com.fury.codestreak.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fury.codestreak.presentation.theme.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    val state = viewModel.state.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Profile Header
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(SurfaceHighlight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.email.take(1).uppercase(),
                style = MaterialTheme.typography.headlineLarge,
                color = TextWhite
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(state.email, style = MaterialTheme.typography.titleLarge, color = TextWhite)
        Text("Level 12 • Coder", style = MaterialTheme.typography.bodyMedium, color = TextGray)

        Spacer(modifier = Modifier.height(32.dp))

        // 2. Codeforces Integration Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.onEvent(ProfileEvent.ShowDialog) },
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo Placeholder
                Box(
                    modifier = Modifier.size(40.dp).background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("CF", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text("Codeforces", style = MaterialTheme.typography.titleMedium, color = TextWhite)
                    if (state.codeforcesRating != null) {
                        Text(
                            text = "${state.codeforcesRank} (${state.codeforcesRating})",
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text("Tap to connect account", style = MaterialTheme.typography.bodySmall, color = TextGray)
                    }
                }
            }
        }

        if (state.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(color = PrimaryBlue)
        }

        if (state.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(state.error, color = ErrorRed, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.weight(1f))

        // 3. Logout Button
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed.copy(alpha = 0.1f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout", color = ErrorRed)
        }
    }

    // 4. Input Dialog
    if (state.isDialogVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(ProfileEvent.HideDialog) },
            containerColor = SurfaceDark,
            title = { Text("Connect Codeforces", color = TextWhite) },
            text = {
                OutlinedTextField(
                    value = state.codeforcesHandle,
                    onValueChange = { viewModel.onEvent(ProfileEvent.UpdateHandle(it)) },
                    label = { Text("Enter Handle (e.g. tourist)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = SurfaceHighlight
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onEvent(ProfileEvent.FetchStats) },
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