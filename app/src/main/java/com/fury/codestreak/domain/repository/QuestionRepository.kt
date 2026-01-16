package com.fury.codestreak.domain.repository

import com.fury.codestreak.domain.model.Question
import com.fury.codestreak.util.Resource

interface QuestionRepository {
    suspend fun getDailyQuestion(): Resource<Question>
    suspend fun markQuestionSolved(id: String)
}