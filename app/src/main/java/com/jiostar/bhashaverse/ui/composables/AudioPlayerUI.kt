package com.jiostar.bhashaverse.ui.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.jiostar.bhashaverse.R
import com.jiostar.bhashaverse.ui.utils.LocalImageLoader

@Composable
fun AudioPlayerUI(
    isPlaying: () -> Boolean,
    isBuffering: () -> Boolean,
    onPlayPauseClick: () -> Unit,
    currentPosition: () -> Float,
    duration: () -> Float,
    onSeek: (Float) -> Unit,
    thumbnailUrl: () -> String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.LightGray), // Placeholder background
            contentAlignment = Alignment.Center
        ) {

            val imageLoader = LocalImageLoader.current
            AsyncImage(
                model = thumbnailUrl(),
                imageLoader = imageLoader,
                contentDescription = "Audio Thumbnail",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onError = { error ->
                    // Debugging: Log the error
                    Log.e("AudioPlayerUI", "AsyncImage Error: ${error.result.throwable}")
                },
                onSuccess = {
                    // Debugging: Log the success
                    Log.d("AudioPlayerUI", "AsyncImage Success")
                }, onLoading = {
                    // Debugging: Log the loading
                    Log.d("AudioPlayerUI", "AsyncImage Loading")
                },
                placeholder = painterResource(id = R.drawable.ic_launcher_background)
            )
            if (isBuffering()) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    color = Color.Blue
                )
            }
            IconButton(
                onClick = onPlayPauseClick,
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.Black.copy(alpha = 0.5f)) // Semi-transparent background
            ) {

                Icon(
                    imageVector = if (isPlaying()) PauseBtn else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying()) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Seekbar
        if (duration() > 0f) {
            Slider(
                value = currentPosition(),
                onValueChange = { onSeek(it) },
                valueRange = 0f..duration(),
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.Blue,
                    activeTrackColor = Color.Blue,
                    inactiveTrackColor = Color.Gray
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Time indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = formatTime(currentPosition().toLong()),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = formatTime(duration().toLong()),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

fun formatTime(timeMs: Long): String {
    if (timeMs <= 0) {
        return "00:00"
    }
    val totalSeconds = timeMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}