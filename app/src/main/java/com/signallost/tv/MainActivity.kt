package com.signallost.tv

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

data class Channel(val number: Int, val name: String, val shortName: String, val videos: List<String>)

private val channels = listOf(
    Channel(1, "90s ROCK", "90s ROCK", listOf("fJ9rUzIMcZQ", "1w7OgIMMRc4", "hTWKbfoikeg")),
    Channel(2, "ALT / GRUNGE", "ALT / GRUNGE", listOf("hTWKbfoikeg", "3mbBbFH9fAg", "PbgKEjNBHqM")),
    Channel(3, "90s COUNTRY", "COUNTRY", listOf("r7qovpFAGrQ")),
    Channel(4, "HIP-HOP / R&B", "HIP-HOP / R&B", listOf("_JZom_gVfuw")),
    Channel(5, "90s POP", "POP", listOf("C-u5WLJ9Yk4")),
    Channel(6, "90s METAL", "METAL", listOf("CD-E-LDc384")),
    Channel(7, "ONE-HIT WONDERS", "ONE-HIT", listOf("DL7-CKirWZE")),
    Channel(8, "90s PARTY MIX", "PARTY MIX", listOf("ZbZSe6N_BXs"))
)

private val NeonBlue = Color(0xFF29C7FF)
private val NeonPink = Color(0xFFFF3AA8)
private val WarmLamp = Color(0xFFFFB25A)
private val TvBlack = Color(0xFF080808)
private val TvEdge = Color(0xFF2D2B2C)
private val LabelCream = Color(0xFFE9DFC8)

