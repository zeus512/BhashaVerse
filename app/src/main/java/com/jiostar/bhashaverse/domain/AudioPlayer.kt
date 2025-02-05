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

class AudioPlayer(private val context: Context, private val okMediaHttpClient: OkHttpClient) {


    private val dataSourceFactory: DataSource.Factory
    val player: ExoPlayer
    private var isPrepared = false

    init {
        // Create a custom TrustManager
        //val trustManager = createTrustManager()

        // Create an SSLSocketFactory using the custom TrustManager
        //val sslSocketFactory = createSSLSocketFactory(trustManager)

        // Create an OkHttpClient with the custom SSLSocketFactory
//        okHttpClient = OkHttpClient.Builder()
//            .sslSocketFactory(sslSocketFactory, trustManager)
//            .hostnameVerifier { _, _ -> true } // Trust all hostnames (use with caution!)
//            .build()

        dataSourceFactory = OkHttpDataSource.Factory(okMediaHttpClient)
        player = ExoPlayer.Builder(context).build()
    }

    private fun createTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                // Accept all client certificates (use with caution!)
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                // Accept all server certificates (use with caution!)
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
    }

    private fun createSSLSocketFactory(trustManager: X509TrustManager): SSLSocketFactory {
        return try {
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
            sslContext.socketFactory
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to create SSLSocketFactory", e)
        } catch (e: KeyManagementException) {
            throw RuntimeException("Failed to create SSLSocketFactory", e)
        }
    }

    @OptIn(UnstableApi::class)
    fun play(url: String, startPosition: Long = 0L) {
        if (!isPrepared) {
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