package com.fury.codestreak.presentation.workspace

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.fury.codestreak.domain.model.Question
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WorkspaceViewModel @Inject constructor() : ViewModel() {

    private val _state = mutableStateOf(WorkspaceState())
    val state: State<WorkspaceState> = _state

    init {
        // Load the dummy question again (In real app, we pass the ID)
        loadQuestion()
    }

    private fun loadQuestion() {
        val dummyQuestion = Question(
            id = "1",
            title = "Palindrome Checker",
            description = "Write a function that reverses a string. The input string is given as an array of characters s.\n\nYou must do this by modifying the input array in-place with O(1) extra memory.",
            difficulty = "Easy",
            topic = "Strings",
            timeEstimate = "10 mins"
        )

        // Starter Code
        val starterCode = """
class Solution {
    fun isPalindrome(x: Int): Boolean {
        // Your code here...
        
    }
}
        """.trimIndent()

        _state.value = _state.value.copy(
            question = dummyQuestion,
            code = starterCode
        )
    }

    fun onCodeChange(newCode: String) {
        _state.value = _state.value.copy(code = newCode)
    }

    fun onSubmit() {
        // Simulating a submission
        _state.value = _state.value.copy(
            isSubmitted = true,
            showSolution = true
        )
    }
}