@Composable
fun SignalLostApp() {
    var channelIndex by remember { mutableIntStateOf(0) }
    var videoIndex by remember { mutableIntStateOf(0) }
    var player by remember { mutableStateOf<YouTubePlayer?>(null) }
    val channel = channels[channelIndex]
    val videoId = channel.videos[videoIndex % channel.videos.size]

    fun selectChannel(index: Int) { channelIndex = index; videoIndex = 0 }
    fun nextVideo() { videoIndex = (videoIndex + 1) % channel.videos.size }
    fun previousVideo() { videoIndex = (videoIndex - 1 + channel.videos.size) % channel.videos.size }

    Surface(Modifier.fillMaxSize(), color = Color.Black) {
        Box(
            Modifier.fillMaxSize().safeDrawingPadding().background(
                Brush.verticalGradient(listOf(Color(0xFF090B18), Color(0xFF21142B), Color(0xFF421D2A), Color(0xFF120B0C)))
            )
        ) {
            // Dim bedroom wall stripes and neon spill.
            Box(Modifier.fillMaxWidth().height(160.dp).background(Brush.horizontalGradient(listOf(Color(0x3319BFFF), Color.Transparent, Color(0x33FF299D)))))
            Column(Modifier.fillMaxSize()) {
                SignalHeader()
                BedroomScene(
                    modifier = Modifier.weight(1f), selectedChannel = channelIndex, videoId = videoId,
                    onSelectChannel = { selectChannel(it) }, onPlayerReady = { player = it }, onVideoEnded = { nextVideo() }
                )
                TransportControls({ previousVideo() }, { player?.play() }, { nextVideo() })
                Text("GOOD MUSIC FINDS A WAY.", Modifier.fillMaxWidth().padding(vertical = 7.dp), NeonBlue,
                    fontFamily = FontFamily.Monospace, fontSize = 10.sp, letterSpacing = 2.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun SignalHeader() {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("SIGNAL LOST", color = NeonBlue, fontWeight = FontWeight.Black, fontSize = 29.sp, letterSpacing = 1.sp)
            Box(Modifier.width(174.dp).height(3.dp).background(NeonPink))
            Text("MUSIC NEVER DISAPPEARS", Modifier.padding(top = 3.dp), Color.White, fontFamily = FontFamily.Monospace, fontSize = 8.sp, letterSpacing = 1.4.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("● ON AIR", color = NeonPink, fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text("199X", color = Color(0xFF9A93A7), fontFamily = FontFamily.Monospace, fontSize = 8.sp)
        }
    }
}

@Composable
private fun BedroomScene(
    modifier: Modifier, selectedChannel: Int, videoId: String, onSelectChannel: (Int) -> Unit,
    onPlayerReady: (YouTubePlayer) -> Unit, onVideoEnded: () -> Unit
) {
    BoxWithConstraints(
        modifier.fillMaxWidth().padding(horizontal = 8.dp)
            .background(Brush.verticalGradient(listOf(Color(0xFF24132F), Color(0xFF351825), Color(0xFF120D16))), RoundedCornerShape(18.dp))
            .border(1.dp, Color(0xFF594060), RoundedCornerShape(18.dp)).padding(9.dp)
    ) {
        val tapeWidth = maxWidth * .33f
        val tvWidth = maxWidth * .64f
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.Bottom) {
                Column(Modifier.width(tapeWidth), verticalArrangement = Arrangement.Bottom) {
                    Text("VIDEO LIBRARY", Modifier.fillMaxWidth().padding(bottom = 4.dp), Color(0xFFB7A9C1), fontFamily = FontFamily.Monospace, fontSize = 7.sp, textAlign = TextAlign.Center)
                    channels.forEachIndexed { i, item -> VhsTape(item, i == selectedChannel) { onSelectChannel(i) } }
                }
                Column(Modifier.width(tvWidth), horizontalAlignment = Alignment.CenterHorizontally) {
                    // Bedroom clutter silhouettes behind the TV.
                    Row(Modifier.fillMaxWidth().height(22.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                        Box(Modifier.width(30.dp).height(15.dp).background(Color(0xFF38202C), RoundedCornerShape(2.dp)))
                        Text("✦", color = WarmLamp, fontSize = 16.sp)
                        Box(Modifier.width(18.dp).height(20.dp).background(Color(0xFF1B263A), RoundedCornerShape(2.dp)))
                    }
                    CrtTelevision(Modifier.fillMaxWidth(), videoId, channels[selectedChannel], onPlayerReady, onVideoEnded)
                }
            }
            // Wood desk with cassette/VHS clutter.
            Box(Modifier.fillMaxWidth().height(24.dp).padding(top = 5.dp).background(Brush.verticalGradient(listOf(Color(0xFF75452C), Color(0xFF351A13))), RoundedCornerShape(4.dp))) {
                Row(Modifier.align(Alignment.CenterEnd).padding(end = 12.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) { Box(Modifier.width(22.dp).height(5.dp).background(Color(0xFF171313), RoundedCornerShape(1.dp))) }
                }
            }
        }
    }
}

@Composable
private fun VhsTape(channel: Channel, selected: Boolean, onClick: () -> Unit) {
    val edge = if (selected) NeonBlue else Color(0xFF4C4748)
    Row(
        Modifier.fillMaxWidth().padding(vertical = 1.5.dp).height(36.dp)
            .background(if (selected) Color(0xFF153247) else Color(0xFF111112), RoundedCornerShape(4.dp))
            .border(if (selected) 2.dp else 1.dp, edge, RoundedCornerShape(4.dp)).clickable { onClick() }.padding(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(6.dp).fillMaxHeight().background(if (selected) NeonPink else Color(0xFF52272D), RoundedCornerShape(1.dp)))
        Column(Modifier.padding(start = 3.dp).weight(1f).fillMaxHeight().background(LabelCream, RoundedCornerShape(2.dp)), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("CH ${channel.number.toString().padStart(2, '0')}", color = Color(0xFF7B3337), fontFamily = FontFamily.Monospace, fontSize = 6.sp)
            Text(channel.shortName, color = Color(0xFF161616), fontWeight = FontWeight.Black, fontSize = 7.5.sp, textAlign = TextAlign.Center, maxLines = 1)
        }
        Column(Modifier.padding(start = 3.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(7.dp).border(1.dp, Color.Gray, CircleShape))
            Text("VHS", color = Color.LightGray, fontFamily = FontFamily.Monospace, fontSize = 6.sp)
        }
    }
}

@Composable
private fun CrtTelevision(modifier: Modifier, videoId: String, channel: Channel, onPlayerReady: (YouTubePlayer) -> Unit, onVideoEnded: () -> Unit) {
    Column(
        modifier.background(Brush.verticalGradient(listOf(Color(0xFF393637), TvBlack)), RoundedCornerShape(18.dp))
            .border(5.dp, TvEdge, RoundedCornerShape(18.dp)).padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.fillMaxWidth().aspectRatio(4f / 3f).background(Color.Black, RoundedCornerShape(9.dp)).border(2.dp, Color(0xFF171717), RoundedCornerShape(9.dp))) {
            SignalLostYouTubePlayer(videoId, onPlayerReady, onVideoEnded, Modifier.fillMaxSize())
        }
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("CH ${channel.number.toString().padStart(2, '0')} • ${channel.shortName}", Modifier.weight(1f), Color(0xFF4DE9FF), fontFamily = FontFamily.Monospace, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            Text("●", color = Color(0xFFFF544D), fontSize = 8.sp)
        }
        Row(Modifier.fillMaxWidth().padding(top = 3.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("SIGNAL LOST", color = Color(0xFF858085), fontFamily = FontFamily.Monospace, fontSize = 6.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) { repeat(4) { Box(Modifier.width(12.dp).height(5.dp).background(Color(0xFF292929), RoundedCornerShape(2.dp))) } }
        }
    }
}

@Composable
private fun SignalLostYouTubePlayer(videoId: String, onPlayerReady: (YouTubePlayer) -> Unit, onVideoEnded: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    var playerView by remember { mutableStateOf<YouTubePlayerView?>(null) }
    var youTubePlayer by remember { mutableStateOf<YouTubePlayer?>(null) }
    AndroidView(modifier = modifier, factory = { ctx ->
        YouTubePlayerView(ctx).apply {
            enableAutomaticInitialization = false
            activity?.lifecycle?.addObserver(this)
            val options = IFramePlayerOptions.Builder(ctx).controls(1).fullscreen(0).autoplay(0).ivLoadPolicy(3).build()
            initialize(object : AbstractYouTubePlayerListener() {
                override fun onReady(player: YouTubePlayer) { youTubePlayer = player; onPlayerReady(player); player.cueVideo(videoId, 0f) }
                override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) { if (state == PlayerConstants.PlayerState.ENDED) onVideoEnded() }
            }, options)
            playerView = this
        }
    })
    LaunchedEffect(youTubePlayer, videoId) { youTubePlayer?.cueVideo(videoId, 0f) }
    DisposableEffect(Unit) { onDispose { playerView?.release(); playerView = null; youTubePlayer = null } }
}

@Composable
private fun TransportControls(onPrevious: () -> Unit, onPlay: () -> Unit, onNext: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(top = 8.dp, start = 18.dp, end = 18.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        MediaButton("|◀", "PREV", false, onPrevious); Spacer(Modifier.width(26.dp)); MediaButton("▶", "PLAY", true, onPlay); Spacer(Modifier.width(26.dp)); MediaButton("▶|", "NEXT", false, onNext)
    }
}

@Composable
private fun MediaButton(icon: String, label: String, primary: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(if (primary) 58.dp else 45.dp).background(if (primary) Brush.radialGradient(listOf(Color(0xFF66DDFF), NeonBlue, Color(0xFF0879B4))) else Brush.verticalGradient(listOf(Color(0xFF282428), Color(0xFF0D0C0D))), if (primary) CircleShape else RoundedCornerShape(12.dp)).border(1.dp, if (primary) Color(0xFF7DE6FF) else Color(0xFF4D474D), if (primary) CircleShape else RoundedCornerShape(12.dp)).clickable { onClick() }, contentAlignment = Alignment.Center) {
            Text(icon, color = Color.White, fontSize = if (primary) 22.sp else 15.sp, fontWeight = FontWeight.Bold)
        }
        Text(label, Modifier.padding(top = 3.dp), Color.White, fontFamily = FontFamily.Monospace, fontSize = 8.sp)
    }
}

private tailrec fun Context.findActivity(): ComponentActivity? = when (this) { is ComponentActivity -> this; is ContextWrapper -> baseContext.findActivity(); else -> null }
