package com.signallost.tv

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        setContent { SignalLostTheme { SignalLostApp() } }
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
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF080A18), Color(0xFF161026), Color(0xFF2B1621), Color(0xFF130C0B))
                    )
                )
                .padding(bottom = 8.dp)
        ) {
            SignalHeader()
            BedroomScene(
                modifier = Modifier.weight(1f),
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
            Text(
                "GOOD MUSIC FINDS A WAY.",
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                color = NeonBlue,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SignalHeader() {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp)) {
        Column {
            Text("SIGNAL LOST", color = NeonBlue, fontWeight = FontWeight.Black, fontSize = 28.sp, letterSpacing = 1.sp)
            Box(modifier = Modifier.padding(top = 1.dp).width(165.dp).height(3.dp).background(NeonPink))
            Text(
                "MUSIC NEVER DISAPPEARS",
                modifier = Modifier.padding(top = 4.dp),
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                letterSpacing = 1.5.sp
            )
        }
    }
}

@Composable
private fun BedroomScene(
    modifier: Modifier,
    selectedChannel: Int,
    videoId: String,
    playRequest: Int,
    onSelectChannel: (Int) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .background(
                Brush.linearGradient(listOf(Color(0xFF1A1130), Color(0xFF391723), Color(0xFF11172B))),
                RoundedCornerShape(18.dp)
            )
            .border(1.dp, Color(0xFF4B345A), RoundedCornerShape(18.dp))
            .padding(10.dp)
    ) {
        val tapeWidth = maxWidth * 0.34f
        val tvWidth = maxWidth * 0.63f

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier.width(tapeWidth),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    channels.forEachIndexed { index, item ->
                        VhsTape(item, index == selectedChannel) { onSelectChannel(index) }
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
                    .height(18.dp)
                    .padding(top = 5.dp)
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
            .height(38.dp)
            .background(glow, RoundedCornerShape(5.dp))
            .border(if (selected) 2.dp else 1.dp, outline, RoundedCornerShape(5.dp))
            .clickable { onClick() }
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(7.dp).fillMaxHeight()
                .background(if (selected) NeonBlue else Color(0xFF4B2424), RoundedCornerShape(2.dp))
        )
        Box(
            modifier = Modifier.padding(start = 4.dp).weight(1f).fillMaxHeight()
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
        Text("VHS", modifier = Modifier.padding(start = 3.dp), color = Color.LightGray, fontFamily = FontFamily.Monospace, fontSize = 7.sp)
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
        // IMPORTANT: Nothing is drawn on top of the WebView. Some Samsung/Android 16
        // WebView builds render static page content but lose the hardware video surface
        // when Compose overlaps that AndroidView. The player owns this rectangle alone.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .background(Color.Black)
                .border(1.dp, Color(0xFF303030))
        ) {
            key(videoId, playRequest) {
                YouTubeEmbed(videoId, playRequest > 0, Modifier.fillMaxSize())
            }
        }

        Text(
            "CH ${channel.number.toString().padStart(2, '0')} • ${channel.shortName}",
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp, start = 2.dp),
            color = Color(0xFF42E8FF),
            fontFamily = FontFamily.Monospace,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 3.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("●", color = Color(0xFFFF5A4F), fontSize = 9.sp)
            Text("SIGNAL LOST", color = Color.Gray, fontFamily = FontFamily.Monospace, fontSize = 7.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(4) {
                    Box(Modifier.width(13.dp).height(5.dp).background(Color(0xFF292929), RoundedCornerShape(2.dp)))
                }
            }
        }
    }
}

@Composable
private fun TransportControls(onPrevious: () -> Unit, onPlay: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp, start = 18.dp, end = 18.dp),
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

@Composable
private fun MediaButton(icon: String, label: String, primary: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(if (primary) 62.dp else 48.dp)
                .background(if (primary) NeonBlue else Color(0xFF171717), if (primary) CircleShape else RoundedCornerShape(14.dp))
                .border(1.dp, if (primary) NeonBlue else Color(0xFF434343), if (primary) CircleShape else RoundedCornerShape(14.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(icon, color = Color.White, fontSize = if (primary) 23.sp else 16.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(label, color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 9.sp)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeEmbed(videoId: String, autoplay: Boolean, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = !autoplay
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                settings.loadsImagesAutomatically = true
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()
                setBackgroundColor(android.graphics.Color.BLACK)
                setLayerType(View.LAYER_TYPE_NONE, null)
                overScrollMode = View.OVER_SCROLL_NEVER
                isFocusable = true
                isFocusableInTouchMode = true

                val auto = if (autoplay) 1 else 0
                val url = "https://www.youtube.com/embed/$videoId?autoplay=$auto&playsinline=1&controls=1&rel=0&fs=0"
                val headers = mapOf(
                    "Referer" to "https://com.signallost.tv/",
                    "Origin" to "https://com.signallost.tv"
                )
                loadUrl(url, headers)
            }
        }
    )
}
