package com.mkandeel.youtubeplayer

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class MainActivity : AppCompatActivity(), VideoStateChanged {

    // don't use viewBinding in this code
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val youTubePlayerView = findViewById<YouTubePlayerView>(R.id.video_player)

        youTubePlayerView.enableAutomaticInitialization = false

        lifecycle.addObserver(youTubePlayerView)

        val controlUi = youTubePlayerView.inflateCustomPlayerUi(R.layout.custom_layout)
        val listener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                val uiController = PlayerUIController(
                    controlUi,
                    youTubePlayer,
                    youTubePlayerView,
                    this@MainActivity,
                    this@MainActivity
                )
                youTubePlayer.addListener(uiController)
                youTubePlayer.loadOrCueVideo(
                    lifecycle, getVideoID("https://www.youtube.com/watch?v=KW8S7NOSYwc&list=PLzZol22jG6OeX8H-PdrOUtpnqYl-vLiWd"), 0f
                )
            }

        }

        val iframeOptions = IFramePlayerOptions.Builder().controls(0).build()
        youTubePlayerView.initialize(listener, iframeOptions)
    }

    // video id will be after "v=" in youtube link
    // so we will substring youtube link from after v= till first & symbol fond -if exist-
    private fun getVideoID(url: String) = url.substringAfter("v=").substringBefore("&")

    // listener for pause and resume video and get current time
    override fun onVideoStateChanged(isPaused: Boolean, currentTime: Float) {
        Log.v("Video State", "$isPaused at $currentTime")
    }
}