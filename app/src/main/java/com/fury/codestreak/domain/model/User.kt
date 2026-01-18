package com.fury.codestreak.domain.model

data class User(
    val uid: String = "",
    val email: String = "",
    val currentStreak: Int = 0,
    val lastSolvedDate: Long = 0L, // Timestamp of last submission
    val solvedQuestionIds: List<String> = emptyList(), // ["q1", "q5"]
    val totalSolved: Int = 0,
    val score: Int = 0, // XP points
    val codeforcesHandle: String? = null,
    val bookmarkedQuestionIds: List<String> = emptyList()
)