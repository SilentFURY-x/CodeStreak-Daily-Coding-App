package com.fury.codestreak.presentation.solution

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fury.codestreak.domain.model.Question
import com.fury.codestreak.domain.repository.QuestionRepository
import com.fury.codestreak.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SolutionViewModel @Inject constructor(
    private val repository: QuestionRepository
) : ViewModel() {

    val solutionCode = mutableStateOf("Loading...")
    val currentQuestion = mutableStateOf<Question?>(null)
    val isBookmarked = mutableStateOf(false) // <--- Track UI State

    init {
        viewModelScope.launch {
            val result = repository.getDailyQuestion()
            if (result is Resource.Success) {
                val question = result.data
                currentQuestion.value = question
                solutionCode.value = question?.solutionCode ?: "No solution found."
                // Initialize UI State
                isBookmarked.value = question?.isBookmarked ?: false
            }
        }
    }

    // Handle Click
    fun onBookmarkClick() {
        viewModelScope.launch {
            currentQuestion.value?.let { q ->
                repository.toggleBookmark(q.id)
                isBookmarked.value = !isBookmarked.value // Update UI instantly
            }
        }
    }
}