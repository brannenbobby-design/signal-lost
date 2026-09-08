package com.signallost.tv

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val subtitle: String,
    val videos: List<String>
)

private val channels = listOf(
    Channel(1, "90s ROCK", "Big guitars. Bigger hooks.", listOf("fJ9rUzIMcZQ", "1w7OgIMMRc4", "hTWKbfoikeg")),
    Channel(2, "ALT / GRUNGE", "Flannel required.", listOf("hTWKbfoikeg", "3mbBbFH9fAg", "PbgKEjNBHqM")),
    Channel(3, "90s COUNTRY", "Boots, bars and backroads.", listOf("r7qovpFAGrQ")),
    Channel(4, "HIP-HOP / R&B", "Golden-era rotation.", listOf("_JZom_gVfuw")),
    Channel(5, "POP", "TRL before TRL.", listOf("C-u5WLJ9Yk4")),
    Channel(6, "METAL", "Turn it up.", listOf("CD-E-LDc384")),
    Channel(7, "ONE-HIT WONDERS", "All killer, no filler.", listOf("DL7-CKirWZE"))
)

private val SignalBlue = Color(0xFF168CFF)
private val Panel = Color(0xFF0A0A0A)
private val Border = Color(0xFF2C2C2C)

@Composable
fun SignalLostApp() {
    var channelIndex by remember { mutableIntStateOf(0) }
    var videoIndex by remember { mutableIntStateOf(0) }
    var webView by remember { mutableStateOf<WebView?>(null) }
    val channel = channels[channelIndex]
    val videoId = channel.videos[videoIndex % channel.videos.size]

    fun changeChannel(newIndex: Int) {
        channelIndex = (newIndex + channels.size) % channels.size
        videoIndex = 0
    }

    fun nextVideo() {
        videoIndex = (videoIndex + 1) % channel.videos.size
    }

    fun previousVideo() {
        videoIndex = (videoIndex - 1 + channel.videos.size) % channel.videos.size
    }

    fun play() {
        webView?.evaluateJavascript("if (window.player && player.playVideo) { player.playVideo(); }", null)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("◉", color = SignalBlue, fontSize = 34.sp)
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "SIGNAL LOST",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "MUSIC NEVER DISAPPEARS",
                        color = Color.LightGray,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(Modifier.height(18.dp))
            HorizontalDivider(color = Border)
            Spacer(Modifier.height(18.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "NOW PLAYING",
                    color = SignalBlue,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                )
                Text(
                    "CH ${channel.number.toString().padStart(2, '0')} • ${channel.name}",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Text(
                    channel.subtitle,
                    color = Color.Gray,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Panel)
                    .border(1.dp, Border, RoundedCornerShape(4.dp))
            ) {
                YouTubeEmbed(
                    videoId = videoId,
                    onEnded = { nextVideo() },
                    onWebViewReady = { webView = it },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ControlButton("◀|", "PREV", false) { previousVideo() }
                ControlButton("▶", "PLAY", true) { play() }
                ControlButton("|▶", "NEXT", false) { nextVideo() }
            }

            Spacer(Modifier.height(22.dp))

            channels.chunked(2).forEach { rowChannels ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowChannels.forEachIndexed { _, item ->
                        ChannelTile(
                            channel = item,
                            selected = item.number == channel.number,
                            modifier = Modifier.weight(1f)
                        ) { changeChannel(item.number - 1) }
                    }
                    if (rowChannels.size == 1) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = Border)
            Spacer(Modifier.height(14.dp))
            Text(
                "GOOD MUSIC FINDS A WAY.",
                color = Color.LightGray,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ControlButton(label: String, caption: String, primary: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(if (primary) 68.dp else 58.dp)
                .background(if (primary) SignalBlue else Panel, if (primary) CircleShape else RoundedCornerShape(18.dp))
                .border(1.dp, if (primary) SignalBlue else Border, if (primary) CircleShape else RoundedCornerShape(18.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(label, color = Color.White, fontSize = if (primary) 28.sp else 20.sp)
        }
        Spacer(Modifier.height(6.dp))
        Text(caption, color = Color.LightGray, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
    }
}

@Composable
private fun ChannelTile(channel: Channel, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(76.dp)
            .background(if (selected) Color(0xFF102A43) else Panel, RoundedCornerShape(12.dp))
            .border(2.dp, if (selected) SignalBlue else Border, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                channel.name,
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "CH ${channel.number.toString().padStart(2, '0')}",
                color = if (selected) SignalBlue else Color.Gray,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeEmbed(
    videoId: String,
    onEnded: () -> Unit,
    onWebViewReady: (WebView) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = {
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = true
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()
                setBackgroundColor(android.graphics.Color.BLACK)
                addJavascriptInterface(object {
                    @android.webkit.JavascriptInterface
                    fun videoEnded() = onEnded()
                }, "Android")
                onWebViewReady(this)
            }
        },
        update = { view ->
            if (view.tag != videoId) {
                view.tag = videoId
                val html = """
                    <!doctype html>
                    <html>
                    <head>
                      <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
                      <style>
                        html,body,#player { margin:0; width:100%; height:100%; background:#000; overflow:hidden; }
                      </style>
                    </head>
                    <body>
                      <div id="player"></div>
                      <script src="https://www.youtube.com/iframe_api"></script>
                      <script>
                        var player;
                        function onYouTubeIframeAPIReady() {
                          player = new YT.Player('player', {
                            videoId: '$videoId',
                            playerVars: {
                              autoplay: 0,
                              controls: 1,
                              rel: 0,
                              playsinline: 1,
                              modestbranding: 1,
                              origin: 'https://www.youtube.com'
                            },
                            events: {
                              onStateChange: function(event) {
                                if (event.data === YT.PlayerState.ENDED) {
                                  Android.videoEnded();
                                }
                              }
                            }
                          });
                        }
                      </script>
                    </body>
                    </html>
                """.trimIndent()
                view.loadDataWithBaseURL("https://www.youtube.com/", html, "text/html", "UTF-8", null)
            }
        }
    )
}
