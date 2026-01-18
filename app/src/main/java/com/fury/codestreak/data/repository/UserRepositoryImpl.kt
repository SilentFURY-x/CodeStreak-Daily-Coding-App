package com.fury.codestreak.data.repository

import com.fury.codestreak.domain.model.User
import com.fury.codestreak.domain.repository.UserRepository
import com.fury.codestreak.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    private val usersCollection = firestore.collection("users")

    override suspend fun createOrUpdateUser(user: User) {
        // Merge = Update fields if exists, Create if not
        usersCollection.document(user.uid).set(user, SetOptions.merge()).await()
    }

    override suspend fun getUser(uid: String): Resource<User> {
        return try {
            val snapshot = usersCollection.document(uid).get().await()
            val user = snapshot.toObject(User::class.java)
            if (user != null) Resource.Success(user) else Resource.Error("User not found")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error")
        }
    }

    override fun getUserFlow(uid: String): Flow<User?> = callbackFlow {
        val listener = usersCollection.document(uid).addSnapshotListener { snapshot, _ ->
            val user = snapshot?.toObject(User::class.java)
            trySend(user)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun incrementStreak(uid: String, questionId: String) {
        val snapshot = usersCollection.document(uid).get().await()
        val currentUser = snapshot.toObject(User::class.java) ?: return

        // 1. Check if already solved TODAY
        val today = Calendar.getInstance()
        val lastDate = Calendar.getInstance().apply { timeInMillis = currentUser.lastSolvedDate }

        val isSameDay = today.get(Calendar.YEAR) == lastDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == lastDate.get(Calendar.DAY_OF_YEAR)

        if (isSameDay && currentUser.solvedQuestionIds.contains(questionId)) {
            return // Already counted streak for today
        }

        // 2. Check Streak Logic
        // Logic: Did we solve something Yesterday?
        // (Simplification: If difference in days is 1, increment. Else, reset to 1)
        // For MVP/Demo: We will just always increment for satisfaction, or implement strict logic later.
        // Let's implement strict logic:

        lastDate.add(Calendar.DAY_OF_YEAR, 1) // Add 1 day to last solved
        val isConsecutive = today.get(Calendar.YEAR) == lastDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == lastDate.get(Calendar.DAY_OF_YEAR)

        var newStreak = if (isConsecutive || currentUser.lastSolvedDate == 0L) currentUser.currentStreak + 1 else 1

        // Edge case: If solving multiple problems in one day, don't double count streak, but do count totalSolved
        if (isSameDay) newStreak = currentUser.currentStreak

        val newSolvedList = currentUser.solvedQuestionIds.toMutableList()
        if (!newSolvedList.contains(questionId)) {
            newSolvedList.add(questionId)
        }

        val updatedUser = currentUser.copy(
            currentStreak = newStreak,
            lastSolvedDate = System.currentTimeMillis(),
            solvedQuestionIds = newSolvedList,
            totalSolved = newSolvedList.size,
            score = currentUser.score + 10 // +10 XP per problem
        )

        createOrUpdateUser(updatedUser)
    }
}