package com.mkandeel.youtubeplayer

interface VideoStateChanged {
    fun onVideoStateChanged(isPaused: Boolean, currentTime: Float)
}