package com.fury.codestreak.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fury.codestreak.domain.model.Question

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val difficulty: String,
    val topic: String,
    val timeEstimate: String,
    val isSolved: Boolean = false,
    val date: Long // Timestamp for "Daily" logic
) {
    // Helper function to convert Database Object -> Domain Object
    fun toDomain(): Question {
        return Question(
            id = id,
            title = title,
            description = description,
            difficulty = difficulty,
            topic = topic,
            timeEstimate = timeEstimate,
            isSolved = isSolved,
            date = date
        )
    }
}

// Helper to convert Domain Object -> Database Object
fun Question.toEntity(): QuestionEntity {
    return QuestionEntity(
        id = id,
        title = title,
        description = description,
        difficulty = difficulty,
        topic = topic,
        timeEstimate = timeEstimate,
        isSolved = isSolved,
        date = date
    )
}