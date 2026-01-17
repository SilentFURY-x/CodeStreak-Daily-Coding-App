package com.fury.codestreak.presentation.solution

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        viewModelScope.launch {
            if (repository.getDailyQuestion() is Resource.Success) {
                solutionCode.value = repository.getDailyQuestion().data?.solutionCode ?: "No solution found."
            }
        }
    }
}