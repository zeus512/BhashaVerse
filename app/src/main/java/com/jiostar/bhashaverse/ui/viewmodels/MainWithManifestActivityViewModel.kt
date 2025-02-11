package com.jiostar.bhashaverse.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jiostar.bhashaverse.data.models.LyricLine
import com.jiostar.bhashaverse.data.models.ProcessAudioRequest
import com.jiostar.bhashaverse.data.models.ProcessTextRequest
import com.jiostar.bhashaverse.data.models.TranscribeRequest
import com.jiostar.bhashaverse.data.models.TranslateRequest
import com.jiostar.bhashaverse.data.models.TtsAudioRequest
import com.jiostar.bhashaverse.domain.AppError
import com.jiostar.bhashaverse.domain.execute
import com.jiostar.bhashaverse.domain.usecase.ProcessAudioUseCase
import com.jiostar.bhashaverse.domain.usecase.ProcessTextUseCase
import com.jiostar.bhashaverse.domain.usecase.TranscribeAudioUseCase
import com.jiostar.bhashaverse.domain.usecase.TranslateTextUseCase
import com.jiostar.bhashaverse.domain.usecase.TtsAudioUseCase
import com.jiostar.bhashaverse.ui.state.MainScreenState
import com.jiostar.bhashaverse.ui.state.MainScreenState.Companion.MainScreenStateDummyData
import com.jiostar.bhashaverse.ui.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainWithManifestActivityViewModel @Inject constructor(
    private val processAudioUseCase: ProcessAudioUseCase,
    private val processTextUseCase: ProcessTextUseCase,
    private val translateTextUseCase: TranslateTextUseCase,
    private val transcribeAudioUseCase: TranscribeAudioUseCase,
    private val ttsAudioUseCase: TtsAudioUseCase

) : ViewModel() {

    private val _mainScreenState = MutableStateFlow<MainScreenState>(MainScreenStateDummyData)
    val mainScreenState: StateFlow<MainScreenState> = _mainScreenState

    init {
        //fetchBasicData()
        //transcribeAudio()
    }

    private fun transcribeAudio() {
        viewModelScope.launch(Dispatchers.IO) {
            transcribeAudioUseCase(
                TranscribeRequest(
                    audioUri = "/Users/goutham.reddy/Downloads/audioshort.mp3",
                    languageCode = "en-UK"
                )
            ).execute(onSuccess = { response ->
                _mainScreenState.update {
                    it.copy(
                        isLoading = false,
                        originLanguageData = MainScreenState.OriginLanguageData(
                            languageCode = "en-UK",
                            languageName = "English",
                            text = response.transcript,
                            textTimestamps = response.transcriptWithTimestamps.map {
                                LyricLine(
                                    timestamp = it.startTime.toLong(),
                                    //timestamp = it.endTime,
                                    text = it.transcript
                                )
                            }.orEmpty()
                        )
                    )
                }
            }, onError = { error ->
                customErrorHandler(error)
            })
        }
    }

    fun transcribeTextToUserGeneratedLanguage(targetLanguages: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            resetPreviousTranslationData()
            processTextUseCase(
                ProcessTextRequest(
                    transcript = mainScreenState.value.originLanguageData.text,
                    targetLanguages = targetLanguages,
                    translateTranscript = true,
                    translateEvents = false,
                )
            ).execute(onSuccess = { response ->
                _mainScreenState.update {
                    it.copy(
                        isLoading = false,
                        translatedLanguageData = MainScreenState.TranslatedLanguageData(
                            languageCode = targetLanguages.firstOrNull().orEmpty(),
                            languageName = targetLanguages.firstOrNull().orEmpty(),
                            text = response.translations.transcript[targetLanguages.firstOrNull()
                                ?: "te"].orEmpty()
                        )
                    )
                }
                initiateTranslatedTextToAudioGeneration()
            }, onError = { error ->
                customErrorHandler(error)
            })
        }
    }

    private fun resetPreviousTranslationData() {
        _mainScreenState.update {
            it.copy(
                isLoading = true,
                translatedLanguageData = MainScreenState.TranslatedLanguageData(),
                translatedAudioData = MainScreenState.TranslatedAudioData()
            )
        }
    }

    private fun initiateTranslatedTextToAudioGeneration() {
        viewModelScope.launch(Dispatchers.IO) {
            ttsAudioUseCase(
                TtsAudioRequest(
                    text = mainScreenState.value.translatedLanguageData.text,
                    languageCode = mainScreenState.value.translatedLanguageData.languageCode,
                )
            ).execute(onSuccess = { response ->
                _mainScreenState.update {
                    it.copy(
                        isLoading = false,
                        translatedAudioData = MainScreenState.TranslatedAudioData(
                            audioUri = Constants.BASE_URL+response.audioUrl,
                            //audioTimestamps = response.audioTimestamps.map { it.toLong() },

                        )
                    )
                }
            }, onError = { error ->
                customErrorHandler(error)
            })
        }
    }

    private fun fetchBasicData() {
        viewModelScope.launch {
            _mainScreenState.update { it.copy(isLoading = true) }
            viewModelScope.launch(Dispatchers.IO) {
                processAudioUseCase(
                    ProcessAudioRequest(
                        audioUri = "/Users/goutham.reddy/Downloads/audioshort.mp3",
                        targetLanguages = listOf("te", "hi", "en-IN"),
                        translateTranscript = true,
                        translateEvents = true,
                        languageCode = "en-UK"
                    )
                ).execute(onSuccess = { response ->
                    _mainScreenState.update {
                        it.copy(
                            isLoading = false,
                            originLanguageData = MainScreenState.OriginLanguageData(
                                languageCode = "en-UK",
                                languageName = "English",
                                text = response.transcript,
                            ),
                            translatedLanguageData = MainScreenState.TranslatedLanguageData(
                                languageCode = "te",
                                languageName = "Telugu",
                                text = response.translations.transcript["te"].orEmpty()
                            )
                        )
                    }
                }, onError = { error ->
                    customErrorHandler(error)
                })
            }

        }
    }

    fun translateText(targetLanguages: List<String>) {
        viewModelScope.launch {
            translateTextUseCase(
                TranslateRequest(
                    text = mainScreenState.value.originLanguageData.text,
                    targetLanguages = targetLanguages
                )
            ).execute(onSuccess = { response ->
                _mainScreenState.update {
                    it.copy(
                        isLoading = false,
                        translatedLanguageData = MainScreenState.TranslatedLanguageData(
                            languageCode = targetLanguages.firstOrNull().orEmpty(),
                            languageName = targetLanguages.firstOrNull().orEmpty(),
                            text = response.translations[targetLanguages.firstOrNull()
                                ?: "te"].orEmpty()
                        )
                    )
                }
                initiateTranslatedTextToAudioGeneration()
                Log.d("MainActivityViewModel", "translateTextUseCase: $response")
            }, onError = {
                _mainScreenState.update {
                    it.copy(
                        isLoading = false, errorMessage = it.errorMessage
                    )
                }

                Log.d("MainActivityViewModel", "translateTextUseCaseError: $it")
            })
        }
    }

    private fun customErrorHandler(error: AppError) {
        _mainScreenState.update {
            it.copy(
                isLoading = false, errorMessage = error.message.orEmpty()
            )
        }
    }
}

