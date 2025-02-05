package com.jiostar.bhashaverse.ui.composables

import android.content.Context
import androidx.compose.foundation.layout.add

import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.network.NetworkFetcher
import coil3.network.okhttp.asNetworkClient
import coil3.util.DebugLogger
import okhttp3.OkHttpClient

@OptIn(ExperimentalCoilApi::class)
fun Context.createImageLoader(): ImageLoader {
    return ImageLoader.Builder(context = this)
        .components {
            add(
                factory = NetworkFetcher.Factory(
                    networkClient = { OkHttpClient.Builder().build().asNetworkClient() },
                )
            )
        }
        .logger(DebugLogger())
        .build()
}