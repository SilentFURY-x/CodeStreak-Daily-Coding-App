package com.fury.codestreak.domain.model

data class Question(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val difficulty: String = "Easy", // Easy, Medium, Hard
    val topic: String = "General",
    val timeEstimate: String = "10 mins",
    val isSolved: Boolean = false,
    val date: Long = 0L, // We will use this to lock/unlock daily questions
    val starterCode: String = "// Write your solution here...",
    val solutionCode: String = "// Solution not available yet",
    val userCode: String? = null,
    val isBookmarked: Boolean = false
)