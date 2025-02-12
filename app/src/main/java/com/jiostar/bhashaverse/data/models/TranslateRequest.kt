package com.jiostar.bhashaverse.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Translate Text
@JsonClass(generateAdapter = true)
data class TranslateRequest(
    val text: String,
    @Json(name = "target_languages") val targetLanguages: List<String>
)

@JsonClass(generateAdapter = true)
data class TranslateResponse(
    val translations: Map<String, String> // Language code to translated text
)

// Transcribe Audio
@JsonClass(generateAdapter = true)
data class TranscribeRequest(
    @Json(name = "audio_uri") val audioUri: String,
    @Json(name = "language_code") val languageCode: String

)

@JsonClass(generateAdapter = true)
data class TranscribeResponse(
    @Json(name = "transcript")
    val transcript: String,
    @Json(name = "transcript_with_timestamps")
    val transcriptWithTimestamps: List<TranscriptWithTimestamp>
)

@JsonClass(generateAdapter = true)
data class TranscriptWithTimestamp(
    @Json(name = "end_time")
    val endTime: Double,
    @Json(name = "start_time")
    val startTime: Double,
    @Json(name = "transcript")
    val transcript: String
)

// Extract Events from Transcript
@JsonClass(generateAdapter = true)
data class ExtractEventsRequest(
    val transcript: String
)

@JsonClass(generateAdapter = true)
data class ExtractEventsResponse(
    val events: List<Event>
)

@JsonClass(generateAdapter = true)
data class Event(
    val entity: String? = null,
    val event: String
)


// Process Text with Translation
@JsonClass(generateAdapter = true)
data class ProcessTextRequest(
    @Json(name = "transcript") val transcript: String,
    @Json(name = "target_languages") val targetLanguages: List<String>,
    @Json(name = "translate_transcript") val translateTranscript: Boolean,
    @Json(name = "translate_events") val translateEvents: Boolean
)

@JsonClass(generateAdapter = true)
data class ProcessTextResponse(
    val events: List<Event>,
    val translations: Translations
)

// Process Audio with Translation
@JsonClass(generateAdapter = true)
data class ProcessAudioRequest(
    @Json(name = "audio_uri") val audioUri: String,
    @Json(name = "language_code") val languageCode: String,
    @Json(name = "target_languages") val targetLanguages: List<String>,
    @Json(name = "translate_transcript") val translateTranscript: Boolean,
    @Json(name = "translate_events") val translateEvents: Boolean
)

@JsonClass(generateAdapter = true)
data class ProcessAudioResponse(
    val events: List<Event>,
    val transcript: String,
    val translations: Translations
)

@JsonClass(generateAdapter = true)
data class Translations(
    //val events: Map<String, Map<String, String>>, // Event type to (entity to (language code to translated text))
    val transcript: Map<String, String> // Language code to translated text
)

// Text-to-Speech (TTS) for Telugu
@JsonClass(generateAdapter = true)
data class TtsAudioRequest(
    val text: String,
    @Json(name = "language_code") val languageCode: String,
    @Json(name = "voice_name") val voiceName: String? = null
)

@JsonClass(generateAdapter = true)
data class TtsAudioResponse(
    @Json(name = "audio_url") val audioUrl: String
)

@JsonClass(generateAdapter = true)
data class AudioChunk(
    @Json(name = "translated_audio_url")  val translatedAudioUrl: String? = null,
    @Json(name = "error")  val error: String? = null,
    val isPlaying: AudioChunkState = AudioChunkState.AVAILABLE,
)
@JsonClass(generateAdapter = true)
data class AudioManifestResponse(
    @Json(name = "audio_chunks") val audioChunks: List<AudioChunk>
)

enum class AudioChunkState {
    PLAYING,
    AVAILABLE,
    PLAYED
}