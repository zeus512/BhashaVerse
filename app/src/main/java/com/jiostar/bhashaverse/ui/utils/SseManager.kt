package com.jiostar.bhashaverse.ui.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit

class SseManager {
    private val sseClient: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.SECONDS) // No read timeout for SSE
        .connectTimeout(15, TimeUnit.SECONDS) // Reasonable connection timeout
        .build()

    private val eventSourceFactory = EventSources.createFactory(sseClient)
    private var eventSource: EventSource? = null

    fun connectToSse(url: String, listener: EventSourceListener) {
        val request = Request.Builder()
            .url(url)
            .addHeader("Accept", "text/event-stream")
            .addHeader("Connection", "keep-alive")
            .build()

        eventSource = eventSourceFactory.newEventSource(request, listener)
    }

    fun disconnect() {
        eventSource?.cancel()
        eventSource = null

    }
}