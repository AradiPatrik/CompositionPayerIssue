package com.aradipatrik.compositionplayerissue

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Composition
import androidx.media3.transformer.CompositionPlayer
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.ui.PlayerView
import com.aradipatrik.compositionplayerissue.ui.theme.CompositionPlayerIssueTheme
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionPlayerIssueTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Main(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@SuppressLint("RestrictedApi")
@Composable
fun Main(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val player = remember {
        val images = listOf(
            Assets.uri("car.jpg"),
            Assets.uri("food2.jpg"),
            Assets.uri("img.png"),
            Assets.uri("party.jpg"),
        )

        val videos = listOf(
            Assets.uri("1.mp4"),
            Assets.uri("2.mov"),
            Assets.uri("food1.mp4"),
            Assets.uri("hotpot.mp4"),
            Assets.uri("1.mp4"),
            Assets.uri("2.mov"),
            Assets.uri("food1.mp4"),
            Assets.uri("hotpot.mp4"),
            Assets.uri("1.mp4"),
            Assets.uri("2.mov"),
            Assets.uri("food1.mp4"),
            Assets.uri("hotpot.mp4"),
            Assets.uri("1.mp4"),
            Assets.uri("2.mov"),
            Assets.uri("food1.mp4"),
            Assets.uri("hotpot.mp4")
        )

        val audio = Assets.uri("audio.mp3")

        val videoEditedMediaItems = videos.map {
            EditedMediaItem.Builder(
                MediaItem.fromUri(it)
                    .buildUpon()
                    .setClippingConfiguration(
                        MediaItem.ClippingConfiguration.Builder()
                            .setStartPositionMs(0)
                            .setEndPositionMs(1.seconds.inWholeMilliseconds)
                            .build()
                    )
                    .build()
            )
                .setRemoveAudio(true)
                .setDurationUs(1.seconds.inWholeMicroseconds)
                .build()
        }

        val imageEditedMediaItems = images.map {
            EditedMediaItem.Builder(
                MediaItem.fromUri(it)
                    .buildUpon()
                    .setImageDurationMs(1.seconds.inWholeMilliseconds)
                    .build()
            )
                .setDurationUs(1.seconds.inWholeMicroseconds)
                .build()
        }

        val videoSequence =
            EditedMediaItemSequence.Builder(videoEditedMediaItems + imageEditedMediaItems).build()
        val audioSequence = EditedMediaItemSequence.Builder(
            EditedMediaItem.Builder(
                MediaItem.fromUri(audio)
                    .buildUpon()
                    .setClippingConfiguration(
                        MediaItem.ClippingConfiguration.Builder()
                            .setStartPositionMs(30.seconds.inWholeMilliseconds)
                            .setEndPositionMs(55.seconds.inWholeMilliseconds)
                            .build()
                    )
                    .build()
            )
                .setDurationUs(100.seconds.inWholeMicroseconds)
                .build()
        )
            .build()

        val composition = Composition.Builder(videoSequence, audioSequence)
            .build()
        val compositionPlayer = CompositionPlayer.Builder(context)
            .build()

        compositionPlayer.playWhenReady = true
        compositionPlayer.setComposition(composition)
        compositionPlayer.prepare()
        compositionPlayer
    }
    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    player.seekTo(0)
                    player.play()
                }, factory = {
                PlayerView(it).apply {
                    this.player = player
                    setEnableComposeSurfaceSyncWorkaround(true)
                }
            })
    }
}

object Assets {
    fun uri(assetName: String): Uri {
        return "file:///android_asset/$assetName".toUri()
    }
}