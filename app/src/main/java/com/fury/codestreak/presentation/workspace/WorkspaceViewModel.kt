package com.fury.codestreak.presentation.workspace

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fury.codestreak.domain.repository.QuestionRepository
import com.fury.codestreak.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val repository: QuestionRepository
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

        // Validation: Don't submit if code hasn't changed or is empty
        if (currentCode.trim() == starterCode.trim() || currentCode.isBlank()) {
            // In a real app, trigger a "Toast" via a UI Event here
            return false
        }

        // Logic: Mark as solved in Repo
        viewModelScope.launch {
            _state.value.question?.let {
                repository.markQuestionSolved(it.id)
            }
        }

        _state.value = _state.value.copy(isSubmitted = true, showSolution = true)
        return true
    }
}