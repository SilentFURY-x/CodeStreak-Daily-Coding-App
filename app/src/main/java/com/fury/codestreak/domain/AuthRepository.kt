package com.fury.codestreak.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.fury.codestreak.util.Resource

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    suspend fun signup(email: String, password: String): Resource<FirebaseUser>
    fun getCurrentUser(): FirebaseUser?
}