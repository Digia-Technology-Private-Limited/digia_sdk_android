package com.digia.digiaui.framework.internals

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.net.URLDecoder
import androidx.core.net.toUri


@Composable
fun InternalYoutubePlayer(
    videoUrl: String,
    isMuted: Boolean = false,
    loop: Boolean = false,
    autoPlay: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val videoId = remember(videoUrl) {
        extractVideoId(videoUrl)
    }

    var youTubePlayer by remember { mutableStateOf<YouTubePlayer?>(null) }

    AndroidView(
        modifier = modifier,
        factory = {
            YouTubePlayerView(context).apply {
                lifecycleOwner.lifecycle.addObserver(this)

                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {

                    override fun onReady(player: YouTubePlayer) {
                        youTubePlayer = player
                    }

                    override fun onStateChange(
                        player: YouTubePlayer,
                        state: PlayerConstants.PlayerState
                    ) {
                        if (loop && state == PlayerConstants.PlayerState.ENDED) {
                            player.seekTo(0f)
                            player.play()
                        }
                    }
                })
            }
        }
    )

    // ▶️ Load / change video safely
    LaunchedEffect(videoId, youTubePlayer) {
        val player = youTubePlayer ?: return@LaunchedEffect
        if (videoId.isNotBlank()) {
            if (autoPlay) {
                player.loadVideo(videoId, 0f)
            } else {
                player.cueVideo(videoId, 0f)
            }
        }
    }

    // 🔇 Mute handling
    LaunchedEffect(isMuted, youTubePlayer) {
        youTubePlayer?.let {
            if (isMuted) it.mute() else it.unMute()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            youTubePlayer = null
        }
    }
}



private fun extractVideoId(input: String): String {
    if (input.isBlank()) return ""

    // Already looks like a videoId
    if (!input.contains("/") && input.length in 10..15) {
        return input
    }

    return try {
        val uri = input.toUri()

        when {
            // youtu.be/VIDEO_ID
            uri.host?.contains("youtu.be") == true ->
                uri.lastPathSegment ?: ""

            // youtube.com/shorts/VIDEO_ID
            uri.path?.startsWith("/shorts/") == true ->
                uri.pathSegments.getOrNull(1) ?: ""

            // youtube.com/embed/VIDEO_ID
            uri.path?.startsWith("/embed/") == true ->
                uri.pathSegments.getOrNull(1) ?: ""

            // youtube.com/watch?v=VIDEO_ID
            else ->
                uri.getQueryParameter("v") ?: ""
        }
    } catch (e: Exception) {
        ""
    }
}

