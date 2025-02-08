package com.jiostar.bhashaverse.domain.usecase

import com.jiostar.bhashaverse.data.models.ExtractEventsRequest
import com.jiostar.bhashaverse.data.models.ExtractEventsResponse
import com.jiostar.bhashaverse.data.models.ProcessAudioRequest
import com.jiostar.bhashaverse.data.models.ProcessAudioResponse
import com.jiostar.bhashaverse.data.models.ProcessTextRequest
import com.jiostar.bhashaverse.data.models.ProcessTextResponse
import com.jiostar.bhashaverse.data.models.TranslateRequest
import com.jiostar.bhashaverse.data.models.TranslateResponse
import com.jiostar.bhashaverse.data.models.TranscribeRequest
import com.jiostar.bhashaverse.data.models.TranscribeResponse
import com.jiostar.bhashaverse.data.models.TtsAudioRequest
import com.jiostar.bhashaverse.data.models.TtsAudioResponse
import com.jiostar.bhashaverse.domain.AppRepository
import com.jiostar.bhashaverse.domain.UseCase
import com.jiostar.bhashaverse.domain.toResult

// Translate Text Use Case
class TranslateTextUseCase(private val repository: AppRepository) :
    UseCase<TranslateRequest, TranslateResponse> {
    override suspend operator fun invoke(params: TranslateRequest): Result<TranslateResponse> =
        repository.translateText(params).toResult()
}

// Transcribe Audio Use Case
class TranscribeAudioUseCase(private val repository: AppRepository) :
    UseCase<TranscribeRequest, TranscribeResponse> {
    override suspend operator fun invoke(params: TranscribeRequest): Result<TranscribeResponse> =
        repository.transcribeAudio(params).toResult()
}

// Extract Events Use Case
class ExtractEventsUseCase(private val repository: AppRepository) :
    UseCase<ExtractEventsRequest, ExtractEventsResponse> {
    override suspend operator fun invoke(params: ExtractEventsRequest): Result<ExtractEventsResponse> =
        repository.extractEvents(params).toResult()
}

// Process Audio Use Case
class ProcessTextUseCase(private val repository: AppRepository) :
    UseCase<ProcessTextRequest, ProcessTextResponse> {
    override suspend operator fun invoke(params: ProcessTextRequest): Result<ProcessTextResponse> =
        repository.processText(params).toResult()
}

// Process Audio Use Case
class ProcessAudioUseCase(private val repository: AppRepository) :
    UseCase<ProcessAudioRequest, ProcessAudioResponse> {
    override suspend operator fun invoke(params: ProcessAudioRequest): Result<ProcessAudioResponse> =
         repository.processAudio(params).toResult()
}

// TTS Telugu Use Case
class TtsAudioUseCase(private val repository: AppRepository) :
    UseCase<TtsAudioRequest, TtsAudioResponse> {
    override suspend operator fun invoke(params: TtsAudioRequest): Result<TtsAudioResponse> =
        repository.ttsTelugu(params).toResult()
}