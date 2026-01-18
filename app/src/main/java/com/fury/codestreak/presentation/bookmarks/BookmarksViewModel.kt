package com.fury.codestreak.presentation.bookmarks

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fury.codestreak.domain.model.Question
import com.fury.codestreak.domain.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookmarksState(
    val savedQuestions: List<Question> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val repository: QuestionRepository
) : ViewModel() {

    private val _state = mutableStateOf(BookmarksState())
    val state: State<BookmarksState> = _state

    init {
        loadBookmarks()
    }

    // Refresh list every time screen opens
    fun loadBookmarks() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val list = repository.getSavedQuestions()
            _state.value = _state.value.copy(
                savedQuestions = list,
                isLoading = false
            )
        }
    }

    fun removeBookmark(questionId: String) {
        viewModelScope.launch {
            repository.toggleBookmark(questionId)
            loadBookmarks() // Refresh list immediately
        }
    }
}