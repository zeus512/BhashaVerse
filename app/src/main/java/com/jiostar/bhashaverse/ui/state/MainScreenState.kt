package com.jiostar.bhashaverse.ui.state

data class MainScreenState(
    val isLoading: Boolean = false,
    val loadingMessage: String = "",
    val errorMessage: String = "",
    val audioFile: String? = "",
    val originalText: String? = "",
    val userPickedLanguage: String? = "",
    val translatedText: String? = "",
    val imageStream: String? = "",
)
