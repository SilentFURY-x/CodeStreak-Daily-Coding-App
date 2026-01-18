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
            when(val result = repository.getDailyQuestion()) {
                is Resource.Success -> {
                    result.data?.let { question ->
                        val isAlreadySolved = question.isSolved

                        // LOGIC: If they have saved code, show it. Otherwise show starter code.
                        val displayCode = question.userCode ?: question.starterCode

                        _state.value = _state.value.copy(
                            question = question,
                            code = if (isAlreadySolved) displayCode else question.starterCode,
                            isSubmitted = isAlreadySolved,
                            showSolution = isAlreadySolved
                        )
                    }
                }
                else -> {}
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
            _state.value.question?.let { question ->
                // 1. Mark Solved AND Save User Code
                repository.markQuestionSolved(question.id, currentCode)

                // 2. Increment Streak
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