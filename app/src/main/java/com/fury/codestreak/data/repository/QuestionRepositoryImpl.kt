package com.fury.codestreak.data.repository

import com.fury.codestreak.data.local.QuestionDao
import com.fury.codestreak.data.local.QuestionEntity
import com.fury.codestreak.domain.model.Question
import com.fury.codestreak.domain.repository.QuestionRepository
import com.fury.codestreak.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val dao: QuestionDao,
    private val firestore: FirebaseFirestore
) : QuestionRepository {

    override suspend fun getDailyQuestion(): Resource<Question> {
        return try {
            // 1. Fetch ALL questions from Firestore "questions" collection
            val snapshot = firestore.collection("questions").get().await()

            if (!snapshot.isEmpty) {
                // 2. The Pool Strategy: Pick one based on the Day of the Year
                // If you have 5 questions, and it's day 6, it wraps around to question 1.
                val allDocs = snapshot.documents
                val dayOfYear = LocalDate.now().toEpochDay()
                val dailyIndex = (dayOfYear % allDocs.size).toInt()
                val targetDoc = allDocs[dailyIndex]

                val entity = QuestionEntity(
                    id = targetDoc.id,
                    title = targetDoc.getString("title") ?: "Unknown Challenge",
                    description = targetDoc.getString("description") ?: "No description provided.",
                    difficulty = targetDoc.getString("difficulty") ?: "Medium",
                    topic = targetDoc.getString("topic") ?: "General",
                    timeEstimate = targetDoc.getString("timeEstimate") ?: "15 mins",
                    date = System.currentTimeMillis(),
                    isSolved = false
                )

                // 3. Cache it locally
                dao.insertQuestions(listOf(entity))
                Resource.Success(entity.toDomain())
            } else {
                fetchLocal()
            }
        } catch (e: Exception) {
            fetchLocal() // Fallback to offline if internet fails
        }
    }

    private suspend fun fetchLocal(): Resource<Question> {
        val entity = dao.getLatestQuestion()
        return if (entity != null) {
            Resource.Success(entity.toDomain())
        } else {
            seedDatabase() // Safety net
            val newEntity = dao.getLatestQuestion()
            if (newEntity != null) Resource.Success(newEntity.toDomain())
            else Resource.Error("No questions available.")
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