package com.signallost.tv

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
    val videos: List<String>
)

private val channels = listOf(
    Channel(1, "90s ROCK", listOf("fJ9rUzIMcZQ", "1w7OgIMMRc4", "hTWKbfoikeg")),
    Channel(2, "ALT / GRUNGE", listOf("hTWKbfoikeg", "3mbBbFH9fAg", "PbgKEjNBHqM")),
    Channel(3, "90s COUNTRY", listOf("dQw4w9WgXcQ")),
    Channel(4, "HIP-HOP / R&B", listOf("rog8ou-ZepE")),
    Channel(5, "POP", listOf("C-u5WLJ9Yk4")),
    Channel(6, "METAL", listOf("CD-E-LDc384")),
    Channel(7, "ONE-HIT WONDERS", listOf("DL7-CKirWZE"))
)

@Composable
fun SignalLostApp() {
    var channelIndex by remember { mutableIntStateOf(0) }
    var videoIndex by remember { mutableIntStateOf(0) }
    val channel = channels[channelIndex]
    val videoId = channel.videos[videoIndex % channel.videos.size]

    fun changeChannel(delta: Int) {
        channelIndex = (channelIndex + delta + channels.size) % channels.size
        videoIndex = 0
    }

    fun nextVideo() {
        videoIndex = (videoIndex + 1) % channel.videos.size
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
        Box(modifier = Modifier.fillMaxSize()) {
            YouTubeEmbed(
                videoId = videoId,
                onEnded = { nextVideo() },
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(18.dp)
                    .background(Color.Black.copy(alpha = 0.72f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column {
                    Text(
                        text = "SIGNAL LOST",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 19.sp
                    )
                    Text(
                        text = "CH ${channel.number.toString().padStart(2, '0')} • ${channel.name}",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(18.dp)
                    .background(Color.Black.copy(alpha = 0.80f), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { changeChannel(-1) }) { Text("CH −") }
                Button(onClick = { nextVideo() }) { Text("NEXT") }
                Button(onClick = { changeChannel(1) }) { Text("CH +") }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeEmbed(
    videoId: String,
    onEnded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = {
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                mediaPlaybackRequiresUserGesture = false
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()
                addJavascriptInterface(object {
                    @android.webkit.JavascriptInterface
                    fun videoEnded() = onEnded()
                }, "Android")
            }
        },
        update = { webView ->
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
                          autoplay: 1,
                          controls: 1,
                          rel: 0,
                          playsinline: 1,
                          modestbranding: 1
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
            webView.loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "UTF-8", null)
        }
    )
}
