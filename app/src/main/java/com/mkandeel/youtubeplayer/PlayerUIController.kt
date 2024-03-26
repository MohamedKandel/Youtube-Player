package com.mkandeel.youtubeplayer

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageButton

import android.widget.RelativeLayout
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.utils.FadeViewHelper
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.views.YouTubePlayerSeekBar
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.views.YouTubePlayerSeekBarListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class PlayerUIController(
    ui: View,
    private val player: YouTubePlayer,
    private val playerView: YouTubePlayerView,
    private var tracker: YouTubePlayerTracker,
    private val context: Context,
    private val listener: VideoStateChanged
) : AbstractYouTubePlayerListener() {
    private var isFullScreen = false
    private var current = 0

    init {
        //tracker = YouTubePlayerTracker()
        player.addListener(tracker)
        initViews(ui)
    }

    private fun initViews(view: View) {
        val container = view.findViewById<View>(R.id.container)
        val root = view.findViewById<RelativeLayout>(R.id.root)
        val seekbar = view.findViewById<YouTubePlayerSeekBar>(R.id.seekBarPlayer)
        val fullscreen = view.findViewById<ImageButton>(R.id.btn_fullscreen)
        val pause = view.findViewById<ImageButton>(R.id.btn_pause)
        player.addListener(seekbar)

        seekbar.youtubePlayerSeekBarListener = object : YouTubePlayerSeekBarListener {
            override fun seekTo(time: Float) {
                player.seekTo(time)
                current = (time/60).toInt()
                listener.onVideoStateChanged(false,current)
            }
        }

        pause.setOnClickListener {
            if (tracker.state == PlayerConstants.PlayerState.PLAYING) {
                pause.setImageResource(R.drawable.play_icon)
                player.pause()
                current = (tracker.currentSecond/60).toInt()
                listener.onVideoStateChanged(true, current)
            } else {
                pause.setImageResource(R.drawable.pause_icon)
                player.play()
                current = (tracker.currentSecond/60).toInt()
                listener.onVideoStateChanged(false, current)
            }
        }

        fullscreen.setOnClickListener {
            if (isFullScreen) {
                playerView.layoutParams = RelativeLayout.LayoutParams(
                    MATCH_PARENT,
                    context.resources.getDimension(com.intuit.sdp.R.dimen._170sdp).toInt())
                fullscreen.setImageResource(R.drawable.full_screen_icon)
            } else {
                playerView.matchParent()
                fullscreen.setImageResource(R.drawable.close_icon)
            }
            isFullScreen = !isFullScreen
        }

        val fadeViewHelper = FadeViewHelper(container)
        fadeViewHelper.animationDuration = FadeViewHelper.DEFAULT_ANIMATION_DURATION
        fadeViewHelper.fadeOutDelay = FadeViewHelper.DEFAULT_FADE_OUT_DELAY
        player.addListener(fadeViewHelper)

        root.setOnClickListener {
            fadeViewHelper.toggleVisibility()
        }

        container.setOnClickListener {
            fadeViewHelper.toggleVisibility()
        }
    }
}