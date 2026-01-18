package com.fury.codestreak.domain.repository

import com.fury.codestreak.domain.model.User
import com.fury.codestreak.util.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun createOrUpdateUser(user: User)
    suspend fun getUser(uid: String): Resource<User>
    suspend fun incrementStreak(uid: String, questionId: String)
    fun getUserFlow(uid: String): Flow<User?> // Live updates
}