package com.fury.codestreak.presentation.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fury.codestreak.domain.model.Question
import com.fury.codestreak.presentation.home.Badge
import com.fury.codestreak.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    viewModel: BookmarksViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onQuestionClick: (String) -> Unit
) {
    val state = viewModel.state.value

    // Refresh data when screen becomes visible
    LaunchedEffect(Unit) {
        viewModel.loadBookmarks()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Questions", color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        if (state.savedQuestions.isEmpty() && !state.isLoading) {
            // Empty State
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No saved questions yet.", color = TextGray)
            }
        } else {
            // List State
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.savedQuestions) { question ->
                    BookmarkCard(
                        question = question,
                        onClick = { onQuestionClick(question.id) },
                        onRemove = { viewModel.removeBookmark(question.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun BookmarkCard(
    question: Question,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceHighlight)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = question.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Badge(text = question.difficulty, color = if(question.difficulty == "Easy") SuccessGreen else Color(0xFFFFC107))
                    Spacer(modifier = Modifier.width(8.dp))
                    Badge(text = question.topic, color = PrimaryBlue)
                }
            }

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.BookmarkRemove, contentDescription = "Remove", tint = TextGray)
            }
        }
    }
}