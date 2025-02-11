package com.jiostar.bhashaverse.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true) // For Moshi code generation
data class ChunkUpdate(
    @Json(name="chunk_index") val chunkIndex: Int,
    @Json(name="audio_url") val audioUrl: String? = null,
    val error: String? = null
)