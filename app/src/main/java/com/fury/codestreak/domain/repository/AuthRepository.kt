package com.fury.codestreak.domain.repository

import android.app.Activity
import com.fury.codestreak.util.Resource
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    suspend fun signup(email: String, password: String): Resource<FirebaseUser>
    // New methods
    suspend fun signInWithGoogle(idToken: String): Resource<FirebaseUser>
    suspend fun signInWithGithub(activity: Activity): Resource<FirebaseUser>
    fun getCurrentUser(): FirebaseUser?
}