package com.fury.codestreak.data.repository

import com.fury.codestreak.data.local.QuestionDao
import com.fury.codestreak.data.local.QuestionEntity
import com.fury.codestreak.domain.model.Question
import com.fury.codestreak.domain.repository.QuestionRepository
import com.fury.codestreak.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val dao: QuestionDao,
    private val firestore: FirebaseFirestore // Inject Firestore
) : QuestionRepository {

    override suspend fun getDailyQuestion(): Resource<Question> {
        return try {
            // 1. Try fetching from Firestore (The "Curated" Source)
            // We assume a collection "questions" with a document "daily" or queried by date.
            // For simplicity/demo: We fetch a document named "daily_question"
            val snapshot = firestore.collection("questions").document("daily_question").get().await()

            if (snapshot.exists()) {
                // Map Firestore data to our Entity
                val entity = QuestionEntity(
                    id = snapshot.id,
                    title = snapshot.getString("title") ?: "Unknown",
                    description = snapshot.getString("description") ?: "No desc",
                    difficulty = snapshot.getString("difficulty") ?: "Easy",
                    topic = snapshot.getString("topic") ?: "General",
                    timeEstimate = snapshot.getString("timeEstimate") ?: "10 mins",
                    date = System.currentTimeMillis(), // Mark as today's
                    isSolved = false // Reset solved status for new question
                )

                // Save to Local DB (Offline Cache)
                dao.insertQuestions(listOf(entity))
                Resource.Success(entity.toDomain())
            } else {
                // Fallback: Check local DB if Firestore fails or is empty
                fetchLocal()
            }
        } catch (e: Exception) {
            // Offline or Error: Load from Room
            fetchLocal()
        }
    }

    private suspend fun fetchLocal(): Resource<Question> {
        val entity = dao.getLatestQuestion()
        return if (entity != null) {
            Resource.Success(entity.toDomain())
        } else {
            // If absolutely nothing exists, seed dummy data (Safety Net)
            seedDatabase()
            val newEntity = dao.getLatestQuestion()
            if (newEntity != null) Resource.Success(newEntity.toDomain())
            else Resource.Error("No questions available offline.")
        }
    }

    override suspend fun markQuestionSolved(id: String) {
        val question = dao.getQuestionById(id)
        question?.let {
            dao.updateQuestion(it.copy(isSolved = true))
        }
    }

    private suspend fun seedDatabase() {
        val dummyQuestions = listOf(
            QuestionEntity(
                id = "1",
                title = "Palindrome Checker",
                description = "Implement a function to determine if a given string reads the same forwards and backwards.",
                difficulty = "Easy",
                topic = "Strings",
                timeEstimate = "15 mins",
                date = System.currentTimeMillis()
            )
        )
        dao.insertQuestions(dummyQuestions)
    }
}