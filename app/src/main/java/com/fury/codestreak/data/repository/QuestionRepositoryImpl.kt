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
                // 1. Fetch ALL questions from Firestore
                val snapshot = firestore.collection("questions").get().await()

                if (!snapshot.isEmpty) {
                    // 2. The Pool Strategy
                    val allDocs = snapshot.documents
                    val dayOfYear = LocalDate.now().toEpochDay()
                    val dailyIndex = (dayOfYear % allDocs.size).toInt()
                    val targetDoc = allDocs[dailyIndex]

                    // 3. CHECK LOCAL FIRST (The Fix!)
                    // Before we overwrite, check if we already have this question saved locally
                    val existingLocalQuestion = dao.getQuestionById(targetDoc.id)

                    val entity = QuestionEntity(
                        id = targetDoc.id,
                        title = targetDoc.getString("title") ?: "Unknown Challenge",
                        description = targetDoc.getString("description") ?: "No description provided.",
                        difficulty = targetDoc.getString("difficulty") ?: "Medium",
                        topic = targetDoc.getString("topic") ?: "General",
                        timeEstimate = targetDoc.getString("timeEstimate") ?: "15 mins",
                        date = System.currentTimeMillis(),
                        // PRESERVE PROGRESS: Use local status if available, else default to false
                        isSolved = existingLocalQuestion?.isSolved ?: false,
                        // NEW FIELDS
                        starterCode = targetDoc.getString("starterCode") ?: "// Write your solution here...",
                        solutionCode = targetDoc.getString("solutionCode") ?: "// Solution not available yet",
                        // PRESERVE USER CODE: Keep what they typed!
                        userCode = existingLocalQuestion?.userCode,
                                // PRESERVE BOOKMARK STATUS
                        isBookmarked = existingLocalQuestion?.isBookmarked ?: false
                    )

                    // 4. Update Cache (Safe Insert)
                    dao.insertQuestions(listOf(entity))
                    Resource.Success(entity.toDomain())
                } else {
                    fetchLocal()
                }
            } catch (e: Exception) {
                fetchLocal() // Fallback to offline
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

        override suspend fun markQuestionSolved(id: String, userCode: String) {
            val question = dao.getQuestionById(id)
            question?.let {
                dao.updateQuestion(it.copy(
                    isSolved = true,
                    userCode = userCode // <--- Save the user's work!
                ))
            }
        }

        // Toggle Logic
        override suspend fun toggleBookmark(id: String) {
            val question = dao.getQuestionById(id)
            question?.let {
                val newStatus = !it.isBookmarked
                dao.updateQuestion(it.copy(isBookmarked = newStatus))
            }
        }

        // Fetch List
        override suspend fun getSavedQuestions(): List<Question> {
            return dao.getBookmarkedQuestions().map { it.toDomain() }
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
                    date = System.currentTimeMillis(),
                    isSolved = false,
                    starterCode = "fun isPalindrome(s: String): Boolean {\n    // Your code here\n}",
                    solutionCode = "fun isPalindrome(s: String): Boolean {\n    return s == s.reversed()\n}",
                    userCode = null
                )
            )
            dao.insertQuestions(dummyQuestions)
        }
    }