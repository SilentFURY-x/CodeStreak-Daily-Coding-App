package com.fury.codestreak.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fury.codestreak.presentation.theme.*
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.fury.codestreak.R // <--- IMPORTANT IMPORT
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    val state = viewModel.state.value
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is AuthUiEvent.NavigateToHome -> {
                    onLoginSuccess()
                }
            }
        }
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { token ->
                viewModel.onEvent(AuthEvent.SignInWithGoogle(token))
            }
        } catch (e: ApiException) {
            // Handle error
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Code,
            contentDescription = "Logo",
            tint = PrimaryBlue,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Master Your Logic", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Build consistent problem-solving habits one day at a time.", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = TextGray)

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth().background(SurfaceDark, RoundedCornerShape(12.dp)).padding(4.dp)
        ) {
            AuthTab(text = "Login", isSelected = state.isLoginMode, onClick = { if (!state.isLoginMode) viewModel.onEvent(AuthEvent.ToggleMode) })
            AuthTab(text = "Sign Up", isSelected = !state.isLoginMode, onClick = { if (state.isLoginMode) viewModel.onEvent(AuthEvent.ToggleMode) })
        }

        Spacer(modifier = Modifier.height(24.dp))

        AuthTextField(value = state.email, onValueChange = { viewModel.onEvent(AuthEvent.EmailChanged(it)) }, label = "Email", icon = Icons.Default.Email)
        Spacer(modifier = Modifier.height(16.dp))
        AuthTextField(value = state.password, onValueChange = { viewModel.onEvent(AuthEvent.PasswordChanged(it)) }, label = "Password", icon = Icons.Default.Lock, isPassword = true)

        Spacer(modifier = Modifier.height(24.dp))

        if (state.error != null) {
            Text(text = state.error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))
        }

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(50.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryBlue) }
        } else {
            Button(
                onClick = { viewModel.onEvent(AuthEvent.Submit) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = if (state.isLoginMode) "Sign In" else "Create Account", style = MaterialTheme.typography.labelLarge, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = SurfaceHighlight)
            Text(text = "OR CONTINUE WITH", modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.bodySmall, color = TextGray)
            HorizontalDivider(modifier = Modifier.weight(1f), color = SurfaceHighlight)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- UPDATED SOCIAL BUTTONS WITH ICONS ---
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // Google Button
            SocialButton(
                text = "Google",
                iconRes = R.drawable.ic_google // Using the XML we created
            ) {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("383269530727-noam9kdth1edltvgvmku5qgh1lrd3ba1.apps.googleusercontent.com")
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                googleSignInLauncher.launch(googleSignInClient.signInIntent)
            }

            // GitHub Button
            SocialButton(
                text = "GitHub",
                iconRes = R.drawable.ic_github // Using the XML we created
            ) {
                viewModel.onEvent(AuthEvent.SignInWithGithub(context as Activity))
            }
        }
    }
}

// --- UPDATED HELPER COMPOSABLES ---

@Composable
fun SocialButton(text: String, iconRes: Int, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SurfaceHighlight),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color.Unspecified // Important: Keeps the original colors of the logo
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, fontWeight = FontWeight.SemiBold)
    }
}

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
fun AuthTextField(value: String, onValueChange: (String) -> Unit, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isPassword: Boolean = false) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextGray) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = TextGray) },
        trailingIcon = if (isPassword) { { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, "Toggle", tint = TextGray) } } } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue, unfocusedBorderColor = SurfaceHighlight, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite),
        singleLine = true
    )
}