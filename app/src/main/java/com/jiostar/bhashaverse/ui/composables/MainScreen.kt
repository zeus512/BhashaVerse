package com.jiostar.bhashaverse.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.jiostar.bhashaverse.data.models.LyricLine
import com.jiostar.bhashaverse.ui.viewmodels.AudioPlayerViewModel
import com.jiostar.bhashaverse.ui.viewmodels.MainActivityViewModel
import com.jiostar.bhashaverse.ui.viewmodels.MainWithManifestActivityViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: MainActivityViewModel = hiltViewModel(),
    audioPlayerViewModel: AudioPlayerViewModel = hiltViewModel()
) {
    val state by viewModel.mainScreenState.collectAsState()
    val audioPlayerState by audioPlayerViewModel.audioPlayerState.collectAsState()
    val currentPosition by audioPlayerViewModel.currentPosition.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    var userPickedLanguageCode by remember { mutableStateOf("hi") }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    audioPlayerViewModel.pause()
                }

                Lifecycle.Event.ON_DESTROY -> {
                    audioPlayerViewModel.stop()
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(key1 = Unit) {
        while (true) {
            audioPlayerViewModel.updateCurrentPosition()
            delay(1000)
        }
    }
    LaunchedEffect(Unit) {
        // viewModel.fetchUsers()
    }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(enabled = true, state = rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        }
        if (state.errorMessage.isNotEmpty()) {
            // Show error message
            Text(text = state.errorMessage)
        }
        AudioPlayerUI(isPlaying = { audioPlayerState.isPlaying },
            onPlayPauseClick = {
                if (audioPlayerState.isPlaying) {
                    audioPlayerViewModel.pause()
                } else {
                    audioPlayerViewModel.play(
                        state.translatedAudioData.audioUri ?: state.audioFile.orEmpty()
                    )
                }
            },
            currentPosition = { currentPosition },
            duration = { audioPlayerState.duration },
            onSeek = { position ->
                audioPlayerViewModel.seekTo(position)
            },
            thumbnailUrl = { state.audioThumbnailImage.orEmpty() },
            isBuffering = { audioPlayerState.isBuffering })
        if (state.originLanguageData.text.isNotEmpty()) {
            HorizontalDivider()
            LanguageCard(
                language = state.originLanguageData.languageName,
                content = state.originLanguageData.text,
                languageCode = state.originLanguageData.languageCode
            )

            HorizontalDivider()

            HorizontalDivider()
            LanguageSelectorAndSendButton(onLanguageSelected = { languageCode ->
                // Do something with the selected language code
                userPickedLanguageCode = languageCode
                println("Selected language code: $languageCode")
            }, onSendClicked = {
                if (userPickedLanguageCode.isNotEmpty()) {
                    viewModel.translateText(
                        listOf(
                            userPickedLanguageCode
                        )
                    )
//                    viewModel.transcribeTextToUserGeneratedLanguage(
//                        listOf(
//                            userPickedLanguageCode
//                        )
//                    )

                }
                // Handle the send button click
                println("Send button clicked")
            })
        }
        if (state.translatedLanguageData.text.isNotEmpty()) {
            HorizontalDivider()
            LanguageCard(
                language = state.translatedLanguageData.languageName,
                content = state.translatedLanguageData.text,
                languageCode = state.translatedLanguageData.languageCode
            )
            if (state.translatedAudioData.audioUri != null) {
                HorizontalDivider()
                Text(text = "Translated Audio Generated")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        audioPlayerViewModel.play(
                            state.translatedAudioData.audioUri ?: state.audioFile.orEmpty()
                        )
                    }) {
                        Text(text = "Play New Audio")
                    }
                }
            }
        }


    }

}

@Composable
private fun OriginalTextUI() {
    val lyrics = listOf(
        LyricLine("Line 1: This is the first line.", 0),
        LyricLine("Line 2: The second line appears now.", 3000),
        LyricLine("Line 3: Here comes the third line.", 6000),
        LyricLine("Line 4: And the fourth line is here.", 9000),
        LyricLine("Line 5: We're now at the fifth line.", 12000),
        LyricLine("Line 6: Sixth line, moving along.", 15000),
        LyricLine("Line 7: Seventh line, almost there.", 18000),
        LyricLine("Line 8: Eighth line, the end is near.", 21000),
        LyricLine("Line 9: Ninth line, almost done.", 24000),
        LyricLine("Line 10: Tenth and final line.", 27000)
    )
    var currentPositionForText by remember { mutableStateOf(0L) }
    var isPlaying by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            coroutineScope.launch {
                while (true) {
                    delay(100)
                    currentPositionForText += 100
                }
            }
        }
    }
    LyricsScreenUI(lyrics = lyrics,
        currentPlaybackPosition = currentPositionForText,
        isPlaying = isPlaying,
        stopPlaying = {
            isPlaying = false
        })
}