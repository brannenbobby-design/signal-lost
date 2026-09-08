package com.signallost.tv

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.signallost.tv.ui.theme.SignalLostTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignalLostTheme {
                SignalLostApp()
            }
        }
    }
}

data class Channel(
    val number: Int,
    val name: String,
    val shortName: String,
    val videos: List<String>
)

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

private val NeonBlue = Color(0xFF16A5FF)
private val NeonPink = Color(0xFFFF2B9F)
private val TvBlack = Color(0xFF080808)
private val TvEdge = Color(0xFF242424)
private val TapeBlack = Color(0xFF111111)
private val LabelCream = Color(0xFFE8DDC9)
private val DeskBrown = Color(0xFF3C2117)

@Composable
fun SignalLostApp() {
    var channelIndex by remember { mutableIntStateOf(0) }
    var videoIndex by remember { mutableIntStateOf(0) }
    var playRequest by remember { mutableIntStateOf(0) }
    val channel = channels[channelIndex]
    val videoId = channel.videos[videoIndex % channel.videos.size]

    fun selectChannel(index: Int) {
        channelIndex = index
        videoIndex = 0
        playRequest = 0
    }

    fun nextVideo() {
        videoIndex = (videoIndex + 1) % channel.videos.size
        playRequest = 0
    }

    fun previousVideo() {
        videoIndex = (videoIndex - 1 + channel.videos.size) % channel.videos.size
        playRequest = 0
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF080A18),
                            Color(0xFF161026),
                            Color(0xFF2B1621),
                            Color(0xFF130C0B)
                        )
                    )
                )
                .padding(bottom = 16.dp)
        ) {
            SignalHeader()
            BedroomScene(
                selectedChannel = channelIndex,
                videoId = videoId,
                playRequest = playRequest,
                onSelectChannel = { selectChannel(it) }
            )
            TransportControls(
                onPrevious = { previousVideo() },
                onPlay = { playRequest++ },
                onNext = { nextVideo() }
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "GOOD MUSIC FINDS A WAY.",
                modifier = Modifier.fillMaxWidth(),
                color = NeonBlue,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
            Text(
                "If PLAY does not start immediately, tap the YouTube play button inside the TV.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 8.dp),
                color = Color.Gray,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SignalHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Column {
            Text(
                "SIGNAL LOST",
                color = NeonBlue,
                fontWeight = FontWeight.Black,
                fontSize = 30.sp,
                letterSpacing = 1.sp
            )
            Box(
                modifier = Modifier
                    .padding(top = 1.dp)
                    .width(170.dp)
                    .height(3.dp)
                    .background(NeonPink)
            )
            Text(
                "MUSIC NEVER DISAPPEARS",
                modifier = Modifier.padding(top = 5.dp),
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                letterSpacing = 1.5.sp
            )
        }
    }
}

@Composable
private fun BedroomScene(
    selectedChannel: Int,
    videoId: String,
    playRequest: Int,
    onSelectChannel: (Int) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF1A1130),
                        Color(0xFF391723),
                        Color(0xFF11172B)
                    )
                )
            )
            .border(1.dp, Color(0xFF4B345A), RoundedCornerShape(18.dp))
            .padding(10.dp)
    ) {
        val tapeWidth = maxWidth * 0.34f
        val tvWidth = maxWidth * 0.63f

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier.width(tapeWidth),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    channels.forEachIndexed { index, item ->
                        VhsTape(
                            channel = item,
                            selected = index == selectedChannel,
                            onClick = { onSelectChannel(index) }
                        )
                    }
                }

                CrtTelevision(
                    modifier = Modifier.width(tvWidth),
                    videoId = videoId,
                    playRequest = playRequest,
                    channel = channels[selectedChannel]
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp)
                    .padding(top = 8.dp)
                    .background(DeskBrown, RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
private fun VhsTape(channel: Channel, selected: Boolean, onClick: () -> Unit) {
    val outline = if (selected) NeonBlue else Color(0xFF383838)
    val glow = if (selected) Color(0xFF102B45) else TapeBlack

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(glow, RoundedCornerShape(5.dp))
            .border(if (selected) 2.dp else 1.dp, outline, RoundedCornerShape(5.dp))
            .clickable { onClick() }
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(7.dp)
                .fillMaxHeight()
                .background(if (selected) NeonBlue else Color(0xFF4B2424), RoundedCornerShape(2.dp))
        )
        Box(
            modifier = Modifier
                .padding(start = 4.dp)
                .weight(1f)
                .fillMaxHeight()
                .background(LabelCream, RoundedCornerShape(2.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                channel.shortName,
                color = Color(0xFF171717),
                fontWeight = FontWeight.Bold,
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
        Text(
            "VHS",
            modifier = Modifier.padding(start = 3.dp),
            color = Color.LightGray,
            fontFamily = FontFamily.Monospace,
            fontSize = 7.sp
        )
    }
}

@Composable
private fun CrtTelevision(
    modifier: Modifier,
    videoId: String,
    playRequest: Int,
    channel: Channel
) {
    Column(
        modifier = modifier
            .background(TvBlack, RoundedCornerShape(14.dp))
            .border(4.dp, TvEdge, RoundedCornerShape(14.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .clip(RoundedCornerShape(18.dp))
                .background(Color.Black)
                .border(2.dp, Color(0xFF303030), RoundedCornerShape(18.dp))
        ) {
            key(videoId, playRequest) {
                YouTubeEmbed(
                    videoId = videoId,
                    autoplay = playRequest > 0,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.58f))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Text(
                    "CH ${channel.number.toString().padStart(2, '0')} • ${channel.shortName}",
                    color = Color(0xFF42E8FF),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(7.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("●", color = Color(0xFFFF5A4F), fontSize = 9.sp)
            Text("SIGNAL LOST", color = Color.Gray, fontFamily = FontFamily.Monospace, fontSize = 7.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(4) {
                    Box(
                        Modifier
                            .width(13.dp)
                            .height(5.dp)
                            .background(Color(0xFF292929), RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}

@Composable
private fun TransportControls(onPrevious: () -> Unit, onPlay: () -> Unit, onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp, start = 18.dp, end = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MediaButton("|◀", "PREV", false, onPrevious)
            Spacer(Modifier.width(26.dp))
            MediaButton("▶", "PLAY", true, onPlay)
            Spacer(Modifier.width(26.dp))
            MediaButton("▶|", "NEXT", false, onNext)
        }
    }
}

@Composable
private fun MediaButton(icon: String, label: String, primary: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(if (primary) 66.dp else 52.dp)
                .background(if (primary) NeonBlue else Color(0xFF171717), if (primary) CircleShape else RoundedCornerShape(14.dp))
                .border(1.dp, if (primary) NeonBlue else Color(0xFF434343), if (primary) CircleShape else RoundedCornerShape(14.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(icon, color = Color.White, fontSize = if (primary) 25.sp else 17.sp)
        }
        Spacer(Modifier.height(5.dp))
        Text(label, color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeEmbed(
    videoId: String,
    autoplay: Boolean,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = !autoplay
                settings.allowContentAccess = true
                settings.allowFileAccess = false
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                settings.loadsImagesAutomatically = true
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()
                setBackgroundColor(android.graphics.Color.BLACK)
                setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)

                val auto = if (autoplay) 1 else 0
                val html = """
                    <!doctype html>
                    <html>
                    <head>
                      <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
                      <style>
                        html,body { margin:0; padding:0; width:100%; height:100%; background:#000; overflow:hidden; }
                        iframe { display:block; border:0; width:100%; height:100%; }
                      </style>
                    </head>
                    <body>
                      <iframe
                        src="https://www.youtube.com/embed/$videoId?autoplay=$auto&playsinline=1&controls=1&rel=0&fs=0"
                        title="Signal Lost player"
                        allow="autoplay; encrypted-media; picture-in-picture"
                      ></iframe>
                    </body>
                    </html>
                """.trimIndent()

                // The non-YouTube base URL gives the embed a real Referer identity.
                // YouTube now requires embedded API clients to identify themselves.
                loadDataWithBaseURL(
                    "https://com.signallost.tv/",
                    html,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        }
    )
}
