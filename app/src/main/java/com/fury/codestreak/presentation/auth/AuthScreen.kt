package com.fury.codestreak.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fury.codestreak.presentation.theme.PrimaryBlue
import com.fury.codestreak.presentation.theme.PrimaryBlueDark
import com.fury.codestreak.presentation.theme.SurfaceDark
import com.fury.codestreak.presentation.theme.SurfaceHighlight
import com.fury.codestreak.presentation.theme.TextGray
import com.fury.codestreak.presentation.theme.TextWhite

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel() // Hilt injects the ViewModel here automatically
) {
    val state = viewModel.state.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. The Header Icon (Using a placeholder for now, or a Code icon)
        Icon(
            imageVector = Icons.Default.Lock, // Placeholder for the Logo
            contentDescription = "Logo",
            tint = PrimaryBlue,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Title and Subtitle
        Text(
            text = "Master Your Logic",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Build consistent problem-solving habits one day at a time.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 3. The Toggle Switch (Login / Sign Up)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            // Login Tab
            AuthTab(
                text = "Login",
                isSelected = state.isLoginMode,
                onClick = { if (!state.isLoginMode) viewModel.onEvent(AuthEvent.ToggleMode) }
            )
            // Sign Up Tab
            AuthTab(
                text = "Sign Up",
                isSelected = !state.isLoginMode,
                onClick = { if (state.isLoginMode) viewModel.onEvent(AuthEvent.ToggleMode) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Input Fields
        AuthTextField(
            value = state.email,
            onValueChange = { viewModel.onEvent(AuthEvent.EmailChanged(it)) },
            label = "Email",
            icon = Icons.Default.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = state.password,
            onValueChange = { viewModel.onEvent(AuthEvent.PasswordChanged(it)) },
            label = "Password",
            icon = Icons.Default.Lock,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Main Action Button with Gradient feel
        Button(
            onClick = { viewModel.onEvent(AuthEvent.Submit) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (state.isLoginMode) "Sign In" else "Create Account",
                style = MaterialTheme.typography.labelLarge,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 6. Social Login Divider
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = SurfaceHighlight)
            Text(
                text = "OR CONTINUE WITH",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = SurfaceHighlight)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 7. Social Buttons (Google & GitHub)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SocialButton(text = "Google") { /* TODO: Implement Google Auth */ }
            SocialButton(text = "GitHub") { /* TODO: Implement GitHub Auth */ }
        }
    }
}

// --- Helper Composables to keep the code clean ---

@Composable
fun RowScope.AuthTab(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(40.dp)
            .background(
                if (isSelected) SurfaceHighlight else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) TextWhite else TextGray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextGray) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = TextGray) },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Password",
                        tint = TextGray
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = SurfaceHighlight,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite
        ),
        singleLine = true
    )
}

@Composable
fun SocialButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SurfaceHighlight),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TextWhite
        )
    ) {
        // We will add Icons here later
        Text(text = text, fontWeight = FontWeight.SemiBold)
    }
}