package com.fury.codestreak.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.fury.codestreak.domain.model.Question
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    init {
        // Load fake data for UI testing
        _state.value = HomeState(
            streak = 12,
            dailyQuestion = Question(
                id = "1",
                title = "Palindrome Checker",
                description = "Implement a function to determine if a given string reads the same forwards and backwards.",
                difficulty = "Easy",
                topic = "Strings",
                timeEstimate = "15 mins"
            )
        )
    }
}

data class HomeState(
    val name: String = "Coder",
    val streak: Int = 0,
    val dailyQuestion: Question? = null,
    val weeklyProgress: List<Boolean> = listOf(true, true, true, true, false, false, false), // True = Solved
    val isLoading: Boolean = false
)