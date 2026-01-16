package com.fury.codestreak.data.repository

import com.fury.codestreak.data.local.QuestionDao
import com.fury.codestreak.data.local.QuestionEntity
import com.fury.codestreak.data.local.toEntity
import com.fury.codestreak.domain.model.Question
import com.fury.codestreak.domain.repository.QuestionRepository
import com.fury.codestreak.util.Resource
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val dao: QuestionDao
) : QuestionRepository {

    override suspend fun getDailyQuestion(): Resource<Question> {
        return try {
            var entity = dao.getLatestQuestion()

            if (entity == null) {
                // DB is empty! Seed it with "Offline" data.
                seedDatabase()
                entity = dao.getLatestQuestion()
            }

            if (entity != null) {
                Resource.Success(entity.toDomain())
            } else {
                Resource.Error("No questions found.")
            }
        } catch (e: Exception) {
            Resource.Error("Database Error: ${e.message}")
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
                description = "Write a function that reverses a string. The input string is given as an array of characters s.\n\nYou must do this by modifying the input array in-place with O(1) extra memory.",
                difficulty = "Easy",
                topic = "Strings",
                timeEstimate = "15 mins",
                date = System.currentTimeMillis()
            ),
            QuestionEntity(
                id = "2",
                title = "Two Sum",
                description = "Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.",
                difficulty = "Easy",
                topic = "Arrays",
                timeEstimate = "20 mins",
                date = System.currentTimeMillis() - 86400000 // Yesterday
            )
        )
        dao.insertQuestions(dummyQuestions)
    }
}