package com.jiostar.bhashaverse.domain

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import okhttp3.OkHttpClient
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class AudioPlayer(context: Context, okMediaHttpClient: OkHttpClient) {


    // Create a custom TrustManager
    private val dataSourceFactory: DataSource.Factory = OkHttpDataSource.Factory(okMediaHttpClient)
    val player: ExoPlayer = ExoPlayer.Builder(context).build()
    private var isPrepared = false

    @OptIn(UnstableApi::class)
    fun play(url: String, startPosition: Long = 0L, updateMediaItem: Boolean = false) {
        if (!isPrepared || updateMediaItem) {
            val mediaItem = createMediaItem(url)
            val mediaSource = createMediaSource(mediaItem)
            player.setMediaSource(mediaSource)
            player.prepare()
            isPrepared = true
        }
        player.seekTo(startPosition) // Seek to the start position
        player.play()
    }

    fun pause() {
        player.pause()
    }

    fun stop() {
        player.stop()
        isPrepared = false
    }

    fun release() {
        player.release()
    }

    @OptIn(UnstableApi::class)
    private fun createMediaSource(mediaItem: MediaItem): MediaSource {
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mediaItem)
    }

    private fun createMediaItem(url: String): MediaItem {
        return MediaItem.Builder()
            .setUri(url)
            .build()
    }
}