package com.fury.codestreak.presentation.workspace

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fury.codestreak.domain.repository.AuthRepository
import com.fury.codestreak.domain.repository.QuestionRepository
import com.fury.codestreak.domain.repository.UserRepository
import com.fury.codestreak.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val repository: QuestionRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = mutableStateOf(WorkspaceState())
    val state: State<WorkspaceState> = _state

    init {
        loadQuestion()
    }

    private fun loadQuestion() {
        viewModelScope.launch {
            // Get the same daily question
            when(val result = repository.getDailyQuestion()) {
                is Resource.Success -> {
                    result.data?.let { question ->
                        _state.value = _state.value.copy(
                            question = question,
                            // LOAD THE REAL STARTER CODE
                            code = question.starterCode
                        )
                    }
                }
                else -> {} // Handle error
            }
        }
    }

    fun onCodeChange(newCode: String) {
        _state.value = _state.value.copy(code = newCode)
    }

    // Returns TRUE if submission is valid, FALSE if ignored
    fun onSubmit(): Boolean {
        val currentCode = _state.value.code
        val starterCode = _state.value.question?.starterCode ?: ""

        if (currentCode.trim() == starterCode.trim() || currentCode.isBlank()) {
            return false
        }

        viewModelScope.launch {
            // 1. Mark local DB
            _state.value.question?.let { question ->
                repository.markQuestionSolved(question.id)

                // 2. Mark Firestore User (The Real Memory)
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    userRepository.incrementStreak(user.uid, question.id)
                }
            }
        }

        _state.value = _state.value.copy(isSubmitted = true, showSolution = true)
        return true
    }
}