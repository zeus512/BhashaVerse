package com.jiostar.bhashaverse.domain


import com.jiostar.bhashaverse.data.ApiService
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
import retrofit2.Response

class AppRepository(private val apiService: ApiService) {
    suspend fun translateText(request: TranslateRequest): Response<TranslateResponse> {
        return apiService.translateText(request)
    }

    suspend fun transcribeAudio(request: TranscribeRequest): Response<TranscribeResponse> {
        return apiService.transcribeAudio(request)
    }

    suspend fun extractEvents(request: ExtractEventsRequest): Response<ExtractEventsResponse> {
        return apiService.extractEvents(request)
    }


    suspend fun processText(request: ProcessTextRequest): Response<ProcessTextResponse> {
        return apiService.processText(request)
    }

    suspend fun processAudio(request: ProcessAudioRequest): Response<ProcessAudioResponse> {
        return apiService.processAudio(request)
    }

    suspend fun ttsTelugu(request: TtsAudioRequest): Response<TtsAudioResponse> {
        return apiService.ttsTelugu(request)
    }
}