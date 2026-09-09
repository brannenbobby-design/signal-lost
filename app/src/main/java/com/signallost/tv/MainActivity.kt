package com.signallost.tv

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.signallost.tv.ui.theme.SignalLostTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SignalLostTheme { SignalLostApp() } }
    }
}

data class Channel(val number: Int, val name: String, val videos: List<String>)

private val channels = listOf(
    Channel(1, "90s", listOf("fJ9rUzIMcZQ", "1w7OgIMMRc4", "hTWKbfoikeg")),
    Channel(2, "CLASSIC ROCK", listOf("1w7OgIMMRc4", "fJ9rUzIMcZQ")),
    Channel(3, "COUNTRY", listOf("r7qovpFAGrQ")),
    Channel(4, "HIP HOP", listOf("_JZom_gVfuw")),
    Channel(5, "POP", listOf("C-u5WLJ9Yk4")),
    Channel(6, "ALTERNATIVE", listOf("hTWKbfoikeg", "3mbBbFH9fAg", "PbgKEjNBHqM")),
    Channel(7, "ONE-HIT WONDERS", listOf("DL7-CKirWZE")),
    Channel(8, "CHILL", listOf("ZbZSe6N_BXs"))
)

@Composable
fun SignalLostApp() {
    var channelIndex by remember { mutableIntStateOf(0) }
    var videoIndex by remember { mutableIntStateOf(0) }
    var player by remember { mutableStateOf<YouTubePlayer?>(null) }
    var playing by remember { mutableStateOf(false) }

    val channel = channels[channelIndex]
    val videoId = channel.videos[videoIndex % channel.videos.size]

    fun chooseChannel(index: Int) {
        channelIndex = index
        videoIndex = 0
        playing = true
    }

    fun next() {
        videoIndex = (videoIndex + 1) % channel.videos.size
        playing = true
    }

    fun previous() {
        videoIndex = (videoIndex - 1 + channel.videos.size) % channel.videos.size
        playing = true
    }

    BoxWithConstraints(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        val sourceW = 640f
        val sourceH = 594f
        val screenRatio = maxWidth.value / maxHeight.value
        val artRatio = sourceW / sourceH
        val artWidth: Dp
        val artHeight: Dp
        if (screenRatio > artRatio) {
            artHeight = maxHeight
            artWidth = maxHeight * artRatio
        } else {
            artWidth = maxWidth
            artHeight = maxWidth / artRatio
        }

        Box(Modifier.size(artWidth, artHeight)) {
            Image(
                painter = painterResource(R.drawable.signal_lost_room),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            // Live player placed directly over the CRT glass in the artwork.
            OverlayRect(artWidth, artHeight, .402f, .272f, .377f, .272f) {
                SignalLostYouTubePlayer(
                    videoId = videoId,
                    autoplay = playing,
                    onPlayerReady = { player = it },
                    onVideoEnded = { next() },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // VHS tape hit areas. The blue border moves with the selected channel.
            channels.indices.forEach { index ->
                val y = .232f + index * .0484f
                OverlayRect(artWidth, artHeight, .126f, y, .215f, .044f) {
                    Box(
                        Modifier.fillMaxSize()
                            .border(
                                width = if (index == channelIndex) 2.dp else 0.dp,
                                color = if (index == channelIndex) Color(0xFF20C9FF) else Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { chooseChannel(index) }
                    )
                }
            }

            // Invisible controls aligned to the buttons already drawn in the artwork.
            TapRect(artWidth, artHeight, .307f, .776f, .105f, .078f) { previous() }
            TapRect(artWidth, artHeight, .447f, .766f, .111f, .095f) {
                playing = true
                player?.play()
            }
            TapRect(artWidth, artHeight, .591f, .776f, .105f, .078f) { next() }
        }
    }
}

@Composable
private fun OverlayRect(
    parentW: Dp,
    parentH: Dp,
    x: Float,
    y: Float,
    w: Float,
    h: Float,
    content: @Composable () -> Unit
) {
    Box(
        Modifier
            .offset(parentW * x, parentH * y)
            .size(parentW * w, parentH * h)
    ) { content() }
}

@Composable
private fun TapRect(parentW: Dp, parentH: Dp, x: Float, y: Float, w: Float, h: Float, onClick: () -> Unit) {
    OverlayRect(parentW, parentH, x, y, w, h) {
        Box(Modifier.fillMaxSize().clickable { onClick() })
    }
}

@Composable
private fun SignalLostYouTubePlayer(
    videoId: String,
    autoplay: Boolean,
    onPlayerReady: (YouTubePlayer) -> Unit,
    onVideoEnded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    var playerView by remember { mutableStateOf<YouTubePlayerView?>(null) }
    var youTubePlayer by remember { mutableStateOf<YouTubePlayer?>(null) }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            YouTubePlayerView(ctx).apply {
                enableAutomaticInitialization = false
                activity?.lifecycle?.addObserver(this)
                val options = IFramePlayerOptions.Builder(ctx)
                    .controls(0)
                    .fullscreen(0)
                    .autoplay(0)
                    .ivLoadPolicy(3)
                    .build()
                initialize(object : AbstractYouTubePlayerListener() {
                    override fun onReady(player: YouTubePlayer) {
                        youTubePlayer = player
                        onPlayerReady(player)
                        if (autoplay) player.loadVideo(videoId, 0f) else player.cueVideo(videoId, 0f)
                    }

                    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                        if (state == PlayerConstants.PlayerState.ENDED) onVideoEnded()
                    }
                }, options)
                playerView = this
            }
        }
    )

    LaunchedEffect(youTubePlayer, videoId, autoplay) {
        val p = youTubePlayer ?: return@LaunchedEffect
        if (autoplay) p.loadVideo(videoId, 0f) else p.cueVideo(videoId, 0f)
    }

    DisposableEffect(Unit) {
        onDispose {
            playerView?.release()
            playerView = null
            youTubePlayer = null
        }
    }
}

private tailrec fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
