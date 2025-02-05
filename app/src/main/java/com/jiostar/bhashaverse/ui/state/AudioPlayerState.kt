package com.jiostar.bhashaverse.ui.state

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val enableControls: Boolean = true,
    val duration: Float = 0f,
    val isBuffering: Boolean = false,
    val playbackPosition: Long = 0L,

) {
}