package com.martinsapps.chessproject

import android.content.Context
import android.media.SoundPool

class SoundPlayer(context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .build()

    private var moveSound: Int = 0
    private var captureSound: Int = 0
    private var wrongSound: Int = 0
    private var goodSound: Int = 0

    init {
        preloadSounds(context)

    }
    private fun preloadSounds(context: Context) {
        moveSound = soundPool.load(context, R.raw.move, 1)
        captureSound = soundPool.load(context, R.raw.capture, 1)
        wrongSound = soundPool.load(context, R.raw.wrong, 1)
        goodSound = soundPool.load(context, R.raw.good, 1)
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                soundPool.play(sampleId, 0f, 0f, 0, 0, 1.0f)
            }
        }
    }


    fun playMoveSound() {
        soundPool.play(moveSound, 1.0f, 1.0f, 0, 0, 1.0f)
    }


    fun playCaptureSound() {
        soundPool.play(captureSound, 1.0f, 1.0f, 0, 0, 1.0f)
    }

    fun playWrongSound(){
        soundPool.play(wrongSound, 1.0f, 1.0f, 0, 0, 1.0f)
    }

    fun playGoodSound(){
        soundPool.play(goodSound, 1.0f, 1.0f, 0, 0, 1.0f)
    }

    fun release() {
        soundPool.release()
    }
}