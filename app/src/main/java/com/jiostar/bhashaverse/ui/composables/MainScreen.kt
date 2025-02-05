package com.jiostar.bhashaverse.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.jiostar.bhashaverse.ui.viewmodels.AudioPlayerViewModel
import com.jiostar.bhashaverse.ui.viewmodels.MainActivityViewModel
import kotlinx.coroutines.delay

@Composable
fun MainScreen(
    viewModel: MainActivityViewModel = hiltViewModel(),
    audioPlayerViewModel: AudioPlayerViewModel = hiltViewModel()
) {
    val state by viewModel.mainScreenState.collectAsState()
    val audioPlayerState by audioPlayerViewModel.audioPlayerState.collectAsState()
    val currentPosition by audioPlayerViewModel.currentPosition.collectAsState()

    LaunchedEffect(key1 = Unit) {
        while (true) {
            audioPlayerViewModel.updateCurrentPosition()
            delay(1000)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.fetchUsers()
    }
    Column(
        Modifier
            .fillMaxSize()
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        }
        if (state.errorMessage.isNotEmpty()) {
            // Show error message
            Text(text = state.errorMessage)
        }
        AudioPlayerUI(
            isPlaying = { audioPlayerState.isPlaying },
            onPlayPauseClick = {
                if (audioPlayerState.isPlaying) {
                    audioPlayerViewModel.pause()
                } else {
                    audioPlayerViewModel.play(state.audioFile.orEmpty())
                }
            },
            currentPosition = { currentPosition },
            duration = { audioPlayerState.duration },
            onSeek = { position ->
                audioPlayerViewModel.seekTo(position)
            },
            thumbnailUrl = { state.audioThumbnailImage.orEmpty() },
            isBuffering = { audioPlayerState.isBuffering }
        )
    }

}