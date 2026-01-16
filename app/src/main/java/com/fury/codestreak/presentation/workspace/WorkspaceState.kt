package com.fury.codestreak.presentation.workspace

import com.fury.codestreak.domain.model.Question

data class WorkspaceState(
    val question: Question? = null,
    val code: String = "", // The user's code
    val isSubmitted: Boolean = false,
    val output: String = "", // Console output
    val showSolution: Boolean = false
)