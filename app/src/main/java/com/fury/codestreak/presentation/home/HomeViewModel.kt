package com.fury.codestreak.presentation.home

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fury.codestreak.domain.model.Question
import com.fury.codestreak.domain.model.User
import com.fury.codestreak.domain.repository.AuthRepository
import com.fury.codestreak.domain.repository.QuestionRepository
import com.fury.codestreak.domain.repository.UserRepository
import com.fury.codestreak.presentation.util.NotificationWorker
import com.fury.codestreak.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: QuestionRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val application: Application
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    init {
        loadDailyQuestion()
        listenToUserUpdates()
        loadNotificationState()
    }

    // 1. Toggle Logic
    fun toggleNotifications() {
        val newState = !_state.value.isNotificationsEnabled
        _state.value = _state.value.copy(isNotificationsEnabled = newState)

        saveNotificationState(newState)

        if (newState) {
            scheduleNotification()
        } else {
            cancelNotification()
        }
    }

    // 2. WorkManager Scheduling (PRODUCTION MODE)
    private fun scheduleNotification() {
        Log.d("CodeStreak", "Scheduling Daily Notification for 9:00 AM...")

        // CHANGED: Switched back to PeriodicWorkRequest (Every 24 Hours)
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(calculateDelay(), TimeUnit.MILLISECONDS) // Calculates time until next 9 AM
            .addTag("daily_reminder")
            .build()

        // CHANGED: Uses enqueueUniquePeriodicWork
        WorkManager.getInstance(application).enqueueUniquePeriodicWork(
            "daily_reminder_work",
            ExistingPeriodicWorkPolicy.UPDATE, // Updates the schedule if called again
            workRequest
        )
    }

    private fun cancelNotification() {
        Log.d("CodeStreak", "Canceling Notification")
        WorkManager.getInstance(application).cancelAllWorkByTag("daily_reminder")
    }

    // 3. Real 9:00 AM Logic (Restored)
    private fun calculateDelay(): Long {
        val currentTime = System.currentTimeMillis()
        val dueTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9) // 9:00 AM
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        // If 9 AM has already passed today, schedule for tomorrow
        if (dueTime.timeInMillis <= currentTime) {
            dueTime.add(Calendar.DAY_OF_YEAR, 1)
        }
        return dueTime.timeInMillis - currentTime
    }

    // 4. Persistence & Data Loading (Unchanged)
    private fun saveNotificationState(enabled: Boolean) {
        val prefs = application.getSharedPreferences("codestreak_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    private fun loadNotificationState() {
        val prefs = application.getSharedPreferences("codestreak_prefs", Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean("notifications_enabled", false)
        _state.value = _state.value.copy(isNotificationsEnabled = enabled)
    }

    private fun listenToUserUpdates() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            viewModelScope.launch {
                userRepository.getUserFlow(currentUser.uid).collectLatest { user ->
                    if (user != null) {
                        _state.value = _state.value.copy(
                            streak = user.currentStreak,
                            name = user.email.split("@")[0],
                            weeklyProgress = calculateWeeklyProgress(user)
                        )
                    }
                }
            }
        }
    }

    private fun calculateWeeklyProgress(user: User): List<Boolean> {
        val today = Calendar.getInstance()
        var dayIndex = today.get(Calendar.DAY_OF_WEEK) - 2
        if (dayIndex < 0) dayIndex = 6

        val progress = MutableList(7) { false }
        val lastSolved = Calendar.getInstance().apply { timeInMillis = user.lastSolvedDate }
        val isSolvedToday = lastSolved.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)

        if (isSolvedToday) {
            progress[dayIndex] = true
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
                    _state.value = _state.value.copy(isLoading = false)
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
    val isLoading: Boolean = false,
    val isNotificationsEnabled: Boolean = false
)