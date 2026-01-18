package com.fury.codestreak.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fury.codestreak.domain.model.Question
import com.fury.codestreak.domain.model.User
import com.fury.codestreak.domain.repository.AuthRepository
import com.fury.codestreak.domain.repository.QuestionRepository
import com.fury.codestreak.domain.repository.UserRepository
import com.fury.codestreak.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: QuestionRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    init {
        loadDailyQuestion()
        listenToUserUpdates()
    }

    private fun listenToUserUpdates() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            viewModelScope.launch {
                userRepository.getUserFlow(currentUser.uid).collectLatest { user ->
                    if (user != null) {
                        _state.value = _state.value.copy(
                            streak = user.currentStreak,
                            name = user.email.split("@")[0], // Extract name from email
                            weeklyProgress = calculateWeeklyProgress(user)
                        )
                    }
                }
            }
        }
    }

    // This logic lights up the bubbles based on your streak
    private fun calculateWeeklyProgress(user: User): List<Boolean> {
        val today = Calendar.getInstance()
        // Calendar.DAY_OF_WEEK: Sun=1, Mon=2, ... Sat=7
        // We want Mon=0, ... Sun=6
        var dayIndex = today.get(Calendar.DAY_OF_WEEK) - 2
        if (dayIndex < 0) dayIndex = 6 // Handle Sunday

        val progress = MutableList(7) { false }

        // 1. Check if solved today
        val lastSolved = Calendar.getInstance().apply { timeInMillis = user.lastSolvedDate }
        val isSolvedToday = lastSolved.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)

        if (isSolvedToday) {
            progress[dayIndex] = true

            // Visual Trick: If streak > 1, fill previous days for satisfaction
            var remainingStreak = user.currentStreak - 1
            var currentBackIndex = dayIndex - 1
            while (remainingStreak > 0 && currentBackIndex >= 0) {
                progress[currentBackIndex] = true
                remainingStreak--
                currentBackIndex--
            }
        }

        return progress
    }

    private fun loadDailyQuestion() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            when (val result = repository.getDailyQuestion()) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        dailyQuestion = result.data,
                        isLoading = false
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