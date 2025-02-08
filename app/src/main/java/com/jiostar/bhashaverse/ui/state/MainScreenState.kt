package com.jiostar.bhashaverse.ui.state

import com.jiostar.bhashaverse.data.models.LyricLine
import com.jiostar.bhashaverse.ui.utils.Constants.DUMMY_AUDIO_URL
import kotlin.time.Duration

data class MainScreenState(
    val isLoading: Boolean = false,
    val loadingMessage: String = "",
    val errorMessage: String = "",
    val audioFile: String? = DUMMY_AUDIO_URL ?: "",
    val audioThumbnailImage: String? = "",
    //val translatedTranscript: Map<String, String> = emptyMap(),
    val imageStream: String? = "",
    val originLanguageData: OriginLanguageData = OriginLanguageData(),
    val translatedLanguageData: TranslatedLanguageData = TranslatedLanguageData(),
    val translatedAudioData: TranslatedAudioData = TranslatedAudioData()
) {

    data class OriginLanguageData(
        val languageCode: String = "",
        val languageName: String = "",
        val text: String = "",
        val textTimestamps: List<LyricLine> = emptyList(),
    )

    data class TranslatedLanguageData(
        val languageCode: String = "",
        val languageName: String = "",
        val text: String = "",
        val textTimestamps: List<Duration> = emptyList(),
    )

    data class TranslatedAudioData(
        val audioUri: String? = null,
        val audioTimestamps: List<Duration> = emptyList(),
        val voiceTone: String = ""
    )

    companion object {
        val MainScreenStateDummyData = MainScreenState(
            isLoading = false,
            loadingMessage = "Loading...",
            errorMessage = "",
            audioFile = "$DUMMY_AUDIO_URL?filename=audioshort.mp3&generated=false",
            audioThumbnailImage = "https://picsum.photos/200",
            imageStream = "",
        )
    }

}
