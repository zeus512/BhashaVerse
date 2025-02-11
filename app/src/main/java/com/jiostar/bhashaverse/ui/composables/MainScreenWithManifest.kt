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
fun MainScreenWithManifest(
    viewModel: MainWithManifestActivityViewModel = hiltViewModel(),
    audioPlayerViewModel: AudioPlayerViewModel = hiltViewModel()
) {
    val state by viewModel.mainScreenState.collectAsState()
    val audioPlayerState by audioPlayerViewModel.audioPlayerState.collectAsState()
    val currentPosition by audioPlayerViewModel.currentPosition.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val audioManifest by audioPlayerViewModel.audioManifest.collectAsState()
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
                    audioPlayerViewModel.loadAndPlayAudio("main")
                }
            },
            currentPosition = { currentPosition },
            duration = { audioPlayerState.duration },
            onSeek = { position ->
                audioPlayerViewModel.seekTo(position)
            },
            thumbnailUrl = { state.audioThumbnailImage.orEmpty() },
            isBuffering = { audioPlayerState.isBuffering })
        audioManifest?.audioChunks?.forEachIndexed { index, chunk ->
            // Display URL or loading/error states
            when {
                chunk.translatedAudioUrl != null -> Text("Chunk ${index + 1}: ${chunk.translatedAudioUrl}")
                chunk.error != null -> Text("Chunk ${index + 1}: Error: ${chunk.error}")
                else -> CircularProgressIndicator() // Show progress indicator for loading
            }
        }

    }

}