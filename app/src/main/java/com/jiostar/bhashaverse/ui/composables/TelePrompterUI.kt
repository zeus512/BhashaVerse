package com.jiostar.bhashaverse.ui.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jiostar.bhashaverse.data.models.LyricLine
import kotlinx.coroutines.launch

@Composable
fun LyricsScreenUI(
    lyrics: List<LyricLine>,
    currentPlaybackPosition: Long,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    stopPlaying: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Find the index of the currently playing line
    val currentLineIndex by remember(currentPlaybackPosition, lyrics) {
        derivedStateOf {
            lyrics.indexOfLast { it.timestamp <= currentPlaybackPosition }
        }
    }

    // Scroll to the current line
    LaunchedEffect(currentLineIndex, isPlaying) {
        if (currentLineIndex >= 0 && isPlaying) {
            coroutineScope.launch {
                listState.animateScrollToItem(currentLineIndex)
            }
        } else {
            stopPlaying()
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(lyrics.size) { index ->
                    val lyricLine = lyrics[index]
                    val isCurrentLine = index == currentLineIndex

                    val textColor by animateColorAsState(
                        targetValue = if (isCurrentLine) Color.White else Color.Gray,
                        animationSpec = tween(durationMillis = 500),
                        label = "textColorAnimation"
                    )

                    val textStyle = if (isCurrentLine) {
                        TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = textColor
                        )
                    } else {
                        TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 18.sp,
                            color = textColor
                        )
                    }

                    Text(
                        text = lyricLine.text,
                        style = textStyle,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}