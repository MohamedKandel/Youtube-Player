package com.mkandeel.youtubeplayer

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageButton
import android.widget.PopupMenu

import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.cardview.widget.CardView
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
    private val listener: VideoStateChanged,
    private val youtubeListener: WatchOnYoutubeListener
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
        val menu = view.findViewById<ImageButton>(R.id.more_icon)
        val youtube = view.findViewById<ImageButton>(R.id.btn_youtube)
        val volume = view.findViewById<ImageButton>(R.id.btn_setting)
        val card = view.findViewById<CardView>(R.id.card_sound)
        val seek_sound = view.findViewById<SeekBar>(R.id.volume)

        val fadeViewHelper = FadeViewHelper(container)



        player.addListener(seekbar)

        seekbar.youtubePlayerSeekBarListener = object : YouTubePlayerSeekBarListener {
            override fun seekTo(time: Float) {
                player.seekTo(time)
                current = (time / 60).toInt()
                listener.onVideoStateChanged(false, current)
            }
        }

        volume.setOnClickListener {
            card.visibility = View.VISIBLE
            //fadeViewHelper.toggleVisibility()
            fadeViewHelper.isDisabled = true
            seek_sound.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    Log.v("Volume changed", "$progress")
                    if (progress > 50) {
                        volume.setImageResource(R.drawable.volume_icon)
                    } else if (progress in 1..50) {
                        volume.setImageResource(R.drawable.volume_mid_icon)
                    } else {
                        volume.setImageResource(R.drawable.mute_icon)
                    }
                    player.setVolume(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    fadeViewHelper.isDisabled = false
                    fadeViewHelper.toggleVisibility()
                    card.visibility = View.GONE
                }

            })
        }

        menu.setOnClickListener {
            fadeViewHelper.isDisabled = true
            //player.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_2)
            val popUpMenu = PopupMenu(context, it)
            popUpMenu.menuInflater.inflate(R.menu.menu, popUpMenu.menu)
            popUpMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.spead_0_25 -> {
                        player.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_0_25)
                    }

                    R.id.spead_0_5 -> {
                        player.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_0_5)
                    }

                    R.id.spead_1 -> {
                        player.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_1)
                    }

                    R.id.spead_1_5 -> {
                        player.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_1_5)
                    }

                    R.id.spead_2 -> {
                        player.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_2)
                    }
                }
                fadeViewHelper.isDisabled = false
                fadeViewHelper.toggleVisibility()
                true
            }
            popUpMenu.setOnDismissListener {
                fadeViewHelper.isDisabled = false
                fadeViewHelper.toggleVisibility()
            }
            popUpMenu.show()
        }

        pause.setOnClickListener {
            if (tracker.state == PlayerConstants.PlayerState.PLAYING) {
                pause.setImageResource(R.drawable.play_icon)
                player.pause()
                current = (tracker.currentSecond / 60).toInt()
                listener.onVideoStateChanged(true, current)
            } else {
                pause.setImageResource(R.drawable.pause_icon)
                player.play()
                current = (tracker.currentSecond / 60).toInt()
                listener.onVideoStateChanged(false, current)
            }
            fadeViewHelper.isDisabled = false
            fadeViewHelper.toggleVisibility()
        }

        youtube.setOnClickListener {
            youtubeListener.onYoutubeClickListener(true)
            fadeViewHelper.isDisabled = false
            fadeViewHelper.toggleVisibility()
        }

        fullscreen.setOnClickListener {
            if (isFullScreen) {
                playerView.layoutParams = RelativeLayout.LayoutParams(
                    MATCH_PARENT,
                    context.resources.getDimension(com.intuit.sdp.R.dimen._170sdp).toInt()
                )
                fullscreen.setImageResource(R.drawable.full_screen_icon)
            } else {
                playerView.matchParent()
                fullscreen.setImageResource(R.drawable.close_icon)
            }
            isFullScreen = !isFullScreen
            fadeViewHelper.isDisabled = false
            fadeViewHelper.toggleVisibility()
        }

        fadeViewHelper.animationDuration = FadeViewHelper.DEFAULT_ANIMATION_DURATION
        fadeViewHelper.fadeOutDelay = FadeViewHelper.DEFAULT_FADE_OUT_DELAY
        player.addListener(fadeViewHelper)

        root.setOnClickListener {
            fadeViewHelper.isDisabled = false
            fadeViewHelper.toggleVisibility()
        }

        container.setOnClickListener {
            fadeViewHelper.isDisabled = false
            fadeViewHelper.toggleVisibility()
        }
    }
}