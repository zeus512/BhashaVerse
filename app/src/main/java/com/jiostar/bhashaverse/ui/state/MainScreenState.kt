package com.jiostar.bhashaverse.ui.state

import com.jiostar.bhashaverse.ui.utils.Constants.DUMMY_AUDIO_URL

data class MainScreenState(
    val isLoading: Boolean = false,
    val loadingMessage: String = "",
    val errorMessage: String = "",
    val audioFile: String? = DUMMY_AUDIO_URL ?: "",
    val audioThumbnailImage: String? = "",
    val originalText: String? = "",
    val userPickedLanguage: String? = "",
    val translatedText: String? = "",
    val imageStream: String? = "",
) {
    companion object {
        val MainScreenStateDummyData = MainScreenState(
            isLoading = false,
            loadingMessage = "Loading...",
            errorMessage = "",
            audioFile = DUMMY_AUDIO_URL,
            audioThumbnailImage = "https://picsum.photos/200",
            originalText = "Hello",
            userPickedLanguage = "en",
            translatedText = "नमस्ते",
            imageStream = "",
        )
    }

}
