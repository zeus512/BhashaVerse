package com.jiostar.bhashaverse.data

import com.jiostar.bhashaverse.data.models.ExtractEventsRequest
import com.jiostar.bhashaverse.data.models.ExtractEventsResponse
import com.jiostar.bhashaverse.data.models.ProcessAudioRequest
import com.jiostar.bhashaverse.data.models.ProcessAudioResponse
import com.jiostar.bhashaverse.data.models.ProcessTextRequest
import com.jiostar.bhashaverse.data.models.ProcessTextResponse
import com.jiostar.bhashaverse.data.models.TranscribeRequest
import com.jiostar.bhashaverse.data.models.TranscribeResponse
import com.jiostar.bhashaverse.data.models.TranslateRequest
import com.jiostar.bhashaverse.data.models.TranslateResponse
import com.jiostar.bhashaverse.data.models.TtsAudioRequest
import com.jiostar.bhashaverse.data.models.TtsAudioResponse
import com.jiostar.bhashaverse.data.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("/users")
    suspend fun getUsers(): Response<List<User>>

    @POST("/translate")
    suspend fun translateText(@Body request: TranslateRequest): Response<TranslateResponse>

    @POST("/transcribe")
    suspend fun transcribeAudio(@Body request: TranscribeRequest): Response<TranscribeResponse>

    @POST("/extract_events")
    suspend fun extractEvents(@Body request: ExtractEventsRequest): Response<ExtractEventsResponse>

    @POST("/process_text")
    suspend fun processText(@Body request: ProcessTextRequest): Response<ProcessTextResponse>

    @POST("/process_audio")
    suspend fun processAudio(@Body request: ProcessAudioRequest): Response<ProcessAudioResponse>

    @POST("/tts")
    suspend fun ttsTelugu(@Body request: TtsAudioRequest): Response<TtsAudioResponse>
}

data class Dummy(val dummy: String)