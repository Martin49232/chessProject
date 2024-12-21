package com.martinsapps.chessproject

import android.content.Context
import android.media.SoundPool

class SoundPlayer(context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(2) // Maximum simultaneous sounds
        .build()

    private var moveSound: Int = 0
    private var captureSound: Int = 0

    init {
        preloadSounds(context)
    }

    //adaws
    private fun preloadSounds(context: Context) {

        moveSound = soundPool.load(context, R.raw.move, 1)
        captureSound = soundPool.load(context, R.raw.capture, 1)
    }


    fun playMoveSound() {
        soundPool.play(moveSound, 1.0f, 1.0f, 0, 0, 1.0f)
    }


    fun playCaptureSound() {
        soundPool.play(captureSound, 1.0f, 1.0f, 0, 0, 1.0f)
    }

    fun release() {
        soundPool.release()
    }
}