package com.fury.codestreak.presentation.home

import androidx.compose.runtime.State
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
class HomeViewModel @Inject constructor(
    private val repository: QuestionRepository
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    init {
        loadDailyQuestion()
    }

    private fun loadDailyQuestion() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            when (val result = repository.getDailyQuestion()) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        dailyQuestion = result.data,
                        isLoading = false,
                        streak = if (result.data?.isSolved == true) 1 else 0
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false
                        // Error handling is silent for now (falls back to cached data in repo)
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }
}

data class HomeState(
    val name: String = "Coder",
    val streak: Int = 0,
    val dailyQuestion: Question? = null,
    val weeklyProgress: List<Boolean> = listOf(false, false, false, false, false, false, false),
    val isLoading: Boolean = false
)