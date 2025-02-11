package com.jiostar.bhashaverse.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import com.jiostar.bhashaverse.data.di.DefaultOkHttpClient
import com.jiostar.bhashaverse.data.models.AudioChunk
import com.jiostar.bhashaverse.data.models.AudioManifestResponse
import com.jiostar.bhashaverse.data.models.ChunkUpdate
import com.jiostar.bhashaverse.domain.AudioPlayer
import com.jiostar.bhashaverse.domain.usecase.GetManifestUseCase
import com.jiostar.bhashaverse.domain.usecase.StartAudioProcessing
import com.jiostar.bhashaverse.ui.state.AudioPlayerState
import com.jiostar.bhashaverse.ui.utils.Constants
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit
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

    private var eventSource: EventSource? = null


    val listener =  object : EventSourceListener() {

        override fun onEvent(
            eventSource: EventSource,
            id: String?,
            type: String?,
            data: String
        ) {
            println("SSE Event: id=$id, type=$type, data=$data") // Detailed log

            // Handle SSE event (same as before)
            val adapter = moshi.adapter(ChunkUpdate::class.java)

            val update = try {
                adapter.fromJson(data)
            } catch (e: Exception) { // Catch any exception during parsing
                println("Error parsing JSON: ${e.message}")
                null
            }

            val chunkIndex = update?.chunkIndex ?: return // Convert to Int

            if (update.audioUrl != null) {
                _audioManifest.update { currentManifest ->
                    currentManifest?.copy(
                        audioChunks = currentManifest.audioChunks.toMutableList()
                            .apply {
                                set(
                                    chunkIndex,
                                    AudioChunk(translatedAudioUrl = update.audioUrl)
                                )
                            }
                    )
                }
                if (chunkIndex == 0 || chunkIndex == audioManifest.value!!.audioChunks.indexOfFirst { it.translatedAudioUrl == null } - 1) {
                    playNextChunk()
                }
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

    init {
        // Start SSE connection (using OkHttp's EventSource)
        val request =
            Request.Builder()
                .url("${Constants.BASE_URL}/stream")
                .addHeader("Accept", "text/event-stream")
                .build() // Correct URL
        eventSource = EventSources.createFactory(OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)  // No timeout for long-lived connections like SSE
            .connectTimeout(30, TimeUnit.SECONDS)   // Set a reasonable connection timeout
            .build())
            .newEventSource(request, listener)
    }
    fun loadAndPlayAudio(audioId: String) {
        viewModelScope.launch {
            try {
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

    private fun playNextChunk() {
        viewModelScope.launch {
            val manifest = audioManifest.value ?: return@launch
            val nextChunkIndex = manifest.audioChunks.indexOfFirst { it.translatedAudioUrl == null }

            if (nextChunkIndex == -1) {
                // All chunks played
                return@launch
            }

            val chunk = manifest.audioChunks[nextChunkIndex]

            if (chunk.translatedAudioUrl != null) {
                audioPlayer.play(chunk.translatedAudioUrl)

                _audioPlayerState.update {
                    it.copy(isPlaying = true, isBuffering = false) // Update isPlaying
                }

                // Preload the next chunk (optional)
                if (nextChunkIndex + 1 < manifest.audioChunks.size) {
                    // ... (Preload logic)
                }

            } else if (chunk.error != null) {
                // Handle error
                println("Error playing chunk: ${chunk.error}")
                _audioPlayerState.update { it.copy(isBuffering = false) } // Stop buffering
            } else {
                _audioPlayerState.update { it.copy(isBuffering = true) } // Start buffering
            }
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
        eventSource?.cancel()
    }
}