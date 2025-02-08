package com.jiostar.bhashaverse.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import com.jiostar.bhashaverse.domain.AudioPlayer
import com.jiostar.bhashaverse.ui.state.AudioPlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.toFloat

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    private val _audioPlayerState = MutableStateFlow(AudioPlayerState())
    val audioPlayerState: StateFlow<AudioPlayerState> = _audioPlayerState

    private val _currentPosition = MutableStateFlow(0f)
    val currentPosition: StateFlow<Float> = _currentPosition

    private var currentPlayingUrl: String? = null
    fun play(url: String) {
        viewModelScope.launch {
            if (currentPlayingUrl != url) {
                audioPlayer.stop()
                currentPlayingUrl = url
            }
            val currentPlaybackPosition = audioPlayerState.value.playbackPosition
            audioPlayer.play(url, currentPlaybackPosition, updateMediaItem = currentPlayingUrl != url)
            val duration = audioPlayer.player.duration
            _audioPlayerState.update {
                it.copy(
                    isPlaying = true,
                    duration = if (duration == C.TIME_UNSET) 0f else duration.toFloat(),
                    isBuffering = true

                )
            }
        }
    }

    fun pause() {
        audioPlayer.pause()
        _audioPlayerState.update {
            it.copy(
                isPlaying = false,
                playbackPosition = audioPlayer.player.currentPosition
            )
        }
    }

    fun stop() {
        audioPlayer.stop()
        _audioPlayerState.update {
            it.copy(
                isPlaying = false,
                playbackPosition = 0L
            )
        }
        _currentPosition.value = 0f
    }

    fun seekTo(position: Float) {
        audioPlayer.player.seekTo(position.toLong())
    }

    fun updateCurrentPosition() {
        viewModelScope.launch {
            val currentPosition = audioPlayer.player.currentPosition
            val duration = audioPlayer.player.duration
            _currentPosition.value = currentPosition.toFloat()
            _audioPlayerState.update {
                it.copy(
                    duration = if (duration == C.TIME_UNSET) 0f else duration.toFloat(),
                    isBuffering = audioPlayer.player.playbackState == androidx.media3.common.Player.STATE_BUFFERING,
                    playbackPosition = currentPosition
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}