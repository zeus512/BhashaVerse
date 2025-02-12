package com.jiostar.bhashaverse.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import com.jiostar.bhashaverse.data.di.DefaultOkHttpClient
import com.jiostar.bhashaverse.data.models.AudioChunk
import com.jiostar.bhashaverse.data.models.AudioChunkState
import com.jiostar.bhashaverse.data.models.AudioManifestResponse
import com.jiostar.bhashaverse.data.models.ChunkUpdate
import com.jiostar.bhashaverse.domain.AudioPlayer
import com.jiostar.bhashaverse.domain.usecase.GetManifestUseCase
import com.jiostar.bhashaverse.domain.usecase.StartAudioProcessing
import com.jiostar.bhashaverse.ui.state.AudioPlayerState
import com.jiostar.bhashaverse.ui.utils.Constants
import com.jiostar.bhashaverse.ui.utils.SseManager
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    private val audioPlayer: AudioPlayer,
    private val getManifestUseCase: GetManifestUseCase,
    private val moshi: Moshi,
    @DefaultOkHttpClient private val okHttpClient: OkHttpClient,
    private val startAudioProcessing: StartAudioProcessing,
) : ViewModel() {

    private val _audioPlayerState = MutableStateFlow(AudioPlayerState())
    val audioPlayerState: StateFlow<AudioPlayerState> = _audioPlayerState

    private val _currentPosition = MutableStateFlow(0f)
    val currentPosition: StateFlow<Float> = _currentPosition

    private val _audioManifest = MutableStateFlow<AudioManifestResponse?>(null)
    val audioManifest: StateFlow<AudioManifestResponse?> = _audioManifest

    val sseManager = SseManager()
    private var isPlaying = false

    val listener = object : EventSourceListener() {

        override fun onEvent(
            eventSource: EventSource,
            id: String?,
            type: String?,
            data: String
        ) {
            println("SSE Event: id=$id, type=$type, data=$data") // Detailed log

            // Handle SSE event (same as before)
            val adapter = moshi.adapter(ChunkUpdate::class.java)

            val chunk = try {
                adapter.fromJson(data)
            } catch (e: Exception) { // Catch any exception during parsing
                println("Error parsing JSON: ${e.message}")
                null
            }

            val chunkIndex = chunk?.chunkIndex ?: return // Convert to Int

            if (chunk.audioUrl != null) {
                println("Chunk $chunkIndex audio URL: ${chunk.audioUrl}")

                _audioManifest.update { currentManifest ->
                    currentManifest?.copy(
                        audioChunks = currentManifest.audioChunks.toMutableList()
                            .apply {
                                set(
                                    chunkIndex,
                                    AudioChunk(translatedAudioUrl = chunk.audioUrl)
                                )
                            }
                    )
                }
                println("Trying to play chunk - $chunkIndex $chunk.audioUrl")
                playNextChunk()

            } else {
                //Handle error
            }
        }

        override fun onFailure(
            eventSource: EventSource,
            t: Throwable?,
            response: Response?
        ) {
            // Handle failure
            println("SSE connection failed: ${eventSource.request()} ${t?.message} ${response?.message}")
            println("SSE connection failed2: ${response?.code} - ${response?.body} -${response?.request}=${eventSource.request()}")

        }

        override fun onOpen(
            eventSource: EventSource,
            response: Response
        ) {
            //Start processing on the server
            viewModelScope.launch {
                try {
                    println("Processing start request began")
                } catch (e: Exception) {
                    println("Processing start request failed")
                }
            }
        }

        override fun onClosed(eventSource: EventSource) {
            println("SSE connection closed.")
        }
    }


    fun loadAndPlayAudio(audioId: String) {
        viewModelScope.launch {
            try {
                sseManager.connectToSse("${Constants.BASE_URL}/stream", listener)
                getManifestUseCase(audioId).onSuccess {
                    _audioManifest.value = it

                    startAudioProcessing(audioId).onSuccess {

                    }.onFailure { println("Processing start request failed") }

                }
                    .onFailure {
                        // Handle failures
                    }
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }

    private fun updateAudioChunk(chunk: AudioChunk, index: Int) {
        _audioManifest.update { currentManifest ->
            currentManifest?.copy(
                audioChunks = currentManifest.audioChunks.toMutableList()
                    .apply {
                        set(
                            index,
                            chunk
                        )
                    }
            )
        }
    }


    private fun playNextChunk() {
        if (isPlaying) return // Prevent concurrent calls
        isPlaying = true // Set the flag
        if (audioManifest.value?.audioChunks?.any { it.isPlaying == AudioChunkState.PLAYING } == true) return // Prevent null pointer exception

        viewModelScope.launch {
            println("inside next chunk function")
            val manifest = audioManifest.value ?: return@launch

            val nextChunkIndex = manifest.audioChunks.indexOfFirst {
                it.translatedAudioUrl != null && it.isPlaying != AudioChunkState.PLAYED && it.error == null
            }

            println("next chunk index: $nextChunkIndex")

            if (nextChunkIndex == -1) {
                // ... (All chunks played or no chunks ready - same logic as before)
                isPlaying = false // Reset the flag
                return@launch
            }

            val chunk = manifest.audioChunks[nextChunkIndex]
            println("Playing audio - ${chunk.translatedAudioUrl}")

            if (chunk.translatedAudioUrl != null) {
                updateAudioChunk(chunk.copy(isPlaying = AudioChunkState.PLAYING), nextChunkIndex)

                chunk.translatedAudioUrl.let { audioPlayer.play("${Constants.BASE_URL}$it", updateMediaItem = true) }

                _audioPlayerState.update {
                    it.copy(isPlaying = true, isBuffering = false)
                }

                /// ExoPlayer Listeners (Corrected)
                audioPlayer.player.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        if (state == Player.STATE_ENDED) {
                            updateAudioChunk(chunk.copy(isPlaying = AudioChunkState.PLAYED), nextChunkIndex)
                            isPlaying = false
                            playNextChunk()
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        updateAudioChunk(chunk.copy(isPlaying = AudioChunkState.AVAILABLE), nextChunkIndex)
                        isPlaying = false
                        _audioPlayerState.update { it.copy(isBuffering = false, isPlaying = false) } // Update error message in state
                        Log.e("Exoplayer Error", error.toString())
                    }
                })

            } else if (chunk.error != null) {
                // ... (error handling - no changes)
            } else {
                // ... (buffering state update - no changes)
            }
            isPlaying = false // Reset the flag after playNextChunk finishes
        }
    }

    private var currentPlayingUrl: String? = null
    fun play(url: String) {
        viewModelScope.launch {
            if (currentPlayingUrl != url) {
                audioPlayer.stop()
                currentPlayingUrl = url
            }
            val currentPlaybackPosition = audioPlayerState.value.playbackPosition
            audioPlayer.play(
                url,
                currentPlaybackPosition,
                updateMediaItem = currentPlayingUrl != url
            )
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
        sseManager.disconnect()
    }
}